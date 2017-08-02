package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.generator.EntryGenerator;

/**
 * @author Evgeny Dobrokvashin
 *         Created by Stalker on 18.07.2017.
 * @version 1.0 2017
 */
public interface EntryTransfer {
	void transferFromGeneratorToRepository(EntryGenerator generator, EntryRepository repository,
										   long numberOfEntriesToTransfer, int entriesBatchSize)
			throws EntryTransferException;
}
