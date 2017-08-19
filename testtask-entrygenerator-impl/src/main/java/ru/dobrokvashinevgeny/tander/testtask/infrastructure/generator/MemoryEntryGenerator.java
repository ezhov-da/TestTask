/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.generator;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;
import ru.dobrokvashinevgeny.tander.testtask.service.generator.EntryGenerator;

/**
 * Реализация генератора Entries в памяти
 */
public class MemoryEntryGenerator implements EntryGenerator {
	private long currentValue = 1L;

	@Override
	public Entry getNewEntry() {
		return new EntryImpl(getNewValue());
	}

	private long getNewValue() {
		return currentValue++;
	}

	@Override
	public void setCurrentValue(long currentValue) {
		this.currentValue = currentValue;
	}
}