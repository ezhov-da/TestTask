package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.mapper;

/**
 * Класс StatementSource
 *
 * @version 1.0
 */
public interface StatementSource {
	String sql();

	Object[] parameters();
}