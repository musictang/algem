/*
 * @(#)BankBranch.java	2.8.r 03/01/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.bank;

import net.algem.contact.Contact;
import net.algem.contact.Person;

/**
 * Branch of bank.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.r
 */
public class BankBranch
        extends Contact
        implements java.io.Serializable
{

  private Bank bank;
  private String branchCode;
  private String domiciliation;
	/**
	 * BIC identifier.
	 * Must respect the pattern : [A-Z]{6,6}[A-Z2-9][A-NP-Z0-9]([A-Z0-9]{3,3}){0,1}.
	 * The BIC is an ISO 9362 standard. It contains 8 or 11 characters:
	 *
	 * 4 digits: bank code
	 * 2-character ISO country code
	 * 2 characters: location
	 * Possibly three additional characters to define an agency or a branch (branch code)
	 *
	 * The systematic supply BIC enables reliable flow of funds.
	 */
  private String bicCode;
  
  public BankBranch() {
    type = Person.BANK;
    firstName = "";
    gender = "";
  }

  public BankBranch(Person pp) {
    super(pp);
    type = Person.BANK;
    firstName = "";
    gender = "";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final BankBranch other = (BankBranch) obj;
    if ((this.branchCode == null) ? (other.branchCode != null) : !this.branchCode.equals(other.branchCode)) {
      return false;
    }
    if ((this.domiciliation == null) ? (other.domiciliation != null) : !this.domiciliation.equals(other.domiciliation)) {
      return false;
    }
    if ((getBankCode() == null) ? (other.getBankCode() != null) : !getBankCode().equals(other.getBankCode())) {
      return false;
    }
    if ((this.bicCode == null) ? (other.bicCode != null) : !this.bicCode.equals(other.bicCode)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + (this.branchCode != null ? this.branchCode.hashCode() : 0);
    hash = 89 * hash + (this.domiciliation != null ? this.domiciliation.hashCode() : 0);
    hash = 89 * hash + (this.bicCode != null ? this.bicCode.hashCode() : 0);
    hash = 89 * hash + (getBankCode() != null ? getBankCode().hashCode() : 0);
    return hash;
  }

  public void setBank(Bank b) {
    bank = b;
  }

  public Bank getBank() {
    return bank;
  }

  public String getBankCode() {
    if (bank != null) {
      return bank.getCode();
    } else {
      return "";
    }
  }

  public void setCode(String s) {
    branchCode = s;
  }

  public String getCode() {
    return branchCode;
  }

  /**
   * Le name équivaut à la domiciliation pour une agence bancaire.
   * @param s
   */
  public void setDomiciliation(String s) {
    domiciliation = s;
    name = s;
  }

  public String getDomiciliation() {
    return domiciliation;
  }

  public String getBicCode() {
    return bicCode;
  }

  public void setBicCode(String bicCode) {
    this.bicCode = bicCode;
  }

  @Override
  public String getGender() {
    return "";
  }

  @Override
  public String getFirstName() {
    return "";
  }

  @Override
  public boolean isValid() {
    return bank.isValid() && name.length() > 1 && branchCode.length() == 5;
  }
}
