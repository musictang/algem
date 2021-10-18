/*
 * @(#)DDMandateTableModel.java 2.9.2 26/01/15
 *
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
package net.algem.accounting;

import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;

/**
 * Direct debit mandate table model.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.9.2
 * @since 2.8.r 08/01/14
 */
public class DDMandateTableModel
        extends JTableModel<DDMandate>
{

  public DDMandateTableModel() {
    header = new String[]{
      BundleUtil.getLabel("Payer.label"),
      BundleUtil.getLabel("Direct.debit.last.label"),
      BundleUtil.getLabel("Direct.debit.signature.label"),
      BundleUtil.getLabel("Direct.debit.seq.type.label"),
      BundleUtil.getLabel("Direct.debit.rum.label"),
      BundleUtil.getLabel("Recurrent.label")
    };


  }

  @Override
  public int getIdFromIndex(int i) {
    return ((DDMandate) getItem(i)).getId();
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Integer.class;
      case 1:
      case 2:
        return DateFr.class;
      case 3:
        return DDSeqType.class;
      case 4:
        return String.class;
      case 5:
        return Boolean.class;
      default:
        return Object.class;
    }
  }

  @Override
  public Object getValueAt(int line, int col) {
    DDMandate dd = tuples.get(line);
    switch (col) {
      case 0:
        return dd.getIdper();
      case 1:
        return new DateFr(dd.getLastDebit());
      case 2:
        return new DateFr(dd.getDateSign());
      case 3:
        return dd.getSeqType();
      case 4:
        return dd.getRum();
      case 5:
        return dd.isRecurrent();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
//    throw new UnsupportedOperationException("Not supported yet.");
  }
}
