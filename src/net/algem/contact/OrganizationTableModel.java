/*
 * @(#) OrganizationTableModel.java Algem 2.15.0 30/07/2017
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
 */
package net.algem.contact;

import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 27/07/2017
 */
public class OrganizationTableModel
  extends JTableModel<Organization> {

  public OrganizationTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Organization.label"),
      BundleUtil.getLabel("Referent.label")
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
        return String.class;
      case 1:
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
    Organization c = tuples.elementAt(line);
    switch (col) {
      case 0:
        return c.getName();
      case 1:
        return c.getReferent();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int col) {
  }

}
