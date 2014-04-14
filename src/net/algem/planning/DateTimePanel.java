/*
 * @(#)DateTimePanel.java	2.8.t 14/04/14
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import net.algem.util.GemCommand;
import net.algem.util.ImageUtil;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 2.8.t 11/04/14
 */
public class DateTimePanel
  extends GemPanel
  implements ActionListener
{
  
  private DateRangePanel datePanel;
  private HourRangePanel hourPanel;
  private GemButton removeBt;
  private ActionListener listener;

  public DateTimePanel() {
    
    datePanel = new DateRangePanel(new DateFr(new Date()));
    datePanel.setBorder(null, null);

    ImageIcon resetIcon = ImageUtil.createImageIcon(ImageUtil.DELETE_ICON);
    removeBt = new GemButton(resetIcon);
    removeBt.addActionListener(this);
    removeBt.setToolTipText(GemCommand.REMOVE_CMD);

    hourPanel = new HourRangePanel();
    hourPanel.setBorder(null);
        
    GemPanel rPanel = new GemPanel();
    rPanel.add(hourPanel);
    rPanel.add(removeBt);

    add(datePanel);
    add(rPanel);
    setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));  
  }

  public DateFr getDate() {
    return datePanel.get();
  }
  
  public void addActionListener(ActionListener listener) {
    this.listener = listener;
  }
  
  public void removeActionListener() {
    this.listener = null;
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

  @Override
  public void actionPerformed(ActionEvent e) {
    if (listener != null) {
      listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.REMOVE_CMD));
    }
  }
  
  public void reset() {
    setDate(new DateFr(new Date()));
    setHourRange(new HourRange(HourRangePanel.DEF_START, HourRangePanel.DEF_START));
  }
  
  
}
