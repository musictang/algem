/*
 * @(#)EnrolmentView.java	2.6.a 17/09/12
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
import net.algem.planning.PlanningService;
import net.algem.util.BundleUtil;
import net.algem.util.ui.*;

/**
 * Course and module order view.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class EnrolmentView
        extends GemBorderPanel

{

  private String[] dayNames;
  private ModuleOrderTableModel module;
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

    dayNames = PlanningService.WEEK_DAYS;
    member = new GemField(35);
    member.setEditable(false);

    module = new ModuleOrderTableModel();
    moduleTable = new JTable(module);

    TableColumnModel cm = moduleTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(30);
    cm.getColumn(1).setPreferredWidth(120);
    cm.getColumn(2).setPreferredWidth(60);
    cm.getColumn(3).setPreferredWidth(60);
    cm.getColumn(4).setPreferredWidth(40);
    cm.getColumn(5).setPreferredWidth(35);
    cm.getColumn(6).setPreferredWidth(30);

    JScrollPane pm = new JScrollPane(moduleTable);
    courseOrderTableModel = new CourseOrderTableModel();
    courseTable = new JTable(courseOrderTableModel);

    cm = courseTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(30);
    cm.getColumn(1).setPreferredWidth(150);
    cm.getColumn(2).setPreferredWidth(60);
    cm.getColumn(3).setPreferredWidth(40);
    cm.getColumn(4).setPreferredWidth(40);

    JScrollPane pc = new JScrollPane(courseTable);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));
    buttons.add(btRemove = new GemButton("Enlever Module"));

    buttons.add(btModify = new GemButton("Modifier Module"));
    buttons.add(btAdd = new GemButton("Ajouter Module"));

    GemPanel padherent = new GemPanel();
    padherent.add(new GemLabel(BundleUtil.getLabel("Member.label")));
    padherent.add(member);

    GemPanel haut = new GemPanel();
    haut.setLayout(new BorderLayout());
    haut.add(padherent, BorderLayout.NORTH);
    haut.add(pm, BorderLayout.CENTER);
    haut.add(buttons, BorderLayout.SOUTH);
    haut.setBorder(new BevelBorder(BevelBorder.LOWERED));

    btCourseModify = new GemButton("Modification Cours");

    GemPanel bas = new GemPanel();
    bas.setLayout(new BorderLayout());
    bas.add(pc, BorderLayout.CENTER);
    bas.add(btCourseModify, BorderLayout.SOUTH);
    bas.setBorder(new BevelBorder(BevelBorder.LOWERED));

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(haut, 0, 0, 1, 1, GridBagHelper.BOTH, 1.0, 0.4);
    gb.add(new GemLabel("Liste des cours correspondants au(x) module(s)"), 0, 1, 1, 1, GridBagHelper.CENTER);
    gb.add(bas, 0, 2, 1, 1, GridBagHelper.BOTH, 1.0, 0.6);

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
  /*

  public void itemStateChanged(ItemEvent e)

  {
  if (e.getSource()  == module)
  {
  if (actionListener != null)
  actionListener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"Modifier"));
  }
  else if (e.getSource()  == cours)
  {
  if (actionListener != null)
  actionListener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"CoursModif"));
  }
  }
   */

  public int getSelectedModule() {
    return moduleTable.getSelectedRow();
  }

  public void addModule(ModuleOrder cmm) {
    module.addItem(cmm);
  }

  public void changeModule(int i, ModuleOrder cmm) {
    module.modItem(i, cmm);
  }

  public void setModifModule(boolean b) {
    modifModule = b;
  }

  public void setModifCours(boolean b) {
    modifCourse = b;
  }

  public void removeModule(int i) {
    module.deleteItem(i);
  }

  public int getSelectedCourse() {
    return courseTable.getSelectedRow();
  }

  public void addCourse(CourseOrder cmc) {
    courseOrderTableModel.addItem(cmc);
  }

  public void changeCourse(int i, CourseOrder cmc) {
    courseOrderTableModel.modItem(i, cmc);// ajouter une mise a jour de la salle
  }

  public void removeCourse(int i) {
    courseOrderTableModel.deleteItem(i);
  }

  public void setMember(Person p) {
    member.setText(p.toString());
  }

  public void clear() {
    member.setText("");
    module.clear();
    courseOrderTableModel.clear();
  }
}
