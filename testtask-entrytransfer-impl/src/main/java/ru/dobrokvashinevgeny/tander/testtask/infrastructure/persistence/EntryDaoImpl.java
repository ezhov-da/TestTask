/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;
import ru.dobrokvashinevgeny.tander.testtask.persistence.*;

import java.sql.*;
import java.util.logging.*;

import static ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.EntryTable.FIELD_NAME;
import static ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.EntryTable.TABLE_NAME;

/**
 * Класс EntryDaoImpl
 */
public class EntryDaoImpl implements EntryDao {
	private final static Logger LOG = Logger.getLogger(EntryDaoImpl.class.getName());

	private final Connection connection;
	private PreparedStatement batchStatement;

	public EntryDaoImpl(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void startTransaction() throws EntryDaoException {
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw  new EntryDaoException(e);
		}
	}

	@Override
	public void completeTransaction() throws EntryDaoException {
		try {
			connection.commit();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e.addSuppressed(e1);
			}
			throw new EntryDaoException(e);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				LOG.log(Level.SEVERE, "Can't close connection", e);
			}
		}
	}

	@Override
	public void startEntryBatch() throws EntryDaoException {
		try {
			batchStatement = connection.prepareStatement("insert into " + TABLE_NAME + "(" +
				FIELD_NAME + ") values(?)");
		} catch (SQLException e) {
			throw new EntryDaoException(e);
		}
	}

	@Override
	public void endEntryBatch() throws EntryDaoException {
		try {
			batchStatement.executeBatch();
		} catch (SQLException e) {
			throw new EntryDaoException(e);
		} finally {
			try {
				batchStatement.close();
			} catch (SQLException e) {
				LOG.log(Level.SEVERE, "Can't close Entry batch statement", e);
			}

			batchStatement = null;
		}
	}

	@Override
	public void putEntryInBatch(Entry entry) throws EntryDaoException {
		try {
			batchStatement.setLong(1, entry.getValue());
			batchStatement.addBatch();
		} catch (SQLException e) {
			throw new EntryDaoException(e);
		}
	}

	@Override
	public void clearEntries() throws EntryDaoException {
		try(PreparedStatement statement = connection.prepareStatement("delete from " + TABLE_NAME)) {
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new EntryDaoException(e);
		}
	}

	@Override
	public void createDataStructure() throws EntryDaoException {
		try(PreparedStatement statement =
				connection.prepareStatement(
					"select null from information_schema.tables where table_name = '" + TABLE_NAME + "'");
			PreparedStatement dropStatement = connection.prepareStatement("drop table " + TABLE_NAME);
			PreparedStatement createStatement = connection.prepareStatement(
				"create table " + TABLE_NAME + " (field int not null)");
			ResultSet rs = statement.executeQuery()) {
			if (rs.next()) {
				dropStatement.executeUpdate();
			}

			createStatement.executeUpdate();
		} catch (SQLException e) {
			throw new EntryDaoException(e);
		}
	}
}