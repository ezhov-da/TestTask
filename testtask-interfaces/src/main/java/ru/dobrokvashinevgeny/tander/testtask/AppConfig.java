package ru.dobrokvashinevgeny.tander.testtask;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.RepositoryConfig;
import ru.dobrokvashinevgeny.tander.testtask.infrastructure.configuration.AppConfigurationException;
import ru.dobrokvashinevgeny.tander.testtask.presentation.configuration.TaskConfiguration;
import ru.dobrokvashinevgeny.tander.testtask.service.TestTaskServiceConfig;

/**
 * Интерфейс конфигурации приложения
 */
public interface AppConfig extends TaskConfiguration, RepositoryConfig, TestTaskServiceConfig {
	/**
	 * Считывание конфигурации приложения из файла
	 * @throws AppConfigurationException если ошибка при конфигурировании из файла
	 */
	void configure() throws AppConfigurationException;

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
}
