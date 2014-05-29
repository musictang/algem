/*
 * @(#)TrainingScheduleView.java	2.8.t 14/04/14
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

package net.algem.planning;

import java.awt.GridBagLayout;
import java.util.List;
import net.algem.contact.teacher.TeacherChoice;
import net.algem.course.CourseChoice;
import net.algem.course.CourseChoiceTrainingModel;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 2.8.t 11/04/14
 */
public class TrainingScheduleView
extends GemPanel{

  private CourseChoice course;
  private DateTimeCtrl dateTimeCtrl;
  private RoomChoice room;
  private TeacherChoice teacher;

  public TrainingScheduleView(DataCache dataCache) {

    course = new CourseChoice(new CourseChoiceTrainingModel(dataCache.getList(Model.Course), true));

    dateTimeCtrl = new DateTimeCtrl();

    room = new RoomChoice(dataCache.getList(Model.Room));
    teacher = new TeacherChoice(dataCache.getList(Model.Teacher));

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new GemLabel(BundleUtil.getLabel("Training.course.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Room.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Teacher.label")), 0, 3, 1, 1, GridBagHelper.WEST);

    gb.add(course, 1, 0, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(dateTimeCtrl, 1, 1, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(room, 1, 2, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(teacher, 1, 3, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);

  }

  public void clear() {
    if (course.getItemCount() > 0) {
      course.setSelectedIndex(0);
    }
    dateTimeCtrl.clear();
  }

  int getCourse() {
    return course.getKey();
  }

  int getRoom() {
    return room.getKey();
  }

  int getTeacher() {
    return teacher.getKey();
  }

  List<GemDateTime> getDates() {
    return dateTimeCtrl.getRanges();
  }

}
