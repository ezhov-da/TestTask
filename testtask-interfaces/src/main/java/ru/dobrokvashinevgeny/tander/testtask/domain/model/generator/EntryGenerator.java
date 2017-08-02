package ru.dobrokvashinevgeny.tander.testtask.domain.model.generator;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.Entry;

/**
 * Генератор данных для использования в тестовом задании
 *
 * @author Evgeny Dobrokvashin
 *         Created by Stalker on 16.07.2017.
 * @version 1.0 July 2017
 */
public interface EntryGenerator {
	/**
	 * Получает новый объект данных
	 * @return объект данных
	 * @throws EntryGeneratorException если произошла ошибка генерации данных
	 */
	Entry getNewEntry() throws EntryGeneratorException;
}
