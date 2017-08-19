/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.configuration;

import ru.dobrokvashinevgeny.tander.testtask.AppConfig;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * Класс AppConfiguration хранитель конфигурации приложения
 */
public class AppConfiguration implements AppConfig {
	public static final String XSLT_FILE_NAME = "1to2.xslt";
	public static final String IN_XML_FILE_NAME = "1.xml";
	public static final String OUT_XML_FILE_NAME = "2.xml";
	public static final String TMP_XML_FILE_NAME = "tmp.xml";

	private final String cfgFilePath;

	private long n;

	private String connectionUrl;

	private String userName;

	private String userPsw;

	private int converterBatchSize;

	private int transformerBatchSize;

	private int transferBatchSize;

	private String entryGeneratorImplClassName;

	private String entryRepositoryImplClassName;

	private String entryTransferImplClassName;

	private String entryConverterImplClassName;

	private String fileRepositoryImplClassName;

	private String calculatorImplClassName;

	private String dataSourceImplClassName;

	public AppConfiguration(String cfgFilePath) {
		this.cfgFilePath = cfgFilePath;
	}

	@Override
	public void configure() throws AppConfigurationException {
		Properties appProperties = getProperties();
		try {
			n = Long.parseLong(appProperties.getProperty("testTask.n"));
		} catch (NumberFormatException e) {
			throw new AppConfigurationException(e);
		}

		connectionUrl = appProperties.getProperty("entry.repository.url");
		userName = appProperties.getProperty("entry.repository.userName");
		userPsw = appProperties.getProperty("entry.repository.userPsw");
		transferBatchSize = getIntParamValue(appProperties, "transfer.batchSize");
		transformerBatchSize = getIntParamValue(appProperties, "transformer.batchSize");
		converterBatchSize = getIntParamValue(appProperties, "converter.batchSize");
		entryGeneratorImplClassName = appProperties.getProperty("bean.entryGeneratorImpl.className");
		entryRepositoryImplClassName = appProperties.getProperty("bean.entryRepositoryImpl.className");
		entryTransferImplClassName = appProperties.getProperty("bean.entryTransferImpl.className");
		entryConverterImplClassName = appProperties.getProperty("bean.entryConverterImpl.className");
		fileRepositoryImplClassName = appProperties.getProperty("bean.fileStoreImpl.className");
		calculatorImplClassName = appProperties.getProperty("bean.calculatorImpl.className");
		dataSourceImplClassName = appProperties.getProperty("bean.dataSourceImpl.className");
	}

	private int getIntParamValue(Properties appProperties, String paramName) throws AppConfigurationException {
		try {
			return Integer.parseInt(appProperties.getProperty(paramName));
		} catch (NumberFormatException e) {
			throw new AppConfigurationException(e);
		}
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
	public String getFileRepositoryImplClassName() {
		return fileRepositoryImplClassName;
	}

	@Override
	public String getCalculatorImplClassName() {
		return calculatorImplClassName;
	}

	@Override
	public int getTransferBatchSize() {
		return transferBatchSize;
	}

	@Override
	public int getConverterBatchSize() {
		return converterBatchSize;
	}

	@Override
	public int getTransformerBatchSize() {
		return transformerBatchSize;
	}

	private Properties getProperties() throws AppConfigurationException {
		Properties result = new Properties();

		try(Reader reader = Files.newBufferedReader(Paths.get(cfgFilePath))) {
			result.load(reader);
		} catch (IOException e) {
			throw new AppConfigurationException(e);
		}

		return result;
	}

	@Override
	public long getN() {
		return n;
	}

	@Override
	public String getConnectionUrl() {
		return connectionUrl;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public String getUserPsw() {
		return userPsw;
	}
}