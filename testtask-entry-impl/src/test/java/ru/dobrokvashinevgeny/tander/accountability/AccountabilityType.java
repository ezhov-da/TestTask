/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.accountability;

public class AccountabilityType {
	private final PartyType commissioner;

	private final PartyType responsible;

	AccountabilityType(PartyType commissioner, PartyType responsible) {
		this.commissioner = commissioner;
		this.responsible = responsible;
	}

	public boolean isResponsibleFor(Party party) {
		return this.responsible.equals(party.getPartyType());
	}

	public boolean isCommissionerFor(Party party) {
		return this.commissioner.equals(party.getPartyType());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AccountabilityType that = (AccountabilityType) o;

		if (commissioner != that.commissioner) return false;
		return responsible == that.responsible;
	}

	@Override
	public int hashCode() {
		int result = commissioner.hashCode();
		result = 31 * result + responsible.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "AccountabilityType{" +
				"commissioner=" + commissioner +
				", responsible=" + responsible +
				'}';
	}
}