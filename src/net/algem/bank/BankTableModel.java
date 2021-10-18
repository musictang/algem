/*
 * @(#)BankTableModel.java	2.9.2 26/01/15
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
package net.algem.bank;

import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.9.2
 */
public class BankTableModel
        extends JTableModel<Bank>
{

  public BankTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Code.label"),
      BundleUtil.getLabel("Name.label"),
      BundleUtil.getLabel("Bank.multi.label")};
  }

  @Override
  public int getIdFromIndex(int i) {
    Bank b = tuples.get(i);
    // XXX return m.getId();
    return 0;
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
      case 1:
        return String.class;
      case 2:
        return Boolean.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  @Override
  public Object getValueAt(int line, int col) {
    Bank b = tuples.get(line);
    switch (col) {
      case 0:
        return b.getCode();
      case 1:
        return b.getName();
      case 2:
        return b.isMulti();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
  }
}
