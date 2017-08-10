package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.mapper;

import org.junit.Test;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DomainObject;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;

import java.sql.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

/**
 * Класс EntryMapperTest
 *
 * @version 1.0
 */
public class EntryMapperTest {
	@Test
	public void findByRangeOk() throws Exception {
		Connection connection = mock(Connection.class);
		PreparedStatement stmt = mock(PreparedStatement.class);
		ResultSet rs = mock(ResultSet.class);
		when(connection.prepareStatement(anyString())).thenReturn(stmt);
		when(stmt.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(true, true, false);
		when(rs.getLong(anyInt())).thenReturn(1L, 2L);

		EntryMapper entryMapper = new EntryMapper();
		entryMapper.setConnection(connection);

		List<Entry> result = entryMapper.findByRange(1L, 2L);

		assertThat(result, equalTo(Arrays.asList(new EntryImpl(1L), new EntryImpl(2L))));
		verify(connection).prepareStatement(anyString());
		verify(stmt).executeQuery();
		verify(stmt, times(2)).setObject(anyInt(), anyObject());
		verify(stmt).close();
		verify(rs, times(3)).next();
		verify(rs, times(2)).getLong(1);
		verify(rs).close();
		verifyNoMoreInteractions(connection, stmt, rs);
	}

	@Test
	public void domainObjectFromResultSetOk() throws Exception {
		EntryMapper entryMapper = new EntryMapper();
		ResultSet rs = mock(ResultSet.class);
		when(rs.getLong(anyInt())).thenReturn(1L);

		DomainObject result = entryMapper.domainObjectFromResultSet(rs);

		assertThat(result, equalTo(new EntryImpl(1L)));
	}

	@Test
	public void doInsertOk() throws Exception {
		EntryMapper entryMapper = new EntryMapper();
		PreparedStatement ps = mock(PreparedStatement.class);

		entryMapper.doInsert(new EntryImpl(1L), ps);

		verify(ps).setLong(1, 1L);
		verifyNoMoreInteractions(ps);
	}

}