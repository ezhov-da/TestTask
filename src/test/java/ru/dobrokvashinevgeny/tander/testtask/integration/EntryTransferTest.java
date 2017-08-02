/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.integration;

import org.hamcrest.Matchers;
import org.junit.Test;
import ru.dobrokvashinevgeny.tander.testtask.Registry;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;
import ru.dobrokvashinevgeny.tander.testtask.service.EntryTransfer;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 23.07.2017.
 */
public class EntryTransferTest {

	private static final int NUMBER_OF_ENTRIES_TO_TRANSFER = 100;
	private static final int ENTRIES_BATCH_SIZE = 10;

	@Test
	public void test() throws Exception {
		Registry registry = new Registry();
		EntryTransfer entryTransfer = registry.entryTransfer();
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
		final EntryRepository repository = registry.entryRepository();
		repository.createDataStructure();

		entryTransfer.transferFromGeneratorToRepository(
				registry.entryGenerator(), repository, NUMBER_OF_ENTRIES_TO_TRANSFER, ENTRIES_BATCH_SIZE);

		final List<Entry> allEntries = repository.getAllEntries();
		assertThat(allEntries, Matchers.hasSize(NUMBER_OF_ENTRIES_TO_TRANSFER));
	}
}