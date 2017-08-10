package ru.dobrokvashinevgeny.tander.testtask.domain.model.entry;

import ru.dobrokvashinevgeny.tander.testtask.domain.model.DomainObject;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.*;

/**
 * Интерфейс Entry
 */
@XmlRootElement(name = "entry")
public interface Entry extends DomainObject {
	@XmlElement(name = "field")
	long getValue();

	/**
	 * Получить XML-представление Entry
	 * @return XML-представление Entry
	 * @throws JAXBException если произошла ошибка во время получения представления
	 */
	String toXml() throws JAXBException;
}
