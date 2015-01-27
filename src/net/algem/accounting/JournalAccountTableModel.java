/*
 * @(#)JournalAccountTableModel.java	2.9.2 26/01/15
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

import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 */
public class JournalAccountTableModel
        extends JTableModel<JournalAccount>
{

  public JournalAccountTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Code.label"),
      BundleUtil.getLabel("Label.label"),
      BundleUtil.getLabel("Account.label")};
  }

  @Override
  public int getIdFromIndex(int i) {
    return 0;
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
      case 1:
      case 2:
        return String.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  @Override
  public Object getValueAt(int ligne, int colonne) {
    JournalAccount j = tuples.elementAt(ligne);
    switch (colonne) {
      case 0:
        return j.getValue();
      case 1:
        return j.getLabel();
      case 2:
        return j.getAccount();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int ligne, int column) {
  }
}

