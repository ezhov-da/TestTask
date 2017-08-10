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
	 * Помещает Entries в хранилище
	 * @param entries список Entries для размещения
	 * @throws EntryRepositoryException если произошла ошибка во время размещения
	 */
	void putEntries(List<Entry> entries) throws EntryRepositoryException;

	/**
	 * Получить Entries из диапазона
	 * @param from начало диапазона
	 * @param to конец диапазона
	 * @return список Entries
	 * @throws EntryRepositoryException если произошла ошибка во время получения
	 */
	List<Entry> getEntriesFromRange(long from, long to) throws EntryRepositoryException;

	/**
	 * Проверить существует ли в репозитарии хоть один Entry
	 * @return {@code true} если существует, {@code false} если нет
	 * @throws EntryRepositoryException если произошла ошибка во время проверки
	 */
	boolean isEntriesExists() throws EntryRepositoryException;

	/**
	 * Удалить все Entry из репозитария
	 * @throws EntryRepositoryException если произошла ошибка во время удаления
	 */
	void clearEntries() throws EntryRepositoryException;

	/**
	 * Создать структуру данных хранения Entry в репозитарии
	 * @throws EntryRepositoryException если произошла ошибка во время создания
	 */
	void createDataStructure() throws EntryRepositoryException;
}