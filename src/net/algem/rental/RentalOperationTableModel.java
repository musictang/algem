/*
 * @(#)RentalOperationTableModel.java	2.17.1 29/08/2019
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

import net.algem.accounting.GemAmount;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.1
 * @since 2.17.1
 */
public class RentalOperationTableModel
  extends JTableModel<RentalOperation> {

  private DataCache dataCache;

  /**
   * Rental Operation table model
   *
   * @param dataCache cache
   */
  public RentalOperationTableModel(DataCache dataCache) {
    this.dataCache = dataCache;
      header = new String[4];

    header[0] = BundleUtil.getLabel("Member.label");
    header[1] = BundleUtil.getLabel("Date.From.label");
    header[2] = BundleUtil.getLabel("Date.To.label");
    header[3] = BundleUtil.getLabel("Amount.label");
  }

  @Override
  public int getIdFromIndex(int i) {
    RentalOperation p = tuples.get(i);
    return p.getId();
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
      case 1:
      case 2:
        return String.class;
      case 3:
        return GemAmount.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  @Override
  public Object getValueAt(int ligne, int colonne) {
    RentalOperation o = tuples.get(ligne);
    switch (colonne) {
      case 0:
        return o.getMemberName();
      case 1:
        return o.getStartDate().toString();
      case 2:
        return o.getEndDate().toString();
      case 3:
        return new GemAmount(o.getAmount());
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int ligne, int column) {
  }
}
