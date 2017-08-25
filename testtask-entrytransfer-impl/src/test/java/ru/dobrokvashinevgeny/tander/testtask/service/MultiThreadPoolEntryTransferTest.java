/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import org.junit.Test;
import ru.dobrokvashinevgeny.tander.testtask.AppFactory;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.service.generator.EntryGenerator;

import java.sql.Connection;
import java.util.concurrent.*;

import static org.mockito.Mockito.*;

/**
 * Класс MultiThreadPoolEntryTransferTest
 */
public class MultiThreadPoolEntryTransferTest {
	private static final long FROM_ENTRY = 1L;
	private static final int AVAILABLE_PROCESSORS = 8;

	@Test
	public void testTransferFromGeneratorToRepository8Cpu8EntriesOk() throws Exception {
		final long NUMBER_OF_ENTRIES_TO_TRANSFER = 8;
		EntryGenerator generator = mock(EntryGenerator.class);
		AppFactory appFactory = mock(AppFactory.class);
		when(appFactory.createEntryGenerator()).thenReturn(generator);
		Connection connection = mock(Connection.class);
		DataSource dataSource = mock(DataSource.class);
		when(dataSource.getConnection()).thenReturn(connection);
		final TransferBatchCallWorker worker = mock(TransferBatchCallWorker.class);
		final ExecutorService executorService = mock(ExecutorService.class);
		final CountDownLatch finishLatch = mock(CountDownLatch.class);

		MultiThreadPoolEntryTransfer entryTransfer = spy(new MultiThreadPoolEntryTransfer());
		when(entryTransfer.createExecutorService(
			any(MultiThreadPoolEntryTransfer.TransferWorkerUncaughtExceptionHandler.class))).thenReturn(executorService);
		when(entryTransfer.createFinishLatch()).thenReturn(finishLatch);
		when(entryTransfer.createTransferBatchCallWorker(anyLong(), anyLong(), any(AppFactory.class),
			any(DataSource.class), any(CountDownLatch.class))).thenReturn(worker);

		entryTransfer.transferFromGeneratorToRepository(
			appFactory, FROM_ENTRY, NUMBER_OF_ENTRIES_TO_TRANSFER, dataSource);

		verify(entryTransfer).createFinishLatch();
		verify(entryTransfer).createExecutorService(
			any(MultiThreadPoolEntryTransfer.TransferWorkerUncaughtExceptionHandler.class));
		for (long i = 1L; i <= AVAILABLE_PROCESSORS; i++) {
			verify(entryTransfer).createTransferBatchCallWorker(i, 1L, appFactory, dataSource, finishLatch);
		}
		verify(executorService, times(AVAILABLE_PROCESSORS)).submit(worker);
		verify(executorService).shutdown();
		verify(executorService).awaitTermination(anyLong(), any(TimeUnit.class));
		verifyNoMoreInteractions(executorService);
	}
}