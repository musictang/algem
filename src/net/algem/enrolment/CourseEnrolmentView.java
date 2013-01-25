/*
 * @(#)CourseEnrolmentView.java	2.7.a 22/11/12
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

import java.awt.GridBagLayout;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.group.Musician;
import net.algem.group.MusicianTableModel;
import net.algem.util.GemLogger;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * View the list of enrolled members.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class CourseEnrolmentView
        extends GemPanel
{

  private MusicianTableModel membersTableModel;
  private JTable memberTable;
  private EnrolmentService service;

  public CourseEnrolmentView(EnrolmentService service) {
    this.service = service;

    membersTableModel = new MusicianTableModel(service.getDataCache());
    memberTable = new JTable(membersTableModel);
    memberTable.setAutoCreateRowSorter(true);

    TableColumnModel cm = memberTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(30);
    cm.getColumn(1).setPreferredWidth(120);
    cm.getColumn(2).setPreferredWidth(120);
    cm.getColumn(3).setPreferredWidth(120);

    JScrollPane pm = new JScrollPane(memberTable);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(pm, 0, 0, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);

  }

  public void load(int id) {
    try {
      Vector<Musician> vm = service.findCourseMembers(id);
      for (Musician m : vm) {
        membersTableModel.addItem(m);
      }
    } catch (SQLException e) {
      GemLogger.logException(getClass().getName() +"#load", e);
    }
  }

  public void clear() {
    membersTableModel.clear();
  }
}
