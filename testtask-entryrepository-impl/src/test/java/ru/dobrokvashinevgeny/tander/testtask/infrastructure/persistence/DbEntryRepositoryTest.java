/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence;

import org.junit.*;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;

import java.sql.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 16.07.2017.
 */
public class DbEntryRepositoryTest {
	//	private static final String DB_URL = "jdbc:h2:mem:h2db-test;DB_CLOSE_DELAY=-1";
	private static final String TABLE_NAME = "Test";
	private static final String FIELD_NAME = "field";
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
	public void testPutOneEntry() throws Exception {
		when(connection.prepareStatement( anyString() )).thenReturn( stmt );

		entryRepository.putEntries(Collections.singletonList( new EntryImpl( 1L ) ));

		verify(connection).prepareStatement(eq("insert into " + TABLE_NAME + "(" + FIELD_NAME + ") values(?)"));
		verify(stmt).setLong(eq(1), eq(1L));
	}

	@Test
	public void testPutTwoEntry() throws Exception {
		when(connection.prepareStatement( anyString() )).thenReturn( stmt );

		entryRepository.putEntries(
				Arrays.asList(
						new EntryImpl( 1L ),
						new EntryImpl( 2L )
				)
		);

		verify( connection ).prepareStatement(eq("insert into " + TABLE_NAME + "(" + FIELD_NAME + ") values(?)"));
		verify( stmt ).setLong( eq(1) , eq(1L) );
		verify( stmt ).setLong( eq(1) , eq(2L) );
		verify( stmt, times( 2 ) ).addBatch();
		verify( stmt ).executeBatch();
	}

	@Test
	public void testEntriesExists() throws Exception {
		when(connection.prepareStatement( anyString() )).thenReturn( stmt );
		ResultSet rs = mock( ResultSet.class );
		when( stmt.executeQuery() ).thenReturn( rs );

		entryRepository.isEntriesExists();

		verify( connection ).prepareStatement( eq("select " + FIELD_NAME + " from " + TABLE_NAME) );
	}

	@Test
	public void testClear() throws Exception {
		when(connection.prepareStatement( anyString() )).thenReturn( stmt );

		entryRepository.clearEntries();

		verify( connection ).prepareStatement( eq("delete from " + TABLE_NAME) );
	}

	@Test
	public void testGetEntriesFromRange() throws Exception {
		when(connection.prepareStatement( anyString() )).thenReturn( stmt );
		ResultSet rs = mock( ResultSet.class );
		when(rs.next()).thenReturn( true, true, true, true, true, false );
		when(rs.getLong(1))
				.thenReturn( 1L, 2L, 3L, 4L, 5L );
		when( stmt.executeQuery() ).thenReturn( rs );

		final int from = 2;
		final int to = 4;
		List<Entry> entries = entryRepository.getEntriesFromRange(from, to);

		verify( connection ).prepareStatement(eq(
				"select " + FIELD_NAME + " from (select field from " + TABLE_NAME + " order by " + FIELD_NAME + ") where field between ? and ?"));
		verify(stmt).setLong(1, from);
		verify(stmt).setLong(2, to);
		assertThat(entries,
				containsInAnyOrder(new EntryImpl( 2L ), new EntryImpl( 3L ), new EntryImpl( 4L )));
	}

	@Test
	public void testCreateDataStructureTableExists() throws Exception {
		when(connection.prepareStatement( anyString() )).thenReturn( stmt );
		ResultSet rs = mock( ResultSet.class );
		when( stmt.executeQuery() ).thenReturn( rs );

		entryRepository.createDataStructure();

		verify( connection ).prepareStatement(
				eq("select null from information_schema.tables where table_name = '" + TABLE_NAME + "'"));
		verify( connection ).prepareStatement(
				eq("create table " + TABLE_NAME + " (" + FIELD_NAME + " int not null)") );
	}

	@Test
	public void testCreateDataStructureTableNotExists() throws Exception {
		when(connection.prepareStatement( anyString() )).thenReturn( stmt );
		ResultSet rs = mock( ResultSet.class );
		when(rs.next()).thenReturn(true,false);
		when(stmt.executeQuery()).thenReturn(rs);

		entryRepository.createDataStructure();

		verify(connection).prepareStatement(
				eq("select null from information_schema.tables where table_name = '" + TABLE_NAME + "'"));
		verify(connection).prepareStatement(eq("drop table " + TABLE_NAME) );
		verify(connection).prepareStatement(
				eq("create table " + TABLE_NAME + " (" + FIELD_NAME + " int not null)"));
	}
}