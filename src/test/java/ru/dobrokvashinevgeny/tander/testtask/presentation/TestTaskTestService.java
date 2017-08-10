/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.presentation;

import org.junit.Test;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.generator.EntryGenerator;
import ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.service.*;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 */
public class TestTaskTestService {
	private static final int BATCH_SIZE = 2;
	private static final long N = 2;

	@Test
	public void test() throws Exception {
		final EntryTransfer entryTransfer = mock(EntryTransfer.class);
		final EntryConverterService converterService = mock(EntryConverterService.class);
		final Calculator calculator = mock(Calculator.class);
		final FileRepository fileRepository = mock(FileRepository.class);
		final EntryGenerator entryGenerator = mock(EntryGenerator.class);
		final EntryRepository entryRepository = mock(EntryRepository.class);
		DataSource dataSource = mock(DataSource.class);
		TestTaskServiceConfig testTaskServiceConfig = new TestTaskServiceConfig() {
			@Override
			public String getEntryGeneratorImplClassName() {
				return "";
			}

			@Override
			public String getEntryRepositoryImplClassName() {
				return null;
			}

			@Override
			public String getEntryTransferImplClassName() {
				return null;
			}

			@Override
			public String getEntryConverterImplClassName() {
				return null;
			}

			@Override
			public String getFileStoreImplClassName() {
				return null;
			}

			@Override
			public String getCalculatorImplClassName() {
				return null;
			}

			@Override
			public int getBatchSize() {
				return 0;
			}
		};

		TestTaskService testTaskService = spy(new TestTaskService(N, testTaskServiceConfig, dataSource));

		testTaskService.calculateSumOfEntriesData();

		verify(entryTransfer)
				.transferFromGeneratorToRepository(
					entryGenerator, entryRepository, N, BATCH_SIZE);
		verify(converterService).convertEntriesToXml(entryRepository, fileRepository, anyString(), BATCH_SIZE);
		verify(converterService)
			.transformEntriesXml(fileRepository, anyString(), anyString(), anyString(), anyString(), BATCH_SIZE);
		verify(calculator).getSumOfEntriesDataFrom(fileRepository, anyString());
	}
}