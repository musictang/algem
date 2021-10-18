/*
 * @(#)MultiBranchTableModel.java	2.9.4.3 23/04/15
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

import net.algem.contact.Address;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.3
 */
public class MultiBranchTableModel
        extends JTableModel<BankBranch>
{

  public MultiBranchTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Id.label"),
      BundleUtil.getLabel("Bank.branch.address.label"),
      BundleUtil.getLabel("Bank.label"),
      BundleUtil.getLabel("Bic.code.label"),
      "Multi"
    };
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
      case 2:
      case 3:
        return String.class;
      case 4:
        return Boolean.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return column == 3;
  }

  @Override
  public Object getValueAt(int line, int col) {

    BankBranch bb = tuples.get(line);
    Address adr = bb.getAddress();
    switch (col) {
      case 0:
        return Integer.valueOf(bb.getId());
      case 1:
        if (adr == null) {
          return "";
        } else {
          return adr.getAdr1() + " " + adr.getCdp() + " " + adr.getCity();
        }
      case 2:
        return bb.getBank().getName();
      case 3:
        return bb.getBicCode();
      case 4:
        return bb.getBank().isMulti();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
    BankBranch bb = tuples.get(line);
    switch(column) {
      case 3:
        bb.setBicCode((String)value);
        break;
    }
  }
}
