package ru.dobrokvashinevgeny.tander.testtask.service;

import java.io.*;

/**
 * Интерфейс файлового хранилища
 */
public interface FileRepository {
	/**
	 * Получить поток чтения файла из хранилища
	 * @param fileName имя файла
	 * @return поток чтения файла из хранилища
	 * @throws FileRepositoryException если произошла ошибка при получении потока
	 */
	BufferedReader getFileDataReaderByName(String fileName) throws FileRepositoryException;

	/**
	 * Получить поток записи в файл в хранилище
	 * @param fileName имя файла
	 * @return поток записи в файл в хранилище
	 * @throws FileRepositoryException если произошла ошибка при получении потока
	 */
	BufferedWriter getFileDataWriterByName(String fileName) throws FileRepositoryException;
}
