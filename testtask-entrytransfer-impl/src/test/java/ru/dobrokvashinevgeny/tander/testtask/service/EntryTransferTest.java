/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import org.junit.*;
import org.mockito.InOrder;
import ru.dobrokvashinevgeny.tander.testtask.AppFactory;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.Entry;
import ru.dobrokvashinevgeny.tander.testtask.service.generator.*;

import java.sql.*;

import static org.mockito.Mockito.*;

/**
 */
public class EntryTransferTest {
	private static final long FROM_ENTRY = 1L;
	private static final int NUMBER_OF_ENTRIES_TO_TRANSFER = 5;
	private static final int ENTRIES_BATCH_SIZE = 2;

	private PreparedStatement preparedStatement;
	private DataSource dataSource;
	private EntryTransfer entryTransfer;
	private EntryGenerator generator;
	private AppFactory appFactory;

	@Before
	public void setUp() throws Exception {
		preparedStatement = mock(PreparedStatement.class);
		Connection connection = mock(Connection.class);
		when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
		dataSource = mock(DataSource.class);
		when(dataSource.getConnection()).thenReturn(connection);
		entryTransfer = new SingleThreadEntryTransfer();
		generator = mock(EntryGenerator.class);
		appFactory = mock(AppFactory.class);
		when(appFactory.createEntryGenerator()).thenReturn(generator);
	}

	@Test
	public void testTransferFromGeneratorToRepositoryOk() throws Exception {
		Entry entry = mock(Entry.class);
		when(generator.getNewEntry()).thenReturn(entry, entry, entry, entry, entry);

		entryTransfer.transferFromGeneratorToRepository(
			appFactory, FROM_ENTRY, NUMBER_OF_ENTRIES_TO_TRANSFER, dataSource);

		InOrder inOrder = inOrder(generator, preparedStatement);
		inOrder.verify(generator).setCurrentValue(FROM_ENTRY);
		inOrder.verify(generator).getNewEntry();
		inOrder.verify(preparedStatement).setLong(anyInt(), anyLong());
		inOrder.verify(preparedStatement).addBatch();
		inOrder.verify(generator).getNewEntry();
		inOrder.verify(preparedStatement).setLong(anyInt(), anyLong());
		inOrder.verify(preparedStatement).addBatch();
		inOrder.verify(generator).getNewEntry();
		inOrder.verify(preparedStatement).setLong(anyInt(), anyLong());
		inOrder.verify(preparedStatement).addBatch();
		inOrder.verify(generator).getNewEntry();
		inOrder.verify(preparedStatement).setLong(anyInt(), anyLong());
		inOrder.verify(preparedStatement).addBatch();
		inOrder.verify(generator).getNewEntry();
		inOrder.verify(preparedStatement).setLong(anyInt(), anyLong());
		inOrder.verify(preparedStatement).addBatch();
		inOrder.verify(preparedStatement).executeBatch();
		inOrder.verify(preparedStatement).close();

		verifyNoMoreInteractions(generator, preparedStatement);
	}

	@Test(expected = EntryTransferException.class)
	public void testTransferFromGeneratorToRepositoryGeneratorFail() throws Exception {
		when(generator.getNewEntry())
				.thenReturn(mock(Entry.class))
				.thenThrow(EntryGeneratorException.class);

		entryTransfer.transferFromGeneratorToRepository(
			appFactory, FROM_ENTRY, NUMBER_OF_ENTRIES_TO_TRANSFER, dataSource);

		InOrder inOrder = inOrder(generator);
		inOrder.verify(generator).getNewEntry();
		verifyNoMoreInteractions(generator);
	}

	@Test(expected = EntryTransferException.class)
	public void testTransferFromGeneratorToRepositoryDBFail() throws Exception {
		final Entry entry = mock(Entry.class);
		when(generator.getNewEntry())
				.thenReturn(entry)
				.thenReturn(entry)
				.thenReturn(entry);
		doThrow(SQLException.class)
				.when(preparedStatement).executeBatch();

		entryTransfer.transferFromGeneratorToRepository(
				appFactory, FROM_ENTRY, NUMBER_OF_ENTRIES_TO_TRANSFER,
			dataSource);

		InOrder inOrder = inOrder(generator, preparedStatement);
		inOrder.verify(generator).getNewEntry();
		inOrder.verify(preparedStatement).setLong(anyInt(), anyLong());
		inOrder.verify(preparedStatement).addBatch();
		inOrder.verify(preparedStatement).executeBatch();
		verifyNoMoreInteractions(generator, preparedStatement);
	}
}