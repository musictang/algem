/*
 * @(#)BranchTableModel.java	2.8.i 08/07/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.i
 */
public class BranchTableModel
        extends JTableModel
{

  public BranchTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Id.label"), 
      BundleUtil.getLabel("Bank.label"),
      BundleUtil.getLabel("Bank.branch.label"),
      "Domiciliation",
      BundleUtil.getLabel("Bic.code.label"),
      BundleUtil.getLabel("Address.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    BankBranch p = (BankBranch) tuples.elementAt(i);
    return p.getId();
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Integer.class;
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
        return String.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return column > 0;
  }

  @Override
  public Object getValueAt(int ligne, int colonne) {
    BankBranch a = (BankBranch) tuples.elementAt(ligne);
    switch (colonne) {
      case 0:
        return new Integer(a.getId());
      case 1:
        return a.getBank().getCode();
      case 2:
        return a.getCode();
      case 3:
        return a.getDomiciliation();
      case 4:
        return a.getBicCode();
      case 5:
        Address adr = a.getAddress();
        return adr == null ? "Pas d'adresse !!" : adr;
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int ligne, int column) {
  }
}
