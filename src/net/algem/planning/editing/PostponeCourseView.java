/*
 * @(#)PostponeCourseView.java	2.8.w 02/09/14
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

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.planning.*;
import net.algem.room.Room;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.GemList;
import net.algem.util.ui.*;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 1.0a 07/07/1999
 */
public class PostponeCourseView
        extends GemPanel
{

  private int roomId; // room id
  private GemField courseLabel;
  private DateFrField currentDate;
  private DateFrField newDate;
  private HourRangePanel currentTime; 
  private GemField currentRoom;
  private HourField newStartTime;
  /** Must be included in the origin range. */
  private HourRangePanel postPoneRange;
  private RoomChoice newRoom;
  private Insets padding;
  private PlanningService service;
  private ScheduleObject orig;

  public PostponeCourseView(GemList<Room> roomList, PlanningService service) 
    {

    this.service = service;
    setBorder(ModifPlanView.DEFAULT_BORDER);
    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    courseLabel = new GemField(ModifPlanView.DEF_FIELD_WIDTH);
    courseLabel.setEditable(false);
    currentDate = new DateFrField();
    currentDate.setEditable(false);
    newDate = new DateFrField();
    currentTime = new HourRangePanel();
    currentTime.setBorder(GemField.getDefaultBorder());
    currentTime.setEditable(false);
    newStartTime = new HourField();

    postPoneRange = new HourRangePanel();
    
    postPoneRange.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseExited(MouseEvent e) {
        checkRange();
      }
    });
    
    currentRoom = new GemField(ModifPlanView.DEF_FIELD_WIDTH);
    currentRoom.setEditable(false);
    newRoom = new RoomChoice(roomList);
    Dimension prefSize = new Dimension(courseLabel.getPreferredSize().width, newRoom.getPreferredSize().height);
    newRoom.setPreferredSize(prefSize);
    padding = new Insets(2, 2, 2, 2);

    gb.add(new GemLabel(BundleUtil.getLabel("Course.label")), 0, 0, 1, 1, padding, GridBagHelper.WEST);
    gb.add(courseLabel, 1, 0, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Current.date.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(currentDate, 1, 1, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("New.date.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(newDate, 1, 2, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Current.hour.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(currentTime, 1, 3, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Range.to.postpone")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(postPoneRange, 1, 4, 1, 1, padding, GridBagHelper.WEST);
    
    gb.add(new GemLabel(BundleUtil.getLabel("New.hour.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(newStartTime, 1, 5, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Current.room.label")), 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(currentRoom, 1, 6, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("New.room.label")), 0, 7, 1, 1, GridBagHelper.WEST);
    gb.add(newRoom, 1, 7, 1, 1, padding, GridBagHelper.WEST);
  }

  /**
   * Sets fields with schedule's data.
   * @param s schedule
   * @param noRange if true, range must be disabled
   */
  void set(ScheduleObject s, boolean noRange) {
    orig = s;
    courseLabel.setText(s.getScheduleLabel());
    currentDate.set(s.getDate().getDate());
    newDate.set(s.getDate().getDate());
    setHour(s.getStart(), s.getEnd());
    setRoom(s.getIdRoom());
    
    postPoneRange.setEditable(!noRange);
    
  }
  
  private void setHour(Hour start, Hour end) {
    currentTime.setStart(start);
    currentTime.setEnd(end);
    newStartTime.set(start);
    postPoneRange.setStart(start);
    postPoneRange.setEnd(end);
    postPoneRange.setMin(start.toMinutes());
    postPoneRange.setMax(end.toMinutes());
  }

  Hour getHourEnd() {//XXX
    return newStartTime.get().end(postPoneRange.getLength());
  }

  private void setRoom(int id) {
    roomId = id;
    newRoom.setKey(roomId);
    currentRoom.setText(((Room) newRoom.getSelectedItem()).getName());
  }


  HourRangePanel getRange() {
    return postPoneRange;
  }

  ScheduleObject getSchedule() {

    ScheduleObject s = new ScheduleObject()
    {

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
    s.setEnd(getHourEnd());
    s.setIdRoom(newRoom.getKey());

    return s;
  }

  /**
   * Checks range on the fly.
   */
  void checkRange() {
    if(!postPoneRange.isEditable()) {
      return;
    }
    
    try {
      ScheduleObject range = new CourseSchedule();
      range.setStart(postPoneRange.getStart());
      range.setEnd(postPoneRange.getEnd());
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
    postPoneRange.setStart(orig.getStart());
    postPoneRange.setEnd(orig.getEnd());
  }

}
