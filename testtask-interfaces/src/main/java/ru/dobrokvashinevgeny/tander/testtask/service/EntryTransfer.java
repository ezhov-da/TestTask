package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.service.generator.EntryGenerator;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;

/**
 * Интерфейс сервиса передачи сгенерированных Entries в их хранилище для сохренения
 */
public interface EntryTransfer {
	/**
	 * Передает сгенерированные Entries в их хранилище для сохренения
	 * @param generator генератор Entries
	 * @param numberOfEntriesToTransfer количество Entries для генерации
	 * @param entriesBatchSize    размер пакета Entries для обработки
	 * @param dataSource
	 * @throws EntryTransferException если произошла ошибка при передаче
	 */
	void transferFromGeneratorToRepository(EntryGenerator generator,
										   long numberOfEntriesToTransfer,
										   int entriesBatchSize, DataSource dataSource)
			throws EntryTransferException;
}
