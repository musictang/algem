/*
 * @(#)AbstractHistoRehearsal.java 2.10.0 15/06/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.util.BundleUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabDialog;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.1.j
 */
public abstract class AbstractHistoRehearsal
  extends FileTabDialog {

  private RehearsalTableModel rehearsalTableModel;
  private JTable table;
  private GemLabel totalLabel;
  protected DateRangePanel datePanel;
  protected int idper;
  protected ActionListener listener;
  protected GemButton btAll;
  private boolean loaded = false;

  /**
   * Inits rehearsal history.
   *
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

    JScrollPane sp = new JScrollPane(table);

    GemPanel infoPanel = new GemPanel();
    GemPanel totalPanel = new GemPanel();
    GemPanel datesPanel = new GemPanel();
    totalLabel = new GemLabel();
    datePanel = new DateRangePanel(DateRangePanel.RANGE_DATE, null);
    datesPanel.add(datePanel);

    totalPanel.add(new JLabel(BundleUtil.getLabel("Total.label") + " : "));
    totalPanel.add(totalLabel);

    infoPanel.add(totalPanel);
    infoPanel.add(datesPanel);

    JPanel mainPanel = new GemPanel(new BorderLayout());
    mainPanel.add(sp, BorderLayout.CENTER);
    mainPanel.add(infoPanel, BorderLayout.SOUTH);

    btAll = new GemButton(BundleUtil.getLabel("Any.label"));
    btAll.setToolTipText(BundleUtil.getLabel("Rehearsal.list.all.tip"));
    btAll.addActionListener(this);
    buttons.add(btAll, 0);

    setLayout(new BorderLayout());
    add(mainPanel, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);

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
   *
   * @param all all schedules if true
   */
  public void load(boolean all) {
    int min = 0;
    List<Schedule> vp = getSchedule(all);

    if (vp != null && vp.size() > 0) {
      loaded = true;
    }
    if (vp != null) {
      for (int i = 0, len = vp.size(); i < len; i++) {
        Schedule p = vp.get(i);
        Hour hd = p.getStart();
        Hour hf = p.getEnd();
        min += hd.getLength(hf);
        rehearsalTableModel.addItem(p);
      }
    }
    totalLabel.setText(Hour.format(min));
  }

  @Override
  public void load() {
    this.load(false);
  }

  public void clear() {
    if (rehearsalTableModel.getRowCount() > 0) {
      rehearsalTableModel.clear();
    }
    totalLabel.setText(null);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    desktop.setWaitCursor();
    if (evt.getSource() == btAll) {
      clear();
      load(true);
    } else {
      super.actionPerformed(evt);
    }
    desktop.setDefaultCursor();
  }

  /**
   * Retrieves the schedules.
   *
   * @param all all the schedules from the beginning
   * @return a list of schedules
   */
  public abstract List<Schedule> getSchedule(boolean all);
}
