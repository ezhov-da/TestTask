/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.presentation;

import org.junit.Test;
import ru.dobrokvashinevgeny.tander.testtask.AppFactory;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;
import ru.dobrokvashinevgeny.tander.testtask.service.generator.EntryGenerator;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.service.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.dobrokvashinevgeny.tander.testtask.infrastructure.configuration.AppConfiguration.*;

/**
 */
public class TestTaskServiceTest {
	private static final int BATCH_SIZE = 2;
	private static final long N = 2;
	private static final long FROM_ENTRY = 1L;

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
			public int getTransferBatchSize() {
				return BATCH_SIZE;
			}

			@Override
			public int getConverterBatchSize() {
				return BATCH_SIZE;
			}

			@Override
			public int getTransformerBatchSize() {
				return BATCH_SIZE;
			}
		};
		AppFactory appFactory = mock(AppFactory.class);
		when(appFactory.createEntryGenerator()).thenReturn(entryGenerator);
		when(appFactory.createCalculator()).thenReturn(calculator);
		when(appFactory.createEntryConverterService()).thenReturn(converterService);
		when(appFactory.createEntryRepository(dataSource)).thenReturn(entryRepository);
		when(appFactory.createEntryTransfer()).thenReturn(entryTransfer);
		when(appFactory.createFileRepository()).thenReturn(fileRepository);

		TestTaskService testTaskService = new TestTaskService(N, appFactory, testTaskServiceConfig, dataSource) {
			@Override
			protected void initEntryRepositoryStructure() throws EntryServiceException {
			}
		};

		testTaskService.calculateSumOfEntriesData();

		verify(entryTransfer).transferFromGeneratorToRepository(appFactory, FROM_ENTRY, N, dataSource);
		verify(converterService)
			.convertEntriesToXml(IN_XML_FILE_NAME, BATCH_SIZE, entryRepository, fileRepository);
		verify(converterService)
			.transformEntriesXml(fileRepository, XSLT_FILE_NAME, IN_XML_FILE_NAME, OUT_XML_FILE_NAME,
				TMP_XML_FILE_NAME, BATCH_SIZE);
		verify(calculator).getSumOfEntriesDataFrom(fileRepository, OUT_XML_FILE_NAME);
	}
}