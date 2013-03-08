/*
 * @(#)HistoRehearsalDlg.java	2.6.a 19/09/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.group.Group;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.PopupDlg;

/**
 * Dialog for rehearsal history.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 25/02/2004
 */
public class HistoRehearsalDlg
        implements ActionListener
{

  private DataCache dataCache;
  private JDialog dlg;
  private GemLabel title;
  private GemLabel nbHour;
  private GemButton btClose;
  private Group group;
  private RehearsalTableModel tableModel;
  private JTable table;

  public HistoRehearsalDlg(Component c, DataCache _dc, String t) {

    dataCache = _dc;
    title = new GemLabel(t);

    dlg = new JDialog(PopupDlg.getTopFrame(c), true);

    btClose = new GemButton(GemCommand.OK_CMD);
    btClose.addActionListener(this);


    tableModel = new RehearsalTableModel();
    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(70);
    cm.getColumn(1).setPreferredWidth(30);
    cm.getColumn(2).setPreferredWidth(30);
    cm.getColumn(3).setPreferredWidth(250);

    JScrollPane pm = new JScrollPane(table);

    GemPanel bottom = new GemPanel();
    nbHour = new GemLabel();
    bottom.add(new JLabel("total heures"));
    bottom.add(nbHour);

    GemPanel center = new GemPanel(new BorderLayout());

    center.add(pm, BorderLayout.CENTER);
    center.add(bottom, BorderLayout.SOUTH);

    Container container = dlg.getContentPane();
    container.setLayout(new BorderLayout());
    container.add(title, BorderLayout.NORTH);
    container.add(center, BorderLayout.CENTER);
    container.add(btClose, BorderLayout.SOUTH);
    dlg.setSize(600, 300);
    dlg.setLocation(100, 100);

  }

  public void load(Group g) {
    group = g;

    int min = 0;
    String query = " WHERE p.ptype=" + Schedule.GROUP_SCHEDULE + " AND p.idper=" + group.getId() + " ORDER BY jour,debut";
    Vector<Schedule> v = ScheduleIO.find(query, dataCache.getDataConnection());
    for (int i = 0; i < v.size(); i++) {
      Schedule p = v.elementAt(i);
      Hour hd = p.getStart();
      Hour hf = p.getEnd();
      min += hd.getLength(hf);
      tableModel.addItem(p);
    }
    int nbh = min / 60;
    int nbm = min % 60;
    nbHour.setText(String.valueOf(nbh) + "h" + String.valueOf(nbm));
  }

  public void show() {
    dlg.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals(GemCommand.OK_CMD)) {
      dlg.setVisible(false);
      //dlg.dispose();
    }
  }
  
}
