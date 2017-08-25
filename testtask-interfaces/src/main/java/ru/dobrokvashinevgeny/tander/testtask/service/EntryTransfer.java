package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.AppFactory;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;

/**
 * Интерфейс сервиса передачи сгенерированных Entries в их хранилище для сохранения
 */
public interface EntryTransfer {
	/**
	 * Передает сгенерированные Entries в их хранилище для сохренения
	 * @param appFactory генератор Entries
	 * @param fromEntry
	 * @param numberOfEntriesToTransfer количество Entries для генерации
	 * @param dataSource    @throws EntryTransferException если произошла ошибка при передаче
	 * */
	void transferFromGeneratorToRepository(AppFactory appFactory,
										   long fromEntry, long numberOfEntriesToTransfer,
										   DataSource dataSource)
			throws EntryTransferException;
}
