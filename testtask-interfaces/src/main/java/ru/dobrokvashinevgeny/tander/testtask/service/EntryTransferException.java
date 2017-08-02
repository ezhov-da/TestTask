package ru.dobrokvashinevgeny.tander.testtask.service;

/**
 * @author Evgeny Dobrokvashin
 *         Created by Stalker on 18.07.2017.
 * @version 1.0 2017
 */
public class EntryTransferException extends Exception {
	public EntryTransferException(String message) {
		super( message );
	}

	public EntryTransferException(String message, Throwable cause) {
		super( message, cause );
	}

	public EntryTransferException(Throwable cause) {
		super( cause );
	}
}