package ru.dobrokvashinevgeny.tander.testtask.service;

/**
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 23.07.2017.
 * @version 1.0 2017
 */
public class TestTaskServiceException extends Exception {
	public TestTaskServiceException(String message) {
		super(message);
	}

	public TestTaskServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public TestTaskServiceException(Throwable cause) {
		super(cause);
	}
}