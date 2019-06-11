/*
 * @(#)AccountChoice.java	2.7.a 14/01/13
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
package net.algem.accounting;

import java.util.Vector;
import net.algem.util.model.GemList;
import net.algem.util.ui.GemChoice;

/**
 * ComboBox for account choice.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.3.c 08/03/12
 */
public class AccountChoice
        extends GemChoice
{

  public AccountChoice(Vector<Account> accounts) {
    super(accounts);
  }

  public AccountChoice(GemList<Account> list) {
    super(new AccountChoiceActiveModel(list));
  }

  @Override
  public int getKey() {
    int key = ((Account) getSelectedItem()).getId();
    return key;
  }

  @Override
  public void setKey(int k) {
    for (int i = 0; i < getItemCount(); i++) {
      if (((Account) getItemAt(i)).getId() == k) {
        setSelectedIndex(i);
        break;
      }
    }
  }

}
