package ru.dobrokvashinevgeny.tander.testtask;

/**
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 23.07.2017.
 * @version 1.0 2017
 */
public class RegistryException extends Exception {
	public RegistryException(String message) {
		super(message);
	}

	public RegistryException(String message, Throwable cause) {
		super(message, cause);
	}

	public RegistryException(Throwable cause) {
		super(cause);
	}
}