/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.presentation;

import org.junit.Test;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.generator.EntryGenerator;
import ru.dobrokvashinevgeny.tander.testtask.service.*;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 20.07.2017.
 */
public class TestTaskTest {
	private static final int BATCH_SIZE = 2;
	private static final int N = 2;

	@Test
	public void test() throws Exception {
		final EntryTransfer entryTransfer = mock(EntryTransfer.class);
		final EntryConverterService converterService = mock(EntryConverterService.class);
		final Calculator calculator = mock(Calculator.class);
		final FileStore fileStore = mock(FileStore.class);
		final EntryGenerator entryGenerator = mock(EntryGenerator.class);
		final EntryRepository entryRepository = mock(EntryRepository.class);

		TestTask testTask = new TestTask(entryGenerator, entryRepository, entryTransfer,
			converterService, calculator, N, BATCH_SIZE);

		testTask.run();

		verify(entryTransfer)
				.transferFromGeneratorToRepository(
					entryGenerator, entryRepository, N, BATCH_SIZE);
		verify(converterService).convertEntriesToXml(entryRepository, fileStore, anyString(), BATCH_SIZE);
		verify(converterService)
			.transformEntriesXml(fileStore, anyString(), anyString(), anyString(), anyString(), BATCH_SIZE);
		verify(calculator).getSumOfEntriesDataFrom(fileStore, anyString());
	}
}