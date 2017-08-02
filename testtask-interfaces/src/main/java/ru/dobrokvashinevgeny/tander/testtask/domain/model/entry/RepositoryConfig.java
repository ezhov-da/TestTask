package ru.dobrokvashinevgeny.tander.testtask.domain.model.entry;

/**
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 23.07.2017.
 * @version 1.0 2017
 */
public interface RepositoryConfig {
	String getConnectionUrl();

	String getUserName();

	String getUserPsw();
}
