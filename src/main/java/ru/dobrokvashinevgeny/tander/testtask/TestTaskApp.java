/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.infrastructure.AppFactoryImpl;
import ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.DbDataSource;
import ru.dobrokvashinevgeny.tander.testtask.service.TestTaskService;

import java.util.logging.*;

/**
 * Приложение TestTaskApp
 */
public class TestTaskApp {
	private final static Logger LOG = Logger.getLogger(Main.class.getName());
	private DataSource dataSource;
	private AppConfig appConfig;

	public TestTaskApp(AppConfig appConfig) {
		this.appConfig = appConfig;
		System.out.println("n = " + appConfig.getN());
		LOG.log(Level.INFO, "n = " + appConfig.getN() +
			", transfer.batchSize = " + appConfig.getTransferBatchSize() +
			", converter.batchSize = " + appConfig.getConverterBatchSize() +
			", transformer.batchSize = " + appConfig.getTransformerBatchSize());
	}

	/**
	 * Инициализирует приложение TestTaskApp
	 * @return инициализированное приложение TestTaskApp
	 */
	TestTaskApp create() {
		dataSource = new DbDataSource(appConfig.getConnectionUrl(), appConfig.getUserName(), appConfig.getUserPsw());
		return this;
	}

	/**
	 * Создает TestTaskService - сервис приложения TestTaskApp
	 * @return TestTaskService - сервис приложения TestTaskApp
	 */
	public TestTaskService createTestTaskService() {
		return new TestTaskService(appConfig.getN(), new AppFactoryImpl(appConfig), appConfig, dataSource);
	}
}