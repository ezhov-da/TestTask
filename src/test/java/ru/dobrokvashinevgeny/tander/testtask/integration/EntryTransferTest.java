/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.integration;

import org.junit.Test;

/**
 *
 */
public class EntryTransferTest {

	private static final int NUMBER_OF_ENTRIES_TO_TRANSFER = 100;
	private static final int ENTRIES_BATCH_SIZE = 10;

	@Test
	public void test() throws Exception {
//		EntryTransfer entryTransfer = appSession.entryTransfer();
		/*RepositoryConfig configuration = new RepositoryConfig() {
			@Override
			public String getConnectionUrl() {
				return "jdbc:h2:mem:h2db-test;DB_CLOSE_DELAY=-1";
			}

			@Override
			public String getUserName() {
				return "sa";
			}

			@Override
			public String getUserPsw() {
				return "";
			}
		};*/
		/*final EntryRepository repository = appSession.entryRepository();
		repository.createDataStructure();

		entryTransfer.transferFromGeneratorToRepository(
				appSession.entryGenerator(), repository, NUMBER_OF_ENTRIES_TO_TRANSFER, ENTRIES_BATCH_SIZE);

		final List<Entry> allEntries = repository.getAllEntries();
		assertThat(allEntries, Matchers.hasSize(NUMBER_OF_ENTRIES_TO_TRANSFER));*/
	}
}