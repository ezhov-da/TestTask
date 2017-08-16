package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.mapper;

import java.sql.Connection;

/**
 * Класс Mapper
 */
public interface Mapper {
	void setConnection(Connection connection);
}