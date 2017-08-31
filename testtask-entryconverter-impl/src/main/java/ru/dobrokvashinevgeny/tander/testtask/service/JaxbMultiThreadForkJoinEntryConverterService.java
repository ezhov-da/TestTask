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
 * Класс JaxbMultiThreadForkJoinEntryConverterService
 */
public class JaxbMultiThreadForkJoinEntryConverterService implements EntryConverterService {
	@Override
	public void convertEntriesToXml(String destXmlFileName, int batchSize, EntryRepository entryRepository,
									FileRepository fileRepository) throws EntryConverterServiceException {
		new JaxbMultiThreadEntryConverterService.JaxbMultiThreadEntryConverterByBatch(destXmlFileName, batchSize,
			entryRepository, fileRepository).execute();
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

	static class JaxbMultiThreadForkJoinEntryConverterByBatch {
		private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

		private final String destXmlFileName;
		private final int batchSize;
		private final EntryRepository entryRepository;
		private final FileRepository fileRepository;
		private final List<String> workersDestXmlFileNames =
			Collections.synchronizedList(new ArrayList<>(AVAILABLE_PROCESSORS));
		private ForkJoinPool forkJoinPool;
		private ConverterWorkerUncaughtExceptionHandler exceptionHandler;

		JaxbMultiThreadForkJoinEntryConverterByBatch(String destXmlFileName, int batchSize,
															EntryRepository entryRepository,
															FileRepository fileRepository) {

			this.destXmlFileName = destXmlFileName;
			this.batchSize = batchSize;
			this.entryRepository = entryRepository;
			this.fileRepository = fileRepository;
		}

		public void execute() throws EntryConverterServiceException {
			setUp();

			try {
				invokeBatchConverterActions();

				throwExceptionIfInThreadsWasException(exceptionHandler);

				joinXmlFilesFromThreadsToOneFile();
			} catch (FileRepositoryException | EntryRepositoryException | IOException | EntryTransferException e) {
				throw new EntryConverterServiceException(e);
			}
		}

		private void setUp() {
			exceptionHandler = new ConverterWorkerUncaughtExceptionHandler();
			forkJoinPool = createForkJoinPool(exceptionHandler);
		}

		ForkJoinPool createForkJoinPool(ConverterWorkerUncaughtExceptionHandler exceptionHandler) {
			return new ForkJoinPool(AVAILABLE_PROCESSORS, new ForkJoinPool.ForkJoinWorkerThreadFactory() {
				private AtomicInteger counter = new AtomicInteger( 0 );
				@Override
				public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
					ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
					thread.setName("ConvertWorkerThread-" + counter.incrementAndGet());
					return thread;
				}
			}, exceptionHandler, false);
		}

		private void invokeBatchConverterActions() throws EntryRepositoryException {
			final long entriesCount = entryRepository.size();
			forkJoinPool.invoke(createBatchRecursiveRecursiveAction(1L, entriesCount,
				workersDestXmlFileNames, batchSize, entryRepository, fileRepository,
				entriesCount / AVAILABLE_PROCESSORS));
		}

		BatchConvertRecursiveAction createBatchRecursiveRecursiveAction(long fromEntry, long entriesCount,
																		List<String> workersDestXmlFileNames, int batchSize,
																		EntryRepository entryRepository,
																		FileRepository fileRepository,
																		long cutoffNumberOfEntriesToConvert) {
			return new BatchConvertRecursiveAction(fromEntry, entriesCount, workersDestXmlFileNames, batchSize,
				entryRepository, fileRepository, cutoffNumberOfEntriesToConvert);
		}

		private void throwExceptionIfInThreadsWasException(ConverterWorkerUncaughtExceptionHandler exceptionHandler) throws EntryTransferException {
			if (exceptionHandler.isExceptionThrown()) {
				throw new EntryTransferException("Exception in BatchConvertRecursiveAction(s), see log for details.");
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

	static class BatchConvertRecursiveAction extends RecursiveAction {
		private final long fromEntry;
		private final long entriesCount;
		private final List<String> workersDestXmlFileNames;
		private final int batchSize;
		private final EntryRepository entryRepository;
		private final FileRepository fileRepository;
		private final long cutoffNumberOfEntriesToConvert;

		BatchConvertRecursiveAction(long fromEntry, long entriesCount, List<String> workersDestXmlFileNames,
									int batchSize, EntryRepository entryRepository, FileRepository fileRepository,
									long cutoffNumberOfEntriesToConvert) {

			this.fromEntry = fromEntry;
			this.entriesCount = entriesCount;
			this.workersDestXmlFileNames = workersDestXmlFileNames;
			this.batchSize = batchSize;
			this.entryRepository = entryRepository;
			this.fileRepository = fileRepository;
			this.cutoffNumberOfEntriesToConvert = cutoffNumberOfEntriesToConvert;
		}

		@Override
		protected void compute() {
			if (entriesCount <= cutoffNumberOfEntriesToConvert ) {
				runSequentialConvertAction();
			} else {
				splitConvertActionOnLeftAndRightActions();
			}
		}

		private void runSequentialConvertAction() {
			final String destXmlFileName = getNewDestXmlFileName();

			SingleThreadConvertEntriesToXmlByBatch singleThreadConverter = new SingleThreadConvertEntriesToXmlByBatch(
				fromEntry, entriesCount, destXmlFileName, batchSize, entryRepository, fileRepository, false
			);

			try {
				singleThreadConverter.execute();
			} catch (EntryConverterServiceException e) {
				completeExceptionally(e);
			}
		}

		private String getNewDestXmlFileName() {
			final String workerDestXmlFileName = getActionDestXmlFileName(Thread.currentThread().getName());
			workersDestXmlFileNames.add(workerDestXmlFileName);
			return workerDestXmlFileName;
		}

		private String getActionDestXmlFileName(String destXmlFileName) {
			return destXmlFileName + "_" + UUID.randomUUID() + ".xml";
		}

		private void splitConvertActionOnLeftAndRightActions() {
			long computedNumberOfEntriesToConvertLeft = entriesCount >>> 1;
			long computedFromRight = fromEntry + computedNumberOfEntriesToConvertLeft;
			long computedNumberOfEntriesToConvertRight =
				entriesCount - computedNumberOfEntriesToConvertLeft;
			BatchConvertRecursiveAction actionLeft =
				new BatchConvertRecursiveAction(fromEntry, computedNumberOfEntriesToConvertLeft,
					workersDestXmlFileNames, batchSize, entryRepository, fileRepository,
					cutoffNumberOfEntriesToConvert);
			BatchConvertRecursiveAction actionRight =
				new BatchConvertRecursiveAction(computedFromRight, computedNumberOfEntriesToConvertRight,
					workersDestXmlFileNames, batchSize, entryRepository, fileRepository,
					cutoffNumberOfEntriesToConvert);
			invokeAll(actionLeft, actionRight);
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
		 * Возвращает было ли исключение во время вполнения задач переноса Entries
		 *
		 * @return
		 */
		boolean isExceptionThrown() {
			return exceptionThrown;
		}
	}
}