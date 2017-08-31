/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import org.junit.Test;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;

import java.io.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Класс JaxbMultiThreadEntryConverterByBatchTest
 */
public class JaxbMultiThreadEntryConverterByBatchTest {
	private static final int AVAILABLE_PROCESSORS = 8;
	private static final String DEST_XML_FILE_NAME = "1.xml";

	@Test
	public void testConvertEntriesToXml8Cpu8EntriesOk() throws Exception {
		EntryRepository entryRepository = mock(EntryRepository.class);
		final long NUMBER_OF_ENTRIES_TO_CONVERT = 8L;
		when(entryRepository.size()).thenReturn(NUMBER_OF_ENTRIES_TO_CONVERT);
		FileRepository fileRepository = mock(FileRepository.class);
		final BufferedWriter bufferedWriter = mock(BufferedWriter.class);
		when(fileRepository.getFileDataWriterByName(anyString())).thenReturn(bufferedWriter);
		final BufferedReader bufferedReader = mock(BufferedReader.class);
		when(bufferedReader.read(any(char[].class))).thenReturn(
			1, -1, 1, -1, 1, -1, 1, -1,
			1, -1, 1, -1, 1, -1, 1, -1);
		when(fileRepository.getFileDataReaderByName(anyString())).thenReturn(bufferedReader);
		final BatchConvertWorker batchConvertWorker = mock(BatchConvertWorker.class);
		final Thread batchConvertThread = mock(Thread.class);

		JaxbMultiThreadEntryConverterService.JaxbMultiThreadEntryConverterByBatch converter =
			spy(new JaxbMultiThreadEntryConverterService.JaxbMultiThreadEntryConverterByBatch(
				DEST_XML_FILE_NAME, 1, entryRepository, fileRepository));
		when(converter.createThreadFor(any(BatchConvertWorker.class), anyInt())).thenReturn(batchConvertThread);
		when(converter.createBatchConvertWorker(anyLong(), anyLong(), anyString(), anyInt(),
			any(EntryRepository.class), any(FileRepository.class))).thenReturn(batchConvertWorker);

		converter.execute();

		for (int i = 0; i < AVAILABLE_PROCESSORS; i++) {
			verify(converter).createThreadFor(any(BatchConvertWorker.class), eq(i));
			verify(converter).createBatchConvertWorker(eq(i + 1L), eq(1L), anyString(),
				eq(1), eq(entryRepository), eq(fileRepository));
		}

		verify(batchConvertThread, times(AVAILABLE_PROCESSORS)).start();

		verify(fileRepository).getFileDataWriterByName(DEST_XML_FILE_NAME);
		verify(fileRepository, times(AVAILABLE_PROCESSORS)).getFileDataReaderByName(anyString());
		verify(bufferedReader, times(AVAILABLE_PROCESSORS*2)).read(any(char[].class));
		verify(bufferedWriter, times(AVAILABLE_PROCESSORS)).write(any(char[].class), anyInt(), anyInt());
		verify(fileRepository, times(AVAILABLE_PROCESSORS)).deleteFile(anyString());
	}

	@Test
	public void testConvertEntriesToXml8Cpu10EntriesOk() throws Exception {
		EntryRepository entryRepository = mock(EntryRepository.class);
		final long NUMBER_OF_ENTRIES_TO_CONVERT = 10L;
		when(entryRepository.size()).thenReturn(NUMBER_OF_ENTRIES_TO_CONVERT);
		FileRepository fileRepository = mock(FileRepository.class);
		final BufferedWriter bufferedWriter = mock(BufferedWriter.class);
		when(fileRepository.getFileDataWriterByName(anyString())).thenReturn(bufferedWriter);
		final BufferedReader bufferedReader = mock(BufferedReader.class);
		when(bufferedReader.read(any(char[].class))).thenReturn(
			1, -1, 1, -1, 1, -1, 1, -1,
			1, -1, 1, -1, 1, -1, 1, -1);
		when(fileRepository.getFileDataReaderByName(anyString())).thenReturn(bufferedReader);
		final BatchConvertWorker batchConvertWorker = mock(BatchConvertWorker.class);
		final Thread batchConvertThread = mock(Thread.class);

		JaxbMultiThreadEntryConverterService.JaxbMultiThreadEntryConverterByBatch converter =
			spy(new JaxbMultiThreadEntryConverterService.JaxbMultiThreadEntryConverterByBatch(
				DEST_XML_FILE_NAME, 1, entryRepository, fileRepository));
		when(converter.createThreadFor(any(BatchConvertWorker.class), anyInt())).thenReturn(batchConvertThread);
		when(converter.createBatchConvertWorker(anyLong(), anyLong(), anyString(), anyInt(),
			any(EntryRepository.class), any(FileRepository.class))).thenReturn(batchConvertWorker);

		converter.execute();

		for (int i = 0; i < AVAILABLE_PROCESSORS; i++) {
			verify(converter).createThreadFor(any(BatchConvertWorker.class), eq(i));
		}
		verify(converter).createBatchConvertWorker(eq(1L), eq(2L), anyString(),
			eq(1), eq(entryRepository), eq(fileRepository));
		verify(converter).createBatchConvertWorker(eq(3L), eq(2L), anyString(),
			eq(1), eq(entryRepository), eq(fileRepository));
		verify(converter).createBatchConvertWorker(eq(5L), eq(1L), anyString(),
			eq(1), eq(entryRepository), eq(fileRepository));
		verify(converter).createBatchConvertWorker(eq(6L), eq(1L), anyString(),
			eq(1), eq(entryRepository), eq(fileRepository));
		verify(converter).createBatchConvertWorker(eq(7L), eq(1L), anyString(),
			eq(1), eq(entryRepository), eq(fileRepository));
		verify(converter).createBatchConvertWorker(eq(8L), eq(1L), anyString(),
			eq(1), eq(entryRepository), eq(fileRepository));
		verify(converter).createBatchConvertWorker(eq(9L), eq(1L), anyString(),
			eq(1), eq(entryRepository), eq(fileRepository));
		verify(converter).createBatchConvertWorker(eq(10L), eq(1L), anyString(),
			eq(1), eq(entryRepository), eq(fileRepository));

		verify(batchConvertThread, times(AVAILABLE_PROCESSORS)).start();

		verify(fileRepository).getFileDataWriterByName(DEST_XML_FILE_NAME);
		verify(fileRepository, times(AVAILABLE_PROCESSORS)).getFileDataReaderByName(anyString());
		verify(bufferedReader, times(AVAILABLE_PROCESSORS*2)).read(any(char[].class));
		verify(bufferedWriter, times(AVAILABLE_PROCESSORS)).write(any(char[].class), anyInt(), anyInt());
		verify(fileRepository, times(AVAILABLE_PROCESSORS)).deleteFile(anyString());
	}

	@Test(expected = EntryConverterServiceException.class)
	public void testConvertEntriesToXml8CpuFail() throws Exception {
		EntryRepository entryRepository = mock(EntryRepository.class);
		final long NUMBER_OF_ENTRIES_TO_CONVERT = 8L;
		when(entryRepository.size()).thenReturn(NUMBER_OF_ENTRIES_TO_CONVERT);
		FileRepository fileRepository = mock(FileRepository.class);
		final BufferedWriter bufferedWriter = mock(BufferedWriter.class);
		when(fileRepository.getFileDataWriterByName(anyString())).thenReturn(bufferedWriter);
		final BufferedReader bufferedReader = mock(BufferedReader.class);
		when(bufferedReader.read(any(char[].class))).thenReturn(
			1, -1, 1, -1, 1, -1, 1, -1,
			1, -1, 1, -1, 1, -1, 1, -1);
		when(fileRepository.getFileDataReaderByName(anyString())).thenReturn(bufferedReader);
		final BatchConvertWorker batchConvertWorker = mock(BatchConvertWorker.class);
		when(batchConvertWorker.getException()).thenReturn(new EntryConverterServiceException(""));
		final Thread batchConvertThread = mock(Thread.class);

		JaxbMultiThreadEntryConverterService.JaxbMultiThreadEntryConverterByBatch converter =
			spy(new JaxbMultiThreadEntryConverterService.JaxbMultiThreadEntryConverterByBatch(
				DEST_XML_FILE_NAME, 1, entryRepository, fileRepository));
		when(converter.createThreadFor(any(BatchConvertWorker.class), anyInt())).thenReturn(batchConvertThread);
		when(converter.createBatchConvertWorker(anyLong(), anyLong(), anyString(), anyInt(),
			any(EntryRepository.class), any(FileRepository.class))).thenReturn(batchConvertWorker);

		converter.execute();
	}
}