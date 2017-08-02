/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryImpl;

import java.sql.*;
import java.util.*;

/**
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 16.07.2017.
 */
public class DbEntryRepository implements EntryRepository {
	private static final int BATCH_SIZE = 500;

	private final String connectionUrl;

	private final String userName;

	private final String userPsw;

	protected Connection connection;

	interface ConnectionWorkTask<T> {
		T call(Connection connection) throws SQLException;
	}

	public DbEntryRepository(String connectionUrl, String userName, String userPsw) {
		this.connectionUrl = connectionUrl;
		this.userName = userName;
		this.userPsw = userPsw;
	}

	private Connection getConnection() throws SQLException {
		if (null != connection) {
			return connection;
		} else {
			return DriverManager.getConnection( connectionUrl, userName, userPsw );
		}
	}

	@Override
	public void putEntries(List<Entry> entries) throws EntryRepositoryException {
		try(Connection connection = getConnection();
			PreparedStatement stmt = connection.prepareStatement( "insert into Test(field) values(?)" )) {
			int batchCounter = 0;
			for (Entry entry : entries) {
				stmt.setLong( 1, entry.getValue() );
				stmt.addBatch();

				if (batchCounter++ > BATCH_SIZE) {
					stmt.executeBatch();
				}
			}

			if (batchCounter <= BATCH_SIZE) {
				stmt.executeBatch();
			}
		} catch (SQLException e) {
			throw new EntryRepositoryException( e );
		}
	}

	@Override
	public List<Entry> getAllEntries() throws EntryRepositoryException {
		return connectionWorkExecutor( new ConnectionWorkTask<List<Entry>>() {
			@Override
			public List<Entry> call(Connection connection) throws SQLException {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
				return jdbcTemplate.executeQuery(
						"select field from Test", new SqlTemplateMatcher<Entry>() {
							@Override
							public Entry match(ResultSet rs) throws SQLException {
								return new EntryImpl( rs.getLong( 1 ) );
							}
						});
			}
		});
	}

	@Override
	public List<Entry> getFirstEntries(long numberEntries) throws EntryRepositoryException {
		return null;
	}

	@Override
	public List<Entry> getEntriesFromRange(long from, long to) throws EntryRepositoryException {
		return null;
	}

	private <T> T connectionWorkExecutor(ConnectionWorkTask<T> task) throws EntryRepositoryException {
		try(Connection connection = getConnection()) {
			return task.call(connection);
		} catch (SQLException e) {
			throw new EntryRepositoryException( e );
		}
	}

	@Override
	public boolean isEntriesExists() throws EntryRepositoryException {
		return connectionWorkExecutor( new ConnectionWorkTask<Boolean>() {
			@Override
			public Boolean call(Connection connection) throws SQLException {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
				return jdbcTemplate.exists("select field from Test");
			}
		});
	}

	@Override
	public void clearEntries() throws EntryRepositoryException {
		connectionWorkExecutor( new ConnectionWorkTask<Void>() {
			@Override
			public Void call(Connection connection) throws SQLException {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
				jdbcTemplate.executeQuery("delete from Test");
				return null;
			}
		});
	}

	@Override
	public void createDataStructure() throws EntryRepositoryException {
		connectionWorkExecutor( new ConnectionWorkTask<Void>() {
			@Override
			public Void call(Connection connection) throws SQLException {
				JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
				if (jdbcTemplate.exists( "select null from information_schema.tables where table_name = 'Test'" )) {
					jdbcTemplate.executeQuery( "drop table Test" );
				}
				jdbcTemplate.executeQuery("create table Test (field int not null)");
				return null;
			}
		});
	}
}