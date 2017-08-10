/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence;

import java.sql.*;

/**
 * Реализация интерфейса источника данных
 */
public class DbDataSource implements DataSource {
	private final String connectionUrl;

	private final String userName;

	private final String userPsw;

	public DbDataSource(String connectionUrl, String userName, String userPsw) {
		this.connectionUrl = connectionUrl;
		this.userName = userName;
		this.userPsw = userPsw;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection( connectionUrl, userName, userPsw );
	}
}