package ru.dobrokvashinevgeny.tander.testtask.service;

/**
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 20.07.2017.
 * @version 1.0 2017
 */
public interface Calculator {
	long getSumOfEntriesDataFrom(FileStore fileStore, String srcFileName) throws CalculatorException;
}
