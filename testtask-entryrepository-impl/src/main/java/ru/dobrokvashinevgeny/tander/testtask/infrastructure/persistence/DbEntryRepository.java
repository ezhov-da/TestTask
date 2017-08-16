/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;
import ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.mapper.*;

import java.sql.*;
import java.util.List;

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
}