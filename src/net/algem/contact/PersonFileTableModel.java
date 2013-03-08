/*
 * @(#)PersonneTableModel.java	2.6.a 18/09/12
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
package net.algem.contact;

import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.0ma
 */
public class PersonFileTableModel
        extends JTableModel
{

  public PersonFileTableModel() {
    super();

    header = new String[]{"id",
                           BundleUtil.getLabel("Person.civility.label"),
                           BundleUtil.getLabel("First.name.label"),
                           BundleUtil.getLabel("Name.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    PersonFile p = (PersonFile) tuples.elementAt(i);
    return p.getId();
  }

  public PersonFile getPersonFile(int i) {
    return (PersonFile) tuples.elementAt(i);
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
    return column > 0;
  }

  @Override
  public Object getValueAt(int ligne, int colonne) {
    PersonFile p = (PersonFile) tuples.elementAt(ligne);
    switch (colonne) {
      case 0:
        return new Integer(p.getId());
      case 1:
        return p.getContact().getGender();
      case 2:
        return p.getContact().getFirstName();
      case 3:
        return p.getContact().getName();
    }
    return null;

  }

  @Override
  public void setValueAt(Object value, int ligne, int column) {
  }
}
