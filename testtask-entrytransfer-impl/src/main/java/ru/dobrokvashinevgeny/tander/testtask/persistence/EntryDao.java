package ru.dobrokvashinevgeny.tander.testtask.persistence;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.Entry;

/**
 * Класс EntryDao
 *
 * @version 1.0
 */
public interface EntryDao {
	/**
	 *
	 */
	void startTransaction() throws EntryDaoException;

	/**
	 *
	 * @throws EntryDaoException
	 */
	void completeTransaction() throws EntryDaoException;

	void startEntryBatch() throws EntryDaoException;

	void endEntryBatch() throws EntryDaoException;

	/**
	 * Помещает Entry в хранилище в пакете
	 * @param entry Entry для размещения
	 * @throws EntryDaoException если произошла ошибка во время размещения
	 */
	void putEntryInBatch(Entry entry) throws EntryDaoException;

	/**
	 * Удалить все Entry из репозитария
	 * @throws EntryDaoException если произошла ошибка во время удаления
	 */
	void clearEntries() throws EntryDaoException;

	/**
	 * Создать структуру данных хранения Entry в репозитарии
	 * @throws EntryDaoException если произошла ошибка во время создания
	 */
	void createDataStructure() throws EntryDaoException;
}