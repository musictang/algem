/*
 * @(#)AgeRangeListCtrl.java 2.6.a 24/09/12
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
 */

package net.algem.config;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumnModel;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.ListCtrl;

/**
 *
 * @author <a href="mailto:nicolasnouet@gmail.com">Nicolas Nouet</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.3.a
 */

public class AgeRangeListCtrl
	extends ListCtrl
{
  private GemButton btClose;
  
  public AgeRangeListCtrl(boolean searchFlag) {
    super(searchFlag);
    tableModel = new AgeRangeTableModel();

    jtable = new JTable(tableModel);
    jtable.setAutoCreateRowSorter(true);

    TableColumnModel cm = jtable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(2);
    cm.getColumn(1).setPreferredWidth(2);
    cm.getColumn(2).setPreferredWidth(10);
    cm.getColumn(3).setPreferredWidth(10);
    cm.getColumn(4).setPreferredWidth(200);

    JScrollPane p = new JScrollPane(jtable);
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));

    add(p, BorderLayout.CENTER);
    GemPanel bp = new GemPanel(new GridLayout(1,1));
    
    back = new GemButton(GemCommand.CREATE_CMD);
    btClose = new GemButton(GemCommand.CLOSE_CMD);
    bp.add(back);
    bp.add(btClose);
    add(bp, BorderLayout.SOUTH);
  }
  
  @Override
  public void addActionListener(ActionListener l) {
    super.addActionListener(l);
    btClose.addActionListener(l);
  }
}

