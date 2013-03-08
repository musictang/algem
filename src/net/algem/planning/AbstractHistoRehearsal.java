/*
 * @(#)AbstractHistoRehearsal.java 2.6.a 19/09/12
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabDialog;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.1.j
 */
public abstract class AbstractHistoRehearsal 
  extends FileTabDialog
{

  private RehearsalTableModel rehearsalTableModel;
  private JTable table;
  private GemLabel nbHours;
  protected DateRangePanel datePanel;
  protected int idper;
  protected ActionListener listener;
  protected GemButton btAll;
  private boolean loaded = false;

  /**
   * Inits rehearsal history.
   * @param desktop
   * @param listener optional
   * @param id contact or group id
   */
  public AbstractHistoRehearsal(GemDesktop desktop, ActionListener listener, int id) {
    super(desktop);
    this.listener = listener;
    this.idper = id;

    rehearsalTableModel = new RehearsalTableModel();
    table = new JTable(rehearsalTableModel);
    table.setAutoCreateRowSorter(true);

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(70);
    cm.getColumn(1).setPreferredWidth(30);
    cm.getColumn(2).setPreferredWidth(30);
    cm.getColumn(3).setPreferredWidth(250);

    JScrollPane pm = new JScrollPane(table);

    GemPanel bottomPanel = new GemPanel();
    GemPanel datesPanel = new GemPanel();
    GemPanel hoursPanel = new GemPanel();
    nbHours = new GemLabel();
    datePanel = new DateRangePanel(DateRangePanel.RANGE_DATE, null);
    DateFr d = datePanel.getStartFr();
    d.decMonth(1);// repets sur un mois par défaut
    datePanel.setStart(d);
    datesPanel.add(datePanel);

    bottomPanel.setLayout(new BorderLayout());

    hoursPanel.add(new JLabel(MessageUtil.getMessage("total.hour")));
    hoursPanel.add(nbHours);

    bottomPanel.add(datesPanel, BorderLayout.NORTH);
    bottomPanel.add(hoursPanel, BorderLayout.CENTER);

    btAll = new GemButton(BundleUtil.getLabel("All.label"));
    btAll.addActionListener(this);
    buttons.add(btAll, 0);

    bottomPanel.add(buttons, BorderLayout.SOUTH);

    setLayout(new BorderLayout());
    add(pm, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
    //load();

  }

  @Override
  public void validation() {
    clear();
    load();
  }

  @Override
  public void cancel() {
    clear();
    if (listener != null) {
      listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "HistoRepet.Abandon"));
    }
  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  /**
   * Loads the schedules.
   * @param all transmitted to method #getSchedule
   */
  public void load(boolean all) {
    int min = 0;
    Vector<Schedule> vp = getSchedule(all);

    if (vp != null && vp.size() > 0) {
      loaded = true;
    }
    //Vector vp = PlanningIO.find(dc, " WHERE p.ptype="+Schedule.PLANREPETGROUPE+" AND p.idper="+groupe.getId()+" ORDER BY jour,debut");
    for (int i = 0; i < vp.size(); i++) {
      Schedule p = vp.elementAt(i);
      Hour hd = p.getStart();
      Hour hf = p.getEnd();
      min += hd.getLength(hf);
      rehearsalTableModel.addItem(p);
    }
    int nbh = min / 60;
    int nbm = min % 60;
    String nm = (nbm < 10) ? "0" : "";
    nm += String.valueOf(nbm);
    nbHours.setText(String.valueOf(nbh) + "h " + nm);
  }

  @Override
  public void load() {
    this.load(false);
  }

  public void clear() {
    if (rehearsalTableModel.getRowCount() > 0) {
      rehearsalTableModel.clear();
    }
    nbHours.setText(null);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btAll) {
      clear();
      load(true);
    }
    else {
      super.actionPerformed(evt);
    }
  }

  /**
   * Retrieves the schedules.
   * @param all all the schedules from the beginning
   * @return a list of schedules
   */
  public abstract Vector<Schedule> getSchedule(boolean all);
}
