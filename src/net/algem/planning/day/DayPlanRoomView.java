/*
 * @(#)DayPlanRoomView.java	2.9.1 27/11/14
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
package net.algem.planning.day;

import java.beans.PropertyChangeEvent;
import java.util.Vector;
import net.algem.config.ConfigUtil;
import net.algem.planning.ScheduleObject;
import net.algem.planning.ScheduleRangeObject;
import net.algem.room.Room;
import net.algem.util.BundleUtil;
import net.algem.util.model.GemList;

/**
 * Day view by room.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc Gobat</a>
 * @version 2.9.1
 * @since 1.0a 07/07/1999
 */
public class DayPlanRoomView
        extends DayPlanTableView
{

  private int estab;
  private GemList<Room> roomList;
  private boolean all;

  public DayPlanRoomView(GemList<Room> list, int e) {
    super(BundleUtil.getLabel("Room.label"));

    estab = e;
    roomList = list;
  }

  @Override
  public void load(java.util.Date d, Vector<ScheduleObject> vs, Vector<ScheduleRangeObject> vr) {

    int cpt = 0;

    dayPlanView.clear();
    dayPlanView.setDate(d);
    date.set(d);

    for (int i = 0; i < roomList.getSize(); i++) {
      Room s = (Room) roomList.getElementAt(i);
      Vector<ScheduleObject> v1 = getPlan(vs, s.getId());
      Vector<ScheduleRangeObject> v2 = getPlan(vr, s.getId());

      if ((v1.size() + v2.size()) > 0) {
        DayPlan pj = new DayPlan();
        pj.setId(s.getId());
        pj.setLabel(s.getName());
        pj.setSchedule(v1);
        pj.setScheduleRange(v2);
        pj.setDailyTimes(ConfigUtil.getTimes(s.getId()));
        dayPlanView.addCol(pj);

        cpt++;
      }
      // test affichage de toutes les salles mêmes vides
      else if (all && s.getId() > 0 && s.getEstab() == estab && s.isActive()) {
        DayPlan pj = new DayPlan();
        pj.setId(s.getId());
        pj.setLabel(s.getName());
        pj.setSchedule(new Vector<ScheduleObject>());
        pj.setScheduleRange(new Vector<ScheduleRangeObject>());
        dayPlanView.addCol(pj);
      }
    }

    dayPlanView.repaint();
    setBar();
  }

  public <T extends ScheduleObject> Vector<T> getPlan(Vector<T> t, int roomId) {

    Vector<T> v = new Vector<T>();
    if (t != null) {
      for (int i = 0; i < t.size(); i++) {
        Room s = t.elementAt(i).getRoom();
        if (s.getId() == roomId && s.getEstab() == estab) {
          v.addElement(t.elementAt(i));
        }
      }
    }
    return v;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {

    if (evt.getPropertyName().equals("@all_rooms")) {
      all = (Boolean) evt.getNewValue();
      DaySchedule model = (DaySchedule) evt.getSource();
      load(model.getDay(),model.getSchedules(), model.getRanges());
    } else {
      super.propertyChange(evt);
    }
  }
}
