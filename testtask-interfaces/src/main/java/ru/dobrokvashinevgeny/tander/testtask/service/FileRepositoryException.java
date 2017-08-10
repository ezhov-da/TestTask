package ru.dobrokvashinevgeny.tander.testtask.service;

/**
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 24.07.2017.
 * @version 1.0 2017
 */
public class FileRepositoryException extends Exception {
	public FileRepositoryException(String message) {
		super(message);
	}

	public FileRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileRepositoryException(Throwable cause) {
		super(cause);
	}
}