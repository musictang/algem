/*
 * @(#)MenuTableModel.java	2.8.u 19/05/14
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
package net.algem.security;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import net.algem.util.BundleUtil;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.u
 * @since 2.6.a 01/08/2012
 */
public class MenuTableModel
        extends AbstractTableModel
{

  private String[] header = {
    BundleUtil.getLabel("Id.label"),
    BundleUtil.getLabel("Menu.label"),
    BundleUtil.getLabel("Authorization.label")
  };
  private Vector<MenuAccess> tuples = new Vector<MenuAccess>();
  private int userId;
  private UserService service;

  public MenuTableModel(UserService service) {
    this.service = service;
  }

  void load(int id) {
    userId = id;
    tuples = ((DefaultUserService) service).getMenuAccess(id);
    fireTableChanged(null);
  }

  @Override
  public String getColumnName(int column) {
    return header[column];
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
    return column > 1;
  }

  @Override
  public int getColumnCount() {
    return header.length;
  }

  @Override
  public int getRowCount() {
    return tuples.size();
  }

  @Override
  public Object getValueAt(int line, int col) {
    MenuAccess m = tuples.elementAt(line);
    switch (col) {
      case 0:
        return m.getId();
      case 1:
        return m.getLabel();
      case 2:
        return m.isAuth();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int col) {
    if (col != 2) {
      return;
    }

    MenuAccess m = tuples.elementAt(line);
    m.setAuth((Boolean) value);
    ((DefaultUserService) service).updateAccess(m, getColumnName(col).toLowerCase(), userId);
  }
}
