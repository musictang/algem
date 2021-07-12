/*
 * @(#)RentableObjectTableModel.java	2.17.1 29/08/2019
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
package net.algem.rental;

import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.1
 * @since 2.17.1 29/08/2019
 */
public class RentableObjectTableModel
        extends JTableModel<RentableObject>
{

  public RentableObjectTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Id.label"),
      BundleUtil.getLabel("Instrument.label"),
      BundleUtil.getLabel("Rentable.brand.label"),
      BundleUtil.getLabel("Rentable.identification.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    RentableObject o = tuples.elementAt(i);
    return o.getId();
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
    RentableObject o = tuples.elementAt(line);
    switch (col) {
      case 0:
        return o.getId();
      case 1:
        return o.getType();
      case 2:
        return o.getMarque();
      case 3:
        return o.getIdentification();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
  }
}
