/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.AppFactory;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;

import java.util.concurrent.*;

/**
 * Класс TransferBatchCallWorker
 */
public class TransferBatchCallWorker implements Callable<Void> {
	private final long fromEntry;
	private final long numberOfEntriesToTransfer;
	private final AppFactory appFactory;
	private final DataSource dataSource;
	private final CountDownLatch finishLatch;

	public TransferBatchCallWorker(long fromEntry, long numberOfEntriesToTransfer, AppFactory appFactory, DataSource dataSource,
								   CountDownLatch finishLatch) {
		this.fromEntry = fromEntry;
		this.numberOfEntriesToTransfer = numberOfEntriesToTransfer;
		this.appFactory = appFactory;
		this.dataSource = dataSource;
		this.finishLatch = finishLatch;
	}

	@Override
	public Void call() throws Exception {
		SingleThreadEntryTransfer singleThreadEntryTransfer = new SingleThreadEntryTransfer();
		singleThreadEntryTransfer.transferFromGeneratorToRepository(appFactory, fromEntry, numberOfEntriesToTransfer,
			dataSource);
		finishLatch.countDown();
		return null;
	}
}