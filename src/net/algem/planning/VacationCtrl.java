/*
 * @(#)VacationCtrl.java	2.8.w 08/07/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

import net.algem.config.ParamChoice;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.ui.*;

/**
 * Vacation controller.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 */
public class VacationCtrl
        extends GemPanel
        implements ActionListener
{

  private DataCache dataCache;
  private VacationTableModel vacationModel;
  private JTable vacationTable;
  private DateFrField start;
  private DateFrField end;
  private GemField label;
  private ParamChoice vChoice;
  private GemButton btAdd;
  private GemButton btModify;
  private GemButton btDelete;

  public VacationCtrl(DataCache _dc) {
    dataCache = _dc;

    vacationModel = new VacationTableModel();
    vacationTable = new JTable(vacationModel);
    vacationTable.setAutoCreateRowSorter(true);
    vacationTable.addMouseListener(new MouseAdapter()
    {

      public void mouseClicked(MouseEvent e) {
        int n = vacationTable.convertRowIndexToModel(vacationTable.getSelectedRow());
        Vacation v = (Vacation) vacationModel.getItem(n);
        start.setText(v.getDay().toString());
        label.setText(v.getLabel());
        vChoice.setKey(v.getVid());//id au lieu de type
      }
    });

    TableColumnModel cm = vacationTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(80);
    cm.getColumn(1).setPreferredWidth(300);

    JScrollPane pm = new JScrollPane(vacationTable);

    btAdd = new GemButton(GemCommand.ADD_CMD);
    btModify = new GemButton(GemCommand.MODIFY_CMD);
    btDelete = new GemButton(GemCommand.DELETE_CMD);
    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));
    buttons.add(btAdd);
    buttons.add(btModify);
    buttons.add(btDelete);
    btAdd.addActionListener(this);
    btModify.addActionListener(this);
    btDelete.addActionListener(this);

    GemPanel bottom = new GemPanel();
    bottom.setLayout(new FlowLayout(FlowLayout.LEFT));
    start = new DateFrField();
    end = new DateFrField();
    label = new GemField(20);
    bottom.add(new GemLabel(BundleUtil.getLabel("Date.From.label") + " : "));
    bottom.add(start);
    bottom.add(new GemLabel(" " + BundleUtil.getLabel("Date.To.label") + " :"));
    bottom.add(end);
    bottom.add(new GemLabel(" "));
    bottom.add(label);
    bottom.add(new GemLabel(BundleUtil.getLabel("Type.label")));
    vChoice = new ParamChoice(dataCache.getVacancyCat());
    bottom.add(vChoice);
    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(pm, 0, 0, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(bottom, 0, 1, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(buttons, 0, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);

    load();
  }

  public void load() {
    Vector<Vacation> v = VacationIO.find("", DataCache.getDataConnection());
    for (int i = 0; i < v.size(); i++) {
      Vacation va = v.elementAt(i);
      vacationModel.addItem(va);
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    if (cmd.equals(GemCommand.MODIFY_CMD)) {
      try {
        modification();
      } catch (Exception e) {
        GemLogger.logException("modification vacances", e, this);
      }
    } else if (cmd.equals(GemCommand.DELETE_CMD)) {
      try {
        suppression();
        clear();
      } catch (Exception e) {
        GemLogger.logException("suppresion vacances", e, this);
      }
    } else if (cmd.equals(GemCommand.ADD_CMD)) {
      try {
        insertion();
        clear();
      } catch (Exception e) {
        GemLogger.logException("insertion vacances", e, this);
      }
    }
  }

  void modification() throws SQLException {
    Vacation v = new Vacation(start.getDateFr(), label.getText());
    v.setVid(vChoice.getKey());
    VacationIO.update(v, DataCache.getDataConnection());

    int n = vacationTable.convertRowIndexToModel(vacationTable.getSelectedRow());
    vacationModel.modItem(n, v);
    clear();
  }

  void insertion() throws SQLException {
    DateFr deb = start.getDateFr();
    while (!deb.after(end.getDateFr())) {
      Vacation v = new Vacation(new DateFr(deb), label.getText());
      v.setVid(vChoice.getKey());// todo -> setId
      VacationIO.insert(v, DataCache.getDataConnection());
      vacationModel.addItem(v);
      deb.incDay(1);
    }
    clear();
  }

  void suppression() throws SQLException {
    int[] rows = vacationTable.getSelectedRows();
    for (int i = rows.length - 1; i >= 0; i--) {
      int n = vacationTable.convertRowIndexToModel(rows[i]);
      Vacation v = (Vacation) vacationModel.getItem(n);
      VacationIO.delete(v, DataCache.getDataConnection());
      vacationModel.deleteItem(n);
    }
    clear();
  }

  void clear() {
    start.setText(DateFr.NULLDATE);
    end.setText(DateFr.NULLDATE);
    label.setText("");
    vChoice.setKey(0);
//		liste.clear();
  }
}
