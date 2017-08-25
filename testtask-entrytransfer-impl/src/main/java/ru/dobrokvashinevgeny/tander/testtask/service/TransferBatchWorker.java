/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.AppFactory;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;

import java.util.logging.*;

/**
 * Класс TransferBatchThread
 */
public class TransferBatchWorker implements Runnable {
	private final static Logger LOG = Logger.getLogger(TransferBatchWorker.class.getName());
	private final long fromEntry;
	private final long numberOfEntriesToTransfer;
	private final AppFactory appFactory;
	private final DataSource dataSource;
	EntryTransferException exception;

	public TransferBatchWorker(long fromEntry, long numberOfEntriesToTransfer, AppFactory appFactory,
							   DataSource dataSource) {
		this.fromEntry = fromEntry;
		this.numberOfEntriesToTransfer = numberOfEntriesToTransfer;
		this.appFactory = appFactory;
		this.dataSource = dataSource;
	}

	@Override
	public void run() {
		SingleThreadEntryTransfer singleThreadEntryTransfer = new SingleThreadEntryTransfer();
		try {
			singleThreadEntryTransfer.transferFromGeneratorToRepository(appFactory, fromEntry, numberOfEntriesToTransfer,
				dataSource);
		} catch (EntryTransferException e) {
			LOG.log(Level.SEVERE, "", e);
			exception = e;
		}
	}

	/**
	 * Возвращает
	 *
	 * @return
	 */
	public EntryTransferException getException() {
		return exception;
	}
}