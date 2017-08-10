/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import org.xml.sax.InputSource;
import ru.dobrokvashinevgeny.tander.testtask.domain.model.entry.*;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.*;
import java.io.*;
import java.util.*;

/**
 * Реализация сервиса преобразования Entries на JAXB
 */
public class JaxbEntryConverterService implements EntryConverterService {
	@Override
	public void convertEntriesToXml(EntryRepository entryRepository, FileRepository fileRepository,
	                                String destXmlFileName, int batchSize)
			throws EntryConverterServiceException {
		try(BufferedWriter destXmlWriter = fileRepository.getFileDataWriterByName(destXmlFileName)) {
			Marshaller marshaller = getJaxbMarshaller();
			writeHeaderTo(destXmlWriter);

			long currentEntryId = 1;
			List<Entry> entries =
					entryRepository.getEntriesFromRange(currentEntryId, getToEntryId(batchSize, currentEntryId));
			while (null != entries) {
				destXmlWriter.write(marshalEntriesBatchToXml(entries, marshaller));

				currentEntryId += batchSize;
				entries = entryRepository.getEntriesFromRange(currentEntryId, getToEntryId(batchSize, currentEntryId));
			}

			writeFooterTo(destXmlWriter);

			destXmlWriter.flush();
		} catch (JAXBException | EntryRepositoryException | FileRepositoryException | IOException e) {
			throw new EntryConverterServiceException(e);
		}
	}

	private Marshaller getJaxbMarshaller() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance( EntryImpl.class );
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		return marshaller;
	}

	private void writeHeaderTo(BufferedWriter destXmlWriter) throws IOException {
		destXmlWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?><entries>");
	}

	private long getToEntryId(int batchSize, long currentEntryId) {
		return currentEntryId + batchSize - 1;
	}

	private String marshalEntriesBatchToXml(List<Entry> entries, Marshaller marshaller) throws JAXBException {
		StringWriter result = new StringWriter();
		for (Entry entry : entries) {
			marshaller.marshal(entry, result);
		}
		result.flush();
		return result.toString();
	}

	private void writeFooterTo(BufferedWriter destXmlWriter) throws IOException {
		destXmlWriter.write("</entries>");
	}

	private class TransformEntriesXml {
		private final FileRepository fileRepository;
		private final String xsltTemplateFileName;
		private final String srcEntriesXmlFileName;
		private final String destXmlFileName;
		private final String tmpXmlFileName;
		private final int batchSize;
		private XMLEventFactory eventFactory;
		private XMLEventWriter destEventWriter;
		private int elementCounter;
		private TransformerFactory transformerFactory;
		private Transformer transformer;
		private XMLInputFactory xmlInputFactory;
		private XMLOutputFactory xmlOutputFactory;
		private XMLEventWriter tmpEventWriter;
		private BufferedWriter tmpXmlWriter;

		public TransformEntriesXml(FileRepository fileRepository, String xsltTemplateFileName, String srcEntriesXmlFileName,
								   String destXmlFileName, String tmpXmlFileName, int batchSize) {
			this.fileRepository = fileRepository;
			this.xsltTemplateFileName = xsltTemplateFileName;
			this.srcEntriesXmlFileName = srcEntriesXmlFileName;
			this.destXmlFileName = destXmlFileName;
			this.tmpXmlFileName = tmpXmlFileName;
			this.batchSize = batchSize;
		}

		public void execute() throws EntryConverterServiceException {
			intFactories();

			try(BufferedReader srcXmlReader = fileRepository.getFileDataReaderByName(srcEntriesXmlFileName);
				BufferedWriter destXmlWriter = fileRepository.getFileDataWriterByName(destXmlFileName);
				BufferedReader xsltTemplateReader = fileRepository.getFileDataReaderByName(xsltTemplateFileName)) {
				initTransformerFrom(xsltTemplateReader);

				XMLEventReader srcEventReader = xmlInputFactory.createXMLEventReader(srcXmlReader);
				destEventWriter = xmlOutputFactory.createXMLEventWriter(destXmlWriter);

				writeStartDocumentToDest(destEventWriter);

				elementCounter = 0;
				try {
					while (srcEventReader.hasNext()) {
						writeSrcXmlEventToDestThroughTmp(srcEventReader.nextEvent(), destEventWriter);
					}

					if (elementCounter <= batchSize) {
						writeEndDocumentToDest(tmpEventWriter);
						transformEntriesXmlFromTmpToDest(destEventWriter);
					}
				} finally {
					if (null != tmpEventWriter) {
						tmpEventWriter.close();
					}

					if (null != tmpXmlWriter) {
						tmpXmlWriter.close();
					}
				}

				writeEndDocumentToDest(destEventWriter);
			} catch (XMLStreamException | FileRepositoryException | IOException | TransformerException e) {
				throw new EntryConverterServiceException(e);
			}
		}

		private void initTransformerFrom(BufferedReader xsltTemplateReader) throws TransformerConfigurationException {
			transformer = transformerFactory.newTransformer(new StreamSource(xsltTemplateReader));
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "no");
		}

		private void intFactories() {
			xmlInputFactory = XMLInputFactory.newInstance();
			xmlOutputFactory = XMLOutputFactory.newInstance();
			xmlOutputFactory.setProperty("escapeCharacters", false);
			eventFactory = XMLEventFactory.newInstance();
			transformerFactory = TransformerFactory.newInstance();
		}

		private void writeStartDocumentToDest(XMLEventWriter destEventWriter)
			throws XMLStreamException {
			destEventWriter.add(eventFactory.createStartDocument());
			destEventWriter.add(eventFactory.createStartElement("", "", "entries"));
			destEventWriter.flush();
		}

		private void writeSrcXmlEventToDestThroughTmp(XMLEvent event, XMLEventWriter destEventWriter)
			throws XMLStreamException, EntryConverterServiceException, FileRepositoryException, IOException {
			if (isStartNewBatch(event)) {
				tmpXmlWriter = fileRepository.getFileDataWriterByName(tmpXmlFileName);
				tmpEventWriter = xmlOutputFactory.createXMLEventWriter(tmpXmlWriter);
				writeStartDocumentToDest(tmpEventWriter);
			}

			switch (event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					if (isEntryStartElement(event)) {
						if (elementCounter++ == batchSize) {
							writeEndDocumentToDest(tmpEventWriter);
							tmpEventWriter.close();
							tmpXmlWriter.close();
							transformEntriesXmlFromTmpToDest(destEventWriter);
							tmpXmlWriter = fileRepository.getFileDataWriterByName(tmpXmlFileName);
							tmpXmlWriter.write("");
							tmpXmlWriter.flush();
							tmpEventWriter = xmlOutputFactory.createXMLEventWriter(tmpXmlWriter);
							writeStartDocumentToDest(tmpEventWriter);
							elementCounter = 0;
						}
					}

					if (isNotEntriesStartElement(event)) {
						writeXmlDataToTmp(tmpEventWriter, event);
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					if (isNotEntriesEndElement(event)) {
						writeXmlDataToTmp(tmpEventWriter, event);
					}
					break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.SPACE:
					writeXmlDataToTmp(tmpEventWriter, event);
					break;
			}
		}

		private boolean isStartNewBatch(XMLEvent event) {
			return 0 == elementCounter && isEntryStartElement(event);
		}

		private boolean isEntryStartElement(XMLEvent event) {
			return event.isStartElement() &&
				event.asStartElement().getName().equals(new QName("entry"));
		}

		private void writeEndDocumentToDest(XMLEventWriter destEventWriter)
			throws XMLStreamException {
			destEventWriter.add(eventFactory.createEndElement("", "", "entries"));
			destEventWriter.add(eventFactory.createEndDocument());
			destEventWriter.flush();
		}

		private void transformEntriesXmlFromTmpToDest(XMLEventWriter destEventWriter) throws
			EntryConverterServiceException {
			StringWriter batchTransformedData = new StringWriter();
			try(BufferedReader tmpXmlReader = fileRepository.getFileDataReaderByName(tmpXmlFileName)) {
				SAXSource source = new SAXSource(new InputSource(tmpXmlReader));
				transformer.transform(source, new StreamResult(batchTransformedData));
				destEventWriter.add(eventFactory.createCharacters(batchTransformedData.toString()));
				destEventWriter.flush();
			} catch (TransformerException | FileRepositoryException | IOException | XMLStreamException e) {
				throw new EntryConverterServiceException("Error at transform file.", e);
			}
		}

		private boolean isNotEntriesStartElement(XMLEvent event) {
			return !event.asStartElement().getName().equals(new QName("entries"));
		}

		private void writeXmlDataToTmp(XMLEventWriter tmpEventWriter, XMLEvent event) throws XMLStreamException {
			tmpEventWriter.add(event);
			tmpEventWriter.flush();
		}

		private boolean isNotEntriesEndElement(XMLEvent event) {
			return !event.asEndElement().getName().equals(new QName("entries"));
		}
	}

	@Override
	public void transformEntriesXml(FileRepository fileRepository, String xsltTemplateFileName, String srcEntriesXmlFileName,
									String destXmlFileName, String tmpXmlFileName, int batchSize)
			throws EntryConverterServiceException {
		new TransformEntriesXml(
			fileRepository, xsltTemplateFileName, srcEntriesXmlFileName,
			destXmlFileName, tmpXmlFileName, batchSize
		).execute();
	}
}