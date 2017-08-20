/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure;

import ru.dobrokvashinevgeny.tander.testtask.*;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;
import ru.dobrokvashinevgeny.tander.testtask.service.*;
import ru.dobrokvashinevgeny.tander.testtask.service.generator.EntryGenerator;

import java.lang.reflect.InvocationTargetException;

/**
 * Класс AppFactoryImpl
 */
public class AppFactoryImpl implements AppFactory {
	private final AppConfig config;

	public AppFactoryImpl(AppConfig config) {
		this.config = config;
	}

	@Override
	public EntryGenerator createEntryGenerator() throws AppFactoryException {
		return (EntryGenerator) getInstanceWoParamsByClassName(config.getEntryGeneratorImplClassName());
	}

	@Override
	public EntryRepository createEntryRepository(DataSource dataSource) throws AppFactoryException {
		try {
			return
				(EntryRepository) Class.forName(config.getEntryRepositoryImplClassName())
					.getDeclaredConstructor(DataSource.class)
					.newInstance(dataSource);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
			NoSuchMethodException | ClassNotFoundException e) {
			throw new AppFactoryException(e);
		}
	}

	@Override
	public EntryTransfer createEntryTransfer() throws AppFactoryException {
		return (EntryTransfer) getInstanceWoParamsByClassName(config.getEntryTransferImplClassName());
	}

	@Override
	public FileRepository createFileRepository() throws AppFactoryException {
		return (FileRepository) getInstanceWoParamsByClassName(config.getFileRepositoryImplClassName());
	}

	@Override
	public EntryConverterService createEntryConverterService() throws AppFactoryException {
		return (EntryConverterService) getInstanceWoParamsByClassName(config.getEntryConverterImplClassName());
	}

	@Override
	public Calculator createCalculator() throws AppFactoryException {
		return (Calculator) getInstanceWoParamsByClassName(config.getCalculatorImplClassName());
	}

	protected Object getInstanceWoParamsByClassName(String className) throws AppFactoryException {
		try {
			return Class.forName(className).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new AppFactoryException(e);
		}
	}
}