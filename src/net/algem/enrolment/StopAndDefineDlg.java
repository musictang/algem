/*
 * @(#)StopAndDefineDlg.java 2.9.10.0 13/06/2016
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 * 
 */

package net.algem.enrolment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.planning.CourseSchedule;
import net.algem.planning.PlanningService;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.10.0 13/06/16
 */
public class StopAndDefineDlg 
        extends JDialog
        implements ActionListener
{

  private GemButton btValid, btCancel;
  private JTable table;
  private JTableModel<CourseSchedule> tableModel;
  private boolean validation;
  private Component parent;
  public StopAndDefineDlg(Frame owner) {
    super(owner);
    this.parent = parent;
  }

  public StopAndDefineDlg(Frame owner, boolean modal) {
    super(owner, BundleUtil.getLabel("Available.courses.label"), modal);
  }
  
  public void createUI(List<CourseSchedule> schedules) {
    setLayout(new BorderLayout());
    tableModel = new AvailableScheduleTableModel();
    load(schedules);
    table = new JTable(tableModel);
    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(20);
    cm.getColumn(1).setPreferredWidth(10);
    cm.getColumn(2).setPreferredWidth(10);
    cm.getColumn(4).setPreferredWidth(40);
    JScrollPane scroll = new JScrollPane(table);
    add(scroll, BorderLayout.CENTER);
    GemPanel buttons = new GemPanel(new GridLayout(1, 2));
    btValid = new GemButton(GemCommand.VALIDATION_CMD);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btValid.addActionListener(this);
    btCancel.addActionListener(this);
    buttons.add(btValid);
    buttons.add(btCancel);
    add(buttons, BorderLayout.SOUTH);
    setSize(GemModule.M_SIZE);
    setLocationRelativeTo(parent);
    setVisible(true);
  }
  
  public void load(List<CourseSchedule> schedules) {
    for (CourseSchedule c : schedules) {
      tableModel.addItem(c);
    }
  }
  
  public CourseSchedule getCourse() {
    return (CourseSchedule) tableModel.getItem(table.getSelectedRow());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    validation = src == btValid;
    setVisible(false);
  }
  
  public boolean isValidation() {
    return validation;
  }
  
  private class AvailableScheduleTableModel extends JTableModel<CourseSchedule> {

    public AvailableScheduleTableModel() {
       header = new String[]{
         BundleUtil.getLabel("Day.label"),
      BundleUtil.getLabel("Start.label"),
      BundleUtil.getLabel("End.label"),
      BundleUtil.getLabel("Course.label"),
      BundleUtil.getLabel("Status.label"),
      BundleUtil.getLabel("Teacher.label")
    };
    }

    @Override
    public int getIdFromIndex(int i) {
      return ((CourseSchedule) getItem(i)).getId();
    }

    @Override
    public Object getValueAt(int line, int col) {
      CourseSchedule c = tuples.elementAt(line);
    switch (col) {
      case 0:
        return PlanningService.WEEK_DAYS[c.getDate().getDayOfWeek()];
      case 1:
        return c.getStart();
      case 2:
        return c.getEnd();
      case 3:
        return c.getCourse().getTitle();
        case 4:
        return c.getAction().getStatus().getLabel();
      case 5:
        return c.getPerson().getFirstnameName();

    }
    return null;
    }

    @Override
    public void setValueAt(Object value, int line, int column) {
      
    }
    
  }
}
