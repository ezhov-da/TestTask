package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;

/**
 * Интерфейс сервиса преобразования Entries
 */
public interface EntryConverterService {
	/**
	 * Конвертирует Entries из хранлища в XML представление в файл
	 * @param destXmlFileName имя XML файла
	 * @param batchSize размер пакета для пакетной обработки данных
	 * @param entryRepository хранилище Entries
	 * @param fileRepository хранилище файлов
	 * @throws EntryConverterServiceException если произошла ошибка во время конвертации
	 */
	void convertEntriesToXml(String destXmlFileName, int batchSize, EntryRepository entryRepository, FileRepository fileRepository)
			throws EntryConverterServiceException;

	/**
	 * Преобразует XML из файла в соотвествии с XSLT-преобразованием
	 * @param fileRepository хранилище файлов
	 * @param xsltTemplateFileName имя файла XSLT-шаблона
	 * @param srcEntriesXmlFileName имя исходного XML-файла
	 * @param destXmlFileName имя результирующего XML-файла
	 * @param tmpXmlFileName имя временного файла
	 * @param batchSize размер пакета для пакетной обработки данных
	 * @throws EntryConverterServiceException если произошла ошибка во время XSLT-преобразования
	 */
	void transformEntriesXml(FileRepository fileRepository, String xsltTemplateFileName, String srcEntriesXmlFileName,
							 String destXmlFileName, String tmpXmlFileName, int batchSize)
		throws EntryConverterServiceException;
}
