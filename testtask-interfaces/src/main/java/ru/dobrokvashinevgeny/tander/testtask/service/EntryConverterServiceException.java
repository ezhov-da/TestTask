package ru.dobrokvashinevgeny.tander.testtask.service;

/**
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 19.07.2017.
 * @version 1.0 2017
 */
public class EntryConverterServiceException extends Exception {
	public EntryConverterServiceException(String message) {
		super( message );
	}

	public EntryConverterServiceException(String message, Throwable cause) {
		super( message, cause );
	}

	public EntryConverterServiceException(Throwable cause) {
		super( cause );
	}
}