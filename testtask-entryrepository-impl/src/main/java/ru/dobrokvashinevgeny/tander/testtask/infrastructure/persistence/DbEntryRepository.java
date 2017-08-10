/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;
import ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.mapper.*;

import java.sql.*;
import java.util.*;

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
	public void putEntries(List<Entry> entries) throws EntryRepositoryException {
		try(Connection connection = dataSource.getConnection()) {
			try {
				connection.setAutoCommit(false);

				EntryMapper entryMapper = new EntryMapper();
				entryMapper.setConnection(connection);
				entryMapper.insertWithBatch(entries);

				connection.commit();
			} catch (SQLException e) {
				connection.rollback();
				throw new EntryRepositoryException(e);
			}

			connection.setAutoCommit(true);
		} catch (SQLException | MapperException e) {
			throw new EntryRepositoryException(e);
		}
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
	public boolean isEntriesExists() throws EntryRepositoryException {
		try(Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement("select field from Test");
			ResultSet rs = statement.executeQuery()) {
			return rs.next();
		} catch (SQLException e) {
			throw new EntryRepositoryException(e);
		}
	}

	@Override
	public void clearEntries() throws EntryRepositoryException {
		try(Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement("delete from Test")) {
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new EntryRepositoryException(e);
		}
	}

	@Override
	public void createDataStructure() throws EntryRepositoryException {
		try(Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement("select null from information_schema.tables where table_name = 'Test'");
			PreparedStatement dropStatement = connection.prepareStatement("drop table Test");
			PreparedStatement createStatement = connection.prepareStatement("create table Test (field int not null)");
			ResultSet rs = statement.executeQuery()) {
			if (rs.next()) {
				dropStatement.executeUpdate();
			}

			createStatement.executeUpdate();
		} catch (SQLException e) {
			throw new EntryRepositoryException(e);
		}
	}
}