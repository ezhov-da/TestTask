package ru.dobrokvashinevgeny.tander.testtask.domain.model.entry;

/**
 * @author Evgeny Dobrokvashin
 *         Created by Stalker on 16.07.2017.
 * @version 1.0 2017
 */
public class EntryRepositoryException extends Exception {
	public EntryRepositoryException(String message) {
		super( message );
	}

	public EntryRepositoryException(String message, Throwable cause) {
		super( message, cause );
	}

	public EntryRepositoryException(Throwable cause) {
		super( cause );
	}
}