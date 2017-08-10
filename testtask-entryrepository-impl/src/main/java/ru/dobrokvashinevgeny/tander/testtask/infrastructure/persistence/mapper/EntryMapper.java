/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.mapper;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.*;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;

import java.sql.*;
import java.util.List;

/**
 * Класс EntryMapper
 *
 * @version 1.0
 */
public class EntryMapper extends AbstractMapper {
	public List<Entry> findByRange(long from, long to) throws MapperException {
		return (List<Entry>) findManyFrom(new FindEntryFromRangeSource(from, to));
	}

	static class FindEntryFromRangeSource implements StatementSource {
		private final long from;
		private final long to;

		public FindEntryFromRangeSource(long from, long to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public String sql() {
			return "select field from Test where field between ? and ?";
		}

		@Override
		public Object[] parameters() {
			return new Object[] { from, to };
		}
	}

	@Override
	protected String insertSql() {
		return "insert into Test(field) values(?)";
	}

	@Override
	protected DomainObject domainObjectFromResultSet(ResultSet rs) throws SQLException {
		return new EntryImpl(rs.getLong(1));
	}

	@Override
	protected void doInsert(DomainObject domainObject, PreparedStatement batchStatement) throws SQLException {
		Entry entry = (Entry) domainObject;
		batchStatement.setLong(1, entry.getValue());
	}
}