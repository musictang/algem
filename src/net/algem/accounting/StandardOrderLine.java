/*
 * @(#) StandardOrderLine.java Algem 2.10.0 19/05/2016
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
 */

package net.algem.accounting;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.9.4.14 18/05/2016
 */
public class StandardOrderLine
{

  protected int id;
  protected String modeOfPayment;
  protected String label;
  protected int amount;
  /** Document number. Numéro de pièce. */
  protected String document = "";
  protected int school;
  protected Account account = new Account();
  /** Cost account. Analytique. */
  protected Account costAccount = new Account();

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getModeOfPayment() {
    return modeOfPayment;
  }

  public void setModeOfPayment(String l) {
    modeOfPayment = l;
  }

public String getLabel() {
    if (label.length() > OrderLineIO.MAX_CHARS_LABEL) {
      return label.substring(0, OrderLineIO.MAX_CHARS_LABEL);
    }
    return label;
  }

  public void setLabel(String s) {
    label = s;
  }


  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public String getDocument() {
    return document;
  }

  public void setDocument(String document) {
    this.document = document;
  }

  public int getSchool() {
    return school;
  }

  public void setSchool(int school) {
    this.school = school;
  }

   public Account getAccount() {
    return account;
  }

  public void setAccount(Account c) {
    account = c;
  }

  public Account getCostAccount() {
    return costAccount;
  }

  public void setCostAccount(Account costAccount) {
    this.costAccount = costAccount;
  }

  public boolean hasSameAccount(StandardOrderLine o) {
    return this.account.equals(o.getAccount());
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + this.id;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final StandardOrderLine other = (StandardOrderLine) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

}
