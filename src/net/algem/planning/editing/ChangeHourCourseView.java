/*
 * @(#)ChangeHourCourseView.java	2.10.0 15/06/2016
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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

import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.planning.Hour;
import net.algem.planning.HourField;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 * Modification of time slot entry panel.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 1.0a 02/09/2001
 */
public class ChangeHourCourseView
        extends JPanel
{

  private DateFrField dateStart;
  private Hour start, end;
  private HourField hour;
  private JLabel courseLabel;

  public ChangeHourCourseView(String label, Hour start, Hour end) {
    this.start = start;
    this.end = end;

    setLayout(new java.awt.GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;

    dateStart = new DateFrField(new Date());
    hour = new HourField();
    this.courseLabel = new JLabel(label);

    GemLabel currentTime = new GemLabel(BundleUtil.getLabel("Hour.label")  + " [" + start + "-" + end + "]");
    gb.add(new JLabel(BundleUtil.getLabel("Course.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(currentTime, 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("From.label")), 0, 2, 1, 1, GridBagHelper.WEST);

    gb.add(courseLabel, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(hour, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(dateStart, 1, 2, 1, 1, GridBagHelper.WEST);

  }

  public DateFr getDateStart() {
    return dateStart.getDateFr();
  }

  public Hour getHour() {
    return hour.getHour();
  }
}
