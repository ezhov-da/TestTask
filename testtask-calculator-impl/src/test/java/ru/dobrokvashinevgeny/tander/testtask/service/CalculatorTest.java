/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import org.junit.Test;

import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

/**
 * Класс CalculatorTest
 *
 * @version 1.0
 */
public class CalculatorTest {
	private static final String IN_XML_FILE_NAME = "2.xml";

	@Test
	public void testCalculatorOk() throws Exception {
		FileStore fileStore = mock(FileStore.class);

		final String inEntriesXml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<entries>" +
					"<entry field=\"1\"/>" +
					"<entry field=\"2\"/>" +
					"<entry field=\"3\"/>" +
					"<entry field=\"4\"/>" +
				"</entries>";
		final BufferedReader inEntriesXmlReaderSpy = spy(new BufferedReader(new StringReader(inEntriesXml)));
		when(fileStore.getFileDataReaderByName(IN_XML_FILE_NAME)).thenReturn(inEntriesXmlReaderSpy);

		Calculator calculator = new SingleThreadCalculator();

		long result = calculator.getSumOfEntriesDataFrom(fileStore, IN_XML_FILE_NAME);

		verify(fileStore).getFileDataReaderByName(IN_XML_FILE_NAME);
		verify(inEntriesXmlReaderSpy, atLeastOnce()).read(any(char[].class), anyInt(), anyInt());
		verify(inEntriesXmlReaderSpy).close();
		verifyNoMoreInteractions(fileStore);
		assertThat(result, equalTo(10L));
	}
}