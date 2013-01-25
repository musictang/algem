/*
 * @(#)WorkshopScheduleView.java	2.7.a 22/11/12
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

import java.awt.GridBagLayout;
import java.util.Date;
import net.algem.contact.teacher.TeacherChoice;
import net.algem.course.CourseChoice;
import net.algem.course.CourseChoiceTypeActiveModel;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Workshop schedule planification view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class WorkshopScheduleView
        extends GemPanel
{

  private GemChoice workshop;
  private DateFrField date;
  private HourField start;
  private HourField end;
  private RoomChoice room;
  private TeacherChoice teacher;

  public WorkshopScheduleView(DataCache dataCache) {

    workshop = new CourseChoice(new CourseChoiceTypeActiveModel(dataCache.getList(Model.Workshop), true, true));
    date = new DateFrField();
    date.set(new DateFr(new Date()));
    start = new HourField();
    end = new HourField();
    room = new RoomChoice(dataCache.getList(Model.Room));
    teacher = new TeacherChoice(dataCache.getList(Model.Teacher));

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    GemPanel hours = new GemPanel();
    hours.add(start);
    hours.add(new GemLabel(BundleUtil.getLabel("Hour.To.label")));
    hours.add(end);

    gb.add(new GemLabel(BundleUtil.getLabel("Workshop.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.label")), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Hour.From.label")), 0, 2, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Room.label")), 0, 3, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Teacher.label")), 0, 4, 1, 1, GridBagHelper.EAST);

    gb.add(workshop, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(date, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(hours, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(room, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(teacher, 1, 4, 1, 1, GridBagHelper.WEST);

  }

  public int getWorkshop() {
    return workshop.getKey();
  }

  public int getRoom() {
    return room.getKey();
  }

  public int getTeacher() {
    return teacher.getKey();
  }

  public DateFr getDate() {
    return date.get();
  }

  public Hour getHourStart() {
    return start.get();
  }

  public Hour getHourEnd() {
    return end.get();
  }

  public void clear() {
    workshop.setSelectedIndex(0);
    date.setText(DateFr.NULLDATE);
    start.setText("00:00");
    end.setText("00:00");
    room.setSelectedIndex(0);
    teacher.setSelectedIndex(0);
  }
}
