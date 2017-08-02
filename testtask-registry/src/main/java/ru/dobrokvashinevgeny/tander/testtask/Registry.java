/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.generator.EntryGenerator;
import ru.dobrokvashinevgeny.tander.testtask.service.*;

import java.util.*;

/**
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 23.07.2017.
 */
public class Registry {
	private static final String APP_CONFIGURATION_IMPL_CLASS_NAME = "ru.dobrokvashinevgeny.tander.testtask.infrastructure.configuration.AppConfiguration";
	private static final Map<String, Object> singletonObjects = new HashMap<>();

	public static EntryGenerator entryGenerator() throws RegistryException {
		return (EntryGenerator) getSingletonInstanceByClassName(appConfiguration().getEntryGeneratorImplClassName());
	}

	private static Object getSingletonInstanceByClassName(String className) throws RegistryException {
		Object result;
		if (singletonObjects.containsKey(className)) {
			result = singletonObjects.get(className);
		} else {
			try {
				result = getInstanceByClassName(className);
				singletonObjects.put(className, result);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				throw new RegistryException("Not found implementation class: " + className + ".", e);
			}
		}
		return result;
	}

	private static Object getInstanceByClassName(String className)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return Class.forName(className).newInstance();
	}

	public static EntryRepository entryRepository() throws RegistryException {
		return (EntryRepository) getSingletonInstanceByClassName(appConfiguration().getEntryRepositoryImplClassName());
	}

	public static EntryTransfer entryTransfer() throws RegistryException {
		return (EntryTransfer) getSingletonInstanceByClassName(appConfiguration().getEntryTransferImplClassName());
	}

	public static EntryConverterService entryConverterService() throws RegistryException {
		return (EntryConverterService) getSingletonInstanceByClassName(appConfiguration().getEntryConverterImplClassName());
	}

	public static Calculator calculator() throws RegistryException {
		return (Calculator) getSingletonInstanceByClassName(appConfiguration().getCalculatorImplClassName());
	}

	public static FileStore fileStore() throws RegistryException {
		return (FileStore) getSingletonInstanceByClassName(appConfiguration().getFileStoreImplClassName());
	}

	public static AppConfig appConfiguration() throws RegistryException {
		return (AppConfig) getSingletonInstanceByClassName(APP_CONFIGURATION_IMPL_CLASS_NAME);
	}
}