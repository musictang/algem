/*
 * @(#)UpdateActionView.java	2.6.a 21/09/12
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

import java.awt.GridBagLayout;
import net.algem.course.Course;
import net.algem.planning.ActionView;
import net.algem.planning.ScheduleObject;
import net.algem.util.BundleUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 * Re-scheduling.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class UpdateActionView
        extends ActionView
{

  private GemField oldTeacher;
  private GemField oldRoom;

  public UpdateActionView(GemDesktop desktop, ScheduleObject plan) {
    super(desktop);
  }

  public void init(ScheduleObject plan) {

    course.setKey(((Course)plan.getActivity()).getId());
    course.setEnabled(false);
    hourPanel.setStart(plan.getStart());
    hourPanel.setEnd(plan.getEnd());
    teacher.setKey(plan.getIdPerson());
    room.setKey(plan.getPlace());

    oldTeacher = new GemField(20);
    oldTeacher.setText(plan.getPerson().getFirstnameName());
    oldTeacher.setEditable(false);

    oldRoom = new GemField(20);
    oldRoom.setText(plan.getRoom().getName());
    oldRoom.setEditable(false);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;
    gb.add(new GemLabel(BundleUtil.getLabel("Course.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.label ")), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Day.label")), 0, 2, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Periodicity.label")), 0, 3, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Hour.label")), 0, 4, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Current.teacher.label")), 0, 5, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Teacher.label")), 0, 6, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Current.room.label")), 0, 7, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Room.label")), 0, 8, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Sessions.number.label")), 0, 9, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Menu.holidays.label")), 0, 10, 1, 1, GridBagHelper.EAST);

    gb.add(course, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(datePanel, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(day, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(periodicity, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(hourPanel, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(oldTeacher, 1, 5, 1, 1, GridBagHelper.WEST);
    gb.add(teacher, 1, 6, 1, 1, GridBagHelper.WEST);
    gb.add(oldRoom, 1, 7, 1, 1, GridBagHelper.WEST);
    gb.add(room, 1, 8, 1, 1, GridBagHelper.WEST);
    gb.add(sessions, 1, 9, 1, 1, GridBagHelper.WEST);
    gb.add(vChoix, 1, 10, 1, 1, GridBagHelper.WEST);
  }

  @Override
  public void clear() {
    super.clear();
  }
}
