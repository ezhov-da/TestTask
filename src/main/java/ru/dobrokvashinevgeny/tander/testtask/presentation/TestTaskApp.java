/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.presentation;

import ru.dobrokvashinevgeny.tander.testtask.*;
import ru.dobrokvashinevgeny.tander.testtask.infrastructure.configuration.AppConfigurationException;

import java.io.IOException;
import java.util.logging.*;

/**
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 18.07.2017.
 */
public class TestTaskApp {
	private final static String LOG_CFG_FILE_NAME = "/logging.properties";
	private final static Logger LOG = Logger.getLogger(TestTaskApp.class.getName());

	public static void main(String[] args) {
		configureLogger();

		if (configureApp()) {
			runApp();
		}
	}

	private static void configureLogger() {
		try {
			LogManager.getLogManager().readConfiguration(TestTaskApp.class.getResourceAsStream(LOG_CFG_FILE_NAME));
		} catch (IOException e) {
			System.err.println("Log config file: " + LOG_CFG_FILE_NAME + " - not found!\n" + e);
		}
	}

	private static boolean configureApp() {
		boolean result = true;

		try {
			Registry.appConfiguration()
					.configure();
		} catch (RegistryException e) {
			processException(e);
			result = false;
		} catch (AppConfigurationException e) {
			processGeneralException(e);
			result = false;
		}

		return result;
	}

	private static void runApp() {
		try {
//			if (Math.sin(0) == 0) throw new Exception();
			try {
				final AppConfig appConfig = Registry.appConfiguration();
				new TestTask(
					Registry.entryGenerator(),
					Registry.entryRepository(),
					Registry.entryTransfer(),
					Registry.entryConverterService(),
					Registry.calculator(),
					appConfig.getN(),
					appConfig.getBatchSize()
				).run();
			} catch (RegistryException e) {
				processException(e);
			}
		} catch (Exception e) {
			processGeneralException(e);
		}
	}

	private static void processException(RegistryException e) {
		System.err.println("Can't find implementation class(es). See details in log.");
		LOG.log(Level.SEVERE, "Can't find implementation class(es).", e);
	}

	private static void processGeneralException(Exception e) {
		final String errMsg = "General app exception occurred.";
		System.err.println(errMsg + " See details in log.");
		LOG.log(Level.SEVERE, errMsg, e);
	}
}