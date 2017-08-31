/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;

import java.util.concurrent.*;

/**
 * Класс BatchConvertCallWorker
 */
class BatchConvertCallWorker implements Callable<Void> {
	private final long fromEntry;
	private final long entriesCount;
	private final String destXmlFileName;
	private final int batchSize;
	private final EntryRepository entryRepository;
	private final FileRepository fileRepository;
	private final CountDownLatch finishLatch;

	public BatchConvertCallWorker(long fromEntry, long entriesCount, String destXmlFileName, int batchSize,
								  EntryRepository entryRepository, FileRepository fileRepository,
								  CountDownLatch finishLatch) {

		this.fromEntry = fromEntry;
		this.entriesCount = entriesCount;
		this.destXmlFileName = destXmlFileName;
		this.batchSize = batchSize;
		this.entryRepository = entryRepository;
		this.fileRepository = fileRepository;
		this.finishLatch = finishLatch;
	}

	@Override
	public Void call() throws Exception {
		SingleThreadConvertEntriesToXmlByBatch singleThreadConverter = new SingleThreadConvertEntriesToXmlByBatch(
			fromEntry, entriesCount, destXmlFileName, batchSize, entryRepository, fileRepository, false
		);

		singleThreadConverter.execute();

		finishLatch.countDown();

		return null;
	}
}