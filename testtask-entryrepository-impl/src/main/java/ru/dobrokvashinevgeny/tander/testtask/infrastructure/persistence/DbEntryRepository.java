/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;
import ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.mapper.*;

import java.sql.*;
import java.util.List;

import static ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.EntryTable.TABLE_NAME;

/**
 * Реализация репозитария Entries в БД
 */
public class DbEntryRepository implements EntryRepository {
	private final DataSource dataSource;

	/**
	 * Ининциализирует хранилище
	 * @param dataSource источник данных
	 */
	public DbEntryRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<Entry> getEntriesFromRange(long from, long to) throws EntryRepositoryException {
		try(Connection connection = dataSource.getConnection()) {
			EntryMapper entryMapper = new EntryMapper();
			entryMapper.setConnection(connection);
			return entryMapper.findByRange(from, to);
		} catch (SQLException | MapperException e) {
			throw new EntryRepositoryException(e);
		}
	}

	@Override
	public long size() throws EntryRepositoryException {
		try(Connection connection = dataSource.getConnection();
			PreparedStatement stmt = connection.prepareStatement("select count(*) from " + TABLE_NAME);
			ResultSet rs = stmt.executeQuery()) {
			long result = 0L;

			if (rs.next()) {
				result = rs.getLong(1);
			}

			return result;
		} catch (SQLException e) {
			throw new EntryRepositoryException(e);
		}
	}
}