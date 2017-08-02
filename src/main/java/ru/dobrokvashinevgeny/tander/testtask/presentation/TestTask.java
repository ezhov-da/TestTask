/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.presentation;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.EntryRepository;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.generator.EntryGenerator;
import ru.dobrokvashinevgeny.tander.testtask.service.*;

import java.util.logging.*;

/**
 * Главный класс приложения
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 18.07.2017.
 */
public class TestTask {
	private final static Logger LOG = Logger.getLogger(TestTask.class.getName());
	private final EntryGenerator entryGenerator;
	private final EntryRepository entryRepository;
	private final EntryTransfer entryTransfer;
	private final EntryConverterService converterService;
	private final Calculator calculator;
	private final long n;
	private final int batchSize;

	public TestTask(EntryGenerator entryGenerator, EntryRepository entryRepository, EntryTransfer entryTransfer,
					EntryConverterService converterService, Calculator calculator, long n, int batchSize) {
		this.entryGenerator = entryGenerator;
		this.entryRepository = entryRepository;
		this.entryTransfer = entryTransfer;
		this.converterService = converterService;
		this.calculator = calculator;
		this.n = n;
		this.batchSize = batchSize;
	}

	public void run() {
		try {
			entryTransfer.transferFromGeneratorToRepository(
					entryGenerator, entryRepository, n, batchSize);

//			converterService.convertEntriesToXml(, );

//			converterService.transformEntriesXml();

//			long sumOfEntries = calculator.getSumOfEntriesDataFrom();
			long sumOfEntries = 0;

			System.out.println("Sum of Entries = " + sumOfEntries);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "TestTask exception", e);
		}
	}
}