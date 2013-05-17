/*
 * @(#)TeacherBreakView.java	2.7.a 22/11/12
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
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import net.algem.course.CourseChoice;
import net.algem.course.CourseChoiceTeacherModel;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * View for editing teacher breaks.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 02/09/2001
 */
public class TeacherBreakView
        extends GemBorderPanel {

  private int teacher;
  private DataCache dataCache;
  private CourseChoice course;
  private DateFrField dateStart;
  private DateFrField dateEnd;
  private HourField hStart;
  private HourField hEnd;
  private RoomChoice room;
  private JLabel msgLabel;

  public TeacherBreakView(DataCache dcache, PlanningService service, int t) {

    teacher = t;
    dataCache = dcache;
    course = new CourseChoice(new CourseChoiceTeacherModel(service.getCourseByTeacher(teacher, dataCache.getStartOfPeriod().toString())));
    room = new RoomChoice(dataCache.getList(Model.Room), true);

    GemPanel content = new GemPanel();

    content.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(content);
    gb.insets = GridBagHelper.SMALL_INSETS;
    gb.add(new JLabel(BundleUtil.getLabel("Course.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(course, 1, 0, 3, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Room.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(room, 1, 1, 3, 1, GridBagHelper.WEST);

    dateStart = new DateFrField();
    dateStart.setToolTipText("<html>" + MessageUtil.getMessage("break.beginning.date.tip") + "</html>");
    gb.add(new JLabel(BundleUtil.getLabel("Date.From.label") + " *"), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(dateStart, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Date.To.label")), 2, 2, 1, 1, GridBagHelper.WEST);
    gb.add(dateEnd = new DateFrField(), 3, 2, 1, 1, GridBagHelper.WEST);

    gb.add(new JLabel(BundleUtil.getLabel("Hour.From.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(hStart = new HourField(), 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Hour.To.label")), 2, 3, 1, 1, GridBagHelper.WEST);
    gb.add(hEnd = new HourField(), 3, 3, 1, 1, GridBagHelper.WEST);

    setLayout(new BorderLayout());
    add(content, BorderLayout.CENTER);
    GemBorderPanel messagePanel = new GemBorderPanel(BorderFactory.createEmptyBorder());
    msgLabel = new JLabel("<html>* " + MessageUtil.getMessage("break.beginning.date.warning") + "</html>");
    messagePanel.add(msgLabel);
    add(messagePanel, BorderLayout.SOUTH);

  }

  public void set(CourseSchedule plan) {
    room.setKey(plan.getPlace());
    course.setKey(plan.getCourse().getId());
    dateStart.set(plan.getDate());
    dateEnd.set(plan.getDate());
    hStart.set(plan.getStart());
    hEnd.set(plan.getStart());
    msgLabel.setText(null);
  }

  public void lock(boolean k) {
    course.setEnabled(!k);
    room.setEnabled(!k);
    dateStart.setEditable(!k);
  }

  public DateFr getDateStart() {
    return dateStart.get();
  }

  public DateFr getDateEnd() {
    return dateEnd.get();
  }

  public Hour getHourStart() {
    return hStart.get();
  }

  public Hour getHourEnd() {
    return hEnd.get();
  }

  public int getCourse() {
    return course.getKey();
  }

  public int getRoom() {
    return room.getKey();
  }

  public void clear() {
  }
}
