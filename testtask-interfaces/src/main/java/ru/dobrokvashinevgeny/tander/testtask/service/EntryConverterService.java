package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;

/**
 * @author Evgeny Dobrokvashin
 *         Created by Stalker on 19.07.2017.
 * @version 1.0 2017
 */
public interface EntryConverterService {
	void convertEntriesToXml(EntryRepository entryRepository, FileStore fileStore, String destXmlFileName, int batchSize)
			throws EntryConverterServiceException;

	void transformEntriesXml(FileStore fileStore, String xsltTemplateFileName, String srcEntriesXmlFileName,
							 String destXmlFileName, String tmpXmlFileName, int batchSize)
		throws EntryConverterServiceException;
}
