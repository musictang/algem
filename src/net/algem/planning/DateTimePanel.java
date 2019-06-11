/*
 * @(#)DateTimePanel.java	2.8.y 29/09/14
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

package net.algem.planning;

import java.awt.Color;
import java.util.Date;
import javax.swing.BorderFactory;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.RemovablePanel;

/**
 * Date and time selection panel with removing button.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.y
 * @since 2.8.t 11/04/14
 */
public class DateTimePanel
  extends RemovablePanel

{
  
  private DateRangePanel datePanel;
  private HourRangePanel hourPanel;

  public DateTimePanel() {
    
    datePanel = new DateRangePanel(new DateFr(new Date()));
    datePanel.setBorder(null, null);

    hourPanel = new HourRangePanel();
    hourPanel.setBorder(null);
        
    GemPanel rPanel = new GemPanel();
    rPanel.add(hourPanel);
    rPanel.add(removeBt);

    add(datePanel);
    add(rPanel);
    //setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, false));
    setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
  }

  public DateFr getDate() {
    return datePanel.get();
  }

  public void setDate(DateFr date) {
    datePanel.setDate(date);
  }

  public HourRange getHourRange() {
    return new HourRange(hourPanel.getStart(), hourPanel.getEnd());
  }

  public void setHourRange(HourRange range) {
    hourPanel.setStart(range.getStart());
    hourPanel.setEnd(range.getEnd());
  }

  public void reset() {
    setDate(new DateFr(new Date()));
    setHourRange(new HourRange(HourRangePanel.DEF_START, HourRangePanel.DEF_START));
  }
  
  
}
