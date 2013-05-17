/*
 * @(#)PostponeCourseView.java	2.8.a 23/04/13
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
import java.awt.Insets;
import net.algem.planning.*;
import net.algem.room.Room;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 1.0a 07/07/1999
 */
public class PostponeCourseView
        extends GemPanel
{

  private DataCache dataCache;
  private int sid; // salle id
  private GemField courseLabel;
  private DateFrField currentDate;
  private DateFrField newDate;
  private GemField currentHour; // heure before
  private GemField currentRoom; // salle before
  private HourField newStartTime; // heure after
  /** Must be included in original range. */
  private HourRangePanel postPoneRange;
  private RoomChoice newRoom; // salle after
  private Insets padding;
  

  public PostponeCourseView(DataCache _dc) {
    dataCache = _dc;
    setBorder(ModifPlanView.DEFAULT_BORDER);
    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    courseLabel = new GemField(25);
    courseLabel.setEditable(false);
    currentDate = new DateFrField();
    currentDate.setEditable(false);
    newDate = new DateFrField();
    currentHour = new GemField(20);
    currentHour.setEditable(false);
    newStartTime = new HourField();
    postPoneRange = new HourRangePanel();
    
    currentRoom = new GemField(20);
    currentRoom.setEditable(false);
    newRoom = new RoomChoice(dataCache.getList(Model.Room));

    padding = new Insets(2, 2, 2, 2);

    gb.add(new GemLabel(BundleUtil.getLabel("Course.label")), 0, 0, 1, 1, padding, GridBagHelper.WEST);
    gb.add(courseLabel, 1, 0, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Current.date.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(currentDate, 1, 1, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("New.date.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(newDate, 1, 2, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Current.hour.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(currentHour, 1, 3, 1, 1, padding, GridBagHelper.WEST);

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
    courseLabel.setText(s.getScheduleLabel());
    currentDate.set(s.getDate().getDate());
    newDate.set(s.getDate().getDate());
    setHour(s.getStart(), s.getEnd());
    setRoom(s.getPlace());
    
    postPoneRange.setEditable(!noRange);
    
  }
  
  private void setHour(Hour start, Hour end) {
    currentHour.setText(BundleUtil.getLabel("Hour.From.label") + " "
            + start + " " + BundleUtil.getLabel("Hour.To.label") + " " + end);
    newStartTime.set(start);
    postPoneRange.setStart(start);
    postPoneRange.setEnd(end);
    postPoneRange.setMin(start.toMinutes());
    postPoneRange.setMax(end.toMinutes());
  }

  Hour getHourEnd() {
    return newStartTime.get().end(postPoneRange.getLength());
  }

  private void setRoom(int id) {
    sid = id;
    newRoom.setKey(sid);
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
    s.setPlace(newRoom.getKey());

    return s;
  }

}
