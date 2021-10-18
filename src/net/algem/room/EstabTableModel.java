/*
 * @(#)EstabTableModel.java	2.11.0 27/09/2016
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
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.room;

import net.algem.contact.Person;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 */
public class EstabTableModel
        extends JTableModel<Establishment>
{

  public EstabTableModel() {

    header = new String[]{
      BundleUtil.getLabel("Id.label"),
      BundleUtil.getLabel("Name.label"),
      BundleUtil.getLabel("Active.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    Person p = tuples.get(i).getPerson();
    return p.getId();
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Integer.class;
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
    return column >= 2;
  }

  @Override
  public Object getValueAt(int line, int col) {
    Establishment e = tuples.get(line);
    Person p = e.getPerson();
    switch (col) {
      case 0:
        return p.getId();
      case 1:
        return p.getName();
      case 2:
        return e.isActive();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int col) {
    Establishment e = tuples.get(line);
    if (col == 2) {
      e.setActive((boolean) value);
    }
    modItem(line, e); 
  }
}
