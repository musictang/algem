/*
 * @(#)DailyTimesEditor.java	2.11.4 15/12/16
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
package net.algem.room;

import net.algem.util.ui.HourCellEditor;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabDialog;
import net.algem.util.ui.JTableModel;
import net.algem.util.ui.MessagePopup;

/**
 * Opening and closing time editor.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.4
 * @since 2.8.w 16/07/14
 */
public class DailyTimesEditor
        extends FileTabDialog
{

  private int roomId;
  private boolean loaded;
  private JTable table;
  private JTableModel tableModel;
  private RoomService service;
  private ActionListener listener;

  public DailyTimesEditor(GemDesktop desktop, int roomId, RoomService service) {
    super(desktop);
    this.roomId = roomId;
    this.service = service;
    tableModel = new DailyTimesTableModel();
    table = new JTable(tableModel);
    setHourCellEditor(1,2);
    JScrollPane scroll = new JScrollPane(table);
    setLayout(new BorderLayout());
    add(scroll, BorderLayout.CENTER);
    btCancel.setText(btCancel.getText() + "/" + BundleUtil.getLabel("Action.closing.label"));
    add(buttons, BorderLayout.SOUTH);
  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public void load() {
    DailyTimes[] times = service.findDailyTimes(roomId);

    if (times != null && times.length > 0) {
      for (DailyTimes dt : times) {
        tableModel.addItem(dt);
      }
      loaded = true;
    }
  }

  @Override
  public void validation() {
    TableCellEditor tce = table.getCellEditor();
    if (tce != null) {
      tce.stopCellEditing();
    }
    List<DailyTimes> times = tableModel.getData();
    try {
      service.updateTimes(roomId, times.toArray(new DailyTimes[7]));
      MessagePopup.information(this, MessageUtil.getMessage("modification.success.label"));
    } catch(SQLException sqe) {
      MessagePopup.warning(this, MessageUtil.getMessage("update.exception.info") + sqe.getMessage());
    }
  }

  @Override
  public void cancel() {
    clear();
    if (listener != null) {
      listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CancelEditingTimes"));
    }
  }
  
  void clear() {
    if (tableModel.getRowCount() > 0) {
      tableModel.clear();
    } 
  }
    
  void addActionListener(ActionListener listener) {
    this.listener = listener;
  }
  
  void removeActionListener() {
    this.listener = null;
  }

  /**
   * Sets to HourField the cell editor for selected columns.
   * @param cols indexed columns
   */
  private void setHourCellEditor(int... cols) {
    HourCellEditor cellEditor = new HourCellEditor();
    for (int i : cols) {
      table.getColumnModel().getColumn(i).setCellEditor(cellEditor);
    }  
  }

}
