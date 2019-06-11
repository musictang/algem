/*
 * @(#)ItemTableModel.java	2.9.2 26/01/15
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

package net.algem.billing;

import net.algem.util.BundleUtil;
import net.algem.config.Param;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 * @since 2.3.a 30/01/12
 */
public class ItemTableModel
        extends JTableModel<Item>
{

  public ItemTableModel()
  {
    header = new String[]{
      "Id",
      BundleUtil.getLabel("Invoice.item.description.label"),
      BundleUtil.getLabel("Invoice.item.price.label"),
      BundleUtil.getLabel("Account.label"),
      BundleUtil.getLabel("Invoice.item.vat.label"),
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    Item it = tuples.elementAt(i);
		return it.getId();
  }

  @Override
  public Object getValueAt(int ligne, int colonne) {
    Item it = tuples.elementAt(ligne);
		switch (colonne)
		{
			case 0:
				return it.getId();
			case 1:
				return it.getDesignation();
			case 2:
				return it.getPrice();
			case 3:
				return it.getAccount();
      case 4:
				return it.getTax();

		}
		return null;
  }

  @Override
  public Class getColumnClass(int column)
  {
    switch (column) {
      case 0:
        return Integer.class;
      case 1:
        return String.class;
      case 2:
        return Float.class;
      case 3:
        return Integer.class;
      case 4:
        return Param.class;
      default:
        return Object.class;
    }
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
