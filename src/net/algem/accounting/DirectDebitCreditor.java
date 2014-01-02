/*
 * @(#)DirectDebitCreditor.java	2.8.r 30/12/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem.
 * Algem is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.algem.accounting;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.r
 * @since 2.8.r 29/12/13
 */
public class DirectDebitCreditor 
{
	
	private String firmName;
	private String bankHouse;
	private String bankBranch;
	private String account;
	private String nne;
	private String iban;
	private String bic;// bic
	private String ics;

	public DirectDebitCreditor() {
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getBankHouse() {
		return bankHouse;
	}

	public void setBankHouse(String bankHouse) {
		this.bankHouse = bankHouse;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String cdtrAgt) {
		this.bic = cdtrAgt;
	}

	public String getFirmName() {
		return firmName;
	}

	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getIcs() {
		return ics;
	}

	public void setIcs(String ics) {
		this.ics = ics;
	}

	public String getNne() {
		return nne;
	}

	public void setNne(String nne) {
		this.nne = nne;
	}

}
