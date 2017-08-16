/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.mapper;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.*;

import java.sql.*;
import java.util.*;

/**
 * Класс AbstractMapper
 */
public abstract class AbstractMapper implements Mapper {
	private Connection connection;

	protected abstract <T extends DomainObject> T domainObjectFromResultSet(ResultSet rs)
		throws SQLException;

	protected List<? extends DomainObject> findManyFrom(StatementSource source) throws MapperException {
		try(PreparedStatement statement = connection.prepareStatement(source.sql())) {
			int paramIndex = 1;
			for (Object parameter : source.parameters()) {
				statement.setObject(paramIndex++, parameter);
			}

			try(ResultSet rs = statement.executeQuery()) {
				return loadAll(rs);
			}
		} catch (SQLException e) {
			throw new MapperException(e);
		}
	}

	private List<? extends DomainObject> loadAll(ResultSet rs) throws SQLException {
		List<? extends DomainObject> result = new ArrayList<>();
		while (rs.next()) {
			result.add(domainObjectFromResultSet(rs));
		}
		return result;
	}

	@Override
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}