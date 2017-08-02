package ru.dobrokvashinevgeny.tander.testtask;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.RepositoryConfig;
import ru.dobrokvashinevgeny.tander.testtask.infrastructure.configuration.AppConfigurationException;
import ru.dobrokvashinevgeny.tander.testtask.presentation.configuration.TaskConfiguration;

/**
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 24.07.2017.
 * @version 1.0 2017
 */
public interface AppConfig extends TaskConfiguration, RepositoryConfig {
	void configure() throws AppConfigurationException;

	String getEntryGeneratorImplClassName();

	String getEntryRepositoryImplClassName();

	String getEntryTransferImplClassName();

	String getEntryConverterImplClassName();

	String getFileStoreImplClassName();

	String getCalculatorImplClassName();

	int getBatchSize();
}
