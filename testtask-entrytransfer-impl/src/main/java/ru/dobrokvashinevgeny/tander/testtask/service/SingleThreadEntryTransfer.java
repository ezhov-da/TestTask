/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.EntryDaoImpl;
import ru.dobrokvashinevgeny.tander.testtask.persistence.*;
import ru.dobrokvashinevgeny.tander.testtask.service.generator.*;

import java.sql.*;

/**
 * Однопоточная реализация сервиса передачи сгенерированных Entries в их хранилище для сохренения
 */
public class SingleThreadEntryTransfer implements EntryTransfer {
	private static final int SQL_BATCH_SIZE = 200;

	@Override
	public void transferFromGeneratorToRepository(EntryGenerator source,
												  long numberOfEntriesToTransfer,
												  int entriesBatchSize, DataSource dataSource)
		throws EntryTransferException {
		try(Connection connection = dataSource.getConnection()) {
			EntryDao entryDao = new EntryDaoImpl(connection);
			entryDao.startTransaction();

			for (int curBatchNumber = 0;
				 curBatchNumber < getNumberOfBatchExecutions(numberOfEntriesToTransfer, SQL_BATCH_SIZE);
				 curBatchNumber++) {
				try {
					entryDao.startEntryBatch();
					transferEntriesBatch(source, entryDao,
						getEntriesBatchSize(numberOfEntriesToTransfer, SQL_BATCH_SIZE, curBatchNumber));
				} finally {
					entryDao.endEntryBatch();
				}
			}

			entryDao.completeTransaction();
		} catch (EntryGeneratorException | EntryDaoException | SQLException e) {
			throw  new EntryTransferException(e);
		}
	}

	private long getNumberOfBatchExecutions(long numberOfEntriesToTransfer, int entriesBatchSize) {
		return (numberOfEntriesToTransfer < entriesBatchSize) ?
			1 :
			getWholeBatchExecutions(numberOfEntriesToTransfer,entriesBatchSize) +
			(0 != getSizeOfTheEntriesLastBatch(numberOfEntriesToTransfer, entriesBatchSize) ? 1 : 0);
	}

	private long getWholeBatchExecutions(long numberOfEntriesToTransfer, int entriesBatchSize) {
		return numberOfEntriesToTransfer / entriesBatchSize;
	}

	private int getSizeOfTheEntriesLastBatch(long numberOfEntriesToTransfer, int entriesBatchSize) {
		return (int)numberOfEntriesToTransfer % entriesBatchSize;
	}

	private void transferEntriesBatch(EntryGenerator source, EntryDao entryDao, int batchSize)
		throws EntryGeneratorException, EntryDaoException {
		for (int entryInBatchCounter = 0; entryInBatchCounter < batchSize; entryInBatchCounter++) {
			entryDao.putEntryInBatch(source.getNewEntry());
		}
	}

	private int getEntriesBatchSize(long numberOfEntriesToTransfer, int entriesBatchSize, int curBatchNumber) {
		if (numberOfEntriesToTransfer < entriesBatchSize) {
			return (int)numberOfEntriesToTransfer;
		}

		if (getWholeBatchExecutions(numberOfEntriesToTransfer, entriesBatchSize) > curBatchNumber) {
			return entriesBatchSize;
		} else {
			return getSizeOfTheEntriesLastBatch(numberOfEntriesToTransfer, entriesBatchSize);
		}
	}
}