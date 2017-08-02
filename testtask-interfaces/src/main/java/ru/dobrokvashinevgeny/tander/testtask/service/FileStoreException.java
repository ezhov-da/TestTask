package ru.dobrokvashinevgeny.tander.testtask.service;

/**
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 24.07.2017.
 * @version 1.0 2017
 */
public class FileStoreException extends Exception {
	public FileStoreException(String message) {
		super(message);
	}

	public FileStoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileStoreException(Throwable cause) {
		super(cause);
	}
}