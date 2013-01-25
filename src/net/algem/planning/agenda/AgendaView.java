/*
 * @(#)AgendaView.java	2.6.a 20/09/12
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
package net.algem.planning.agenda;

import java.awt.AWTEventMulticaster;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleObject;
import net.algem.planning.ScheduleView;
import net.algem.util.ui.GemPanel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class AgendaView
        extends GemPanel
        implements ActionListener, ScheduleView
{

  Point p;
  ActionListener actionListener;
  ScheduleObject clickPlan;

  public AgendaView() {
    super();
  }

  @Override
  public Schedule getSchedule() {
    return clickPlan;
  }

  @Override
  public java.util.Vector getScheduleRanges() {
    return null;
  }

  public void reset() {
    actionListener = null;
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() instanceof Date) {
      p = getLocationOnScreen();
      if (actionListener != null) {
        actionListener.actionPerformed(evt);
      }
    }
  }

  @Override
  public Point getClickPosition() {
    /* Point p = getLocationOnScreen();
     *
     * p.x += clickx; p.y += clicky;
     */
    return p;
  }
}
