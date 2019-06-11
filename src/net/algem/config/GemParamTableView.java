/*
 * @(#)GemParamTableView.java 2.14.0 08/06/17
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

package net.algem.config;

import javax.swing.table.TableColumnModel;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.5.a 22/06/2012
 */
public class GemParamTableView
  extends ParamTableView
{

   public GemParamTableView(String title, JTableModel model) {
    super(title, model, 1);
  }

  @Override
  public void setColumnModel() {
    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(25);
    cm.getColumn(1).setPreferredWidth(25);
    cm.getColumn(2).setPreferredWidth(400);
  }

  @Override
  public void addRow(Param p) {
    if (p instanceof GemParam) {
      tableModel.addItem((GemParam) p);
    }
  }

}
