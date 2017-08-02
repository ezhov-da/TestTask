/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.domain.model.entry;

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import java.io.StringWriter;

/**
 * Объект данных для передачи данных тестового задания
 * @version 1.0 July 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 16.07.2017.
 */
@XmlRootElement(name = "entry")
public class EntryImpl implements Entry {
	private long value;

	private EntryImpl() { /* For JAXB Only! */ }

	/**
	 * Создает новый экземпляр объекта данных
	 * @param value значение содержимого объекта данных
	 */
	public EntryImpl(long value) {
		this.value = value;
	}

	@Override
	@XmlElement(name = "field")
	public long getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EntryImpl entry = (EntryImpl) o;

		return value == entry.value;
	}

	@Override
	public int hashCode() {
		return new Long(value).hashCode();
	}

	@Override
	public String toString() {
		return "EntryImpl{" +
				"value=" + value +
				'}';
	}

	@Override
	public String toXml() throws JAXBException {
		Marshaller marshaller = getJaxbMarshaller();
		StringWriter result = new StringWriter();

		marshaller.marshal( this, result);

		return result.toString();
	}

	private Marshaller getJaxbMarshaller() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance( EntryImpl.class );
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		return marshaller;
	}
}