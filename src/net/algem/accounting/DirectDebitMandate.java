/*
 * @(#)DirectDebitMandate.java	2.8.r 30/12/13
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
class DirectDebitMandate
{

	private int id;
	private int idper;
	private String name;
	private String rum;
	private String dateSign;
	private String iban;
	private String bic;
	private String ics;

	DirectDebitMandate(int idper) {
		this.idper = idper;
	}

	public int getIdper() {
		return idper;
	}

	String getBic() {
		return bic;
	}

	void setBic(String bic) {
		this.bic = bic;
	}

	public String getDateSign() {
		return dateSign;
	}

	public void setDateSign(String dateSign) {
		this.dateSign = dateSign;
	}

	String getIban() {
		return iban;
	}

	void setIban(String iban) {
		this.iban = iban;
	}

	String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	String getRum() {
		return rum;
	}

	void setRum(String rum) {
		this.rum = rum;
	}

	String getIcs() {
		return ics;
	}

	void setIcs(String icsCreditor) {
		this.ics = icsCreditor;
	}
	
	
}
