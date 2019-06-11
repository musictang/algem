/*
 * @(#)RehearsalPassTableModel.java 2.9.4.12 01/09/15
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
package net.algem.contact.member;

import java.text.NumberFormat;
import net.algem.planning.Hour;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.12
 */
public class RehearsalPassTableModel
        extends JTableModel<RehearsalPass>
{

  NumberFormat nf = NumberFormat.getInstance();
  
  public RehearsalPassTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Id.label"),
      BundleUtil.getLabel("Label.label"),
      BundleUtil.getLabel("Amount.label"),
      BundleUtil.getLabel("Duration.min.label"),
      BundleUtil.getLabel("Total.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    RehearsalPass pass = tuples.elementAt(i);
    return pass.getId();
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Integer.class;
      case 1:
        return String.class;
      case 2:
        return Float.class;
      case 3:
        return Hour.class;
      case 4:
        return String.class;
      default:
        return Object.class;
    }
  }

  @Override
  public Object getValueAt(int row, int col) {
    RehearsalPass pass = tuples.elementAt(row);
    switch (col) {
      case 0:
        return pass.getId();
      case 1:
        return pass.getLabel();
      case 2:
        return pass.getAmount();
      case 3:
        return new Hour(pass.getMin());
      case 4:
//        return new Hour(pass.getTotalTime());
        return nf.format(pass.getTotalTime() / 60d);
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int row, int col) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }
}
