/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 26.07.2017.
 */
public class FileSystemFileStoreTest {
	private static final String READ_FILE_NAME = "test_read1.txt";
	private static final String WRITE_FILE_NAME = "test_write1.txt";
	private static final String FAIL_WRITE_FILE_NAME = "z:\\test_write1.txt";
	private static final String TEST_DATA = "Test Тест";

	@Test
	public void testGetFileDataReaderByNameOk() throws Exception {
		FileStore fileStore = new FileSystemFileStore();

		createFileInFileSystemWithData(READ_FILE_NAME, TEST_DATA);

		try(BufferedReader reader = fileStore.getFileDataReaderByName(READ_FILE_NAME)) {
			assertThat(reader.readLine(), equalTo(TEST_DATA));
		} finally {
			Files.deleteIfExists(Paths.get(READ_FILE_NAME));
		}
	}

	private void createFileInFileSystemWithData(String fileName, String data) throws IOException {
		try(BufferedWriter fileWriter = Files.newBufferedWriter(Paths.get(fileName), Charset.forName("UTF-8"))) {
			fileWriter.write(data);
			fileWriter.flush();
		}
	}

	@Test(expected = FileStoreException.class)
	public void testGetFileDataReaderByNameFail() throws Exception {
		FileStore fileStore = new FileSystemFileStore();

		try(BufferedReader reader = fileStore.getFileDataReaderByName(READ_FILE_NAME)) {
			assertThat(reader.readLine(), equalTo(TEST_DATA));
		}
	}

	@Test
	public void testGetFileDataWriterByNameOk() throws Exception {
		FileStore fileStore = new FileSystemFileStore();

		try(BufferedWriter writer = fileStore.getFileDataWriterByName(WRITE_FILE_NAME)) {
			writer.write(TEST_DATA);
		}

		try(BufferedReader reader = Files.newBufferedReader(Paths.get(WRITE_FILE_NAME), Charset.forName("UTF-8"))) {
			assertThat(reader.readLine(), equalTo(TEST_DATA));
		}
	}

	@Test(expected = FileStoreException.class)
	public void testGetFileDataWriterByNameFail() throws Exception {
		FileStore fileStore = new FileSystemFileStore();

		try(BufferedWriter writer = fileStore.getFileDataWriterByName(FAIL_WRITE_FILE_NAME)) {
			writer.write(TEST_DATA);
		}
	}
}