/*
 * @(#)HourStatView.java	2.8.k 25/07/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.edition;

import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import net.algem.config.ParamChoice;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;
import net.algem.util.model.GemList;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Hours statitics entry.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.k
 */
public class HourStatView
        extends GemPanel
{

  private DateFrField dateStart;
  private DateFrField dateEnd;
  private JCheckBox detail;
  private ParamChoice schoolChoice;

  public HourStatView(DataConnection dc, GemList schools) {
    setLayout(new java.awt.GridBagLayout());
    setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;

    dateStart = new DateFrField(new Date());
    dateEnd = new DateFrField(new Date());
    detail = new JCheckBox();
    schoolChoice = new ParamChoice(schools.getData());
    schoolChoice.setBorder(null);
    
    GemPanel dates = new GemPanel();
    dates.add(dateStart);
    dates.add(new JLabel(BundleUtil.getLabel("Date.To.label")));
    dates.add(dateEnd);

    gb.add(new JLabel(BundleUtil.getLabel("Date.From.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new JLabel(BundleUtil.getLabel("School.label")), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(dates, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(schoolChoice, 1, 1, 1, 1, GridBagHelper.WEST);
    
  }

  public DateFr getDateStart() {
    return dateStart.getDateFr();
  }

  public DateFr getDateEnd() {
    return dateEnd.getDateFr();
  }

  public int getSchool() {
    return schoolChoice.getKey();
  }

  public boolean withDetail() {
    return detail.isSelected();
  }
}
