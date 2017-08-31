/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.domain.model.entry;

import java.util.List;

/**
 * Интерфейс репозитария Entries
 */
public interface EntryRepository {
	/**
	 * Получить Entries из диапазона
	 * @param from начало диапазона
	 * @param to конец диапазона
	 * @return список Entries
	 * @throws EntryRepositoryException если произошла ошибка во время получения
	 */
	List<Entry> getEntriesFromRange(long from, long to) throws EntryRepositoryException;

	/**
	 * Получить количество Entry хранящихся в хранилище
	 * @return количество Entry хранящихся в хранилище
	 */
	long size() throws EntryRepositoryException;
}