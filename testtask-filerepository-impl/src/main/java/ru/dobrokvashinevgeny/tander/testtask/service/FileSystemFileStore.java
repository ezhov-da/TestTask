/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;

/**
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 26.07.2017.
 */
public class FileSystemFileStore implements FileStore {
	@Override
	public BufferedReader getFileDataReaderByName(String fileName) throws FileStoreException {
		try {
			return Files.newBufferedReader(Paths.get(fileName), Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw new FileStoreException(e);
		}
	}

	@Override
	public BufferedWriter getFileDataWriterByName(String fileName) throws FileStoreException {
		try {
			return Files.newBufferedWriter(Paths.get(fileName), Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw new FileStoreException(e);
		}
	}
}