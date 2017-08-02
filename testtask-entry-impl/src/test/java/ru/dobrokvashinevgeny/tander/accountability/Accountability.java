/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.accountability;

public class Accountability {
	private final AccountabilityType accountabilityType;

	private final Party commissioner;

	private final Party responsible;

	Accountability(AccountabilityType accountabilityType, Party commissioner, Party responsible) {
		this.accountabilityType = accountabilityType;
		this.commissioner = commissioner;
		this.responsible = responsible;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Accountability that = (Accountability) o;

		if (!accountabilityType.equals(that.accountabilityType)) return false;
		if (!commissioner.equals(that.commissioner)) return false;
		return responsible.equals(that.responsible);
	}

	@Override
	public int hashCode() {
		int result = accountabilityType.hashCode();
		result = 31 * result + commissioner.hashCode();
		result = 31 * result + responsible.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Accountability{" +
				"accountabilityType=" + accountabilityType +
				", commissioner=" + commissioner +
				", responsible=" + responsible +
				'}';
	}
}