/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.presentation;

import org.junit.Test;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.generator.EntryGenerator;
import ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.service.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static ru.dobrokvashinevgeny.tander.testtask.infrastructure.configuration.AppConfiguration.*;

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
				return "generator";
			}

			@Override
			public String getEntryRepositoryImplClassName() {
				return "entryRep";
			}

			@Override
			public String getEntryTransferImplClassName() {
				return "transfer";
			}

			@Override
			public String getEntryConverterImplClassName() {
				return "converter";
			}

			@Override
			public String getFileRepositoryImplClassName() {
				return "filerep";
			}

			@Override
			public String getCalculatorImplClassName() {
				return "calculator";
			}

			@Override
			public int getBatchSize() {
				return BATCH_SIZE;
			}
		};

		TestTaskService testTaskService = new TestTaskService(N, testTaskServiceConfig, dataSource) {
			@Override
			protected Object getInstanceWoParamsByClassName(String className) throws TestTaskServiceException {
				switch (className) {
					case "generator": return entryGenerator;
					case "entryRep": return entryRepository;
					case "transfer": return entryTransfer;
					case "converter": return converterService;
					case "filerep": return fileRepository;
					case "calculator": return calculator;
				}
				return null;
			}

			@Override
			protected EntryRepository createEntryRepository(DataSource dataSource) throws TestTaskServiceException {
				return entryRepository;
			}
		};

		testTaskService.calculateSumOfEntriesData();

		verify(entryTransfer)
				.transferFromGeneratorToRepository(
					entryGenerator, entryRepository, N, BATCH_SIZE);
		verify(converterService)
			.convertEntriesToXml(entryRepository, fileRepository, IN_XML_FILE_NAME, BATCH_SIZE);
		verify(converterService)
			.transformEntriesXml(fileRepository, XSLT_FILE_NAME, IN_XML_FILE_NAME, OUT_XML_FILE_NAME,
				TMP_XML_FILE_NAME, BATCH_SIZE);
		verify(calculator).getSumOfEntriesDataFrom(fileRepository, OUT_XML_FILE_NAME);
	}
}