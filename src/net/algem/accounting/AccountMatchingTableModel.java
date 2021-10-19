/*
 * @(#)AccountMatchingTableModel.java 2.11.0 23/09/16
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 * 
 */
package net.algem.accounting;

import net.algem.config.Param;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 * @since 2.9.4.13 06/10/2015
 */
public class AccountMatchingTableModel
        extends JTableModel<AccountPref>
{

  public AccountMatchingTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Personal.account.label"),
      BundleUtil.getLabel("Revenue.account.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    return ((AccountPref) getItem(i)).getAccount().getId();
  }

  @Override
  public Object getValueAt(int line, int col) {
    AccountPref p = tuples.get(line);
    switch (col) {
      case 0:
        return p.getAccount();
      case 1:
        return p.getCostAccount();
    }
    return null;
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Account.class;
      case 1:
        return Param.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return column == 1;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
    AccountPref p = tuples.get(line);
    switch (column) {
      case 0:
        p.setAccount((Account) value);
        break;
      case 1:
        p.setCostAccount((Param) value);
        break;
    }
  }

}
