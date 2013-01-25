/*
 * @(#)OrderTableModel.java	2.6.a 17/09/12
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
package net.algem.enrolment;

import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 * Table model for member order.
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class OrderTableModel
        extends JTableModel
{

  public OrderTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Number.label"),
      BundleUtil.getLabel("Date.label"),
      BundleUtil.getLabel("Id.label"),
      BundleUtil.getLabel("Name.label"),
      BundleUtil.getLabel("First.name.label"),
      BundleUtil.getLabel("Payer.label")
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
        return DateFr.class;
      case 2:
        return Integer.class;
      case 3:
      case 4:
        return String.class;
      case 5:
        return Integer.class;
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
    MemberOrder cmd = (MemberOrder) tuples.elementAt(line);
    switch (col) {
      case 0:
        return new Integer(cmd.getId());
      case 1:
        return cmd.getCreation();
      case 2:
        return new Integer(cmd.getMember());
      case 3:
        return cmd.getMemberName();
      case 4:
        return cmd.getMemberFirstname();
      case 5:
        return new Integer(cmd.getPayer());
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
  }
}
