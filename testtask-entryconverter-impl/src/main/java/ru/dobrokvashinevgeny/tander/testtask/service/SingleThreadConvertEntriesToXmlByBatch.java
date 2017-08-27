/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;

import javax.xml.bind.*;
import java.io.*;
import java.util.List;

/**
 * Однопоточный пакетный конвертер Entries из хранлища в XML представление в файл
 */
public class SingleThreadConvertEntriesToXmlByBatch {
	private final long fromEntry;
	private final long entriesCount;
	private final String destXmlFileName;
	private final int batchSize;
	private final EntryRepository entryRepository;
	private final FileRepository fileRepository;

	/**
	 * Создает новый экземпляр по параметрам
	 * @param fromEntry id Entry с которого начинать конвертацию
	 * @param entriesCount количество Entry для коныертации
	 * @param destXmlFileName имя XML файла
	 * @param batchSize размер пакета для пакетной обработки данных
	 * @param entryRepository хранилище Entries
	 * @param fileRepository хранилище файлов
	 */
	public SingleThreadConvertEntriesToXmlByBatch(long fromEntry, long entriesCount, String destXmlFileName,
												  int batchSize, EntryRepository entryRepository,
												  FileRepository fileRepository) {
		this.fromEntry = fromEntry;
		this.entriesCount = entriesCount;
		this.destXmlFileName = destXmlFileName;
		this.batchSize = batchSize;
		this.entryRepository = entryRepository;
		this.fileRepository = fileRepository;
	}

	/**
	 * Запуск конвертации
	 * @throws EntryConverterServiceException если произошла ошибка во время конвертации
	 */
	public void execute() throws EntryConverterServiceException {
		try (BufferedWriter destXmlWriter = fileRepository.getFileDataWriterByName(destXmlFileName)) {
			Marshaller marshaller = getJaxbMarshaller();
			writeHeaderTo(destXmlWriter);

			long currentEntryId = fromEntry;
			while (currentEntryId <= getMaxEntryId()) {
				List<Entry> entries =
					entryRepository.getEntriesFromRange(currentEntryId, getToEntryId(currentEntryId));

				marshalEntriesBatchToDest(marshaller, entries, destXmlWriter);

				currentEntryId = getToEntryId(currentEntryId) + 1;
			}

			writeFooterTo(destXmlWriter);

			destXmlWriter.flush();
		} catch (JAXBException | EntryRepositoryException | FileRepositoryException | IOException e) {
			throw new EntryConverterServiceException(e);
		}
	}

	private Marshaller getJaxbMarshaller() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(EntryImpl.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		return marshaller;
	}

	private void writeHeaderTo(BufferedWriter destXmlWriter) throws IOException {
		destXmlWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?><entries>");
	}

	private long getToEntryId(long currentEntryId) {
		long result = currentEntryId + batchSize - 1;
		if (result > getMaxEntryId()) {
			result = getMaxEntryId();
		}
		return result;
	}

	long getMaxEntryId() {
		return fromEntry + entriesCount;
	}

	private void marshalEntriesBatchToDest(Marshaller marshaller, List<Entry> entries, BufferedWriter destXmlWriter)
		throws JAXBException, IOException {
		for (Entry entry : entries) {
			marshaller.marshal(entry, destXmlWriter);
		}

		destXmlWriter.flush();
	}

	private void writeFooterTo(BufferedWriter destXmlWriter) throws IOException {
		destXmlWriter.write("</entries>");
	}
}