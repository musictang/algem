/*
 * @(#)MemberFollowUpEditor.java	2.8.f 24/05/13
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
package net.algem.contact.member;

import java.awt.Cursor;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.contact.PersonFile;
import net.algem.course.Course;
import net.algem.planning.FollowUpDlg;
import net.algem.planning.PlanningException;
import net.algem.planning.ScheduleRangeObject;
import net.algem.planning.ScheduleRangeTableModel;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.MessagePopup;

/**
 * Follow up list controller for a member.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.f
 */
public class MemberFollowUpEditor
        extends FileTab
        implements ActionListener {

  private boolean loaded;
  private PersonFile personFile;
  private GemButton btModify;
  private GemButton btDelete;
  private ScheduleRangeTableModel scheduleRange;
  private JTable rangeTable;
  private MemberService memberService;

  public MemberFollowUpEditor(GemDesktop _desktop, PersonFile pf) {
    super(_desktop);
    memberService = new MemberService(dataCache.getDataConnection());
    personFile = pf;

    scheduleRange = new ScheduleRangeTableModel(dataCache);
    rangeTable = new JTable(scheduleRange);
    rangeTable.setAutoCreateRowSorter(true);

    TableColumnModel cm = rangeTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(50);
    cm.getColumn(1).setPreferredWidth(20);
    cm.getColumn(2).setPreferredWidth(20);
    cm.getColumn(3).setPreferredWidth(200);
    cm.getColumn(4).setPreferredWidth(250);

    JScrollPane pm = new JScrollPane(rangeTable);

    btModify = new GemButton(GemCommand.VIEW_EDIT_CMD);// consulter/modifier
    btDelete = new GemButton(GemCommand.DELETE_CMD);

    btModify.addActionListener(this);
    btDelete.addActionListener(this);

    setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(pm, 0, 0, 3, 2, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(btModify, 0, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(btDelete, 1, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public void load() {
    loaded = true;

    Vector<ScheduleRangeObject> v = null;
    try {
      v = memberService.findFollowUp(personFile.getId());
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    if (v != null) {
      for (int i = 0; i < v.size(); i++) {
        scheduleRange.addItem(v.elementAt(i));
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {

    if (rangeTable.getSelectedRow() < 0) {
      return;
    }
    int n = rangeTable.convertRowIndexToModel(rangeTable.getSelectedRow());

    Object src = evt.getSource();

    if (src == btModify) {
      try {
        modification(n);
      } catch (SQLException e) {
        GemLogger.logException("modification suivi pédagogique", e, this);
      } catch (PlanningException pe) {
        GemLogger.logException("modification suivi pédagogique", pe, this);
      }
    } else if (src == btDelete) {
      try {
        suppression(n);
      } catch (SQLException e) {
        GemLogger.logException("suppression suivi pédagogique", e, this);
      }
    }
  }

  void modification(int n) throws PlanningException, SQLException {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    ScheduleRangeObject p = (ScheduleRangeObject) scheduleRange.getItem(n);

    Course c = p.getCourse();

    if (c.isCollective() && p.getNote() <= 0) { // ?? <= 0
      MessagePopup.error(this, MessageUtil.getMessage("follow.up.modification.warning"));
    } else {
      FollowUpDlg dlg = new FollowUpDlg(desktop, p, c.getTitle());
      setCursor(Cursor.getDefaultCursor());
      dlg.entry();
      if (!dlg.isValidation()) {
        return;
      }
      planningService.updateFollowUp(p, dlg.getText());
      p.setFollowUp(dlg.getText());
      scheduleRange.modItem(n, p);
    }

    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  void insertion(int n) throws SQLException {
    //plages.addItem(v);
  }

  void suppression(int n) throws SQLException {

    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    ScheduleRangeObject p = (ScheduleRangeObject) scheduleRange.getItem(n);
    try {
      dc.setAutoCommit(false);
      planningService.deleteFollowUp(p);
      p.setNote(0);
      p.setFollowUp(null);
      scheduleRange.modItem(n, p);
      dc.commit();
    } catch (Exception e1) {
      GemLogger.logException("transaction update", e1);
      dc.rollback();
    } finally {
      dc.setAutoCommit(true);
    }
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }
}
