/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;

/**
 * Реализация сервиса преобразования Entries на JAXB
 */
public class JaxbEntryConverterService implements EntryConverterService {
	private static final long FROM_ENTRY = 1L;

	@Override
	public void convertEntriesToXml(String destXmlFileName, int batchSize, EntryRepository entryRepository,
									FileRepository fileRepository)
			throws EntryConverterServiceException {
		try {
			new SingleThreadConvertEntriesToXmlByBatch(
				FROM_ENTRY, entryRepository.size(), destXmlFileName, batchSize, entryRepository, fileRepository
			).execute();
		} catch (EntryRepositoryException e) {
			throw new EntryConverterServiceException(e);
		}
	}

	@Override
	public void transformEntriesXml(FileRepository fileRepository, String xsltTemplateFileName,
									String srcEntriesXmlFileName, String destXmlFileName, String tmpXmlFileName,
									int batchSize)
			throws EntryConverterServiceException {
		new TransformEntriesXmlByBatch(
			fileRepository, xsltTemplateFileName, srcEntriesXmlFileName,
			destXmlFileName, tmpXmlFileName, batchSize
		).execute();
	}

}