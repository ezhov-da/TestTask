/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * Класс JaxbMultiThreadEntryConverterService
 */
public class JaxbMultiThreadEntryConverterService implements EntryConverterService {
	private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

	@Override
	public void convertEntriesToXml(String destXmlFileName, int batchSize, EntryRepository entryRepository,
									FileRepository fileRepository) throws EntryConverterServiceException {
		new JaxbMultiThreadEntryConverterByBatch(destXmlFileName, batchSize, entryRepository, fileRepository).execute();
	}

	static class JaxbMultiThreadEntryConverterByBatch {
		private final Logger LOG = Logger.getLogger(JaxbMultiThreadEntryConverterByBatch.class.getName());
		private final String destXmlFileName;
		private final int batchSize;
		private final EntryRepository entryRepository;
		private final FileRepository fileRepository;
		private final List<Thread> convertBatchThreads = new ArrayList<>(AVAILABLE_PROCESSORS);
		private final List<BatchConvertWorker> batchConvertWorkers = new ArrayList<>(AVAILABLE_PROCESSORS);
		private final List<String> workersDestXmlFileNames = new ArrayList<>(AVAILABLE_PROCESSORS);

		public JaxbMultiThreadEntryConverterByBatch(String destXmlFileName, int batchSize,
													EntryRepository entryRepository, FileRepository fileRepository) {
			this.destXmlFileName = destXmlFileName;
			this.batchSize = batchSize;
			this.entryRepository = entryRepository;
			this.fileRepository = fileRepository;
		}

		public void execute() throws EntryConverterServiceException {
			try {
				createBatchConverterThreads();

				startBatchConverterThreads();

				waitForBatchConverterThreadsEndWork();

				throwExceptionIfInThreadsWasException();

				joinXmlFilesFromThreadsToOneFile();
			} catch (IOException | FileRepositoryException | EntryRepositoryException | InterruptedException e) {
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

		private void createBatchConverterThreads() throws EntryRepositoryException {
			long numberOfEntriesToTransfer = entryRepository.size();
			long exactBatchSize = numberOfEntriesToTransfer / AVAILABLE_PROCESSORS;
			long restCount = numberOfEntriesToTransfer % AVAILABLE_PROCESSORS;
			long from = 1L;
			for (int procNumber = 0; procNumber < AVAILABLE_PROCESSORS; procNumber++) {
				addNewConvertBatchTread(exactBatchSize, restCount, from, procNumber);
				from = from + getAdjustedBatchSize(exactBatchSize, restCount);
				restCount = getAdjustedRestCount(restCount);
			}
		}

		private void addNewConvertBatchTread(long exactBatchSize, long restCount, long from, int procNumber) {
			final BatchConvertWorker batchConvertWorker =
				getNewBatchConvertWorker(exactBatchSize, restCount, from, procNumber);
			convertBatchThreads.add(createThreadFor(batchConvertWorker, procNumber));
		}

		private BatchConvertWorker getNewBatchConvertWorker(long exactBatchSize, long restCount, long from, int procNumber) {
			final BatchConvertWorker batchConvertWorker =
				createBatchConvertWorker(from, getAdjustedBatchSize(exactBatchSize, restCount), getNewDestXmlFileName(procNumber), batchSize, entryRepository,
					fileRepository);
			batchConvertWorkers.add(batchConvertWorker);
			return batchConvertWorker;
		}

		BatchConvertWorker createBatchConvertWorker(long fromEntry, long entriesCount, String destXmlFileName,
													int batchSize, EntryRepository entryRepository,
													FileRepository fileRepository) {
			return new BatchConvertWorker(fromEntry, entriesCount, destXmlFileName, batchSize, entryRepository,
				fileRepository);
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

		Thread createThreadFor(BatchConvertWorker batchConvertWorker, int i) {
			return new Thread(batchConvertWorker, "BatchConvertThread-" + i);
		}

		private long getAdjustedRestCount(long restCount) {
			if (restCount > 0) {
				restCount--;
			}
			return restCount;
		}

		void startBatchConverterThreads() {
			for (Thread convertBatchThread : convertBatchThreads) {
				convertBatchThread.start();
			}
		}

		void waitForBatchConverterThreadsEndWork() throws InterruptedException {
			for (Thread convertBatchThread : convertBatchThreads) {
				convertBatchThread.join();
			}
		}

		void throwExceptionIfInThreadsWasException() throws EntryConverterServiceException {
			boolean isExceptionThrown = false;
			for (BatchConvertWorker batchConvertWorker : batchConvertWorkers) {
				EntryConverterServiceException exception = batchConvertWorker.getException();
				if (null != exception) {
					LOG.log(Level.SEVERE, "Exception in thread during multi thread convert:", exception);
					isExceptionThrown = true;
				}
			}

			if (isExceptionThrown) {
				throw new EntryConverterServiceException("Exception in thread, see log for details.");
			}
		}

		void joinXmlFilesFromThreadsToOneFile() throws IOException, FileRepositoryException {
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

		private void processWorkerXmlFile(String workerDestXmlFileName, BufferedWriter destXmlWriter) throws IOException, FileRepositoryException {
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

	@Override
	public void transformEntriesXml(FileRepository fileRepository, String xsltTemplateFileName,
									String srcEntriesXmlFileName, String destXmlFileName, String tmpXmlFileName,
									int batchSize) throws EntryConverterServiceException {
		new TransformEntriesXmlByBatch(
			fileRepository, xsltTemplateFileName, srcEntriesXmlFileName,
			destXmlFileName, tmpXmlFileName, batchSize
		).execute();
	}
}