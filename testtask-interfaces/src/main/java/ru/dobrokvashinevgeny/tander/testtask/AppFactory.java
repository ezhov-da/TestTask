package ru.dobrokvashinevgeny.tander.testtask;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;
import ru.dobrokvashinevgeny.tander.testtask.service.*;
import ru.dobrokvashinevgeny.tander.testtask.service.generator.EntryGenerator;

/**
 * Класс AppFactory
 */
public interface AppFactory {
	EntryGenerator createEntryGenerator() throws AppFactoryException;

	EntryRepository createEntryRepository(DataSource dataSource) throws AppFactoryException;

	EntryTransfer createEntryTransfer() throws AppFactoryException;

	FileRepository createFileRepository() throws AppFactoryException;

	EntryConverterService createEntryConverterService() throws AppFactoryException;

	Calculator createCalculator() throws AppFactoryException;
}