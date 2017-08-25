/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import org.junit.Test;
import ru.dobrokvashinevgeny.tander.testtask.AppFactory;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.service.generator.EntryGenerator;

import java.sql.Connection;

import static org.mockito.Mockito.*;

/**
 * Класс MultiThreadEntryTransferTest
 */
public class MultiThreadEntryTransferTest {
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
		final TransferBatchWorker transferBatchWorker = mock(TransferBatchWorker.class);
		final Thread transferBatchThread = mock(Thread.class);

		MultiThreadEntryTransfer entryTransfer = spy(new MultiThreadEntryTransfer());
		when(entryTransfer.createThreadFor(any(TransferBatchWorker.class), anyLong())).thenReturn(transferBatchThread);
		when(entryTransfer.createTransferBatchWorker(
			anyLong(), anyLong(), any(AppFactory.class), any(DataSource.class))).thenReturn(transferBatchWorker);

		entryTransfer.transferFromGeneratorToRepository(
			appFactory, FROM_ENTRY, NUMBER_OF_ENTRIES_TO_TRANSFER, dataSource);

		for (long i = 0L; i < AVAILABLE_PROCESSORS; i++) {
			verify(entryTransfer).createThreadFor(any(TransferBatchWorker.class), eq(i));
			verify(entryTransfer).createTransferBatchWorker(i + 1L, 1L, appFactory, dataSource);
		}
		verify(transferBatchThread, times(AVAILABLE_PROCESSORS)).start();
	}

	@Test
	public void testTransferFromGeneratorToRepository8Cpu14EntriesOk() throws Exception {
		final long NUMBER_OF_ENTRIES_TO_TRANSFER = 14;
		EntryGenerator generator = mock(EntryGenerator.class);
		AppFactory appFactory = mock(AppFactory.class);
		when(appFactory.createEntryGenerator()).thenReturn(generator);
		Connection connection = mock(Connection.class);
		DataSource dataSource = mock(DataSource.class);
		when(dataSource.getConnection()).thenReturn(connection);
		final TransferBatchWorker transferBatchWorker = mock(TransferBatchWorker.class);
		final Thread transferBatchThread = mock(Thread.class);

		MultiThreadEntryTransfer entryTransfer = spy(new MultiThreadEntryTransfer());
		when(entryTransfer.createThreadFor(any(TransferBatchWorker.class), anyLong())).thenReturn(transferBatchThread);
		when(entryTransfer.createTransferBatchWorker(
			anyLong(), anyLong(), any(AppFactory.class), any(DataSource.class))).thenReturn(transferBatchWorker);

		entryTransfer.transferFromGeneratorToRepository(
			appFactory, FROM_ENTRY, NUMBER_OF_ENTRIES_TO_TRANSFER, dataSource);

		for (long i = 0L; i < AVAILABLE_PROCESSORS; i++) {
			verify(entryTransfer).createThreadFor(any(TransferBatchWorker.class), eq(i));
		}
		verify(entryTransfer).createTransferBatchWorker(1L, 2L, appFactory, dataSource);
		verify(entryTransfer).createTransferBatchWorker(3L, 2L, appFactory, dataSource);
		verify(entryTransfer).createTransferBatchWorker(5L, 2L, appFactory, dataSource);
		verify(entryTransfer).createTransferBatchWorker(7L, 2L, appFactory, dataSource);
		verify(entryTransfer).createTransferBatchWorker(9L, 2L, appFactory, dataSource);
		verify(entryTransfer).createTransferBatchWorker(11L, 2L, appFactory, dataSource);
		verify(entryTransfer).createTransferBatchWorker(13L, 1L, appFactory, dataSource);
		verify(entryTransfer).createTransferBatchWorker(14L, 1L, appFactory, dataSource);
		verify(transferBatchThread, times(AVAILABLE_PROCESSORS)).start();
	}

	@Test(expected = EntryTransferException.class)
	public void testTransferFromGeneratorToRepositoryFail() throws Exception {
		final long NUMBER_OF_ENTRIES_TO_TRANSFER = 8;
		EntryGenerator generator = mock(EntryGenerator.class);
		AppFactory appFactory = mock(AppFactory.class);
		when(appFactory.createEntryGenerator()).thenReturn(generator);
		DataSource dataSource = mock(DataSource.class);
		final TransferBatchWorker transferBatchWorker = mock(TransferBatchWorker.class);
		when(transferBatchWorker.getException()).thenReturn(new EntryTransferException(""));
		final Thread transferBatchThread = mock(Thread.class);

		MultiThreadEntryTransfer entryTransfer = spy(new MultiThreadEntryTransfer());
		when(entryTransfer.createThreadFor(any(TransferBatchWorker.class), anyLong())).thenReturn(transferBatchThread);
		when(entryTransfer.createTransferBatchWorker(
			anyLong(), anyLong(), any(AppFactory.class), any(DataSource.class))).thenReturn(transferBatchWorker);

		entryTransfer.transferFromGeneratorToRepository(
			appFactory, FROM_ENTRY, NUMBER_OF_ENTRIES_TO_TRANSFER, dataSource);
	}
}