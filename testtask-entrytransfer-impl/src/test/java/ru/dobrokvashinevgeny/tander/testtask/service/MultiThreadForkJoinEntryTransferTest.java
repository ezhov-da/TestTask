/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import org.junit.Test;
import ru.dobrokvashinevgeny.tander.testtask.AppFactory;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.service.generator.EntryGenerator;

import java.sql.Connection;
import java.util.concurrent.ForkJoinPool;

import static org.mockito.Mockito.*;

/**
 * Класс MultiThreadForkJoinEntryTransferTest
 */
public class MultiThreadForkJoinEntryTransferTest {
	private static final long FROM_ENTRY = 1L;

	@Test
	public void testTransferFromGeneratorToRepository8Cpu8EntriesOk() throws Exception {
		final long NUMBER_OF_ENTRIES_TO_TRANSFER = 8;
		EntryGenerator generator = mock(EntryGenerator.class);
		AppFactory appFactory = mock(AppFactory.class);
		when(appFactory.createEntryGenerator()).thenReturn(generator);
		Connection connection = mock(Connection.class);
		DataSource dataSource = mock(DataSource.class);
		when(dataSource.getConnection()).thenReturn(connection);
		final ForkJoinPool forkJoinPool = mock(ForkJoinPool.class);
		MultiThreadForkJoinEntryTransfer.TransferBatchRecursiveAction transferBatchRecursiveAction =
			mock(MultiThreadForkJoinEntryTransfer.TransferBatchRecursiveAction.class);

		MultiThreadForkJoinEntryTransfer entryTransfer = spy(new MultiThreadForkJoinEntryTransfer());
		when(entryTransfer.createForkJoinPool(
			any(MultiThreadForkJoinEntryTransfer.TransferWorkerUncaughtExceptionHandler.class)))
			.thenReturn(forkJoinPool);
		when(entryTransfer.createTransferBatchRecursiveAction(appFactory, FROM_ENTRY, NUMBER_OF_ENTRIES_TO_TRANSFER,
			dataSource, 1)).thenReturn(transferBatchRecursiveAction);

		entryTransfer.transferFromGeneratorToRepository(
			appFactory, FROM_ENTRY, NUMBER_OF_ENTRIES_TO_TRANSFER, dataSource);

		verify(entryTransfer).createForkJoinPool(
			any(MultiThreadForkJoinEntryTransfer.TransferWorkerUncaughtExceptionHandler.class));
		verify(forkJoinPool).invoke(transferBatchRecursiveAction);
		verifyNoMoreInteractions(forkJoinPool);
	}
}