/*
 * @(#)ModifPlanTeacherDlg.java	2.8.w 08/09/14
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

import java.util.Date;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.algem.contact.teacher.SubstituteTeacherList;
import net.algem.planning.CourseSchedule;
import net.algem.planning.DateFr;
import net.algem.planning.PlanningService;
import net.algem.planning.ScheduleObject;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;

/**
 * comment
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @author <a href="mailto:damien.loustau@gmail.com">Damien Loustau</a>
 * @version 2.8.w
 * @since 1.0a 07/07/1999
 */
public class ModifPlanTeacherDlg
        extends ModifPlanDlg
{

  private int origTeacher;
  private ModifPlanTeacherView view;

  public ModifPlanTeacherDlg(GemDesktop desktop, SubstituteTeacherList substitutes, PlanningService service)  {
    super(desktop.getFrame());
    
    view = new ModifPlanTeacherView(desktop.getDataCache(), substitutes, service);
    validation = false;

    dlg = new JDialog(desktop.getFrame(), true);
    addContent(view, "Schedule.teacher.modification.title");
    dlg.setSize(450,450);
  }

  @Override
  public void entry() {
    dlg.setVisible(true);
  }

  @Override
  public boolean isEntryValid() {
    
    String wt = BundleUtil.getLabel("Warning.label");
    
    if (origTeacher == view.getId()) {
      JOptionPane.showMessageDialog(dlg, MessageUtil.getMessage("same.teacher.choice"), wt, JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (view.getEnd().before(view.getStart())) {
      JOptionPane.showMessageDialog(dlg, MessageUtil.getMessage("end.date.invalid.choice"), wt, JOptionPane.ERROR_MESSAGE);
      return false;
    }

    if (view.getHourEnd().le(view.getHourStart())) {
      JOptionPane.showMessageDialog(dlg, MessageUtil.getMessage("hour.range.error"), wt, JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  @Override
  public boolean isValidate() {
    return validation;
  }
  
  public void set(ScheduleObject schedule) {
    view.setTitle(schedule.getScheduleLabel());
    view.setId(schedule.getIdPerson());
    origTeacher = schedule.getIdPerson();
    view.setTime(schedule);
  }

  public void setDate(Date d) {
    view.setStart(d);
    view.setEnd(d);

  }
  
  ScheduleObject getSchedule() {
    ScheduleObject s = new CourseSchedule();
    s.setIdPerson(view.getId());
    s.setStart(view.getHourStart());
    s.setEnd(view.getHourEnd());
    return s;
  }
  
  Boolean getMemoAbs() {
    return view.getMemoAbs();    
  }
  
  Boolean getMemoRepl() {
    return view.getMemoRepla();
  }
  
  String getNoteAbs() {
    return view.getNoteAbs();
  }

  DateFr getStart() {
    return view.getStart();
  }

  DateFr getEnd() {
    return view.getEnd();
  }

}