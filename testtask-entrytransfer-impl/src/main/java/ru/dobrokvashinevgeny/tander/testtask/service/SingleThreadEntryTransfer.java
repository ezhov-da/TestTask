/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.generator.*;

import java.util.*;

/**
 * Однопоточная реализация сервиса передачи сгенерированных Entries в их хранилище для сохренения
 */
public class SingleThreadEntryTransfer implements EntryTransfer {
	@Override
	public void transferFromGeneratorToRepository(EntryGenerator source, EntryRepository entryRepository,
												  long numberOfEntriesToTransfer,
												  int entriesBatchSize)
			throws EntryTransferException {
		final List<Entry> entries = new ArrayList<>(entriesBatchSize);
		try {
			for (int curBatchNumber = 0;
			     curBatchNumber < getNumberOfBatchExecutions(numberOfEntriesToTransfer, entriesBatchSize);
			     curBatchNumber++) {
				transferEntriesBatch(source, entryRepository, entries,
						getEntriesBatchSize(numberOfEntriesToTransfer, entriesBatchSize, curBatchNumber));
			}
		} catch (EntryGeneratorException | EntryRepositoryException e) {
			throw new EntryTransferException( e );
		}
	}

	private long getNumberOfBatchExecutions(long numberOfEntriesToTransfer, int entriesBatchSize) {
		return getWholeBatchExecutions(numberOfEntriesToTransfer, entriesBatchSize) +
				(0 != getSizeOfTheEntriesLastBatch(numberOfEntriesToTransfer, entriesBatchSize) ? 1 : 0);
	}

	private long getWholeBatchExecutions(long numberOfEntriesToTransfer, int entriesBatchSize) {
		return numberOfEntriesToTransfer / entriesBatchSize;
	}

	private long getSizeOfTheEntriesLastBatch(long numberOfEntriesToTransfer, int entriesBatchSize) {
		return numberOfEntriesToTransfer % entriesBatchSize;
	}

	private void transferEntriesBatch(EntryGenerator source, EntryRepository destination, List<Entry> entries,
									  long entriesBatchSize) throws EntryGeneratorException, EntryRepositoryException {
		populateEntriesFromSource(entries, source, entriesBatchSize);

		destination.putEntries( entries );

		entries.clear();
	}

	private void populateEntriesFromSource(List<Entry> entries, EntryGenerator source, long entriesBatchSize)
			throws EntryGeneratorException {
		for (int j = 0; j < entriesBatchSize; j++) {
			entries.add(source.getNewEntry());
		}
	}

	private long getEntriesBatchSize(long numberOfEntriesToTransfer, int entriesBatchSize, int curBatchNumber) {
		if (getWholeBatchExecutions(numberOfEntriesToTransfer, entriesBatchSize) > curBatchNumber) {
			return entriesBatchSize;
		} else {
			return getSizeOfTheEntriesLastBatch(numberOfEntriesToTransfer, entriesBatchSize);
		}
	}
}