/*
 * @(#)ModifPlanTeacherView.java	2.8.w 02/09/14
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
package net.algem.planning.editing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JCheckBox;
import net.algem.contact.teacher.*;
import net.algem.course.Course;
import net.algem.planning.*;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JPanel;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Teacher modification view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 */
public class ModifPlanTeacherView
        extends ModifPlanView
        implements ActionListener
{

  private GemField before;
  private GemChoice after;
  private JCheckBox checkAll;
  private JCheckBox checkAbsence;
  private JCheckBox checkReplacement;
  private SubstituteTeacherChoice substitute;
  private HourRangePanel hourRange;
  private JCheckBox replacement;
  private PlanningService service;
  private ScheduleObject orig;
  private GemField noteAbs;
  private GemLabel noteLabel;
  protected GemDesktop desktop;

  public ModifPlanTeacherView(DataCache dataCache, SubstituteTeacherList substitutes, PlanningService service) {
    super(dataCache);
    this.service = service;
    before = new GemField(DEF_FIELD_WIDTH);
    before.setEditable(false);
    
    after = new TeacherChoice(dataCache.getList(Model.Teacher));
    Dimension prefSize = new Dimension(before.getPreferredSize().width, after.getPreferredSize().height);
    after.setPreferredSize(prefSize);
    hourRange = new HourRangePanel();
    hourRange.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseExited(MouseEvent e) {
        checkRange();
      }
    });
    checkAll = new JCheckBox(BundleUtil.getLabel("Teacher.all.label"));
    checkAll.setBorder(null);
    checkAll.addActionListener(this);

    gb.add(new GemLabel(BundleUtil.getLabel("Hour.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(hourRange, 1, 2, 1, 1, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Current.teacher.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(before, 1, 3, 1, 1, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("New.teacher.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(after, 1, 5, 1, 1, GridBagHelper.WEST);

    gb.add(checkAll, 1, 6, 1, 1, GridBagHelper.WEST);
    //Optional display of substitutes
    if (substitutes != null && substitutes.getSize() > 0) {
      replacement = new JCheckBox(BundleUtil.getLabel("Substitute.activate.label"));
      replacement.setBorder(null);
      replacement.addActionListener(this);
      substitute = new SubstituteTeacherChoice(substitutes);
      substitute.setPreferredSize(prefSize);
      substitute.setEnabled(false);
      
      gb.add(replacement, 1, 7, 1, 1, GridBagHelper.WEST);
      gb.add(new GemLabel(BundleUtil.getLabel("Substitute.label")), 0, 8, 1, 1, GridBagHelper.WEST);
      gb.add(substitute, 1, 8, 1, 1, GridBagHelper.WEST);
    }
    checkAbsence = new JCheckBox(BundleUtil.getLabel("Teacher.notif.absence"));
    checkAbsence.setBorder(null);
    checkAbsence.addActionListener(this);

    checkReplacement = new JCheckBox(BundleUtil.getLabel("Teacher.notif.replacement"));
    checkReplacement.setBorder(null);
    checkReplacement.addActionListener(this);
    
    gb.add(Box.createVerticalStrut(10), 0, 9, 2, 1, GridBagHelper.WEST);
    gb.add(checkAbsence, 0, 10, 2, 1, GridBagHelper.WEST);
    gb.add(checkReplacement, 0, 12, 2, 1, GridBagHelper.WEST);

  }

  @Override
  public void setId(int sid) {
    id = sid;
    after.setKey(id);
    // si le prof fait partie des non actifs
    if (after.getKey() == 0) {
      checkAll.setSelected(true);
      actionPerformed(new ActionEvent(checkAll, ActionEvent.ACTION_PERFORMED, checkAll.getActionCommand()));
      after.setKey(id);
    }

    before.setText(after.getSelectedItem().toString());
  }

  /**
   * Gets the selected teacher id or substitute id
   * if the list of substitutes is activated.
   *
   * @return a teacher id
   */
  @Override
  public int getId() {
    if (withReplacement()) {
      return substitute.getKey();
    } else {
      return after.getKey();
    }
  }
  
  public boolean getMemoAbs() {
    if (checkAbsence.isSelected()) {
      return true;
    }
    else {
      return false;
    }
  }
  
  public boolean getMemoRepla() {
    if (checkReplacement.isSelected()) {
      return true;
    }
    else {
      return false;
    }
  }
  
  public String getNoteAbs() {
    return noteAbs.getText();
  }

  void setTime(ScheduleObject s) {
    orig = s;
    hourRange.setStart(s.getStart());
    hourRange.setEnd(s.getEnd());
    if (s instanceof CourseSchedule && ((Course) s.getActivity()).isCollective()) {
      hourRange.setEditable(false);
    }
  }

  Hour getHourStart() {
    return hourRange.getStart();
  }

  Hour getHourEnd() {
    return hourRange.getEnd();
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    if (src == replacement) {
      substitute.setEnabled(withReplacement());
      after.setEnabled(!withReplacement());
    } // afficher tous les profs ou seulement les actifs
    else if (src == checkAll) {
      if (((JCheckBox) src).isSelected()) {
        after.setModel(new TeacherChoiceModel(dataCache.getList(Model.Teacher)));
      } else {
        after.setModel(new TeacherActiveChoiceModel(dataCache.getList(Model.Teacher), true));
      }
    } else if (src == checkAbsence) {
      loadNoteAbsence();
    }
  }

  /**
   * Specifies if the list of substitutes is activated.
   *
   * @return checkbox selection
   */
  private boolean withReplacement() {
    if (replacement != null) {
      return replacement.isSelected();
    }
    return false;
  }
  
  private AbsenceNotification getMemo() {
    AbsenceNotification memo = null;
    if (checkAbsence.isSelected() || checkReplacement.isSelected()) {
      memo = new AbsenceNotification();
      memo.setAbsence(checkAbsence.isSelected());
      memo.setReplacement(checkReplacement.isSelected());
      memo.setNote(noteAbs == null ? null : noteAbs.getText().trim());
    }
    return memo;
  }

  /**
   * Checks range on the fly.
   */
  void checkRange() {
    if (!hourRange.isEditable()) {
      return;
    }

    try {
      ScheduleObject range = new CourseSchedule();
      range.setStart(hourRange.getStart());
      range.setEnd(hourRange.getEnd());
      Vector<ScheduleTestConflict> v = service.checkRange(orig, range);
      if (v != null && v.size() > 0) {
        MessagePopup.warning(this, MessageUtil.getMessage("invalid.time.slot"));
        resetRange();
      }
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
      resetRange();
    }
  }

  private void resetRange() {
    hourRange.setStart(orig.getStart());
    hourRange.setEnd(orig.getEnd());
  }
  
  /**
   * Adds or remove a note component depending on the state of {@code checkAbsence}.
   */
  private void loadNoteAbsence() {
    if (checkAbsence.isSelected()) {
      if (noteAbs == null) {
        noteAbs = new GemField(DEF_FIELD_WIDTH);
        noteLabel = new GemLabel(BundleUtil.getLabel("Reason.label"));
        gb.add(noteLabel, 0, 11, 1, 1, GridBagHelper.WEST);
        gb.add(noteAbs, 1, 11, 1, 1, GridBagHelper.WEST);
        revalidate();
      } 
    } else {
      if (noteAbs != null) {
        Component c = gb.getContainer();
        if (c instanceof JPanel) {
          ((JPanel) gb.getContainer()).remove(noteLabel);
          ((JPanel) gb.getContainer()).remove(noteAbs);
          noteLabel = null;
          noteAbs = null;
          revalidate();
        }
      }
    }
  }
}
