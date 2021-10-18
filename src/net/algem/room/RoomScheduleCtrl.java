/*
 * @(#)RoomScheduleCtrl.java	2.8.o 10/10/13
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

package net.algem.room;

import java.awt.BorderLayout;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import net.algem.planning.ScheduleDetailCtrl;
import net.algem.planning.ScheduleObject;
import net.algem.planning.ScheduleRangeObject;
import net.algem.planning.month.AbstractMonthScheduleCtrl;
import net.algem.planning.month.MonthPlanRoomView;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.o
 * @since 2.8.o 08/10/13
 */
public class RoomScheduleCtrl 
extends AbstractMonthScheduleCtrl 
 {

  private int roomId;
  private MonthPlanRoomView roomView;

  public RoomScheduleCtrl(GemDesktop desktop, int roomId) {
    super(desktop);
    this.roomId = roomId;

    detailCtrl = new ScheduleDetailCtrl(desktop, modifCtrl, false);
    roomView = new MonthPlanRoomView(null);
    roomView.addActionListener(this);
    monthSchedule.addPropertyChangeListener(roomView);

    setLayout(new BorderLayout());
    add(roomView, BorderLayout.CENTER);
  }

  @Override
  public void load() {
    load(new Date());
  }
  
  @Override
  protected void load(Date d) {
    
    setMonthRange(d);
      // recherche  dans les plannings
    String query = " WHERE p.lieux = " + roomId
            + " AND p.jour >= '" + start + "' AND p.jour <= '" + end + "'"
            + " ORDER BY p.jour,p.debut";

    try {
      List<ScheduleObject> vpl = planningService.getSchedule(query);
      
      query = " AND p.lieux = " + roomId
                + " AND p.jour >= '" + start + "' AND p.jour <= '" + end + "'"
                + " ORDER BY p.jour, pg.debut";
      List<ScheduleRangeObject> vpg = planningService.getScheduleRange(query);
      monthSchedule.setSchedule(start.getDate(), end.getDate(), vpl);

      // setup des plages
      monthSchedule.setScheduleRange(start.getDate(), end.getDate(), vpg);
      roomView.load(cal.getTime(), vpl, vpg);
      loaded = true;
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }


}
