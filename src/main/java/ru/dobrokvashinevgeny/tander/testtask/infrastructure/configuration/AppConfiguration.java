/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.configuration;

import ru.dobrokvashinevgeny.tander.testtask.AppConfig;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 23.07.2017.
 */
public class AppConfiguration implements AppConfig {
	private static final String APP_CONFIG_FILE_NAME = "appConfig.properties";

	private long n;

	private String connectionUrl;

	private String userName;

	private String userPsw;

	private int batchSize;

	private String entryGeneratorImplClassName;

	private String entryRepositoryImplClassName;

	private String entryTransferImplClassName;

	private String entryConverterImplClassName;

	private String fileStoreImplClassName;

	private String calculatorImplClassName;

	@Override
	public void configure() throws AppConfigurationException {
		Properties appProperties = getProperties();
		try {
			n = Long.parseLong(appProperties.getProperty("entity.repository.url"));
		} catch (NumberFormatException e) {
			throw new AppConfigurationException(e);
		}

		connectionUrl = appProperties.getProperty("entity.repository.url");
		userName = appProperties.getProperty("entity.repository.userName");
		userPsw = appProperties.getProperty("entity.repository.userPsw");

		try {
			batchSize = Integer.parseInt(appProperties.getProperty("batchSize"));
		} catch (NumberFormatException e) {
			throw new AppConfigurationException(e);
		}

		entryGeneratorImplClassName = appProperties.getProperty("bean.entryGeneratorImpl.className");
		entryRepositoryImplClassName = appProperties.getProperty("bean.entryRepositoryImpl.className");
		entryTransferImplClassName = appProperties.getProperty("bean.entryTransferImpl.className");
		entryConverterImplClassName = appProperties.getProperty("bean.entryConverterImpl.className");
		fileStoreImplClassName = appProperties.getProperty("bean.fileStoreImpl.className");
		calculatorImplClassName = appProperties.getProperty("bean.calculatorImpl.className");
	}

	@Override
	public String getEntryGeneratorImplClassName() {
		return entryGeneratorImplClassName;
	}

	@Override
	public String getEntryRepositoryImplClassName() {
		return entryRepositoryImplClassName;
	}

	@Override
	public String getEntryTransferImplClassName() {
		return entryTransferImplClassName;
	}

	@Override
	public String getEntryConverterImplClassName() {
		return entryConverterImplClassName;
	}

	@Override
	public String getFileStoreImplClassName() {
		return fileStoreImplClassName;
	}

	@Override
	public String getCalculatorImplClassName() {
		return calculatorImplClassName;
	}

	@Override
	public int getBatchSize () {
		return batchSize;
	}

	private Properties getProperties() throws AppConfigurationException {
		Properties result = new Properties();

		try(Reader reader = Files.newBufferedReader(Paths.get(APP_CONFIG_FILE_NAME))) {
			result.load(reader);
		} catch (IOException e) {
			throw new AppConfigurationException(e);
		}

		return result;
	}

	/**
	 * Возвращает
	 *
	 * @return
	 */
	@Override
	public long getN() {
		return n;
	}

	/**
	 * Возвращает
	 *
	 * @return
	 */
	@Override
	public String getConnectionUrl() {
		return connectionUrl;
	}

	/**
	 * Возвращает
	 *
	 * @return
	 */
	@Override
	public String getUserName() {
		return userName;
	}

	/**
	 * Возвращает
	 *
	 * @return
	 */
	@Override
	public String getUserPsw() {
		return userPsw;
	}
}