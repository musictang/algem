/*
 * @(#)ModuleTableModel.java	2.12.0 14/03/17
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
 *
 */
package net.algem.course;

import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 * Table model for modules.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 */
public class ModuleTableModel
        extends JTableModel<Module>
{

  public ModuleTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Id.label"),
      BundleUtil.getLabel("Code.label"),
      BundleUtil.getLabel("Name.label"),
      BundleUtil.getLabel("Active.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    Module m = tuples.elementAt(i);
    return m.getId();
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Integer.class;
      case 1:
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
  public Object getValueAt(int line, int col) {
    Module m = tuples.elementAt(line);
    switch (col) {
      case 0:
        return m.getId();
      case 1:
        return m.getCode();
      case 2:
        return m.getTitle();
      case 3:
        return m.isActive();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
  }
}
