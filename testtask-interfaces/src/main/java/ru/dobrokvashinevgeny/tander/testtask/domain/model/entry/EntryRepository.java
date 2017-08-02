/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.testtask.domain.model.entry;

import java.util.List;

/**
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 16.07.2017.
 */
public interface EntryRepository {
	void putEntries(List<Entry> entries) throws EntryRepositoryException;

	List<Entry> getAllEntries() throws EntryRepositoryException;

	List<Entry> getFirstEntries(long numberEntries) throws EntryRepositoryException;

	List<Entry> getEntriesFromRange(long from, long to) throws EntryRepositoryException;

	boolean isEntriesExists() throws EntryRepositoryException;

	void clearEntries() throws EntryRepositoryException;

	void createDataStructure() throws EntryRepositoryException;
}