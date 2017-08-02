/*
* Copyright (c) 2017 Eugeny Dobrokvashin, All Rights Reserved.
*/

package ru.dobrokvashinevgeny.tander.accountability;

import java.util.*;

/**
 * @version 1.0 2017
 * @author Evgeny Dobrokvashin
 * Created by Stalker on 26.07.2017.
 */
public class MaterialBuilderContext {
	private final List<Party> partyList;

	private final List<AccountabilityType> accountabilityTypeList;

	private List<Accountability> accountabilityList = new ArrayList<>();

	private Deque<Party> currentPath = new ArrayDeque<>();

	private Party nullParty() {
		return new Party(PartyType.nullValue, "");
	}

	private AccountabilityType nullAccountabilityType() {
		return new AccountabilityType(PartyType.nullValue, PartyType.nullValue);
	}

	public MaterialBuilderContext() {
		partyList = Arrays.asList(
				new Party(PartyType.root, "root/data"),
				new Party(PartyType.SpcCtg_Materials, "SpcCtg_Materials"),
				new Party(PartyType.SpcSec_Products, "SpcSec_Products"),
				new Party(PartyType.Suppliers, "Suppliers"),
				new Party(PartyType.Uom, "UoM"),
				new Party(PartyType.Faset_Wine, "Faset_Wine")
		);

		accountabilityTypeList = Arrays.asList(
				new AccountabilityType(PartyType.root, PartyType.SpcCtg_Materials),
				new AccountabilityType(PartyType.root, PartyType.SpcSec_Products),
				new AccountabilityType(PartyType.root, PartyType.SpcSec_Prom),
				new AccountabilityType(PartyType.root, PartyType.Faset_Wine),
				new AccountabilityType(PartyType.SpcCtg_Materials, PartyType.Suppliers),
				new AccountabilityType(PartyType.SpcCtg_Materials, PartyType.Uom)
		);

		currentPath.push(getPartyByName("root/data"));
	}

	public void material() throws MaterialBuilderContextException {
		final Party materialParty = getPartyByName("SpcCtg_Materials");
		if (!addNewAccountabilityForParty(materialParty)) {
			throw new MaterialBuilderContextException("Can't place material on this level!");
		}
	}

	public void uom() throws MaterialBuilderContextException {
		final Party uomParty = getPartyByName("UoM");
		if (!addNewAccountabilityForParty(uomParty)) {
			throw new MaterialBuilderContextException("Can't place uom on this level!");
		}
	}

	public void product() throws MaterialBuilderContextException {
		final Party productParty = getPartyByName("SpcSec_Products");
		if (!addNewAccountabilityForParty(productParty)) {
			throw new MaterialBuilderContextException("Can't place product on this level!");
		}
	}

	public void supplier() throws MaterialBuilderContextException {
		final Party supplierParty = getPartyByName("Suppliers");
		if (!addNewAccountabilityForParty(supplierParty)) {
			throw new MaterialBuilderContextException("Can't place supplier on this level!");
		}
	}

	public void fasetWine() throws MaterialBuilderContextException {
		final Party fasetWineParty = getPartyByName("Faset_Wine");
		if (!addNewAccountabilityForParty(fasetWineParty)) {
			throw new MaterialBuilderContextException("Can't place fasetWine on this level!");
		}
	}

	private Party getPartyByName(String name) {
		for (Party party : partyList) {
			if (party.getName().equals(name)) {
				return party;
			}
		}

		return nullParty();
	}

	private boolean addNewAccountabilityForParty(Party party) throws MaterialBuilderContextException {
		final AccountabilityType accountabilityTypeToAdd = getAccountabilityTypeWithResponsibleForParty(party);
		if (accountabilityTypeToAdd.equals(nullAccountabilityType())) {
			throw new MaterialBuilderContextException("Can't find AccountabilityType for " + party);
		}

		return correctCurrentPathFor(accountabilityTypeToAdd, party);
	}

	private AccountabilityType getAccountabilityTypeWithResponsibleForParty(Party party) {
		for (AccountabilityType accountabilityType : accountabilityTypeList) {
			if (accountabilityType.isResponsibleFor(party)) {
				return accountabilityType;
			}
		}

		return nullAccountabilityType();
	}

	private boolean correctCurrentPathFor(AccountabilityType accountabilityTypeToAdd, Party party) {
		Party lastParty = currentPath.peek();

		while (null != lastParty) {
			if (accountabilityTypeToAdd.isCommissionerFor(lastParty)) {
				saveNewAccountability(accountabilityTypeToAdd, party, lastParty);
				return true;
			} else {
				currentPath.pop();
			}

			lastParty = currentPath.peek();
		}

		return false;
	}

	private void saveNewAccountability(AccountabilityType accountabilityType, Party responsible, Party party) {
		accountabilityList.add(new Accountability(accountabilityType, party, responsible));
		currentPath.push(responsible);
	}
}