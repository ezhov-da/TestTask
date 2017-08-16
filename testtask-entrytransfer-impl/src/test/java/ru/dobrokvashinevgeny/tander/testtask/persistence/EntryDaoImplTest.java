/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.persistence;

import org.junit.*;
import ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.EntryDaoImpl;

import java.sql.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.EntryTable.FIELD_NAME;
import static ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.EntryTable.TABLE_NAME;

/**
 * Класс EntryDaoImplTest
 */
public class EntryDaoImplTest {
	private EntryDao entryDao;
	private Connection connection;
	private PreparedStatement stmt;

	@Before
	public void setUp() throws Exception {
		connection = mock(Connection.class);
		stmt = mock(PreparedStatement.class);
		entryDao = new EntryDaoImpl(connection);
	}

	@Test
	public void testClearOk() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(stmt);

		entryDao.clearEntries();

		verify(connection).prepareStatement(eq("delete from " + TABLE_NAME));
	}

	@Test
	public void testCreateDataStructureTableExistsOk() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(stmt);
		ResultSet rs = mock(ResultSet.class);
		when(stmt.executeQuery()).thenReturn(rs);

		entryDao.createDataStructure();

		verify(connection).prepareStatement(
			eq("select null from information_schema.tables where table_name = '" + TABLE_NAME + "'"));
		verify(connection).prepareStatement(
			eq("create table " + TABLE_NAME + " (" + FIELD_NAME + " int not null)"));
	}

	@Test
	public void testCreateDataStructureTableNotExistsOk() throws Exception {
		when(connection.prepareStatement(anyString())).thenReturn(stmt);
		ResultSet rs = mock(ResultSet.class);
		when(rs.next()).thenReturn(true, false);
		when(stmt.executeQuery()).thenReturn(rs);

		entryDao.createDataStructure();

		verify(connection).prepareStatement(
			eq("select null from information_schema.tables where table_name = '" + TABLE_NAME + "'"));
		verify(connection).prepareStatement(eq("drop table " + TABLE_NAME));
		verify(connection).prepareStatement(
			eq("create table " + TABLE_NAME + " (" + FIELD_NAME + " int not null)"));
	}
}