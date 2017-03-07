/*
 * @(#)AdministrativeTableView.java		2.9.4.2 10/04/15
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
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.planning;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.algem.room.Room;
import net.algem.room.RoomChoice;
import net.algem.room.RoomChoiceEstabModel;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.model.GemList;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.HourCellEditor;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.2
 * @since 2.9.4.0 18/03/15
 */
public class AdministrativeTableView
        extends GemPanel
{

  private final JTableModel<AdministrativeActionModel> tableModel;
  private final String weekDays[] = PlanningService.WEEK_DAYS;
  private DayOfWeek[] days = new DayOfWeek[7];
  private RoomChoice roomChoice;
  private final JTable table;
  private TableColumn roomTableColumn;

  public AdministrativeTableView(GemList<Room> roomList, int estab) {
    tableModel = new AdministrativeActionTableModel();
    table = new JTable(tableModel);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    final TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(40);
    cm.getColumn(1).setPreferredWidth(15);
    cm.getColumn(2).setPreferredWidth(15);
    cm.getColumn(3).setPreferredWidth(60);

    days[0] = new DayOfWeek(Calendar.MONDAY, weekDays[Calendar.MONDAY]);
    days[1] = new DayOfWeek(Calendar.TUESDAY, weekDays[Calendar.TUESDAY]);
    days[2] = new DayOfWeek(Calendar.WEDNESDAY, weekDays[Calendar.WEDNESDAY]);
    days[3] = new DayOfWeek(Calendar.THURSDAY, weekDays[Calendar.THURSDAY]);
    days[4] = new DayOfWeek(Calendar.FRIDAY, weekDays[Calendar.FRIDAY]);
    days[5] = new DayOfWeek(Calendar.SATURDAY, weekDays[Calendar.SATURDAY]);
    days[6] = new DayOfWeek(Calendar.SUNDAY, weekDays[Calendar.SUNDAY]);

    cm.getColumn(0).setCellEditor(new DefaultCellEditor(new JComboBox(days)));

    HourCellEditor cellEditor = new HourCellEditor();
    cm.getColumn(1).setCellEditor(cellEditor);
    cm.getColumn(2).setCellEditor(cellEditor);
    roomChoice = new RoomChoice(new RoomChoiceEstabModel(roomList, estab));
    roomTableColumn = cm.getColumn(3);
    roomTableColumn.setCellEditor(new DefaultCellEditor(roomChoice));
    table.setRowHeight(table.getRowHeight() + 3);

    JScrollPane scroll = new JScrollPane(table);
    setLayout(new BorderLayout());
    add(scroll, BorderLayout.CENTER);
    final GemButton btRemove = new GemButton(GemCommand.REMOVE_CMD);
    btRemove.setToolTipText(BundleUtil.getLabel("Remove.selected.line.tip"));
    final GemButton btAdd = new GemButton(GemCommand.ADD_CMD);
    btAdd.setToolTipText(BundleUtil.getLabel("Administrative.scheduling.add.tip"));
    GemPanel buttons = new GemPanel(new GridLayout(1, 2));
    ActionListener btController = new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btAdd) {
          stopCellEditing();
          int idx = table.getSelectedRow();
          if (idx >= 0) {
            AdministrativeActionModel a = (AdministrativeActionModel) tableModel.getItem(idx);
            tableModel.getData().add(idx, createAction(a.getDay()));
            tableModel.fireTableRowsInserted(idx, idx);
          }
        } else if (src == btRemove) {
          stopCellEditing();
          int idx = table.getSelectedRow();
          if (idx >= 0) {
            tableModel.deleteItem(idx);
          }
        }
      }
    };
    buttons.add(btRemove);
    buttons.add(btAdd);
    btRemove.addActionListener(btController);
    btAdd.addActionListener(btController);
    add(buttons, BorderLayout.SOUTH);
    setPreferredSize(new Dimension(480, 200));//! IMPORTANT

  }
  
  private void stopCellEditing() {
    TableCellEditor tce = table.getCellEditor();
    if (tce != null) {
      tce.stopCellEditing();
    }
  }

  void setEstab(GemList<Room> list, int estab) {
    stopCellEditing();
    roomChoice = new RoomChoice(new RoomChoiceEstabModel(list, estab));
    roomTableColumn.setCellEditor(new DefaultCellEditor(roomChoice));
  }

  void load() {
    tableModel.addItem(createAction(days[0]));
    tableModel.addItem(createAction(days[1]));
    tableModel.addItem(createAction(days[2]));
    tableModel.addItem(createAction(days[3]));
    tableModel.addItem(createAction(days[4]));
    tableModel.addItem(createAction(days[5]));
    tableModel.addItem(createAction(days[6]));
  }

  private AdministrativeActionModel createAction(DayOfWeek dow) {
    AdministrativeActionModel a = new AdministrativeActionModel();
    a.setDay(dow);
    a.setStart(new Hour("00:00"));
    a.setEnd(new Hour("00:00"));
    a.setRoom(new Room(0, ""));
    return a;
  }

  List<AdministrativeActionModel> getRows() {
    stopCellEditing();
    return tableModel.getData();
  }

  void clear() {
    tableModel.clear();
    load();
    tableModel.fireTableDataChanged();
  }

}
