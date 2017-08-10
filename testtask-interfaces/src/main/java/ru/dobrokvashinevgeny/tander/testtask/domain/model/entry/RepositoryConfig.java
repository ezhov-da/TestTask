package ru.dobrokvashinevgeny.tander.testtask.domain.model.entry;

/**
 * Интерфейс конфигурационных данных репозитария Entries
 */
public interface RepositoryConfig {
	/**
	 * URL соединения с БД
	 * @return URL
	 */
	String getConnectionUrl();

	/**
	 * Логин пользователя БД
	 * @return логин
	 */
	String getUserName();

	/**
	 * Пароль пользователя БД
	 * @return пароль
	 */
	String getUserPsw();
}
