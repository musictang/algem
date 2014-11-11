/*
 * @(#)ModuleOrderTableModel.java	2.9.1 10/11/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.enrolment;

import net.algem.accounting.GemAmount;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 * Table model for module order.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 */
public class ModuleOrderTableModel
        extends JTableModel
{

  public ModuleOrderTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Id.label"),
      BundleUtil.getLabel("Module.label"),
      BundleUtil.getLabel("Start.label"),
      BundleUtil.getLabel("End.label"),
      BundleUtil.getLabel("Module.basic.rate.label"),
      BundleUtil.getLabel("Mode.of.payment.label"),
      BundleUtil.getLabel("Hours.label")
//      "NbEch"
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    ModuleOrder m = (ModuleOrder) tuples.elementAt(i);
    return m.getIdOrder();
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Integer.class;
      case 1:
        return String.class;
      case 2:
      case 3:
        return DateFr.class;
      case 4:
        return GemAmount.class;
      case 5:
        return String.class;
      case 6:
        return Hour.class;
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
    ModuleOrder m = (ModuleOrder) tuples.elementAt(ligne);
    switch (colonne) {
      case 0:
        return new Integer(m.getModule());//id du module
      case 1:
        return m.getTitle();
      case 2:
        return m.getStart();
      case 3:
        return m.getEnd();
      case 4:
        return new GemAmount(m.getPrice());
      case 5:
        return m.getModeOfPayment();
      case 6:
        return new Hour(m.getTotalTime());
//      case 7:
//        return new Integer(m.getNOrderLines());
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int ligne, int column) {
  }
}
