package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence;

import java.sql.*;

/**
 * @author Evgeny Dobrokvashin
 *         Created by Stalker on 16.07.2017.
 * @version 1.0 2017
 */
public interface SqlTemplateMatcher<T> {
	T match(ResultSet rs) throws SQLException;
}
