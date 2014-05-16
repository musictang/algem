/*
 * @(#)GroupPassRehearsalView.java	2.8.t 16/05/14
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
package net.algem.group;

import java.awt.GridBagLayout;
import java.util.Date;
import net.algem.planning.DateFr;
import net.algem.planning.DateRangePanel;
import net.algem.planning.Hour;
import net.algem.planning.HourRangePanel;
import net.algem.planning.day.DayChoice;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Entry panel for group pass rehearsal scheduling.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 */
public class GroupPassRehearsalView
        extends GemPanel
{

  private DataCache dc;
  private DateRangePanel date;
  private HourRangePanel hour;
  private RoomChoice room;
  private DayChoice day;

  public GroupPassRehearsalView(DataCache _dc) {
    dc = _dc;
    date = new DateRangePanel(DateRangePanel.RANGE_DATE, null);
    hour = new HourRangePanel(60 * 8);
    room = new RoomChoice(dc.getList(Model.Room));
    day = new DayChoice();

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new GemLabel(BundleUtil.getLabel("Date.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Day.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Hour.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Room.label")), 0, 3, 1, 1, GridBagHelper.WEST);

    gb.add(date, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(day, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(hour, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(room, 1, 3, 1, 1, GridBagHelper.WEST);
  }

  public int getRoom() {
    return room.getKey();
  }

  public int getDay() {
    return day.getDay();
  }

  public DateFr getDateStart() {
    return date.getStartFr();
  }

  public DateFr getDateEnd() {
    return date.getEndFr();
  }

  public Hour getHourStart() {
    return hour.getStart();
  }

  public Hour getHourEnd() {
    return hour.getEnd();
  }

  public void clear() {
    date.setStart(new Date());
    date.setEnd(new Date());
    hour.setStart("09:00");
    hour.setEnd("10:00");
    room.setSelectedIndex(0);
    day.setSelectedIndex(0);
  }
}
