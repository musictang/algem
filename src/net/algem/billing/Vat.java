/*
 * @(#)Vat  2.14.0 07/06/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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

package net.algem.billing;

import net.algem.accounting.Account;
import net.algem.config.Param;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.7.a 17/01/2013
 */
public class Vat
  extends Param
{

  private static final long serialVersionUID = 3078588727265655506L;

  private Account account;

  public Vat(int id, String key, Account account) {
    this.id = id;
    this.key = key;
    this.account = account;
  }

  public Vat() {
  }

  public Vat(Vat t) {
    if (t != null) {
      this.id = t.id;
      this.key = t.key;
      this.account = t.account;
    }
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  public float getRate() {
    try {
      return Float.parseFloat(key);
    } catch(NumberFormatException ex) {
      return 0.0f;
    }
  }

  @Override
  public String getValue() {
    return account != null ? account.getLabel() : null;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
    if (account != null) {
      this.value = account.getLabel();
    }
  }

  @Override
  public String toString() {
    return key;
  }

}
