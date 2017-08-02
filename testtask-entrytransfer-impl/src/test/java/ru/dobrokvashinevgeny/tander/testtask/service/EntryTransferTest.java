/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import org.junit.*;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.generator.*;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.*;

/**
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 18.07.2017.
 */
public class EntryTransferTest {
	private final int numberOfEntriesToTransfer = 5;
	private final int entriesBatchSize = 2;

	private EntryTransfer entryTransfer;
	private EntryGenerator generator;
	private EntryRepository repository;

	@Before
	public void setUp() throws Exception {
		entryTransfer = new SingleThreadEntryTransfer();
		generator = Mockito.mock(EntryGenerator.class);
		repository = Mockito.mock(EntryRepository.class);
	}

	@Test
	public void testTransferFromGeneratorToRepositoryOk() throws Exception {
		List<Integer> entriesSizes = new ArrayList<>();
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] arguments = invocation.getArguments();
				if (null != arguments && arguments.length == 1 && null != arguments[0]) {
					List<Entry> entries = (List<Entry>) arguments[0];
					entriesSizes.add(entries.size());
				}

				return null;
			}
		}).when(repository).putEntries(anyListOf(Entry.class));


		entryTransfer.transferFromGeneratorToRepository(
				generator, repository, numberOfEntriesToTransfer, entriesBatchSize
		);

		InOrder inOrder = inOrder(generator, repository);
		inOrder.verify(generator, times(entriesBatchSize)).getNewEntry();
		inOrder.verify(repository).putEntries(anyListOf(Entry.class));
		inOrder.verify(generator, times(entriesBatchSize)).getNewEntry();
		inOrder.verify(repository).putEntries(anyListOf(Entry.class));
		inOrder.verify(generator).getNewEntry();
		inOrder.verify(repository).putEntries(anyListOf(Entry.class));

		assertEquals(entriesBatchSize, entriesSizes.get(0).intValue());
		assertEquals(entriesBatchSize, entriesSizes.get(1).intValue());

		final int sizeOfTheEntriesLastBatch = numberOfEntriesToTransfer % entriesBatchSize;
		assertEquals(sizeOfTheEntriesLastBatch, entriesSizes.get(2).intValue());

		verifyNoMoreInteractions(generator, repository);
	}

	@Test(expected = EntryTransferException.class)
	public void testTransferFromGeneratorToRepositoryGeneratorFail() throws Exception {
		when(generator.getNewEntry())
				.thenReturn(mock(Entry.class))
				.thenThrow(EntryGeneratorException.class);

		entryTransfer.transferFromGeneratorToRepository(
				generator, repository, numberOfEntriesToTransfer, entriesBatchSize
		);

		InOrder inOrder = inOrder(generator, repository);
		inOrder.verify(generator).getNewEntry();
		verifyNoMoreInteractions(generator, repository);
	}

	@Test(expected = EntryTransferException.class)
	public void testTransferFromGeneratorToRepositoryRepositoryFail() throws Exception {
		final Entry entry = mock(Entry.class);
		when(generator.getNewEntry())
				.thenReturn(entry)
				.thenReturn(entry)
				.thenReturn(entry);
		doThrow(EntryRepositoryException.class)
				.doNothing()
				.when(repository).putEntries(anyListOf(Entry.class));

		entryTransfer.transferFromGeneratorToRepository(
				generator, repository, numberOfEntriesToTransfer, entriesBatchSize
		);

		InOrder inOrder = inOrder(generator, repository);
		inOrder.verify(generator).getNewEntry();
		verifyNoMoreInteractions(generator, repository);
	}
}