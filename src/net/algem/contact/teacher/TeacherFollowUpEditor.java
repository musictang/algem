/*
 * @(#)TeacherFollowUpEditor.java	2.6.a 18/09/12
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
package net.algem.contact.teacher;

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
import javax.swing.table.TableColumnModel;
import net.algem.contact.PersonFile;
import net.algem.course.CourseTeacherTableModel;
import net.algem.planning.*;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Follow-up editor for teacher.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
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
  private JTable scheduleRangeTable;
  private TeacherService service;
  private PlanningService planningService;

  public TeacherFollowUpEditor(GemDesktop _desktop, PersonFile _dossier) {
    super(_desktop);

    service = new TeacherService(dc);
    planningService = new PlanningService(dc);
    pFile = _dossier;

    courseTableModel = new CourseTeacherTableModel();
    scheduleRangeTable = new JTable(courseTableModel);
    scheduleRangeTable.setAutoCreateRowSorter(true);

    TableColumnModel cm = scheduleRangeTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(30);
    cm.getColumn(1).setPreferredWidth(20);
    cm.getColumn(2).setPreferredWidth(20);
    cm.getColumn(3).setPreferredWidth(150);
    cm.getColumn(4).setPreferredWidth(300);

    JScrollPane pm = new JScrollPane(scheduleRangeTable);

    btLoad = new GemButton(GemCommand.LOAD_CMD);
    btLoad.addActionListener(this);
    dateStart = new DateFrField(new Date());
    dateEnd = new DateFrField(new Date());
    GemPanel header = new GemPanel();
    header.add(new JLabel(BundleUtil.getLabel("Date.From.label")));
    header.add(dateStart);
    header.add(new JLabel(BundleUtil.getLabel("Date.To.label")));
    header.add(dateEnd);
    header.add(btLoad);

    btModify = new GemButton(BundleUtil.getLabel("View.modify.label"));
    btModify.addActionListener(this);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 1));
    buttons.add(btModify);

    setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(header, 0, 0, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(pm, 0, 1, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(buttons, 0, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);

  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public void load() {
    loaded = true;
    courseTableModel.clear();

    Vector<Schedule> v = service.getSchedule(pFile.getId(), dateStart.toString(), dateEnd.toString());
    try {
      for (int i = 0; i < v.size(); i++) {
        CourseSchedule pc = new CourseSchedule(v.elementAt(i));
        pc.setCourse(planningService.getCourseFromAction(pc.getIdAction()));
        pc.setFollowUp(planningService.getCollectiveFollowUp(pc.getNote()));
        courseTableModel.addItem(pc);
      }
    } catch (SQLException ex) {
      System.err.println(getClass().getName() + "#load " + ex.getMessage());
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    Object src = evt.getSource();
    if (src == btLoad) {
      load();
      return;
    }

    if (scheduleRangeTable.getSelectedRow() < 0) {
      return;
    }

    int n = scheduleRangeTable.convertRowIndexToModel(scheduleRangeTable.getSelectedRow());

    if (src == btModify) {
      try {
        modification(n);
      } catch (SQLException e) {
        System.err.println(e.getMessage());
        //dc.logException("modification suivi pédagogique", e, desktop.getFrame());
      }
    }
  }

  void modification(int n) throws SQLException {

    CourseSchedule p = (CourseSchedule) courseTableModel.getItem(n);
    CollectiveFollowUpDlg dlg = new CollectiveFollowUpDlg(desktop, planningService, p, p.getCourse().toString());
    dlg.entry();
    if (!dlg.isValidation()) {
      return;
    }

    p.setFollowUp(dlg.getText());
    courseTableModel.modItem(n, p);
    dlg.exit();
  }

  void insertion(int n) throws SQLException {
    //plages.addItem(v);
    clear();
  }

  void clear() {
//		liste.clear();
  }
}
