/*
 * @(#)JournalAccount.java	2.9.4.13 05/11/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

import net.algem.config.Param;

/**
 * Accounting journal.
 * A journal is a param associated with an account.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.2.a
 */
public class JournalAccount
  extends Param
{
  private static final long serialVersionUID = 5684789480708725622L;
  private String label;
  private Account account;

  public JournalAccount() {
  }

  public JournalAccount(String key, String value) {
    super(key, value);
  }

  public void setAccount(Account c) {
    account = c;
  }

  public Account getAccount() {
    return account;
  }

  public void setLabel(String l) {
    label = l;
  }

  public String getLabel() {
    return label;
  }

}
