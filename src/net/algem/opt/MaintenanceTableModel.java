/*
 * @(#)MaintenanceTableModel.java	1.0a 07/07/1999
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
package net.algem.opt;

import net.algem.util.ui.JTableModel;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @deprecated
 */
public class MaintenanceTableModel
        extends JTableModel
{

  public MaintenanceTableModel() {
    super();

    header = new String[]{"Jour", "Qui", "Niveau", "Demande", "Fait"};
  }

  @Override
  public int getIdFromIndex(int i) {
    return 0;
  }

  // TableModel Interface
  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
      case 1:
      case 2:
      case 3:
        return String.class;
      case 4:
        return Boolean.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;	//column == 4;
  }

  @Override
  public Object getValueAt(int ligne, int colonne) {
    Maintenance v = (Maintenance) tuples.elementAt(ligne);
    switch (colonne) {
      case 0:
        return v.getJour().toString();
      case 1:
        return v.getPersonne();
      case 2:
        return Maintenance.types[v.getType()];
      case 3:
        return v.getTexte();
      case 4:
        return v.getFait();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int ligne, int column) {
    //System.out.println("MaintenanceTable.setValue v="+value+" l="+ligne+" c="+column);
  }
}
