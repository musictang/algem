/*
 * @(#)Bic.java	2.6.a 14/09/12
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

import net.algem.accounting.AccountUtil;

/**
 * Bank Identifier Code  (RIB in french).
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class Bic
        implements java.io.Serializable
{

  private int idper;
  private String establishment;
  private String branch;
  private String account;
  private String bicKey;
  private int bnqid;

  public Bic(int _id) {
    idper = _id;
  }

  @Override
  public String toString() {
    return establishment + branch + account + bicKey;
  }

  public boolean equals(Bic r) {
    return (r != null
            && establishment.equals(r.establishment)
            && branch.equals(r.branch)
            && account.equals(r.account)
            && bicKey.equals(r.bicKey)
            && bnqid == r.bnqid);
  }

  public void setId(int i) {
    idper = i;
  }

  public int getId() {
    return idper;
  }

  public void setEstablishment(String s) {
    establishment = s.trim();
  }

  public String getEstablishment() {
    return establishment;
  }

  public void setBranch(String s) {
    branch = s.trim();
  }

  public String getBranch() {
    return branch;
  }

  public void setAccount(String s) {
    account = s.trim();
  }

  public String getAccount() {
    return account;
  }

  public void setBicKey(String s) {
    bicKey = s.trim();
  }

  public String getBicKey() {
    return bicKey;
  }

  public void setBranchId(int i) {
    bnqid = i;
  }

  public int getBranchId() {
    return bnqid;
  }

  public boolean hasCorrectLength() {
    // TODO EG menage sql rib compte à blanc
    return establishment.length() == 5
            && branch.length() == 5
            && account.length() == 11
            && bicKey.length() == 2;
            
  }

  public boolean isValid() {
    return AccountUtil.isBicOk(this.toString());
  }

  public boolean isEmpty() {
    return establishment.length() == 0
            && branch.length() == 0
            && account.length() == 0
            && bicKey.length() == 0;
  }
}
