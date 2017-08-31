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
 * с использованием ForkJoin Pool
 */
public class MultiThreadForkJoinEntryTransfer implements EntryTransfer {
	private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

	@Override
	public void transferFromGeneratorToRepository(AppFactory appFactory, long fromEntry, long numberOfEntriesToTransfer,
												  DataSource dataSource) throws EntryTransferException {
		final TransferWorkerUncaughtExceptionHandler transferWorkerUncaughtExceptionHandler =
			new TransferWorkerUncaughtExceptionHandler();
		ForkJoinPool forkJoinPool = createForkJoinPool(transferWorkerUncaughtExceptionHandler);

		forkJoinPool.invoke(createTransferBatchRecursiveAction(appFactory, fromEntry, numberOfEntriesToTransfer,
			dataSource, numberOfEntriesToTransfer / AVAILABLE_PROCESSORS));

		if (transferWorkerUncaughtExceptionHandler.isExceptionThrown()) {
			throw new EntryTransferException("Exception in TransferBatchRecursiveAction(s), see log for details.");
		}
	}

	ForkJoinPool createForkJoinPool(TransferWorkerUncaughtExceptionHandler transferWorkerUncaughtExceptionHandler) {
		return new ForkJoinPool(AVAILABLE_PROCESSORS, new ForkJoinPool.ForkJoinWorkerThreadFactory() {
			private AtomicInteger counter = new AtomicInteger( 0 );
			@Override
			public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
				ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
				thread.setName("TransferWorkerThread-" + counter.incrementAndGet());
				return thread;
			}
		}, transferWorkerUncaughtExceptionHandler, false);
	}

	static class TransferBatchRecursiveAction extends RecursiveAction {
		private final AppFactory appFactory;
		private final long fromEntry;
		private final long numberOfEntriesToTransfer;
		private final DataSource dataSource;
		private final long cutoffNumberOfEntriesToTransfer;

		TransferBatchRecursiveAction(AppFactory appFactory, long fromEntry, long numberOfEntriesToTransfer,
									 DataSource dataSource, long cutoffNumberOfEntriesToTransfer) {
			this.appFactory = appFactory;
			this.fromEntry = fromEntry;
			this.numberOfEntriesToTransfer = numberOfEntriesToTransfer;
			this.dataSource = dataSource;
			this.cutoffNumberOfEntriesToTransfer = cutoffNumberOfEntriesToTransfer;
		}

		@Override
		protected void compute() {
			if (numberOfEntriesToTransfer <= cutoffNumberOfEntriesToTransfer ) {
				runSequentialTransferAction();
			} else {
				splitTransferActionOnLeftAndRightActions();
			}
		}

		void runSequentialTransferAction() {
			SingleThreadEntryTransfer singleThreadEntryTransfer = new SingleThreadEntryTransfer();
			try {
				singleThreadEntryTransfer.transferFromGeneratorToRepository(appFactory, fromEntry,
					numberOfEntriesToTransfer, dataSource);
			} catch (EntryTransferException e) {
				completeExceptionally(e);
			}
		}

		private void splitTransferActionOnLeftAndRightActions() {
			long computedNumberOfEntriesToTransferLeft = numberOfEntriesToTransfer >>> 1;
			long computedFromRight = fromEntry + computedNumberOfEntriesToTransferLeft;
			long computedNumberOfEntriesToTransferRight =
				numberOfEntriesToTransfer - computedNumberOfEntriesToTransferLeft;
			TransferBatchRecursiveAction actionLeft =
				new TransferBatchRecursiveAction(appFactory, fromEntry, computedNumberOfEntriesToTransferLeft,
					dataSource, cutoffNumberOfEntriesToTransfer);
			TransferBatchRecursiveAction actionRight =
				new TransferBatchRecursiveAction(appFactory, computedFromRight,
					computedNumberOfEntriesToTransferRight, dataSource, cutoffNumberOfEntriesToTransfer);
			invokeAll(actionLeft, actionRight);
		}
	}

	TransferBatchRecursiveAction createTransferBatchRecursiveAction(AppFactory appFactory, long fromEntry,
																	long numberOfEntriesToTransfer,
																	DataSource dataSource,
																	long cutoffNumberOfEntriesToTransfer) {
		return new TransferBatchRecursiveAction(appFactory, fromEntry, numberOfEntriesToTransfer, dataSource,
			cutoffNumberOfEntriesToTransfer);
	}

	class TransferWorkerUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
		private final Logger LOG = Logger.getLogger(MultiThreadPoolEntryTransfer.TransferWorkerUncaughtExceptionHandler.class.getName());
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
		public boolean isExceptionThrown() {
			return exceptionThrown;
		}
	}
}