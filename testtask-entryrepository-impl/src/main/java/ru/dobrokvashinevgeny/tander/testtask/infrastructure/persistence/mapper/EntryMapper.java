/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.mapper;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.*;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;

import java.sql.*;
import java.util.List;

import static ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.EntryTable.FIELD_NAME;
import static ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.EntryTable.TABLE_NAME;

/**
 * Класс EntryMapper
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
			return "select " + FIELD_NAME +
				" from (select " + FIELD_NAME + " from " + TABLE_NAME + " order by " + FIELD_NAME + ") as t " +
				"where " + FIELD_NAME + " between ? and ?";
		}

		@Override
		public Object[] parameters() {
			return new Object[] { from, to };
		}
	}

	@Override
	protected DomainObject domainObjectFromResultSet(ResultSet rs) throws SQLException {
		return new EntryImpl(rs.getLong(1));
	}
}