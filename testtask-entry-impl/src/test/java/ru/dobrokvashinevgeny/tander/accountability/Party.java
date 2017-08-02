/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.accountability;

public class Party {
	private final PartyType partyType;

	private final String name;

	public Party(PartyType partyType, String name) {
		this.partyType = partyType;
		this.name = name;
	}

	/**
	 * Возвращает
	 *
	 * @return
	 */
	public PartyType getPartyType() {
		return partyType;
	}

	/**
	 * Возвращает
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Party party = (Party) o;

		if (partyType != party.partyType) return false;
		return name.equals(party.name);
	}

	@Override
	public int hashCode() {
		int result = partyType.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Party{" +
				"partyType=" + partyType +
				", name='" + name + '\'' +
				'}';
	}
}