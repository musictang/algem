/*
 * @(#)TeacherFollowUpEditor.java	2.10.0 07/06/2016
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
package net.algem.contact.teacher;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import net.algem.contact.Note;
import net.algem.contact.PersonFile;
import net.algem.course.Course;
import net.algem.course.CourseTeacherTableModel;
import net.algem.planning.*;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Follow-up editor for teacher.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 */
public class TeacherFollowUpEditor
        extends FileTab
        implements ActionListener
{

  private boolean loaded;
  private PersonFile pFile;
  private DateFrField dateStart;
  private DateFrField dateEnd;
  private GemButton btModify;
  private GemButton btLoad;
  private CourseTeacherTableModel courseTableModel;
  private JTable table;
  private TeacherService service;
//  private PlanningService planningService;
  private GemLabel totalTime;

  public TeacherFollowUpEditor(GemDesktop desktop, PersonFile dossier) {
    super(desktop);

//    planningService = new PlanningService(dc);
    service = new TeacherService(planningService, dc);
    pFile = dossier;

    courseTableModel = new CourseTeacherTableModel();
    table = new JTable(courseTableModel);
    table.setAutoCreateRowSorter(true);

    final ListSelectionModel listSelectionModel = table.getSelectionModel();
    listSelectionModel.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
          return;
        }
        int total = 0;
        if (!listSelectionModel.isSelectionEmpty()) {
          int rows[] = table.getSelectedRows();
          for (int i = 0; i < rows.length; i++) {
            Hour start = new Hour((String) table.getModel().getValueAt(table.convertRowIndexToModel(rows[i]), 1));
            Hour end = new Hour((String) table.getModel().getValueAt(table.convertRowIndexToModel(rows[i]), 2));
            total += start.getLength(end);
          }
        }
        totalTime.setText(Hour.format(total));
      }
    });

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(30);
    cm.getColumn(1).setPreferredWidth(20);
    cm.getColumn(2).setPreferredWidth(20);
    cm.getColumn(3).setPreferredWidth(150);
    cm.getColumn(4).setPreferredWidth(300);

    JScrollPane pm = new JScrollPane(table);

    btLoad = new GemButton(GemCommand.LOAD_CMD);
    btLoad.addActionListener(this);
    dateStart = new DateFrField(new Date());
    dateEnd = new DateFrField(new Date());

    GemPanel datePanel = new GemPanel();
    totalTime = new GemLabel();
    datePanel.add(new GemLabel(BundleUtil.getLabel("Total.label") + " :"));
    datePanel.add(totalTime);
    datePanel.add(new JLabel(BundleUtil.getLabel("Date.From.label").toLowerCase()));
    datePanel.add(dateStart);
    datePanel.add(new JLabel(BundleUtil.getLabel("Date.To.label")));
    datePanel.add(dateEnd);
    datePanel.add(btLoad);

    btModify = new GemButton(BundleUtil.getLabel("View.modify.label"));
    btModify.addActionListener(this);

    GemPanel footer = new GemPanel(new BorderLayout());
    footer.add(datePanel, BorderLayout.NORTH);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 1));
    buttons.add(btModify);
    footer.add(buttons, BorderLayout.SOUTH);

    setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(pm, 0, 0, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(footer, 0, 1, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);

  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public void load() {
    loaded = true;
    courseTableModel.clear();
    int min = 0;
    try {
      Vector<ScheduleRangeObject> v = service.getSchedule(pFile.getId(), dateStart.toString(), dateEnd.toString());
      for (int i = 0; i < v.size(); i++) {
        ScheduleRangeObject r = v.elementAt(i);
        CourseSchedule pc = new CourseSchedule(v.elementAt(i));
        pc.setCourse(r.getCourse());
        if (pc.getCourse().isCollective()) {
          Schedule s = planningService.getScheduleByRange(pc.getId());
          if (s != null) {
            pc.setId(s.getId());
          }
          Note n = planningService.getCollectiveFollowUpByRange(r.getId());
          if (n != null) {
            pc.setNote(n.getId());
            pc.setNoteValue(n.getText());
          } else {
            pc.setNote(0);
          }
        } else {
          pc.setNoteValue(planningService.getFollowUp(r.getNote()));
        }
        courseTableModel.addItem(pc);
        Hour hd = pc.getStart();
        Hour hf = pc.getEnd();
        min += hd.getLength(hf);
      }
      totalTime.setText(Hour.format(min));

    } catch (SQLException ex) {
      GemLogger.log(getClass().getName() + "#load " + ex.getMessage());
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    Object src = evt.getSource();
    if (src == btLoad) {
      desktop.setWaitCursor();
      load();
      desktop.setDefaultCursor();
      return;
    }

    if (table.getSelectedRow() < 0) {
      return;
    }

    int n = table.convertRowIndexToModel(table.getSelectedRow());

    if (src == btModify) {
      try {
        modification(n);
      } catch (SQLException e) {
        GemLogger.log(e.getMessage());
      }
    }
  }

  void modification(int n) throws SQLException {

    CourseSchedule p = (CourseSchedule) courseTableModel.getItem(n);
    Course c = p.getCourse();
    if (c == null) {
      return;
    }
    CommonFollowUpDlg dlg = new CommonFollowUpDlg(desktop, planningService, p, p.getCourse().toString(), false);
    dlg.entry();
    if (!dlg.isValidation()) {
      return;
    }
    p.setNoteValue(dlg.getText());
    courseTableModel.modItem(n, p);
    dlg.exit();
  }

  void insertion(int n) throws SQLException {
    clear();
  }

  void clear() {
//		liste.clear();
  }
}
