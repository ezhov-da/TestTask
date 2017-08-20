package ru.dobrokvashinevgeny.tander.testtask.service;

/**
 * Интерфейс конфигурационных данных сервиса приложения
 */
public interface TestTaskServiceConfig {
	/**
	 * Размер пакета для пакетной обработки данных из генератора в репозитарий
	 */
	int getTransferBatchSize();

	/**
	 * Размер пакета для пакетной обработки данных converter to XML
	 */
	int getConverterBatchSize();

	/**
	 * Размер пакета для пакетной обработки данных XSLT transformer
	 */
	int getTransformerBatchSize();
}