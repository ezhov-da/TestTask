package ru.dobrokvashinevgeny.tander.testtask.service;

/**
 * Интерфейс конфигурационных данных сервиса приложения
 */
public interface TestTaskServiceConfig {
	/**
	 * Имя класса реализации интерфейса EntryGenerator
	 */
	String getEntryGeneratorImplClassName();

	/**
	 * Имя класса реализации интерфейса EntryRepository
	 */
	String getEntryRepositoryImplClassName();

	/**
	 * Имя класса реализации интерфейса EntryTransfer
	 */
	String getEntryTransferImplClassName();

	/**
	 * Имя класса реализации интерфейса EntryConverter
	 */
	String getEntryConverterImplClassName();

	/**
	 * Имя класса реализации интерфейса FileRepository
	 */
	String getFileRepositoryImplClassName();

	/**
	 * Имя класса реализации интерфейса Calculator
	 */
	String getCalculatorImplClassName();

	/**
	 * Размер пакета для пакетной обработки данных из генератора в репозитарий
	 */
	int getTransferBatchSize();

	/**
	 * Размер пакета для пакетной обработки данных converter to XML
	 */
	int getСonverterBatchSize();

	/**
	 * Размер пакета для пакетной обработки данных XSLT transformer
	 */
	int getTransformerBatchSize();
}