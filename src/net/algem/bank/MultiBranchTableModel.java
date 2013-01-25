/*
 * @(#)MultiBranchTableModel.java	2.6.a 14/09/12
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

import net.algem.contact.Address;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class MultiBranchTableModel
        extends JTableModel
{

  public MultiBranchTableModel() {
    header = new String[]{"id", "Adresse Agence", "Banque", "Multi"};
  }

  @Override
  public int getIdFromIndex(int i) {
    return 0;
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Integer.class;
      case 1:
        return String.class;
      case 2:
        return String.class;
      case 3:
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
  public Object getValueAt(int line, int colonne) {

    BankBranch ag = (BankBranch) tuples.elementAt(line);
    Address adr = ag.getAddress();
    switch (colonne) {
      case 0:
        return new Integer(ag.getId());
      case 1:
        if (adr == null) {
          return "";
        } else {
          return adr.getAdr1() + " " + adr.getCdp() + " " + adr.getCity();
        }
      case 2:
        return ag.getBank().getName();
      case 3:
        return ag.getBank().isMulti();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
  }
}
