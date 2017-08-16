/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.EntryDaoImpl;
import ru.dobrokvashinevgeny.tander.testtask.persistence.*;

import java.sql.*;

/**
 * Класс EntryService
 */
public class EntryService {
	private final DataSource dataSource;

	public EntryService(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void createDataStructure() throws EntryServiceException {
		try(Connection connection = dataSource.getConnection()) {
			final EntryDao entryDao = new EntryDaoImpl(connection);
			entryDao.createDataStructure();
		} catch (SQLException | EntryDaoException e) {
			throw new EntryServiceException(e);
		}
	}
}