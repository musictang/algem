/*
 * @(#)SubscriptionCardSessionTableModel.java 2.9.2 26/01/15
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
 */


package net.algem.contact.member;

import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.ui.JTableModel;

/**
 * Table model for the history of sessions taken on a subscription card.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 * @since 2.9.2 08/01/15
 */
public class SubscriptionCardSessionTableModel
  extends JTableModel<SubscriptionCardSession>
{

   public SubscriptionCardSessionTableModel() {
    header = new String[] {
      BundleUtil.getLabel("Date.label"),
      BundleUtil.getLabel("Start.label"),
      BundleUtil.getLabel("End.label"),
      BundleUtil.getLabel("Duration.label")
    };
  }

  @Override
  public int getIdFromIndex(int i) {
    return 0;
  }

  @Override
  public Class getColumnClass(int col) {
    switch (col) {
      case 0:
        return DateFr.class;
      case 1:
      case 2:
      case 3:
        return Hour.class;
      default:
        return Object.class;
    }
  }

  @Override
  public Object getValueAt(int line, int col) {
    SubscriptionCardSession session = tuples.get(line);
    Hour start = session.getStart();
    Hour end = session.getEnd();
    switch (col) {
      case 0:
        Schedule s = ScheduleIO.findId(session.getScheduleId(), DataCache.getDataConnection());
        return s != null ? s.getDate() : new DateFr();
      case 1:
        return start != null ? start : new Hour();
      case 2:
        return end != null ? end : new Hour();
      case 3:
        return start != null && end != null ? new Hour(start.getLength(end)) : new Hour();
      default:
        return Object.class;
    }
  }

  @Override
  public void setValueAt(Object value, int line, int column) {

  }

}
