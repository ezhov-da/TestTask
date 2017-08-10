/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;

/**
 * Реализация файлового хранилища в фаловой системе ОС
 */
public class FileSystemFileRepository implements FileRepository {
	private static final String FILE_CHARSET = "UTF-8";

	@Override
	public BufferedReader getFileDataReaderByName(String fileName) throws FileRepositoryException {
		try {
			return Files.newBufferedReader(Paths.get(fileName), Charset.forName(FILE_CHARSET));
		} catch (IOException e) {
			throw new FileRepositoryException(e);
		}
	}

	@Override
	public BufferedWriter getFileDataWriterByName(String fileName) throws FileRepositoryException {
		try {
			return Files.newBufferedWriter(Paths.get(fileName), Charset.forName(FILE_CHARSET));
		} catch (IOException e) {
			throw new FileRepositoryException(e);
		}
	}
}