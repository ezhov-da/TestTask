package ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence;

import java.sql.*;

/**
 * Интерфейс источника данных
 */
public interface DataSource {

	/**
	 * Получить соединение
	 * @return соединение
	 * @throws SQLException если произошла ошибка во время получения соединения
	 */
	Connection getConnection() throws SQLException;
}