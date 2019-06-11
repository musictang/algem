/*
 * @(#)SubstituteTeacherCtrl.java	2.8.w 08/07/14
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
package net.algem.contact.teacher;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.contact.*;
import net.algem.course.Course;
import net.algem.course.CourseChoice;
import net.algem.course.CourseChoiceActiveModel;
import net.algem.room.EstabChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.model.GemCloseVetoException;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Substitute teachers management.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.0n
 */
public class SubstituteTeacherCtrl
        extends GemPanel
        implements ActionListener
{

  private final GemDesktop desktop;
  private final DataCache dataCache;
  private EstabChoice estabChoice;
  private CourseChoice courseChoice;
  
  /** Substituted. */
  private TeacherChoice teacherChoice;
  
  /** Substitute. */
  private TeacherChoice substituteChoice;
  
  /** All teachers (active or not). */
  private JCheckBox jcbAll;
  
  private JCheckBox[] days;
  
  /** Favorite. */
  private JCheckBox favorite;
  
  private GemButton btAdd;
  private GemButton btModify;
  private GemButton btDelete;
  private GemButton btClose;
  
  private SubstituteTeacherTableModel substituteModel;
  private JTable table;

  public static String[] WEEK_DAYS = {"lu", "ma", "me", "je", "ve", "sa", "di"};

  public SubstituteTeacherCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    dataCache = this.desktop.getDataCache();

    substituteModel = new SubstituteTeacherTableModel();
    table = new JTable(substituteModel);
    table.setAutoCreateRowSorter(true);
    table.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1) {
          viewPerson();
        } else {
          int n = table.convertRowIndexToModel(table.getSelectedRow());
          SubstituteTeacher r = (SubstituteTeacher) substituteModel.getItem(n);
          setEditSubstitute(r);
        }
      }
    });

    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(45);
    cm.getColumn(1).setPreferredWidth(120);
    cm.getColumn(2).setPreferredWidth(120);
    cm.getColumn(3).setPreferredWidth(120);
    for (int i = 4; i < 12; i++) {
      cm.getColumn(i).setPreferredWidth(15);
    }

    JScrollPane pm = new JScrollPane(table);

    btDelete = new GemButton(GemCommand.DELETE_CMD);
    btAdd = new GemButton(GemCommand.ADD_CMD);
    btModify = new GemButton(GemCommand.MODIFY_CMD);
    btClose = new GemButton(GemCommand.CLOSE_CMD);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));
    buttons.add(btDelete);
    buttons.add(btAdd);
    buttons.add(btModify);
    buttons.add(btClose);

    btDelete.addActionListener(this);
    btAdd.addActionListener(this);
    btModify.addActionListener(this);
    btClose.addActionListener(this);

    estabChoice = new EstabChoice(dataCache.getList(Model.Establishment));
    courseChoice = new CourseChoice(new CourseChoiceActiveModel(dataCache.getList(Model.Course), true));//XXX active ?
    teacherChoice = new TeacherChoice(dataCache.getList(Model.Teacher), true);
    substituteChoice = new TeacherChoice(dataCache.getList(Model.Teacher), true);
    days = new JCheckBox[7];
    for (int i = 0; i < 7; i++) {
      days[i] = new JCheckBox(WEEK_DAYS[i]);
    }
    GemPanel p1 = new GemPanel();
    p1.setLayout(new BorderLayout());
    GemPanel p1_top = new GemPanel();
    p1_top.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(p1_top);
    GemPanel p1_bottom = new GemPanel();
    gb.insets = new Insets(4, 4, 4, 4);
    gb.add(new GemLabel(BundleUtil.getLabel("Establishment.label")), 0, 0, 1, 1, GridBagHelper.WEST, 1.0, 1.0);
    gb.add(estabChoice, 1, 0, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 1.0);
    gb.add(new GemLabel(BundleUtil.getLabel("Course.label")), 2, 0, 1, 1, GridBagHelper.WEST, 1.0, 1.0);
    gb.add(courseChoice, 3, 0, 2, 1, GridBagHelper.HORIZONTAL, 1.0, 1.0);
    gb.add(new GemLabel(BundleUtil.getLabel("Substituted.label")), 0, 1, 1, 1, GridBagHelper.WEST, 1.0, 1.0);
    gb.add(teacherChoice, 1, 1, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 1.0);
    gb.add(new GemLabel(BundleUtil.getLabel("Substitute.label")), 2, 1, 1, 1, GridBagHelper.WEST, 1.0, 1.0);
    gb.add(substituteChoice, 3, 1, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 1.0);
    jcbAll = new JCheckBox(BundleUtil.getLabel("Every.label"));
    jcbAll.setActionCommand(GemCommand.ALL_CMD);
    jcbAll.addActionListener(this);
    gb.add(jcbAll, 4, 1, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 1.0);
    favorite = new JCheckBox(BundleUtil.getLabel("Favorite.label"));
    gb.add(favorite, 0, 5, 5, 1, GridBagHelper.HORIZONTAL, 1.0, 1.0);
    for (int i = 0; i < 7; i++) {
      days[i] = new JCheckBox(WEEK_DAYS[i]);
      p1_bottom.add(days[i]);
    }

    p1.add(p1_top, BorderLayout.NORTH);
    p1.add(p1_bottom, BorderLayout.SOUTH);
    setLayout(new GridBagLayout());
    gb = new GridBagHelper(this);

    gb.add(pm, 0, 0, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(p1, 0, 1, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(buttons, 0, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    load();
  }

  public void load() {
    try {
      Vector<SubstituteTeacher> v = SubstituteTeacherIO.findAll(DataCache.getDataConnection());
      for (int i = 0; i < v.size(); i++) {
        substituteModel.addItem(v.elementAt(i));
      }
      if (v != null && !v.isEmpty()) {
        //teacherChoice.setKey(v.elementAt(0).getTeacher().getId());
        teacherChoice.setSelectedItem(v.elementAt(0).getTeacher());
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  public void setEditSubstitute(SubstituteTeacher r) {
    estabChoice.setKey(r.getEstablishment());
    
    courseChoice.setKey(r.getCourse() == null ? 0 : r.getCourse().getId());
    teacherChoice.setKey(r.getTeacher() == null ? 0 : r.getTeacher().getId());
    int id = r.getSubstitute() == null ? 0 : r.getSubstitute().getId();
    substituteChoice.setKey(id);
    
    // si le remplacant fait partie des profs non actifs
    if (substituteChoice.getKey() == 0) {
      jcbAll.setSelected(true);
      actionPerformed(new ActionEvent(jcbAll, ActionEvent.ACTION_PERFORMED, jcbAll.getActionCommand()));
      substituteChoice.setKey(id);
    }
    for (int i = 0; i < 7; i++) {
      days[i].setSelected(r.daysToArray()[i]);
    }
    favorite.setSelected(r.isFavorite());
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    
    if (cmd.equals(GemCommand.CLOSE_CMD)) {
      try {
        desktop.getSelectedModule().close();
      } catch (GemCloseVetoException ex) {
         System.err.println(ex.getMessage());
      }
    }

    int n = -1;
    if (!cmd.equals(GemCommand.ADD_CMD) && !cmd.equals(GemCommand.ALL_CMD)) {

      int selectedRow = table.getSelectedRow();
      if (selectedRow < 0) {
        return;
      }
      n = table.convertRowIndexToModel(selectedRow);
      if (n < 0) {
        return;
      }
    }

    if (cmd.equals(GemCommand.MODIFY_CMD)) {
      try {
        modification(n);
      } catch (Exception e) {
        GemLogger.logException("modification remplacant", e, this);
      }
    } else if (cmd.equals(GemCommand.DELETE_CMD)) {
      try {
        suppression(n);
        clear();
      } catch (Exception e) {
        GemLogger.logException("suppresion remplacant", e, this);
      }
    } else if (cmd.equals(GemCommand.ADD_CMD)) {
      try {
        insertion();
        clear();
      } catch (Exception e) {
        GemLogger.logException("insertion remplacant", e, this);
      }
    } else if (cmd.equals(GemCommand.ALL_CMD)) {
      if (((JCheckBox) evt.getSource()).isSelected()) {
        substituteChoice.setModel(new TeacherChoiceModel(dataCache.getList(Model.Teacher)));
      } else {
        substituteChoice.setModel(new TeacherActiveChoiceModel(dataCache.getList(Model.Teacher), true));
      }
    }

  }

  void modification(int n) throws SQLException {
    SubstituteTeacher r = (SubstituteTeacher) substituteModel.getItem(n);
    SubstituteTeacher old = new SubstituteTeacher();
    old.setCourse(r.getCourse());
    old.setEstablishment(r.getEstablishment());
    old.setTeacher(r.getTeacher());
    old.setSubstitute(r.getSubstitute());

    String j = new String();
    for (JCheckBox jcb : days) {
      j += jcb.isSelected() ? "1" : "0";
    }
    r.setEstablishment(estabChoice.getKey());
    r.setDays(j);
    r.setFavorite(favorite.isSelected());
    r.setSubstitute((Person) substituteChoice.getSelectedItem());

    SubstituteTeacherIO.update(old, r, DataCache.getDataConnection());
    substituteModel.modItem(n, r);
    clear();
  }

  void insertion() throws SQLException {

    int estab = estabChoice.getKey();
    Course c = (Course) courseChoice.getSelectedItem();
    Person dp = (Person) teacherChoice.getSelectedItem();
    Person dpr = (Person) substituteChoice.getSelectedItem();
    String j = new String();
    for (JCheckBox jcb : days) {
      j += jcb.isSelected() ? "1" : "0";
    }
    boolean b = favorite.isSelected();
    SubstituteTeacher r = new SubstituteTeacher(estab, c, dp, dpr, j, b);
    if (null == SubstituteTeacherIO.find(r, DataCache.getDataConnection())) {
      SubstituteTeacherIO.insert(r, DataCache.getDataConnection());
    } else {
      MessagePopup.information(null, "Ce remplacement exitste déjà");
      return;
    }
    substituteModel.addItem(r);
    clear();
  }

  public void viewPerson() {
    int n = table.convertRowIndexToModel(table.getSelectedRow());
    if (n < 0) {
      return;
    }

    SubstituteTeacher r = (SubstituteTeacher) substituteModel.getItem(n);
    // il est nécessaire de récupérer les adresses, tel et email éventuels du contact
    try {
      Person t = (Teacher) DataCache.findId(r.getSubstitute().getId(), Model.Teacher);
      Contact c = new Contact(t);
      ContactIO.complete(c, DataCache.getDataConnection());
//    Contact c = ContactIO.findId(r.getSubstitute().getId(), dataCache.getDataConnection());
      PersonFile f = new PersonFile(c);
      ((PersonFileIO) DataCache.getDao(Model.PersonFile)).complete(f);

      PersonFileEditor editor = new PersonFileEditor(f);
      desktop.addModule(editor);
    } catch (SQLException ex) {
      GemLogger.logException("complete dossier remplacant", ex);
    }
  }

  void suppression(int n) throws SQLException {
    SubstituteTeacher r = (SubstituteTeacher) substituteModel.getItem(n);
    SubstituteTeacherIO.delete(r, DataCache.getDataConnection());

    substituteModel.deleteItem(n);
    clear();
  }

  public void clear() {
  }
}
