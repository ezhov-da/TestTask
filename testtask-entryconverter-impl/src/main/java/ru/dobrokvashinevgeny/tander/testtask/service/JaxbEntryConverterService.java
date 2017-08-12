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
			while (null != entries && entries.size() > 0) {
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

	@Override
	public void transformEntriesXml(FileRepository fileRepository, String xsltTemplateFileName, String srcEntriesXmlFileName,
									String destXmlFileName, String tmpXmlFileName, int batchSize)
			throws EntryConverterServiceException {
		new TransformEntriesXmlByBatch(
			fileRepository, xsltTemplateFileName, srcEntriesXmlFileName,
			destXmlFileName, tmpXmlFileName, batchSize
		).execute();
	}

	private class TransformEntriesXmlByBatch {
		private final FileRepository fileRepository;
		private final String xsltTemplateFileName;
		private final String srcEntriesXmlFileName;
		private final String destXmlFileName;
		private final String tmpXmlFileName;
		private final int batchSize;
		private XMLEventFactory eventFactory;
		private XMLEventWriter destEventWriter;
		private int entryCounter;
		private TransformerFactory transformerFactory;
		private Transformer transformer;
		private XMLInputFactory xmlInputFactory;
		private XMLOutputFactory xmlOutputFactory;
		private XMLEventWriter tmpEventWriter;
		private BufferedWriter tmpXmlWriter;
		private boolean isNotStopProcessSrcXml = true;

		public TransformEntriesXmlByBatch(FileRepository fileRepository, String xsltTemplateFileName, String srcEntriesXmlFileName,
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

				transformXmlFromSrcToDest(srcXmlReader, destXmlWriter);
			} catch (XMLStreamException | FileRepositoryException | IOException | TransformerException e) {
				throw new EntryConverterServiceException(e);
			}
		}

		private void intFactories() {
			xmlInputFactory = XMLInputFactory.newInstance();
			xmlOutputFactory = XMLOutputFactory.newInstance();
			xmlOutputFactory.setProperty("escapeCharacters", false);
			eventFactory = XMLEventFactory.newInstance();
			transformerFactory = TransformerFactory.newInstance();
		}

		private void initTransformerFrom(BufferedReader xsltTemplateReader) throws TransformerConfigurationException {
			transformer = transformerFactory.newTransformer(new StreamSource(xsltTemplateReader));
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "no");
		}

		private void transformXmlFromSrcToDest(BufferedReader srcXmlReader, BufferedWriter destXmlWriter)
			throws XMLStreamException, EntryConverterServiceException, FileRepositoryException, IOException {
			XMLEventReader srcEventReader = xmlInputFactory.createXMLEventReader(srcXmlReader);
			destEventWriter = xmlOutputFactory.createXMLEventWriter(destXmlWriter);

			writeStartDocumentToDest(destEventWriter);

			try {
				while (srcEventReader.hasNext() && isNotStopProcessSrcXml) {
					processSrcXmlEvent(srcEventReader.nextEvent(), destEventWriter);
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
		}

		private void writeStartDocumentToDest(XMLEventWriter destEventWriter)
			throws XMLStreamException {
			destEventWriter.add(eventFactory.createStartDocument());
			destEventWriter.add(eventFactory.createStartElement("", "", "entries"));
			destEventWriter.flush();
		}

		private void processSrcXmlEvent(XMLEvent event, XMLEventWriter destEventWriter)
			throws XMLStreamException, EntryConverterServiceException, FileRepositoryException, IOException {
			switch (event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					processSrcStartElementEvent(event);
					break;
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.SPACE:
					writeXmlDataToTmp(tmpEventWriter, event);
					break;
				case XMLStreamConstants.END_ELEMENT:
					processSrcEndElementEvent(event, destEventWriter);
					break;
			}
		}

		private void processSrcStartElementEvent(XMLEvent event) throws FileRepositoryException, XMLStreamException {
			if (isEntriesStartElement(event)) {
				entryCounter = 0;
			} else if (isEntryStartElement(event)) {
				if (0 == entryCounter) {
					tmpXmlWriter = fileRepository.getFileDataWriterByName(tmpXmlFileName);
					tmpEventWriter = xmlOutputFactory.createXMLEventWriter(tmpXmlWriter);
					writeStartDocumentToDest(tmpEventWriter);
				}
				writeXmlDataToTmp(tmpEventWriter, event);
			} else {
				writeXmlDataToTmp(tmpEventWriter, event);
			}
		}

		private boolean isEntriesStartElement(XMLEvent event) {
			return event.asStartElement().getName().equals(new QName("entries"));
		}

		private boolean isEntryStartElement(XMLEvent event) {
			return event.isStartElement() &&
				event.asStartElement().getName().equals(new QName("entry"));
		}

		private void writeXmlDataToTmp(XMLEventWriter tmpEventWriter, XMLEvent event) throws XMLStreamException {
			tmpEventWriter.add(event);
			tmpEventWriter.flush();
		}

		private void processSrcEndElementEvent(XMLEvent event, XMLEventWriter destEventWriter)
			throws XMLStreamException, IOException, EntryConverterServiceException {
			if (isEntryEndElement(event)) {
				writeXmlDataToTmp(tmpEventWriter, event);
				if (++entryCounter == batchSize) {
					entryCounter = 0;
					writeEndDocumentToDest(tmpEventWriter);
					tmpEventWriter.close();
					tmpXmlWriter.close();
					transformEntriesXmlFromTmpToDest(destEventWriter);
				}
			} else if (isEntriesEndElement(event)) {
				if (entryCounter > 0) {
					writeEndDocumentToDest(tmpEventWriter);
					tmpEventWriter.close();
					tmpXmlWriter.close();
					transformEntriesXmlFromTmpToDest(destEventWriter);
				}
				isNotStopProcessSrcXml = false;
			} else {
				writeXmlDataToTmp(tmpEventWriter, event);
			}
		}

		private boolean isEntryEndElement(XMLEvent event) {
			return event.isEndElement() &&
				event.asEndElement().getName().equals(new QName("entry"));
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

		private boolean isEntriesEndElement(XMLEvent event) {
			return event.asEndElement().getName().equals(new QName("entries"));
		}
	}
}