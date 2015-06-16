/*
 * @(#)PriceCellRenderer.java 2.9.4.7 08/06/2015
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 * 
 */
package net.algem.util.ui;

import java.awt.Component;
import java.text.NumberFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import net.algem.accounting.AccountUtil;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.7
 * @since 2.9.4.7 08/06/2015
 */
public class PriceCellRenderer
        extends DefaultTableCellRenderer
{
  NumberFormat nf = AccountUtil.getNumberFormat(2, 2);

  @Override
  public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component c = super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, column);
    if (c instanceof JLabel && value instanceof Number) {
      JLabel label = (JLabel) c;
      label.setHorizontalAlignment(JLabel.RIGHT);
      Number num = (Number) value;
      String text = nf.format(num);
      label.setText(text);
    }
    return c;
  }
}
