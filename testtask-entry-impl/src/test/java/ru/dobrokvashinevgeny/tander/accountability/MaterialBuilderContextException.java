package ru.dobrokvashinevgeny.tander.accountability;

/**
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 26.07.2017.
 * @version 1.0 2017
 */
public class MaterialBuilderContextException extends Exception {
	public MaterialBuilderContextException(String message) {
		super(message);
	}

	public MaterialBuilderContextException(String message, Throwable cause) {
		super(message, cause);
	}

	public MaterialBuilderContextException(Throwable cause) {
		super(cause);
	}
}