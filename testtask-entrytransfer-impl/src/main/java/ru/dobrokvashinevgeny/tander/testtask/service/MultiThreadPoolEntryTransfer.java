/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.AppFactory;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;

/**
 * Многопоточная релизация сервиса передачи сгенерированных Entries в их хранилище для сохранения
 * с использованием ExecutorService
 */
public class MultiThreadPoolEntryTransfer implements EntryTransfer {
	private final static Logger LOG = Logger.getLogger(MultiThreadPoolEntryTransfer.class.getName());
	private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

	@Override
	public void transferFromGeneratorToRepository(AppFactory appFactory, long fromEntry,
												  long numberOfEntriesToTransfer, DataSource dataSource)
		throws EntryTransferException {
		long exactBatchSize = numberOfEntriesToTransfer / AVAILABLE_PROCESSORS;
		long restCount = numberOfEntriesToTransfer % AVAILABLE_PROCESSORS;
		long from = fromEntry;
		final TransferWorkerUncaughtExceptionHandler transferWorkerUncaughtExceptionHandler =
			new TransferWorkerUncaughtExceptionHandler();
		ExecutorService executorService = createExecutorService(transferWorkerUncaughtExceptionHandler);
		final CountDownLatch finishLatch = createFinishLatch();

		for (long i = 0; i < AVAILABLE_PROCESSORS; i++) {
			final long adjustedBatchSize = getAdjustedBatchSize(exactBatchSize, restCount);
			executorService.submit(createTransferBatchCallWorker(
				from, adjustedBatchSize, appFactory, dataSource, finishLatch));
			from = from + adjustedBatchSize;
			restCount = getAdjustedRestCount(restCount);
		}

		waitForTransferWorkersFinish(finishLatch);

		if (transferWorkerUncaughtExceptionHandler.isExceptionThrown()) {
			throw new EntryTransferException("Exception in thread(s), see log for details.");
		}

		executorService.shutdown();
		try {
			executorService.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			LOG.log(Level.WARNING, "Executor Service await termination was interrupted.", e);
		}
	}

	ExecutorService createExecutorService(TransferWorkerUncaughtExceptionHandler exceptionHandler) {
		return Executors.newFixedThreadPool(AVAILABLE_PROCESSORS, new ThreadFactory() {
			private AtomicInteger counter = new AtomicInteger( 0 );
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setName("TransferWorkerThread-" + counter.incrementAndGet());
				thread.setUncaughtExceptionHandler(exceptionHandler);
				return thread;
			}
		});
	}

	CountDownLatch createFinishLatch() {
		return new CountDownLatch(AVAILABLE_PROCESSORS);
	}

	class TransferWorkerUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
		private final Logger LOG = Logger.getLogger(TransferWorkerUncaughtExceptionHandler.class.getName());
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

	private long getAdjustedBatchSize(long exactBatchSize, long restCount) {
		return exactBatchSize + ((restCount > 0) ? 1 : 0);
	}

	TransferBatchCallWorker createTransferBatchCallWorker(long from, long batchSize, AppFactory appFactory,
														  DataSource dataSource, CountDownLatch finishLatch) {
		return new TransferBatchCallWorker(from, batchSize, appFactory, dataSource, finishLatch);
	}

	private long getAdjustedRestCount(long restCount) {
		if (restCount > 0) {
			restCount--;
		}
		return restCount;
	}

	private void waitForTransferWorkersFinish(CountDownLatch finishLatch) {
		try {
			finishLatch.await();
		} catch (InterruptedException ignore) { /*NOP*/ }
	}
}