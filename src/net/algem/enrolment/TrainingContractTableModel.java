/*
 * @(#) TrainingContractTableModel.java Algem 2.15.0 30/08/2017
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
package net.algem.enrolment;

import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 30/08/2017
 */
public class TrainingContractTableModel
        extends JTableModel<TrainingContract>
{

  public TrainingContractTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Id.label"),
      BundleUtil.getLabel("Label.label"),
      BundleUtil.getLabel("Funding.label"),
      BundleUtil.getLabel("Start.label"),
      BundleUtil.getLabel("End.label")
    };
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
      case 4:
        return DateFr.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  @Override
  public int getIdFromIndex(int i) {
    return 0;
  }

  @Override
  public Object getValueAt(int line, int col) {
    TrainingContract t = tuples.elementAt(line);
    switch (col) {
      case 0:
        return t.getId();
      case 1:
        return t.getLabel();
      case 2:
//          Person p = (Person) DataCache.findId(, Model.Person);
//          return p == null ? "" : p.getOrganization() == null ? "" : p.getOrganization().getName();
        return t.getFunding();
      case 3:
        return new DateFr(t.getStart());
      case 4:
        return new DateFr(t.getEnd());
      default:
        return null;
    }
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
  }

}
