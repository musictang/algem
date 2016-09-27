/*
 * @(#)ModifPlanRangeView.java	2.9.4.0 26/03/2015
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
import net.algem.planning.*;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;


/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.0
 * @since 1.0a 07/07/1999
 */
public class ModifPlanRangeView
        extends GemPanel
{

  private int roomId;
  private GemField scheduleLabel;
  private Hour hStart, hEnd;
  private DateRangePanel dateRange;
  private HourRangePanel currentRange; // time before
  private HourRangePanel newRange; // time after

  public ModifPlanRangeView() {

    setBorder(ModifPlanView.DEFAULT_BORDER);
    setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    dateRange = new DateRangePanel();
    scheduleLabel = new GemField(ModifPlanView.DEF_FIELD_WIDTH);
    scheduleLabel.setEditable(false);
    currentRange = new HourRangePanel();
    currentRange.setBorder(GemField.getDefaultBorder());
    currentRange.setEditable(false);
    newRange = new HourRangePanel();

    gb.add(new GemLabel(BundleUtil.getLabel("Heading.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(scheduleLabel, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.From.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(dateRange, 1, 1, 1, 1, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Current.hour.range.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(currentRange, 1, 2, 1, 1, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("New.hour.range.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(newRange, 1, 3, 1, 1, GridBagHelper.WEST);

  }

  void setHour(Hour start, Hour end) {
    currentRange.setStart(start);
    currentRange.setEnd(end);
    hStart = new Hour(start);
    hEnd = new Hour(end);
    newRange.setStart(start);
    newRange.setEnd(end);
  }

  Hour getOldHourStart() {
    return hStart;
  }

  Hour getOldHourEnd() {
    return hEnd;
  }

  Hour getNewHourStart() {
    return newRange.getStart();
  }

  Hour getNewHourEnd() {
    return newRange.getEnd();
  }

  void setRoomId(int id) {
    roomId = id;
  }

  int getRoomId() {
    return roomId;
  }

  void setStart(DateFr d) {
    dateRange.setStart(d);
  }

  DateFr getStart() {
    return dateRange.getStartFr();
  }

  void setEnd(DateFr d) {
    dateRange.setEnd(d);
  }

  DateFr getEnd() {
    return dateRange.getEndFr();
  }

  void setTitle(String s) {
    scheduleLabel.setText(s);
  }

}
