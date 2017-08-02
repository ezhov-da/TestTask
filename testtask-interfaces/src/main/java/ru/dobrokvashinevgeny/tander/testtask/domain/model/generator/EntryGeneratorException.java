package ru.dobrokvashinevgeny.tander.testtask.domain.model.generator;

/**
 * @author Evgeny Dobrokvashin
 *         Created by Stalker on 16.07.2017.
 * @version 1.0 2017
 */
public class EntryGeneratorException extends Exception {
	public EntryGeneratorException(String message) {
		super( message );
	}

	public EntryGeneratorException(String message, Throwable cause) {
		super( message, cause );
	}

	public EntryGeneratorException(Throwable cause) {
		super( cause );
	}
}