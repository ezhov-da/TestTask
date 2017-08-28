/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;

import java.util.logging.*;

/**
 * Класс BatchConvertWorker
 */
class BatchConvertWorker implements Runnable {
	private final static Logger LOG = Logger.getLogger(BatchConvertWorker.class.getName());

	private final long fromEntry;
	private final long entriesCount;
	private final String destXmlFileName;
	private final int batchSize;
	private final EntryRepository entryRepository;
	private final FileRepository fileRepository;
	private EntryConverterServiceException exception;

	public BatchConvertWorker(long fromEntry, long entriesCount, String destXmlFileName, int batchSize,
							  EntryRepository entryRepository, FileRepository fileRepository) {

		this.fromEntry = fromEntry;
		this.entriesCount = entriesCount;
		this.destXmlFileName = destXmlFileName;
		this.batchSize = batchSize;
		this.entryRepository = entryRepository;
		this.fileRepository = fileRepository;
	}

	@Override
	public void run() {
		SingleThreadConvertEntriesToXmlByBatch singleThreadConverter = new SingleThreadConvertEntriesToXmlByBatch(
			fromEntry, entriesCount, destXmlFileName, batchSize, entryRepository, fileRepository, false
		);

		try {
			singleThreadConverter.execute();
		} catch (EntryConverterServiceException e) {
			LOG.log(Level.SEVERE, "", e);
			exception = e;
		}
	}

	/**
	 * Возвращает
	 *
	 * @return
	 */
	public EntryConverterServiceException getException() {
		return exception;
	}
}