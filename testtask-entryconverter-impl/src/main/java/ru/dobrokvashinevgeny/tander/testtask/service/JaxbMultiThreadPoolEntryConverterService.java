/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;

/**
 * Класс JaxbMultiThreadPoolEntryConverterService
 */
public class JaxbMultiThreadPoolEntryConverterService implements EntryConverterService {
	@Override
	public void convertEntriesToXml(String destXmlFileName, int batchSize, EntryRepository entryRepository,
									FileRepository fileRepository) throws EntryConverterServiceException {
		new JaxbMultiThreadPoolEntryConverterByBatch(destXmlFileName, batchSize, entryRepository, fileRepository)
			.execute();
	}

	@Override
	public void transformEntriesXml(FileRepository fileRepository, String xsltTemplateFileName,
									String srcEntriesXmlFileName, String destXmlFileName, String tmpXmlFileName,
									int batchSize) throws EntryConverterServiceException {
		new TransformEntriesXmlByBatch(
			fileRepository, xsltTemplateFileName, srcEntriesXmlFileName,
			destXmlFileName, tmpXmlFileName, batchSize
		).execute();
	}

	static class JaxbMultiThreadPoolEntryConverterByBatch {
		private final Logger LOG = Logger.getLogger(JaxbMultiThreadPoolEntryConverterByBatch.class.getName());
		private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

		private final String destXmlFileName;
		private final int batchSize;
		private final EntryRepository entryRepository;
		private final FileRepository fileRepository;
		private final List<String> workersDestXmlFileNames = new ArrayList<>(AVAILABLE_PROCESSORS);
		private ConverterWorkerUncaughtExceptionHandler exceptionHandler;
		private CountDownLatch finishLatch;
		private ExecutorService executorService;

		public JaxbMultiThreadPoolEntryConverterByBatch(String destXmlFileName, int batchSize,
														EntryRepository entryRepository,
														FileRepository fileRepository) {

			this.destXmlFileName = destXmlFileName;
			this.batchSize = batchSize;
			this.entryRepository = entryRepository;
			this.fileRepository = fileRepository;
		}

		public void execute() throws EntryConverterServiceException {
			try {
				setUp();

				submitBatchConverterTasks();

				waitForConverterWorkersFinish();

				throwExceptionIfInThreadsWasException();

				shutdownPool();

				joinXmlFilesFromThreadsToOneFile();
			} catch (IOException | FileRepositoryException | EntryRepositoryException e) {
				throw new EntryConverterServiceException(e);
			} finally {
				for (String workerDestXmlFileName : workersDestXmlFileNames) {
					try {
						fileRepository.deleteFile(workerDestXmlFileName);
					} catch (FileRepositoryException e) {
						LOG.log(Level.SEVERE, "Can't delete temp file : " + workerDestXmlFileName, e);
					}
				}
			}
		}

		void setUp() {
			exceptionHandler = new ConverterWorkerUncaughtExceptionHandler();
			executorService = createExecutorService(exceptionHandler);
			finishLatch = createFinishLatch();
		}

		ExecutorService createExecutorService(ConverterWorkerUncaughtExceptionHandler exceptionHandler) {
			return Executors.newFixedThreadPool(AVAILABLE_PROCESSORS, new ThreadFactory() {
				private AtomicInteger counter = new AtomicInteger( 0 );
				@Override
				public Thread newThread(Runnable r) {
					Thread thread = new Thread(r);
					thread.setName("ConvertWorkerThread-" + counter.incrementAndGet());
					thread.setUncaughtExceptionHandler(exceptionHandler);
					return thread;
				}
			});
		}

		CountDownLatch createFinishLatch() {
			return new CountDownLatch(AVAILABLE_PROCESSORS);
		}

		private void submitBatchConverterTasks() throws EntryRepositoryException {
			long numberOfEntriesToTransfer = entryRepository.size();
			long exactBatchSize = numberOfEntriesToTransfer / AVAILABLE_PROCESSORS;
			long restCount = numberOfEntriesToTransfer % AVAILABLE_PROCESSORS;
			long from = 1L;
			for (int procNumber = 0; procNumber < AVAILABLE_PROCESSORS; procNumber++) {
				executorService.submit(createBatchConvertCallWorker(
					from, getAdjustedBatchSize(exactBatchSize, restCount), getNewDestXmlFileName(procNumber),
					batchSize, entryRepository, fileRepository, finishLatch));
				from = from + getAdjustedBatchSize(exactBatchSize, restCount);
				restCount = getAdjustedRestCount(restCount);
			}
		}

		BatchConvertCallWorker createBatchConvertCallWorker(long fromEntry, long entriesCount,
															String destXmlFileName, int batchSize,
															EntryRepository entryRepository,
															FileRepository fileRepository,
															CountDownLatch finishLatch) {
			return new BatchConvertCallWorker(fromEntry, entriesCount, destXmlFileName, batchSize, entryRepository,
				fileRepository, finishLatch);
		}

		private long getAdjustedBatchSize(long exactBatchSize, long restCount) {
			return exactBatchSize + ((restCount > 0) ? 1 : 0);
		}

		private String getNewDestXmlFileName(int i) {
			final String workerDestXmlFileName = getWorkerDestXmlFileName(destXmlFileName, i);
			workersDestXmlFileNames.add(workerDestXmlFileName);
			return workerDestXmlFileName;
		}

		private String getWorkerDestXmlFileName(String destXmlFileName, int i) {
			return i + "_" + UUID.randomUUID() + "-" + destXmlFileName;
		}

		private long getAdjustedRestCount(long restCount) {
			if (restCount > 0) {
				restCount--;
			}
			return restCount;
		}

		private void waitForConverterWorkersFinish() {
			try {
				finishLatch.await();
			} catch (InterruptedException ignore) { /*NOP*/ }
		}

		private void throwExceptionIfInThreadsWasException() throws EntryConverterServiceException {
			if (exceptionHandler.isExceptionThrown()) {
				throw new EntryConverterServiceException("Exception in thread(s), see log for details.");
			}
		}

		private void shutdownPool() {
			executorService.shutdown();
			try {
				executorService.awaitTermination(1, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				LOG.log(Level.WARNING, "Executor Service await termination was interrupted.", e);
			}
		}

		private void joinXmlFilesFromThreadsToOneFile() throws IOException, FileRepositoryException {
			try(BufferedWriter destXmlWriter = fileRepository.getFileDataWriterByName(destXmlFileName)) {
				writeHeaderTo(destXmlWriter);

				for (String workerDestXmlFileName : workersDestXmlFileNames) {
					processWorkerXmlFile(workerDestXmlFileName, destXmlWriter);
				}

				writeFooterTo(destXmlWriter);
			}
		}

		private void writeHeaderTo(BufferedWriter destXmlWriter) throws IOException {
			destXmlWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?><entries>");
		}

		private void processWorkerXmlFile(String workerDestXmlFileName, BufferedWriter destXmlWriter)
			throws IOException, FileRepositoryException {
			try (BufferedReader reader = fileRepository.getFileDataReaderByName(workerDestXmlFileName)) {
				char[] buffer = new char[1024];
				int amount = reader.read(buffer);
				while (-1 != amount) {
					destXmlWriter.write(buffer, 0, amount);
					amount = reader.read(buffer);
				}
				destXmlWriter.flush();
			}
		}

		private void writeFooterTo(BufferedWriter destXmlWriter) throws IOException {
			destXmlWriter.write("</entries>");
		}
	}

	static class ConverterWorkerUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
		private final Logger LOG = Logger.getLogger(ConverterWorkerUncaughtExceptionHandler.class.getName());
		private boolean exceptionThrown = false;

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			exceptionThrown = true;
			LOG.log(Level.WARNING, t.getName() + " raise exception.", e);
		}

		/**
		 * Возвращает
		 *
		 * @return
		 */
		public boolean isExceptionThrown() {
			return exceptionThrown;
		}
	}
}