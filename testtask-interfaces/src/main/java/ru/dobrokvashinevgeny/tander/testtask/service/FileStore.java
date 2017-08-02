package ru.dobrokvashinevgeny.tander.testtask.service;

import java.io.*;

/**
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 24.07.2017.
 * @version 1.0 2017
 */
public interface FileStore {
	BufferedReader getFileDataReaderByName(String fileName) throws FileStoreException;

	BufferedWriter getFileDataWriterByName(String fileName) throws FileStoreException;
}
