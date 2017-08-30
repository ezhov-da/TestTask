/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;

import java.io.*;
import java.util.concurrent.ForkJoinPool;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Класс JaxbMultiThreadForkJoinEntryConverterByBatchTest
 */
public class JaxbMultiThreadForkJoinEntryConverterByBatchTest {
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
		final ForkJoinPool forkJoinPool = mock(ForkJoinPool.class);
		JaxbMultiThreadForkJoinEntryConverterService.BatchConvertRecursiveAction batchConvertRecursiveAction =
			mock(JaxbMultiThreadForkJoinEntryConverterService.BatchConvertRecursiveAction.class);

		JaxbMultiThreadForkJoinEntryConverterService.JaxbMultiThreadForkJoinEntryConverterByBatch converter =
			spy(new JaxbMultiThreadForkJoinEntryConverterService.JaxbMultiThreadForkJoinEntryConverterByBatch(
				DEST_XML_FILE_NAME, 1, entryRepository, fileRepository
			));
		when(converter.createForkJoinPool(
			any(JaxbMultiThreadForkJoinEntryConverterService.ConverterWorkerUncaughtExceptionHandler.class)))
			.thenReturn(forkJoinPool);
		when(converter.createBatchRecursiveRecursiveAction(anyLong(), anyLong(), ArgumentMatchers.any(), anyInt(),
			any(EntryRepository.class), any(FileRepository.class), anyLong())).thenReturn(batchConvertRecursiveAction);

		converter.execute();

		verify(converter).createForkJoinPool(
			any(JaxbMultiThreadForkJoinEntryConverterService.ConverterWorkerUncaughtExceptionHandler.class));
		verify(forkJoinPool).invoke(batchConvertRecursiveAction);
		verify(fileRepository).getFileDataWriterByName(DEST_XML_FILE_NAME);
		verifyNoMoreInteractions(forkJoinPool);
	}
}