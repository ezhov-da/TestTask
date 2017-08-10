package ru.dobrokvashinevgeny.tander.testtask.service;

/**
 * Интерфейс конфигурационных данных сервиса приложения
 */
public interface TestTaskServiceConfig {
	/**
	 *
	 * @return
	 */
	String getEntryGeneratorImplClassName();

	/**
	 *
	 * @return
	 */
	String getEntryRepositoryImplClassName();

	/**
	 *
	 * @return
	 */
	String getEntryTransferImplClassName();

	/**
	 *
	 * @return
	 */
	String getEntryConverterImplClassName();

	/**
	 *
	 * @return
	 */
	String getFileStoreImplClassName();

	/**
	 *
	 * @return
	 */
	String getCalculatorImplClassName();

	/**
	 * Размер пакета для паектной обработки данных в приложении
	 * @return
	 */
	int getBatchSize();
}