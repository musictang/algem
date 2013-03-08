/*
 * @(#)BankBranch.java	2.6.a 14/09/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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
 * @version 2.6.a
 */
public class BankBranch
        extends Contact
        implements java.io.Serializable
{

  private Bank bank;
  private String branchCode;
  private String domiciliation;

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

  public boolean equals(BankBranch a) {
    return branchCode.equals(a.branchCode)
            && domiciliation.equals(a.domiciliation)
            && getBankCode().equals(a.getBankCode());
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
