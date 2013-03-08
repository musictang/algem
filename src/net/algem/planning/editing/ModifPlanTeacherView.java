/*
 * @(#)ModifPlanTeacherView.java	2.7.h 21/02/13
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
package net.algem.planning.editing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import net.algem.contact.teacher.*;
import net.algem.course.Course;
import net.algem.planning.CourseSchedule;
import net.algem.planning.Hour;
import net.algem.planning.HourRangePanel;
import net.algem.planning.ScheduleObject;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 * Teacher modification view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.h
 */
public class ModifPlanTeacherView
        extends ModifPlanView
        implements ActionListener
{

  private GemField before;
  private GemChoice after;
  private JCheckBox checkAll;
  private SubstituteTeacherChoice substitute;
  private HourRangePanel hourRange;
  private JCheckBox replacement;

  public ModifPlanTeacherView(DataCache dataCache, SubstituteTeacherList substitutes) {
    super(dataCache);
    before = new GemField(20);
    before.setEditable(false);
    after = new TeacherChoice(dataCache.getList(Model.Teacher));
    
    hourRange = new HourRangePanel();
    checkAll = new JCheckBox(BundleUtil.getLabel("Teacher.all.label"));
    checkAll.addActionListener(this);
//    gb.add(new GemLabel(BundleUtil.getLabel("Hour.From.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(hourRange, 1, 2, 3, 1, GridBagHelper.WEST);
    
    gb.add(new GemLabel(BundleUtil.getLabel("Current.teacher.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(before, 1, 3, 3, 1, GridBagHelper.WEST);
    
    gb.add(checkAll, 0, 4, 3, 1, GridBagHelper.WEST);
    
    gb.add(new GemLabel(BundleUtil.getLabel("New.teacher.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(after, 1, 5, 3, 1, GridBagHelper.WEST);
    //Optional display of substitutes
    if (substitutes != null && substitutes.getSize() > 0) {
      replacement = new JCheckBox(BundleUtil.getLabel("Substitute.activate.label"));
      replacement.addActionListener(this);
      substitute = new SubstituteTeacherChoice(substitutes);
      substitute.setEnabled(false);
      gb.add(replacement, 0, 6, 3, 1, GridBagHelper.WEST);
      gb.add(new GemLabel(BundleUtil.getLabel("Substitute.label")), 0, 7, 1, 1, GridBagHelper.WEST);
      gb.add(substitute, 1, 7, 3, 1, GridBagHelper.WEST);
    }
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
  
  void setTime(ScheduleObject s) {
    hourRange.setStart(s.getStart());
    hourRange.setEnd(s.getEnd());
    if (s instanceof CourseSchedule &&  ((Course) s.getActivity()).isCollective()) {
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
    }
    // afficher tous les profs ou seulement les actifs
    else if (src == checkAll) {
      if (((JCheckBox) src).isSelected()) {
        after.setModel(new TeacherChoiceModel(dataCache.getList(Model.Teacher)));
      } else {
        after.setModel(new TeacherActiveChoiceModel(dataCache.getList(Model.Teacher), true));
      }
    }
  }

  /**
   * Specifies if the list of substitutes is activated.
   * @return checkbox selection
   */
  private boolean withReplacement() {
    if (replacement != null) {
      return replacement.isSelected();
    }
    return false;
  }
}
