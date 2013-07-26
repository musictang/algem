/*
 * @(#)BranchListCtrl.java	2.8.i 09/07/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.bank;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import net.algem.util.ui.ListCtrl;

/**
 * Branch list controller.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.i
 */
public class BranchListCtrl
        extends ListCtrl
{

  public BranchListCtrl() {
    tableModel = new BranchTableModel();

    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);

    setColumns(20,20,20,100,60,200);

    JScrollPane p = new JScrollPane(table);
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));

    add("Center", p);
  }
}
