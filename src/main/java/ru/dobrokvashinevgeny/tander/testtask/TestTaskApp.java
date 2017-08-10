/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask;

import ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.*;
import ru.dobrokvashinevgeny.tander.testtask.service.*;

/**
 * Класс TestTaskApp
 *
 * @version 1.0
 */
public class TestTaskApp {
	private DataSource dataSource;
	private AppConfig appConfig;

	public TestTaskApp(AppConfig appConfig) {
		this.appConfig = appConfig;
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
		return new TestTaskService(appConfig.getN(), appConfig, dataSource);
	}
}