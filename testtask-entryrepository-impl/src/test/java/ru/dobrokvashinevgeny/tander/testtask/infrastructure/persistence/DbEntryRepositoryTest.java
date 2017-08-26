/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence;

import org.junit.*;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;

import java.sql.*;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.EntryTable.FIELD_NAME;
import static ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.EntryTable.TABLE_NAME;

/**
 */
public class DbEntryRepositoryTest {
	private EntryRepository entryRepository;
	private Connection connection;
	private PreparedStatement stmt;

	@Before
	public void setUp() throws Exception {
		DataSource dataSource = mock(DataSource.class);
		connection = mock(Connection.class);
		stmt = mock(PreparedStatement.class);

		when(dataSource.getConnection()).thenReturn(connection);

		entryRepository = new DbEntryRepository(dataSource);
	}

	@Test
	public void testGetEntriesFromRangeOk() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(stmt);
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(true, true, true, false);
		when(rs.getLong(1))
			.thenReturn(2L, 3L, 4L);
		when(stmt.executeQuery()).thenReturn(rs);

		final long from = 2;
		final long to = 4;
		List<Entry> entries = entryRepository.getEntriesFromRange(from, to);

		verify(connection).prepareStatement(eq(
			"select " + FIELD_NAME + " from (select field from " + TABLE_NAME + " order by " + FIELD_NAME + ") as t where field between ? and ?"));
		verify(stmt).setObject(1, from);
		verify(stmt).setObject(2, to);
		assertThat(entries,
			containsInAnyOrder(new EntryImpl(2L), new EntryImpl(3L), new EntryImpl(4L)));
	}
}