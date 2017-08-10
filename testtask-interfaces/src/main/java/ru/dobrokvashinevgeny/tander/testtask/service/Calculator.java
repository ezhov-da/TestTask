package ru.dobrokvashinevgeny.tander.testtask.service;

/**
 * Интерфейс сервиса вычислителя
 */
public interface Calculator {

	/**
	 * Вычислить сумму полей field у Entries в XML-файле
	 * @param fileRepository хранилище файлов
	 * @param srcFileName имя исходного XML-файла
	 * @return сумма
	 * @throws CalculatorException если произошла ошибка во время вычисления
	 */
	long getSumOfEntriesDataFrom(FileRepository fileRepository, String srcFileName) throws CalculatorException;
}
