/*
 * @(#)ModifPlanRangeView.java	2.6.a 21/09/12
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
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.planning.Hour;
import net.algem.planning.HourRangePanel;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;


/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class ModifPlanRangeView
        extends GemPanel
{

  private int roomId;
  private GemField courseLabel;
  private Hour hStart, hEnd;
  private DateFrField from, to;
  private GemField currentRange; // time before
  private HourRangePanel newRange; // time after

  public ModifPlanRangeView() {

    setBorder(ModifPlanView.DEFAULT_BORDER);
    setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    courseLabel = new GemField(25);
    courseLabel.setEditable(false);
    from = new DateFrField();
    from.setEditable(false);
    to = new DateFrField();
    currentRange = new GemField(20);
    currentRange.setEditable(false);
    newRange = new HourRangePanel();

    Insets padding = new Insets(2, 2, 2, 2); // espacement

    gb.add(new GemLabel(BundleUtil.getLabel("Course.label")), 0, 0, 1, 1, padding, GridBagHelper.EAST);
    gb.add(courseLabel, 1, 0, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Date.From.label")), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(from, 1, 1, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Date.To.label")), 0, 2, 1, 1, GridBagHelper.EAST);
    gb.add(to, 1, 2, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Current.hour.range.label")), 0, 3, 1, 1, GridBagHelper.EAST);
    gb.add(currentRange, 1, 3, 1, 1, padding, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("New.hour.range.label")), 0, 4, 1, 1, GridBagHelper.EAST);
    gb.add(newRange, 1, 4, 1, 1, padding, GridBagHelper.WEST);

  }

  void setHour(Hour start, Hour end) {
    currentRange.setText("de " + start + " à " + end);
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
    from.set(d);
  }

  DateFr getStart() {
    return from.getDateFr();
  }

  void setEnd(DateFr d) {
    to.set(d);
  }

  DateFr getEnd() {
    return to.getDateFr();
  }

  void setTitle(String s) {
    courseLabel.setText(s);
  }

}
