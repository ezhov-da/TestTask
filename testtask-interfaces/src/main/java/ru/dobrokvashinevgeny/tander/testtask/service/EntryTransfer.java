package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.generator.EntryGenerator;

/**
 * Интерфейс сервиса передачи сгенерированных Entries в их хранилище для сохренения
 */
public interface EntryTransfer {
	/**
	 * Передает сгенерированные Entries в их хранилище для сохренения
	 * @param generator генератор Entries
	 * @param entryRepository хранилище Entries
	 * @param numberOfEntriesToTransfer количество Entries для генерации
	 * @param entriesBatchSize	размер пакета Entries для обработки
	 * @throws EntryTransferException если произошла ошибка при передаче
	 */
	void transferFromGeneratorToRepository(EntryGenerator generator, EntryRepository entryRepository,
										   long numberOfEntriesToTransfer,
										   int entriesBatchSize)
			throws EntryTransferException;
}
