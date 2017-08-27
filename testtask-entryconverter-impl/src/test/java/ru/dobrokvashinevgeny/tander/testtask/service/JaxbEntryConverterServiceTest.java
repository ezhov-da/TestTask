/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;

import java.io.*;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

/**
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 18.07.2017.
 */
public class JaxbEntryConverterServiceTest {
	private static final String XSLT_FILE_NAME = "1to2.xslt";
	private static final String IN_XML_FILE_NAME = "1.xml";
	private static final String OUT_XML_FILE_NAME = "2.xml";
	private static final String TMP_XML_FILE_NAME = "tmp.xml";
	private static final int BATCH_SIZE = 3;
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	@Test
	public void testConvertOddEntriesToXmlOk() throws Exception {
		EntryConverterService converterService = new JaxbEntryConverterService();
		EntryRepository entryRepository = mock(EntryRepository.class);
		FileRepository fileRepository = mock(FileRepository.class);

		List<List<Entry>> entries = new ArrayList<>();
		int counter = 1;
		long entriesCount = BATCH_SIZE * 3 + 2;
		for(int j = 0; j < entriesCount / BATCH_SIZE; j++) {
			List<Entry> entriesBatch = new ArrayList<>();
			for (long i = 1; i <= BATCH_SIZE; i++) {
				entriesBatch.add(new EntryImpl(counter++));
			}
			entries.add(entriesBatch);
		}

		List<Entry> entriesBatch = new ArrayList<>();
		for (long i = 1; i <= entriesCount % BATCH_SIZE; i++) {
			entriesBatch.add(new EntryImpl(counter++));
		}
		if (entriesBatch.size() > 0) {
			entries.add(entriesBatch);
		}
		entries.add(new ArrayList<>());
		System.out.println(entries);

		final StringWriter writer = new StringWriter();
		when(fileRepository.getFileDataWriterByName(OUT_XML_FILE_NAME)).thenReturn(new BufferedWriter(writer));
		when(entryRepository.getEntriesFromRange(anyLong(), anyLong()))
				.thenAnswer(AdditionalAnswers.returnsElementsOf(entries));
		when(entryRepository.size()).thenReturn(entriesCount);

		StringBuilder expectedEntriesXml = new StringBuilder(XML_HEADER + "<entries>");
		for (int i = 1; i <= entriesCount; i++ ) {
			expectedEntriesXml.append(getEntryXml(i));
		}
		expectedEntriesXml.append("</entries>");

		converterService.convertEntriesToXml(OUT_XML_FILE_NAME, BATCH_SIZE, entryRepository, fileRepository);

		verify(fileRepository).getFileDataWriterByName(OUT_XML_FILE_NAME);
		verify(entryRepository).size();
		verify(entryRepository).getEntriesFromRange(1, 3);
		verify(entryRepository).getEntriesFromRange(4, 6);
		verify(entryRepository).getEntriesFromRange(7, 9);
		verify(entryRepository).getEntriesFromRange(10, 12);
		verifyNoMoreInteractions(fileRepository, entryRepository);
		assertThat(writer.toString(), equalTo(expectedEntriesXml.toString()));
	}

	@Test
	public void testConvertEvenEntriesToXmlOk() throws Exception {
		EntryConverterService converterService = new JaxbEntryConverterService();
		EntryRepository entryRepository = mock(EntryRepository.class);
		FileRepository fileRepository = mock(FileRepository.class);

		List<List<Entry>> entries = new ArrayList<>();
		int counter = 1;
		long entriesCount = BATCH_SIZE * 4;
		for(int j = 0; j < entriesCount / BATCH_SIZE; j++) {
			List<Entry> entriesBatch = new ArrayList<>();
			for (long i = 1; i <= BATCH_SIZE; i++) {
				entriesBatch.add(new EntryImpl(counter++));
			}
			entries.add(entriesBatch);
		}

		entries.add(new ArrayList<>());

		final StringWriter writer = new StringWriter();
		when(fileRepository.getFileDataWriterByName(OUT_XML_FILE_NAME)).thenReturn(new BufferedWriter(writer));
		when(entryRepository.getEntriesFromRange(anyLong(), anyLong()))
				.thenAnswer(AdditionalAnswers.returnsElementsOf(entries));
		when(entryRepository.size()).thenReturn(entriesCount);

		StringBuilder expectedEntriesXml = new StringBuilder(XML_HEADER + "<entries>");
		for (int i = 1; i <= entriesCount; i++ ) {
			expectedEntriesXml.append(getEntryXml(i));
		}
		expectedEntriesXml.append("</entries>");

		converterService.convertEntriesToXml(OUT_XML_FILE_NAME, BATCH_SIZE, entryRepository, fileRepository);

		verify(fileRepository).getFileDataWriterByName(OUT_XML_FILE_NAME);
		verify(entryRepository).size();
		verify(entryRepository).getEntriesFromRange(1, 3);
		verify(entryRepository).getEntriesFromRange(4, 6);
		verify(entryRepository).getEntriesFromRange(7, 9);
		verify(entryRepository).getEntriesFromRange(10, 12);
		verify(entryRepository).getEntriesFromRange(13, 13);
		verifyNoMoreInteractions(fileRepository, entryRepository);
		assertThat(writer.toString(), equalTo(expectedEntriesXml.toString()));
	}

	private String getEntryXml(long field) {
		return "<entry>" +
				"<field>" + field + "</field>" +
				"</entry>";
	}

	@Test
	public void testTransform10EntriesWithBatch5XmlOk() throws Exception {
		EntryConverterService converterService = new JaxbEntryConverterService();
		FileRepository fileRepository = mock(FileRepository.class);
		String xslt = getResourceFileAsString("/" + XSLT_FILE_NAME);

		final String inEntriesXml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<entries>" +
				"<entry><field>1</field></entry>" +
				"<entry><field>2</field></entry>" +
				"<entry><field>3</field></entry>" +
				"<entry><field>4</field></entry>" +
				"<entry><field>5</field></entry>" +
				"<entry><field>6</field></entry>" +
				"<entry><field>7</field></entry>" +
				"<entry><field>8</field></entry>" +
				"<entry><field>9</field></entry>" +
				"<entry><field>10</field></entry>" +
				"</entries>";

		final String tmpFirstBatchData =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<entries>" +
				"<entry><field>1</field></entry>" +
				"<entry><field>2</field></entry>" +
				"<entry><field>3</field></entry>" +
				"<entry><field>4</field></entry>" +
				"<entry><field>5</field></entry>" +
				"</entries>";

		final String tmpSecondBatchData =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<entries>" +
				"<entry><field>6</field></entry>" +
				"<entry><field>7</field></entry>" +
				"<entry><field>8</field></entry>" +
				"<entry><field>9</field></entry>" +
				"<entry><field>10</field></entry>" +
				"</entries>";

		final String expectedOutEntriesXml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<entries>" +
				"<entry field=\"1\"/>" +
				"<entry field=\"2\"/>" +
				"<entry field=\"3\"/>" +
				"<entry field=\"4\"/>" +
				"<entry field=\"5\"/>" +
				"<entry field=\"6\"/>" +
				"<entry field=\"7\"/>" +
				"<entry field=\"8\"/>" +
				"<entry field=\"9\"/>" +
				"<entry field=\"10\"/>" +
				"</entries>";

		StringWriter actualOutEntriesXml = new StringWriter();
		StringWriter tmpXmlWriterFirstBatch = new StringWriter();
		StringWriter tmpXmlWriterSecondBatch = new StringWriter();
		final BufferedReader xsltReaderSpy = spy(new BufferedReader(new StringReader(xslt)));
		when(fileRepository.getFileDataReaderByName(XSLT_FILE_NAME)).thenReturn(xsltReaderSpy);
		final BufferedReader inEntriesXmlReaderSpy = spy(new BufferedReader(new StringReader(inEntriesXml)));
		when(fileRepository.getFileDataReaderByName(IN_XML_FILE_NAME)).thenReturn(inEntriesXmlReaderSpy);
		final BufferedWriter actualOutEntriesXmlSpy = spy(new BufferedWriter(actualOutEntriesXml));
		when(fileRepository.getFileDataWriterByName(OUT_XML_FILE_NAME)).thenReturn(actualOutEntriesXmlSpy);
		final BufferedWriter tmpBufferedWriterFirstBatch = new BufferedWriter(tmpXmlWriterFirstBatch);
		final BufferedWriter tmpBufferedWriterSecondBatch = new BufferedWriter(tmpXmlWriterSecondBatch);
		final BufferedWriter bufferedWriter = new BufferedWriter(new StringWriter());
		when(fileRepository.getFileDataWriterByName(TMP_XML_FILE_NAME))
			.thenReturn(tmpBufferedWriterFirstBatch)
			.thenReturn(tmpBufferedWriterSecondBatch)
			.thenReturn(bufferedWriter);
		doAnswer(new Answer<BufferedReader>() {
			private int callCount = 1;
			@Override
			public BufferedReader answer(InvocationOnMock invocation) throws Throwable {
				if (callCount++ == 1) {
					return new BufferedReader(new StringReader(tmpXmlWriterFirstBatch.toString()));
				}
				else {
					return new BufferedReader(new StringReader(tmpXmlWriterSecondBatch.toString()));
				}
			}
		}).when(fileRepository).getFileDataReaderByName(TMP_XML_FILE_NAME);

		converterService.transformEntriesXml(
			fileRepository, XSLT_FILE_NAME, IN_XML_FILE_NAME, OUT_XML_FILE_NAME, TMP_XML_FILE_NAME, 5);

		verify(fileRepository).getFileDataReaderByName(XSLT_FILE_NAME);
		verify(fileRepository).getFileDataReaderByName(IN_XML_FILE_NAME);
		verify(fileRepository).getFileDataWriterByName(OUT_XML_FILE_NAME);
		verify(fileRepository, times(2)).getFileDataReaderByName(TMP_XML_FILE_NAME);
		verify(fileRepository, times(2)).getFileDataWriterByName(TMP_XML_FILE_NAME);

		/*verify(xsltReaderSpy, times(3)).read(any(), anyInt(), anyInt());
		verify(xsltReaderSpy, times(2)).close();
		verify(inEntriesXmlReaderSpy, times(3)).read(any(char[].class), anyInt(), anyInt());
		verify(inEntriesXmlReaderSpy, times(2)).close();
		verify(actualOutEntriesXmlSpy, times(10)).write(anyString(), anyInt(), anyInt());
		verify(tmpXmlWriterSpy, times(29)).write(anyString(), anyInt(), anyInt());
		verify(tmpXmlWriterSpy, times(18)).flush();*/
		verifyNoMoreInteractions(fileRepository);
		assertThat(actualOutEntriesXml.toString(), equalTo(expectedOutEntriesXml));
		assertThat(tmpXmlWriterFirstBatch.toString(), equalTo(tmpFirstBatchData));
		assertThat(tmpXmlWriterSecondBatch.toString(), equalTo(tmpSecondBatchData));
	}

	@Test
	public void testTransform9EntriesWithBatch5XmlOkNew() throws Exception {
		EntryConverterService converterService = new JaxbEntryConverterService();
		FileRepository fileRepository = mock(FileRepository.class);
		String xslt = getResourceFileAsString("/" + XSLT_FILE_NAME);

		final String inEntriesXml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<entries>" +
				"<entry><field>1</field></entry>" +
				"<entry><field>2</field></entry>" +
				"<entry><field>3</field></entry>" +
				"<entry><field>4</field></entry>" +
				"<entry><field>5</field></entry>" +
				"<entry><field>6</field></entry>" +
				"<entry><field>7</field></entry>" +
				"<entry><field>8</field></entry>" +
				"<entry><field>9</field></entry>" +
				"</entries>";

		final String tmpFirstBatchData =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<entries>" +
				"<entry><field>1</field></entry>" +
				"<entry><field>2</field></entry>" +
				"<entry><field>3</field></entry>" +
				"<entry><field>4</field></entry>" +
				"<entry><field>5</field></entry>" +
				"</entries>";

		final String tmpSecondBatchData =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<entries>" +
				"<entry><field>6</field></entry>" +
				"<entry><field>7</field></entry>" +
				"<entry><field>8</field></entry>" +
				"<entry><field>9</field></entry>" +
				"</entries>";

		final String expectedOutEntriesXml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<entries>" +
				"<entry field=\"1\"/>" +
				"<entry field=\"2\"/>" +
				"<entry field=\"3\"/>" +
				"<entry field=\"4\"/>" +
				"<entry field=\"5\"/>" +
				"<entry field=\"6\"/>" +
				"<entry field=\"7\"/>" +
				"<entry field=\"8\"/>" +
				"<entry field=\"9\"/>" +
				"</entries>";

		StringWriter actualOutEntriesXml = new StringWriter();
		StringWriter tmpXmlWriterFirstBatch = new StringWriter();
		StringWriter tmpXmlWriterSecondBatch = new StringWriter();
		final BufferedReader xsltReaderSpy = spy(new BufferedReader(new StringReader(xslt)));
		when(fileRepository.getFileDataReaderByName(XSLT_FILE_NAME)).thenReturn(xsltReaderSpy);
		final BufferedReader inEntriesXmlReaderSpy = spy(new BufferedReader(new StringReader(inEntriesXml)));
		when(fileRepository.getFileDataReaderByName(IN_XML_FILE_NAME)).thenReturn(inEntriesXmlReaderSpy);
		final BufferedWriter actualOutEntriesXmlSpy = spy(new BufferedWriter(actualOutEntriesXml));
		when(fileRepository.getFileDataWriterByName(OUT_XML_FILE_NAME)).thenReturn(actualOutEntriesXmlSpy);
		final BufferedWriter tmpBufferedWriterFirstBatch = new BufferedWriter(tmpXmlWriterFirstBatch);
		final BufferedWriter tmpBufferedWriterSecondBatch = new BufferedWriter(tmpXmlWriterSecondBatch);
		final BufferedWriter bufferedWriter = new BufferedWriter(new StringWriter());
		when(fileRepository.getFileDataWriterByName(TMP_XML_FILE_NAME))
			.thenReturn(tmpBufferedWriterFirstBatch)
			.thenReturn(tmpBufferedWriterSecondBatch)
			.thenReturn(bufferedWriter);
		doAnswer(new Answer<BufferedReader>() {
			private int callCount = 1;
			@Override
			public BufferedReader answer(InvocationOnMock invocation) throws Throwable {
				if (callCount++ == 1) {
					return new BufferedReader(new StringReader(tmpXmlWriterFirstBatch.toString()));
				}
				else {
					return new BufferedReader(new StringReader(tmpXmlWriterSecondBatch.toString()));
				}
			}
		}).when(fileRepository).getFileDataReaderByName(TMP_XML_FILE_NAME);

		converterService.transformEntriesXml(
			fileRepository, XSLT_FILE_NAME, IN_XML_FILE_NAME, OUT_XML_FILE_NAME, TMP_XML_FILE_NAME, 5);

		verify(fileRepository).getFileDataReaderByName(XSLT_FILE_NAME);
		verify(fileRepository).getFileDataReaderByName(IN_XML_FILE_NAME);
		verify(fileRepository).getFileDataWriterByName(OUT_XML_FILE_NAME);
		verify(fileRepository, times(2)).getFileDataReaderByName(TMP_XML_FILE_NAME);
		verify(fileRepository, times(2)).getFileDataWriterByName(TMP_XML_FILE_NAME);

		/*verify(xsltReaderSpy, times(3)).read(any(), anyInt(), anyInt());
		verify(xsltReaderSpy, times(2)).close();
		verify(inEntriesXmlReaderSpy, times(3)).read(any(char[].class), anyInt(), anyInt());
		verify(inEntriesXmlReaderSpy, times(2)).close();
		verify(actualOutEntriesXmlSpy, times(10)).write(anyString(), anyInt(), anyInt());
		verify(tmpXmlWriterSpy, times(29)).write(anyString(), anyInt(), anyInt());
		verify(tmpXmlWriterSpy, times(18)).flush();*/
		verifyNoMoreInteractions(fileRepository);
		assertThat(actualOutEntriesXml.toString(), equalTo(expectedOutEntriesXml));
		assertThat(tmpXmlWriterFirstBatch.toString(), equalTo(tmpFirstBatchData));
		assertThat(tmpXmlWriterSecondBatch.toString(), equalTo(tmpSecondBatchData));
	}

	private String getResourceFileAsString(String fileName) throws IOException {
		byte[] contents = new byte[1024];
		InputStream xsltImputStream = getClass().getResourceAsStream(fileName);
		int bytesRead = xsltImputStream.read(contents);
		StringBuilder strFileContents = new StringBuilder();
		while(bytesRead != -1) {
			strFileContents.append(new String(contents, 0, bytesRead));
			bytesRead = xsltImputStream.read(contents);
		}
		return strFileContents.toString();
	}
}