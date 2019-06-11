/*
 * @(#)AccountTableView.java	2.14.0 08/06/17
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

package net.algem.accounting;

import javax.swing.table.TableColumnModel;
import net.algem.util.ui.JTableModel;
import net.algem.config.ParamTableView;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.3.c 09/03/12
 */
public class AccountTableView
  extends ParamTableView
  {

  public AccountTableView(String title, JTableModel<Account> model) {
    super(title, model, 1);
  }

  @Override
  public void setColumnModel() {
    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(25);
    cm.getColumn(1).setPreferredWidth(100);
    cm.getColumn(2).setPreferredWidth(300);
    cm.getColumn(3).setPreferredWidth(30);
  }

}
