/*
 * @(#) DeferRehearsalView.java Algem 2.15.8 26/03/2018
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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
 */
package net.algem.planning.editing;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import net.algem.planning.DateFrField;
import net.algem.planning.Hour;
import net.algem.planning.HourField;
import net.algem.planning.HourRangePanel;
import net.algem.planning.PlanningService;
import net.algem.planning.ScheduleObject;
import net.algem.room.Room;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.model.GemList;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.8
 * @since 2.15.8 26/03/2018
 */
public class DeferRehearsalView
  extends GemPanel {

  private int roomId; // room id
  private GemField scheduleLabel;
  private DateFrField currentDate;
  private DateFrField newDate;
  private HourRangePanel currentTime;
  private GemField currentRoom;
  private HourField newStartTime;
  private RoomChoice newRoom;
  private Insets padding;
  private PlanningService service;
  private ScheduleObject orig;

  public DeferRehearsalView(GemList<Room> roomList, PlanningService service) {

    this.service = service;
    setBorder(ModifPlanView.DEFAULT_BORDER);
    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    scheduleLabel = new GemField(ModifPlanView.DEF_FIELD_WIDTH);
    scheduleLabel.setEditable(false);
    currentDate = new DateFrField();
    currentDate.setEditable(false);
    newDate = new DateFrField();
    currentTime = new HourRangePanel();
    currentTime.setBorder(GemField.getDefaultBorder());
    currentTime.setEditable(false);
    newStartTime = new HourField();

    currentRoom = new GemField(ModifPlanView.DEF_FIELD_WIDTH);
    currentRoom.setEditable(false);
    newRoom = new RoomChoice(roomList);
    Dimension prefSize = new Dimension(scheduleLabel.getPreferredSize().width, newRoom.getPreferredSize().height);
    newRoom.setPreferredSize(prefSize);
    padding = new Insets(2, 2, 2, 2);

    gb.add(new GemLabel(BundleUtil.getLabel("Heading.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(scheduleLabel, 1, 0, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Current.date.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(currentDate, 1, 1, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("New.date.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(newDate, 1, 2, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Current.hour.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(currentTime, 1, 3, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("New.hour.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(newStartTime, 1, 4, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Current.room.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(currentRoom, 1, 5, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("New.room.label")), 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(newRoom, 1, 6, 1, 1, padding, GridBagHelper.WEST);
  }

  /**
   * Sets fields with schedule's data.
   *
   * @param s schedule
   */
  void set(ScheduleObject s) {
    orig = s;
    scheduleLabel.setText(s.getScheduleLabel());
    currentDate.set(s.getDate().getDate());
    newDate.set(s.getDate().getDate());
    setHour(s.getStart(), s.getEnd());
    setRoom(s.getIdRoom());
  }

  private void setHour(Hour start, Hour end) {
    currentTime.setStart(start);
    currentTime.setEnd(end);
    newStartTime.set(start);
  }

  private void setRoom(int id) {
    roomId = id;
    newRoom.setKey(roomId);
    currentRoom.setText(((Room) newRoom.getSelectedItem()).getName());
  }

  ScheduleObject getSchedule() {
    ScheduleObject s = new ScheduleObject() {

      @Override
      public String getScheduleLabel() {
        return null;
      }

      @Override
      public String getScheduleDetail() {
        return null;
      }
    };

    s.setDate(newDate.getDateFr());
    s.setStart(newStartTime.get());
    int duration = currentTime.getLength();
    s.setEnd(newStartTime.get().end(duration));
    s.setIdRoom(newRoom.getKey());

    return s;
  }
}
