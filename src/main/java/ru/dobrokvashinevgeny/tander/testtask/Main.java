/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask;

import ru.dobrokvashinevgeny.tander.testtask.infrastructure.configuration.AppConfigurationException;
import ru.dobrokvashinevgeny.tander.testtask.service.*;

import java.io.IOException;
import java.util.logging.*;

/**
 * Запускающий класс
 */
public class Main {
	private final static String LOG_CFG_FILE_NAME = "/logging.properties";
	private final static String APP_CONFIG_FILE_NAME = "app.properties";
	private final static Logger LOG = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {
		long beginTime = System.currentTimeMillis();
		configureLogger();

		try {
			TestTaskApp testTaskApp = Applications.fromConfig(APP_CONFIG_FILE_NAME).create();
			TestTaskService testTaskService = testTaskApp.createTestTaskService();

			long sumOfEntries = testTaskService.calculateSumOfEntriesData();

			System.out.println("Sum of Entries = " + sumOfEntries);
			LOG.log(Level.INFO, "Work time: " + (System.currentTimeMillis() - beginTime) + " ms.");
		} catch (AppConfigurationException | TestTaskServiceException e) {
			processGeneralException(e);
		}
	}

	private static void configureLogger() {
		try {
			LogManager.getLogManager().readConfiguration(TestTaskApp.class.getResourceAsStream(LOG_CFG_FILE_NAME));
		} catch (IOException e) {
			System.err.println("Log config file: " + LOG_CFG_FILE_NAME + " - not found!\n" + e);
		}
	}

	private static void processGeneralException(Exception e) {
		final String errMsg = "General app exception occurred.";
		System.err.println(errMsg + " See details in log.");
		LOG.log(Level.SEVERE, errMsg, e);
	}
}