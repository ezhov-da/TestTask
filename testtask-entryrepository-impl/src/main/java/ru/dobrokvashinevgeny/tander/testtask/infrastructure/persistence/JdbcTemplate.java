/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence;

import java.sql.*;
import java.util.*;

/**
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 16.07.2017.
 */
class JdbcTemplate {
	private final Connection connection;

	JdbcTemplate(Connection connection) {
		this.connection = connection;
	}

	int executeQuery(String queryText) throws SQLException {
		try(PreparedStatement stmt = connection.prepareStatement(queryText)) {
			return stmt.executeUpdate();
		}
	}

	boolean exists(String queryText) throws SQLException {
		try(PreparedStatement stmt = connection.prepareStatement(queryText);
		    ResultSet rs = stmt.executeQuery()) {
			return rs.next();
		}
	}

	<T> List<T> executeQuery(String queryText, SqlTemplateMatcher<T> sqlMatcher) throws SQLException {
		List<T> result = new ArrayList<>();
		try(PreparedStatement stmt = connection.prepareStatement(queryText);
		    ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				result.add(sqlMatcher.match( rs ));
			}
		}

		return result;
	}
}