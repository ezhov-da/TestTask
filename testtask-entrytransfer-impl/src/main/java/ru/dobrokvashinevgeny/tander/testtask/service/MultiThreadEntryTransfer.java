/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.AppFactory;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;

import java.util.*;
import java.util.logging.*;

/**
 * Многопоточная релизация сервиса передачи сгенерированных Entries в их хранилище для сохранения
 * с использованием отдельных потоков Thread
 */
public class MultiThreadEntryTransfer implements EntryTransfer {
	private final static Logger LOG = Logger.getLogger(MultiThreadEntryTransfer.class.getName());
	private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

	@Override
	public void transferFromGeneratorToRepository(AppFactory appFactory, long fromEntry, long numberOfEntriesToTransfer,
												  DataSource dataSource) throws EntryTransferException {
		List<Thread> transferBatchThreads = new ArrayList<>(AVAILABLE_PROCESSORS);
		List<TransferBatchWorker> transferBatchWorkers = new ArrayList<>(AVAILABLE_PROCESSORS);

		try {
			long exactBatchSize = numberOfEntriesToTransfer / AVAILABLE_PROCESSORS;
			long restCount = numberOfEntriesToTransfer % AVAILABLE_PROCESSORS;
			long from = fromEntry;
			for (long i = 0; i < AVAILABLE_PROCESSORS; i++) {
				final long adjustedBatchSize = getAdjustedBatchSize(exactBatchSize, restCount);
				final TransferBatchWorker transferBatchThread =
					createTransferBatchWorker(from, adjustedBatchSize, appFactory, dataSource);
				transferBatchWorkers.add(transferBatchThread);
				transferBatchThreads.add(createThreadFor(transferBatchThread, i));
				from = from + adjustedBatchSize;
				restCount = getAdjustedRestCount(restCount);
			}

			for (Thread transferBatchThread : transferBatchThreads) {
				transferBatchThread.start();
			}

			for (Thread transferBatchThread : transferBatchThreads) {
				transferBatchThread.join();
			}

			boolean isExceptionThrown = false;
			for (TransferBatchWorker transferBatchWorker : transferBatchWorkers) {
				EntryTransferException exception = transferBatchWorker.getException();
				if (null != exception) {
					LOG.log(Level.SEVERE, "Exception in thread during multi thread transfer:", exception);
					isExceptionThrown = true;
				}
			}

			if (isExceptionThrown) {
				throw new EntryTransferException("Exception in thread, see log for details.");
			}
		} catch (InterruptedException e) {
			throw new EntryTransferException(e);
		}
	}

	TransferBatchWorker createTransferBatchWorker(long from, long adjustedBatchSize, AppFactory appFactory,
												  DataSource dataSource) {
		return new TransferBatchWorker(from, adjustedBatchSize, appFactory, dataSource);
	}

	Thread createThreadFor(TransferBatchWorker transferBatchThread, long i) {
		return new Thread(transferBatchThread, "TransferBatchThread-" + i);
	}

	private long getAdjustedBatchSize(long exactBatchSize, long restCount) {
		return exactBatchSize + ((restCount > 0) ? 1 : 0);
	}

	private long getAdjustedRestCount(long restCount) {
		if (restCount > 0) {
			restCount--;
		}
		return restCount;
	}
}