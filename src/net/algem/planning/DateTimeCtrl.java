/*
 * @(#)DateTimeCtrl.java	2.12.0 08/03/17
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
package net.algem.planning;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.ui.DateCellEditor;
import net.algem.util.ui.DateTimeTableModel;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.HourCellEditor;
import net.algem.util.ui.MessagePopup;

/**
 * This controller is used to add or remove DateTimePanel components.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 * @since 2.8.t 11/04/14
 */
public class DateTimeCtrl
  extends GemPanel
  implements ActionListener
{

  private JTable timetable;
  private DateTimeTableModel tableModel;
  protected GemButton plus;
  protected GemButton minus;

  public DateTimeCtrl() {
    this.tableModel = new DateTimeTableModel();
    this.timetable = new JTable(tableModel);

    setLayout(new BorderLayout());
    plus = new GemButton("+");
    plus.setMargin(new Insets(0, 4, 0, 4)); //reduction de la taille du bouton
    plus.addActionListener(this);
    plus.setToolTipText(GemCommand.ADD_CMD);

    minus = new GemButton("-");
    minus.setMargin(new Insets(0, 4, 0, 4));
    minus.addActionListener(this);
    minus.setToolTipText(BundleUtil.getLabel("Remove.selected.line.tip"));

    JPanel actionPanel = new JPanel();
    actionPanel.add(minus);
    actionPanel.add(plus);
    GemPanel top = new GemPanel(new BorderLayout());
    top.add(new GemLabel(BundleUtil.getLabel("DateTime.label")), BorderLayout.WEST);
    top.add(actionPanel, BorderLayout.EAST);
    top.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    add(top, BorderLayout.NORTH);

    HourCellEditor cellEditor = new HourCellEditor();
    timetable.getColumnModel().getColumn(0).setCellEditor(new DateCellEditor());
    timetable.getColumnModel().getColumn(1).setCellEditor(cellEditor);
    timetable.getColumnModel().getColumn(2).setCellEditor(cellEditor);
    JScrollPane tableScroll = new JScrollPane(timetable);
    add(tableScroll,BorderLayout.CENTER);
    setPreferredSize(new Dimension(320, 200));

  }

  List<GemDateTime> getRanges() {
    List<GemDateTime> ranges = new ArrayList<GemDateTime>();
    for(DateTimeActionModel d : tableModel.getData()) {
      if (d.isActive()) {
        ranges.add(new GemDateTime(d.getDate(), new HourRange(d.getStart(), d.getEnd())));
      }
    }
    return ranges;
  }

  public void add() {
    int rows = timetable.getRowCount();
    DateTimeActionModel dam = new DateTimeActionModel();
    if (rows > 0) {
      stopCellEditing();
      DateTimeActionModel m = tableModel.getItem(rows -1);
      DateFr d2 = new DateFr(m.getDate());
      d2.incDay(1);
      dam.setDate(d2);
      dam.setStart(new Hour(m.getStart()));
      dam.setEnd(new Hour(m.getEnd()));
    }
    tableModel.addItem(dam);
  }

  public void remove() {
    final int row = timetable.getSelectedRow();
    if (row == -1) {
      MessagePopup.warning(this, MessageUtil.getMessage("no.line.selected"));
      return;
    }
    stopCellEditing();// IMPORTANT
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        tableModel.deleteItem(row);
      }
    });

  }

  public void clear() {
    tableModel.clear();
  }

  void stopCellEditing() {
    TableCellEditor tce = timetable.getCellEditor();
      if (tce != null) {
        tce.stopCellEditing();
      }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
   Object src = e.getSource();
    if (src == plus) {
      add();
    } else if (src == minus) {
      remove();
    }
  }
}
