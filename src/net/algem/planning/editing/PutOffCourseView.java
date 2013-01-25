/*
 * @(#)PutOffCourseView.java	2.7.a 23/11/12
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

import java.awt.AWTEventMulticaster;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Date;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.planning.Hour;
import net.algem.planning.HourRangePanel;
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
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class PutOffCourseView
        extends GemPanel
{

  private DataCache dataCache;
  private int sid; // salle id
  private GemField courseLabel;
  private Hour hStart, hEnd;
  private DateFrField currentDay;
  private DateFrField newDay;
  private GemField currentHour; // heure before
  private GemField currentRoom; // salle before
  private HourRangePanel newHour; // heure after
  private RoomChoice newRoom; // salle after
  private ActionListener actionListener;
  private Insets padding;

  public PutOffCourseView(DataCache _dc) {
    dataCache = _dc;
    setBorder(ModifPlanView.DEFAULT_BORDER);
    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    courseLabel = new GemField(25);
    courseLabel.setEditable(false);
    currentDay = new DateFrField();
    currentDay.setEditable(false);
    newDay = new DateFrField();
    currentHour = new GemField(20);
    currentHour.setEditable(false);
    newHour = new HourRangePanel();

    currentRoom = new GemField(20);
    currentRoom.setEditable(false);
    newRoom = new RoomChoice(dataCache.getList(Model.Room));

    padding = new Insets(2, 2, 2, 2);

    gb.add(new GemLabel(BundleUtil.getLabel("Course.label")), 0, 0, 1, 1, padding, GridBagHelper.WEST);
    gb.add(courseLabel, 1, 0, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Current.date.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(currentDay, 1, 1, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("New.date.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(newDay, 1, 2, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Current.hour.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(currentHour, 1, 3, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("New.hour.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(newHour, 1, 4, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Current.room.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(currentRoom, 1, 5, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("New.room.label")), 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(newRoom, 1, 6, 1, 1, padding, GridBagHelper.WEST);
  }

  void setHour(Hour start, Hour end) {
    currentHour.setText(BundleUtil.getLabel("Hour.From.label") + " "
            + start + " " + BundleUtil.getLabel("Hour.From.label") + " " + end);
    hStart = new Hour(start);
    hEnd = new Hour(end);
    newHour.setStart(start);
    newHour.setEnd(end);
  }

  Hour getOldHourStart() {
    return hStart;
  }

  Hour getOldHourEnd() {
    return hEnd;
  }

  Hour getHourEnd() {
    return newHour.getEnd();
  }

  Hour getHourStart() {
    return newHour.getStart();
  }

  void setRoom(int id) {
    sid = id;
    newRoom.setKey(sid);
    currentRoom.setText(((Room) newRoom.getSelectedItem()).getName());
  }

  int getRoomId() {
    return newRoom.getKey();
  }

  void setStart(Date d) {
    currentDay.set(d);
  }

  DateFr getStart() {
    return currentDay.getDateFr();
  }

  void setNewStart(Date d) {
    newDay.set(d);
  }

  DateFr getNewStart() {
    return newDay.getDateFr();
  }

  void setTitle(String s) {
    courseLabel.setText(s);
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }
}
