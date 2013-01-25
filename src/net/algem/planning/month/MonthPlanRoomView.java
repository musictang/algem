/*
 * @(#)MonthPlanRoomView.java	2.6.a 21/09/12
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
package net.algem.planning.month;

import net.algem.planning.ScheduleDetailEvent;
import net.algem.planning.ScheduleObject;
import net.algem.room.Room;
import net.algem.util.ui.GemChoice;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class MonthPlanRoomView
        extends MonthPlanDetailView
{

  public MonthPlanRoomView(GemChoice _choixSalle) {
    super(_choixSalle);
  }

  @Override
  public boolean isFiltered(ScheduleObject p) {
    Room ps = (Room) choice.getSelectedItem();
    return ps != null && p.getRoom() != null && p.getRoom().getId() == ps.getId();
  }

  @Override
  public void detailChange(ScheduleDetailEvent evt) {
    choice.setKey(((ScheduleObject) evt.getSchedule()).getRoom().getId());
  }
}
