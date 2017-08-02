package ru.dobrokvashinevgeny.tander.testtask.domain.model.entry;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.*;

/**
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 19.07.2017.
 * @version 1.0 2017
 */
@XmlRootElement(name = "entry")
public interface Entry {
	@XmlElement(name = "field")
	long getValue();

	String toXml() throws JAXBException;
}
