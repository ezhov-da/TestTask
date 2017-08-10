/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask;

import ru.dobrokvashinevgeny.tander.testtask.infrastructure.configuration.*;

/**
 * Класс Applications - фабрика приложений
 *
 * @version 1.0
 */
public class Applications {
	/**
	 * Создает приложение TestTaskApp из конфигурационного файла
	 * @param cfgFilePath путь к конфигурационному файлу
	 * @return приложение TestTaskApp
	 * @throws AppConfigurationException если ошибки при конфигурировании приложения
	 */
	public static TestTaskApp fromConfig(String cfgFilePath) throws AppConfigurationException {
		AppConfig appConfig = new AppConfiguration(cfgFilePath);
		appConfig.configure();

		return new TestTaskApp(appConfig);
	}
}