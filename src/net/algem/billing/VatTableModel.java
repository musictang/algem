/*
 * @(#)VatTableModel  2.14.0 07/06/17
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

package net.algem.billing;

import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.3.c 12/03/12
 */
public class VatTableModel
        extends JTableModel<Vat>
{

  public VatTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Rate.label"),
      BundleUtil.getLabel("Account.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    return getItem(i).getId();
  }

   @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  @Override
  public Object getValueAt(int line, int col) {
    Vat t = tuples.elementAt(line);
    switch (col) {
      case 0:
        return t.getKey();
      case 1:
        return t.getAccount();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {

  }


}
