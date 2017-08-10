/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.generator.EntryGenerator;
import ru.dobrokvashinevgeny.tander.testtask.infrastructure.persistence.DataSource;

import java.lang.reflect.InvocationTargetException;

/**
 * Класс TestTaskService - сервис приложения TestTaskApp
 */
public class TestTaskService {
	private static final String XSLT_FILE_NAME = "1to2.xslt";
	private static final String IN_XML_FILE_NAME = "1.xml";
	private static final String OUT_XML_FILE_NAME = "2.xml";
	private static final String TMP_XML_FILE_NAME = "tmp.xml";

	private final TestTaskServiceConfig config;
	private final DataSource dataSource;
	private final long n;

	/**
	 * Инициализирует новый сервис приложения
	 * @param n количество генерируемых Entries
	 * @param config данные конфигурации сервиса
	 * @param dataSource источник данных
	 */
	public TestTaskService(long n, TestTaskServiceConfig config,
						   DataSource dataSource) {
		this.n = n;
		this.config = config;
		this.dataSource = dataSource;
	}

	/**
	 * Вычисляет сумму поля field у сгенерериованных Entries
	 * @return сумма поля field у сгенерериованных Entries
	 * @throws TestTaskServiceException если проихошла ошибка во время выполнения сервиса
	 */
	public long calculateSumOfEntriesData() throws TestTaskServiceException {
		try {
			transferFromGeneratorToRepository();

			convertEntriesToXml();

			transformEntriesXml();

			return getSumOfEntriesData();
		} catch (EntryTransferException | CalculatorException | EntryConverterServiceException e) {
			throw new TestTaskServiceException(e);
		}
	}

	private void transferFromGeneratorToRepository()
		throws TestTaskServiceException, EntryTransferException {
		final EntryGenerator entryGenerator = createEntryGenerator();
		final EntryTransfer entryTransfer = createEntryTransfer();
		final EntryRepository entryRepository = createEntryRepository(dataSource);
		entryTransfer.transferFromGeneratorToRepository(entryGenerator, entryRepository, n, config.getBatchSize());
	}

	private EntryGenerator createEntryGenerator() throws TestTaskServiceException {
		return (EntryGenerator) getInstanceWoParamsByClassName(config.getEntryGeneratorImplClassName());
	}

	protected Object getInstanceWoParamsByClassName(String className) throws TestTaskServiceException {
		try {
			return Class.forName(className).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new TestTaskServiceException(e);
		}
	}

	protected EntryRepository createEntryRepository(DataSource dataSource) throws TestTaskServiceException {
		try {
			return
				(EntryRepository) Class.forName(config.getEntryRepositoryImplClassName())
					.getDeclaredConstructor(DataSource.class)
					.newInstance(dataSource);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
			NoSuchMethodException | ClassNotFoundException e) {
			throw new TestTaskServiceException(e);
		}
	}

	private EntryTransfer createEntryTransfer() throws TestTaskServiceException {
		return (EntryTransfer) getInstanceWoParamsByClassName(config.getEntryTransferImplClassName());
	}

	private void convertEntriesToXml() throws TestTaskServiceException, EntryConverterServiceException {
		final EntryRepository entryRepository = createEntryRepository(dataSource);

		final FileRepository fileRepository = createFileStore();

		final EntryConverterService converterService = createEntryConverterService();

		converterService.convertEntriesToXml(entryRepository, fileRepository, IN_XML_FILE_NAME, config.getBatchSize());
	}

	private FileRepository createFileStore() throws TestTaskServiceException {
		return (FileRepository) getInstanceWoParamsByClassName(config.getFileRepositoryImplClassName());
	}

	private EntryConverterService createEntryConverterService() throws TestTaskServiceException {
		return (EntryConverterService) getInstanceWoParamsByClassName(config
				.getEntryConverterImplClassName());
	}

	private void transformEntriesXml() throws TestTaskServiceException, EntryConverterServiceException {
		final FileRepository fileRepository = createFileStore();

		final EntryConverterService converterService = createEntryConverterService();

		converterService.transformEntriesXml(fileRepository, XSLT_FILE_NAME, IN_XML_FILE_NAME, OUT_XML_FILE_NAME,
			TMP_XML_FILE_NAME, config.getBatchSize());
	}

	private long getSumOfEntriesData() throws TestTaskServiceException, CalculatorException {
		final FileRepository fileRepository = createFileStore();

		final Calculator calculator = (Calculator) getInstanceWoParamsByClassName(config.getCalculatorImplClassName());

		return calculator.getSumOfEntriesDataFrom(fileRepository, OUT_XML_FILE_NAME);
	}
}