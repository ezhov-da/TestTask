/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import org.junit.Test;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;

import java.io.*;
import java.util.concurrent.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Класс JaxbMultiThreadPoolEntryConverterByBatchTest
 */
public class JaxbMultiThreadPoolEntryConverterByBatchTest {
	private static final int AVAILABLE_PROCESSORS = 8;
	private static final String DEST_XML_FILE_NAME = "1.xml";

	@Test
	public void testConvertEntriesToXml8Cpu8EntriesOk() throws Exception {
		final long NUMBER_OF_ENTRIES_TO_CONVERT = 8L;
		EntryRepository entryRepository = mock(EntryRepository.class);
		when(entryRepository.size()).thenReturn(NUMBER_OF_ENTRIES_TO_CONVERT);
		FileRepository fileRepository = mock(FileRepository.class);
		final BufferedWriter bufferedWriter = mock(BufferedWriter.class);
		when(fileRepository.getFileDataWriterByName(anyString())).thenReturn(bufferedWriter);
		final BufferedReader bufferedReader = mock(BufferedReader.class);
		when(bufferedReader.read(any(char[].class))).thenReturn(
			1, -1, 1, -1, 1, -1, 1, -1,
			1, -1, 1, -1, 1, -1, 1, -1);
		when(fileRepository.getFileDataReaderByName(anyString())).thenReturn(bufferedReader);
		final BatchConvertCallWorker worker = mock(BatchConvertCallWorker.class);
		final ExecutorService executorService = mock(ExecutorService.class);
		final CountDownLatch finishLatch = mock(CountDownLatch.class);

		JaxbMultiThreadPoolEntryConverterService.JaxbMultiThreadPoolEntryConverterByBatch converter =
			spy(new JaxbMultiThreadPoolEntryConverterService.JaxbMultiThreadPoolEntryConverterByBatch(
				DEST_XML_FILE_NAME, 1, entryRepository, fileRepository));
		when(converter.createExecutorService(
			any(JaxbMultiThreadPoolEntryConverterService.ConverterWorkerUncaughtExceptionHandler.class)))
			.thenReturn(executorService);
		when(converter.createFinishLatch()).thenReturn(finishLatch);
		when(converter.createBatchConvertCallWorker(anyLong(), anyLong(), anyString(), anyInt(),
			any(EntryRepository.class), any(FileRepository.class), any(CountDownLatch.class))).thenReturn(worker);

		converter.execute();

		verify(converter).createFinishLatch();
		verify(converter).createExecutorService(
			any(JaxbMultiThreadPoolEntryConverterService.ConverterWorkerUncaughtExceptionHandler.class));
		for (int i = 0; i < AVAILABLE_PROCESSORS; i++) {
			verify(converter).createBatchConvertCallWorker(eq(i + 1L), eq(1L), anyString(),
				eq(1), eq(entryRepository), eq(fileRepository), eq(finishLatch));
		}

		verify(executorService, times(AVAILABLE_PROCESSORS)).submit(worker);
		verify(executorService).shutdown();
		verify(executorService).awaitTermination(anyLong(), any(TimeUnit.class));
		verify(fileRepository).getFileDataWriterByName(DEST_XML_FILE_NAME);
		verify(fileRepository, times(AVAILABLE_PROCESSORS)).getFileDataReaderByName(anyString());
		verify(bufferedReader, times(AVAILABLE_PROCESSORS*2)).read(any(char[].class));
		verify(bufferedWriter, times(AVAILABLE_PROCESSORS)).write(any(char[].class), anyInt(), anyInt());
		verify(fileRepository, times(AVAILABLE_PROCESSORS)).deleteFile(anyString());
		verifyNoMoreInteractions(executorService);
	}
}