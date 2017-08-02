package ru.dobrokvashinevgeny.tander.testtask.infrastructure.configuration;

/**
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 23.07.2017.
 * @version 1.0 2017
 */
public class AppConfigurationException extends Exception {
	public AppConfigurationException(String message) {
		super(message);
	}

	public AppConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AppConfigurationException(Throwable cause) {
		super(cause);
	}
}