package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.mapper;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.DomainObject;

import java.sql.Connection;
import java.util.List;

/**
 * Класс Mapper
 *
 * @version 1.0
 */
public interface Mapper {
	void insertWithBatch(List<? extends DomainObject> domainObjects) throws MapperException;

	void setConnection(Connection connection);
}