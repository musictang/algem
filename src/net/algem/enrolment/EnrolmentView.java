/*
 * @(#)EnrolmentView.java	2.8.a 23/04/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.enrolment;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumnModel;
import net.algem.contact.Person;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.ui.*;

/**
 * Course and module order view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 1.0a 07/07/1999
 */
public class EnrolmentView
        extends GemBorderPanel

{

  //private String[] dayNames;
  private ModuleOrderTableModel moduleTableModel;
  private JTable moduleTable;
  private CourseOrderTableModel courseOrderTableModel;
  private JTable courseTable;
  private GemField member;
  private GemButton btAdd;
  private GemButton btRemove;
  private GemButton btModify;
  private GemButton btCourseModify;
  private boolean modifModule = true;
  private boolean modifCourse = true;
  private ActionListener actionListener;

  public EnrolmentView() {

    //dayNames = PlanningService.WEEK_DAYS;
    member = new GemField(35);
    member.setEditable(false);

    moduleTableModel = new ModuleOrderTableModel();
    moduleTable = new JTable(moduleTableModel);

    TableColumnModel cm = moduleTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(10);
    cm.getColumn(1).setPreferredWidth(250);
    cm.getColumn(2).setPreferredWidth(40);
    cm.getColumn(3).setPreferredWidth(40);
    cm.getColumn(4).setPreferredWidth(40);
    cm.getColumn(5).setPreferredWidth(10);

    JScrollPane scrollPane = new JScrollPane(moduleTable);
    courseOrderTableModel = new CourseOrderTableModel();
    courseTable = new JTable(courseOrderTableModel);

    cm = courseTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(60);
    cm.getColumn(1).setPreferredWidth(300);
    cm.getColumn(2).setPreferredWidth(20);
    cm.getColumn(3).setPreferredWidth(20);
    cm.getColumn(4).setPreferredWidth(20);

    JScrollPane pc = new JScrollPane(courseTable);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));
    buttons.add(btRemove = new GemButton(MemberEnrolmentDlg.MODULE_REMOVE));
    buttons.add(btModify = new GemButton(MemberEnrolmentDlg.MODULE_MODIFY));
    buttons.add(btAdd = new GemButton(MemberEnrolmentDlg.MODULE_ADD));

    GemPanel memberPanel = new GemPanel();
    memberPanel.add(new GemLabel(BundleUtil.getLabel("Member.label")));
    memberPanel.add(member);

    GemPanel top = new GemPanel();
    top.setLayout(new BorderLayout());
    top.add(memberPanel, BorderLayout.NORTH);
    top.add(scrollPane, BorderLayout.CENTER);
    top.add(buttons, BorderLayout.SOUTH);
    top.setBorder(new BevelBorder(BevelBorder.LOWERED));

    btCourseModify = new GemButton(MemberEnrolmentDlg.COURSE_MODIFY);

    GemPanel bottom = new GemPanel();
    bottom.setLayout(new BorderLayout());
    bottom.add(pc, BorderLayout.CENTER);
    bottom.add(btCourseModify, BorderLayout.SOUTH);
    bottom.setBorder(new BevelBorder(BevelBorder.LOWERED));

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(top, 0, 0, 1, 1, GridBagHelper.BOTH, 1.0, 0.4);
    gb.add(new GemLabel(MessageUtil.getMessage("course.list.by.module")), 0, 1, 1, 1, GridBagHelper.CENTER);
    gb.add(bottom, 0, 2, 1, 1, GridBagHelper.BOTH, 1.0, 0.6);

  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
    if (modifModule) {
      btAdd.addActionListener(l);
      btRemove.addActionListener(l);
      btModify.addActionListener(l);
    }
    if (modifCourse) {
      btCourseModify.addActionListener(l);
    }
  }

  int getSelectedModule() {
    return moduleTable.getSelectedRow();
  }

  void addModule(ModuleOrder mo) {
    moduleTableModel.addItem(mo);
  }

  void changeModule(int i, ModuleOrder mo) {
    moduleTableModel.modItem(i, mo);
  }

  void removeModule(int i) {
    moduleTableModel.deleteItem(i);
  }

  int getSelectedCourse() {
    return courseTable.getSelectedRow();
  }

  void addCourse(CourseOrder order) {
    courseOrderTableModel.addItem(order);
  }

  CourseOrder getCourseOrder(int row) {
    return (CourseOrder) courseOrderTableModel.getItem(row);
  }

  void changeCourse(int i, CourseOrder co) {
    courseOrderTableModel.modItem(i, co);
  }

  void removeCourse(int i) {
    courseOrderTableModel.deleteItem(i);
  }

  void remove(CourseOrder co) {
    courseOrderTableModel.deleteItem(co);
  }

  void setMember(Person p) {
    member.setText(p.toString());
  }

  public void clear() {
    member.setText("");
    moduleTableModel.clear();
    courseOrderTableModel.clear();
  }
}
