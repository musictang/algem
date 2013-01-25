/*
 * @(#)WorkshopView.java	2.6.d 08/11/12
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
package net.algem.planning;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.contact.*;
import net.algem.course.Course;
import net.algem.course.WorkshopDlg;
import net.algem.course.WorkshopIO;
import net.algem.group.Musician;
import net.algem.group.MusicianTableModel;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.d
 */
public class WorkshopView
        extends GemPanel
        implements ActionListener
{

  private DataCache dataCache;
  private int id;
  private GemNumericField no;
  private GemField name;
  private GemField label;
  private GemNumericField places;
  private GemNumericField level;
  private JCheckBox active;
  
  private MusicianTableModel members;
  private JTable memberTable;
  private GemDesktop desktop;

  public WorkshopView(GemDesktop _desktop) {
    desktop = _desktop;
    dataCache = desktop.getDataCache();

    no = new GemNumericField(6);
    no.setEditable(false);
    
    name = new GemField(32, 32);
    label = new GemField(16, 16);
    
    places = new GemNumericField();
    level = new GemNumericField(2);
    active = new JCheckBox();

    members = new MusicianTableModel(dataCache);
    memberTable = new JTable(members);
    memberTable.addMouseListener(new MouseAdapter()
    {

      @Override
      public void mousePressed(MouseEvent e) {
        if (e.getClickCount() == 2) {
          viewPerson();
        }
      }
    });
    memberTable.setAutoCreateRowSorter(true);

    TableColumnModel cm = memberTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(30);
    cm.getColumn(1).setPreferredWidth(120);
    cm.getColumn(2).setPreferredWidth(120);
    cm.getColumn(3).setPreferredWidth(120);

    JScrollPane pm = new JScrollPane(memberTable);
    GemPanel p = new GemPanel();
    p.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(p);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new GemLabel("No"), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Name.label")), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Label.label")), 0, 2, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Place.number.label")), 0, 3, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Level.label")), 0, 4, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Active.label")), 0, 5, 1, 1, GridBagHelper.EAST);

    gb.add(no, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(name, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(label, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(places, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(level, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(active, 1, 5, 1, 1, GridBagHelper.WEST);
    
    this.setLayout(new BorderLayout());
    add(p, BorderLayout.NORTH);
    add(pm, BorderLayout.CENTER);

  }

  private void viewPerson() {
    int n = memberTable.getSelectedRow();
    if (n < 0) {
      return;
    }

    Musician m = (Musician) members.getItem(n);
    PersonFile pf = new PersonFile(new Contact(m));
    try {
      ((PersonFileIO) DataCache.getDao(Model.PersonFile)).complete(pf);
    } catch (SQLException ex) {
      GemLogger.logException("complete dossier atelier view", ex);
    }
    PersonFileEditor editor = new PersonFileEditor(pf);
    desktop.addModule(editor);
  }

  public void addItemListener(ItemListener l) {
    //liste.addItemListener(l);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals(GemCommand.ADD_CMD)) {
      WorkshopDlg d = new WorkshopDlg(this, "ajout adhérent", dataCache);
      d.show();
      if (d.isValidation()) {
        addRow(d.get());
      }
    } else if (e.getActionCommand().equals("Enlever")) {
      int n = memberTable.getSelectedRow();
      try {
        Musician p = (Musician) members.getItem(n);
        WorkshopIO.deleteAdherent(id, p.getId(), dataCache.getDataConnection());
        members.deleteItem(n);
      } catch (Exception ex) {
        GemLogger.logException("Erreur suppression", ex, this);
      }
    }
  }

  public void set(Course c) {
//    course.setId(c.getId());
    id = c.getId();
    no.setText(String.valueOf(c.getId()));
    name.setText(c.getTitle());
    label.setText(c.getLabel());
    places.setText(String.valueOf(c.getNPlaces()));
    level.setText(String.valueOf(c.getLevel()));
    active.setSelected(c.isActive());
  }
  
  public Course get() {
    Course g = new Course();
    g.setId(id);
    g.setTitle(name.getText());
    g.setLabel(label.getText());
    g.setNPlaces(Short.parseShort(places.getText()));
    g.setLevel(Short.parseShort(level.getText()));
    g.setActive(active.isSelected());
    
    return g;
  }

  public Vector<Person> getMember() {
    return members.getData();
  }

  public void addRow(Musician a) {
    if (a == null) {
      return;
    }

    members.addItem(a);
  }

  public void deleteCurrent() {
    //liste.deleteItem(liste.selected());
  }

  public void clear() {
    no.setText("");
    name.setText("");
    members.clear();
    //prof.setSelectedIndex(0);
  }

  public int getTeacherId() {
    return 0;	// prof.getKey();
  }

  public String getTeacher() {
    //XXXreturn prof.getLibel();
    return "";
  }

  public void setTeacherId(int p) {
    //prof.setKey(p);
  }

  public void setTeacher(String p) {
    //XXXprof.setLibel(p);
  }
}
