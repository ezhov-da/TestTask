/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.domain.model.entry;

import org.hamcrest.*;
import org.junit.Test;

/**
 */
public class EntryTest {
	@Test
	public void testToXmlOneOk() throws Exception {
		final long value = 1L;
		EntryImpl entry = new EntryImpl( value );

		String expectedEntryXml = "<entry><field>" + value + "</field></entry>";

		String actualEntryXml = entry.toXml();

		MatcherAssert.assertThat(actualEntryXml, Matchers.equalTo( expectedEntryXml ));
	}

	@Test
	public void testToXmlTwoOk() throws Exception {
		final long value = 2L;
		EntryImpl entry = new EntryImpl( value );

		String expectedEntryXml = "<entry><field>" + value + "</field></entry>";

		String actualEntryXml = entry.toXml();

		MatcherAssert.assertThat(actualEntryXml, Matchers.equalTo( expectedEntryXml ));
	}
}