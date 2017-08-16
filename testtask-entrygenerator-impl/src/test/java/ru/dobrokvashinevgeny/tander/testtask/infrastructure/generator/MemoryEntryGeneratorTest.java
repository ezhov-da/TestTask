/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.infrastructure.generator;

import org.hamcrest.*;
import org.junit.Test;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;
import ru.dobrokvashinevgeny.tander.testtask.service.generator.EntryGenerator;

import java.util.*;

/**
 * MemoryEntryGeneratorTest
 */
public class MemoryEntryGeneratorTest {
	@Test
	public void testFirstValueIsOne() throws Exception {
		EntryGenerator entryGenerator = new MemoryEntryGenerator();

		Entry entry = entryGenerator.getNewEntry();

		MatcherAssert.assertThat( entry, Matchers.is(new EntryImpl(1L)));
	}

	@Test
	public void testSequenceIsArithmeticProgressionWithDifferenceEqOne() throws Exception {
		EntryGenerator entryGenerator = new MemoryEntryGenerator();

		List<Long> differences = new ArrayList<>();
		Entry priorEntry = entryGenerator.getNewEntry();
		final int sequenceCount = 10;
		for (int i = 1; i <= sequenceCount; i++) {
			Entry nextEntry = entryGenerator.getNewEntry();
			differences.add( nextEntry.getValue() - priorEntry.getValue());
			priorEntry = nextEntry;
		}

		long sumOfDifferences = getSumOfDifferences(differences);

		MatcherAssert.assertThat(sumOfDifferences / sequenceCount, Matchers.is(1L));
	}

	private long getSumOfDifferences(List<Long> differences) {
		long result = 0L;
		for (Long difference : differences) {
			result = result + difference;
		}
		return result;
	}
}