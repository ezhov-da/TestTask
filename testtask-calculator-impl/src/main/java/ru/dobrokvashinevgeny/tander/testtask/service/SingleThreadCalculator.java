/*
* Copyright (c) 2017 Tander, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.service;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.*;

/**
 * Класс SingleThreadCalculator
 *
 */
public class SingleThreadCalculator implements Calculator {
	@Override
	public long getSumOfEntriesDataFrom(FileStore fileStore, String srcFileName) throws CalculatorException {
		try(BufferedReader srcReader = fileStore.getFileDataReaderByName(srcFileName)) {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			ParseXmlSAXHandler handler = new ParseXmlSAXHandler();
			saxParser.parse(new InputSource(srcReader), handler);
			return handler.getSum();
		} catch (FileStoreException | SAXException | ParserConfigurationException | IOException e) {
			throw new CalculatorException(e);
		}
	}
}

class ParseXmlSAXHandler extends DefaultHandler {
	private static final String XML_ENTRY_NODE = "entry";
	private static final String XML_FIELD_NODE = "field";
	private long sum = 0;
	@Override
	public void startElement(String uri, String localName,String qName,
							 Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase(XML_ENTRY_NODE)) {
			int attrIndex = attributes.getIndex(XML_FIELD_NODE);
			if (attrIndex < 0) {
				throw new SAXException("Missing '" + XML_FIELD_NODE + "' attribute!");
			}

			sum += Integer.parseInt(attributes.getValue(attrIndex));
		}
	}

	public long getSum() {
		return sum;
	}
}