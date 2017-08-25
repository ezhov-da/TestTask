/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import ru.dobrokvashinevgeny.tander.testtask.*;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.DataSource;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;

import java.util.logging.*;

/**
 * Класс TestTaskService - сервис приложения TestTaskApp
 */
public class TestTaskService {
	private final static Logger LOG = Logger.getLogger(TestTaskService.class.getName());
	private static final String XSLT_FILE_NAME = "1to2.xslt";
	private static final String IN_XML_FILE_NAME = "1.xml";
	private static final String OUT_XML_FILE_NAME = "2.xml";
	private static final String TMP_XML_FILE_NAME = "tmp.xml";

	private final AppFactory appFactory;
	private final TestTaskServiceConfig testTaskServiceConfig;
	private final DataSource dataSource;
	private final long n;

	/**
	 * Инициализирует новый сервис приложения
	 * @param n количество генерируемых Entries
	 * @param appFactory данные конфигурации сервиса
	 * @param testTaskServiceConfig
	 * @param dataSource источник данных
	 */
	public TestTaskService(long n, AppFactory appFactory,
						   TestTaskServiceConfig testTaskServiceConfig, DataSource dataSource) {
		this.n = n;
		this.appFactory = appFactory;
		this.testTaskServiceConfig = testTaskServiceConfig;
		this.dataSource = dataSource;
	}

	/**
	 * Вычисляет сумму поля field у сгенерериованных Entries
	 * @return сумма поля field у сгенерериованных Entries
	 * @throws TestTaskServiceException если проихошла ошибка во время выполнения сервиса
	 */
	public long calculateSumOfEntriesData() throws TestTaskServiceException, AppFactoryException {
		try {
			initEntryRepositoryStructure();

			transferFromGeneratorToRepository();

			convertEntriesToXml();

			transformEntriesXml();

			return getSumOfEntriesData();
		} catch (EntryTransferException | CalculatorException | EntryConverterServiceException |
			EntryServiceException e) {
			throw new TestTaskServiceException(e);
		}
	}

	protected void initEntryRepositoryStructure() throws EntryServiceException {
		final EntryService entryService = new EntryService(dataSource);
		entryService.createDataStructure();
	}

	private void transferFromGeneratorToRepository()
		throws TestTaskServiceException, EntryTransferException, AppFactoryException {
		final EntryTransfer entryTransfer = appFactory.createEntryTransfer();
		long beginTime = System.currentTimeMillis();
		entryTransfer.transferFromGeneratorToRepository(appFactory, 1, n, dataSource);
		LOG.log(Level.INFO, "transferFromGeneratorToRepository duration - " + (System.currentTimeMillis() -
			beginTime) + " ms");
	}

	private void convertEntriesToXml()
		throws TestTaskServiceException, EntryConverterServiceException, AppFactoryException {
		final EntryRepository entryRepository = appFactory.createEntryRepository(dataSource);

		final FileRepository fileRepository = appFactory.createFileRepository();

		final EntryConverterService converterService = appFactory.createEntryConverterService();

		converterService.convertEntriesToXml(entryRepository, fileRepository, IN_XML_FILE_NAME,
			testTaskServiceConfig.getConverterBatchSize());
	}

	private void transformEntriesXml() throws TestTaskServiceException, EntryConverterServiceException, AppFactoryException {
		final FileRepository fileRepository = appFactory.createFileRepository();

		final EntryConverterService converterService =appFactory.createEntryConverterService();

		converterService.transformEntriesXml(fileRepository, XSLT_FILE_NAME, IN_XML_FILE_NAME, OUT_XML_FILE_NAME,
			TMP_XML_FILE_NAME, testTaskServiceConfig.getTransformerBatchSize());
	}

	private long getSumOfEntriesData() throws TestTaskServiceException, CalculatorException, AppFactoryException {
		final FileRepository fileRepository = appFactory.createFileRepository();

		final Calculator calculator = appFactory.createCalculator();

		return calculator.getSumOfEntriesDataFrom(fileRepository, OUT_XML_FILE_NAME);
	}
}