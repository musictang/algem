/*
 * @(#)HistoRehearsalView.java 2.6.a 18/09/12
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
package net.algem.contact.member;

import java.awt.event.ActionListener;
import java.util.Vector;
import net.algem.planning.AbstractHistoRehearsal;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;

/**
 * Rehearsal history tab.
 * The rehearsals of the last 30 days are loaded by default.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class HistoRehearsalView extends AbstractHistoRehearsal
{

  public HistoRehearsalView(GemDesktop desktop, ActionListener listener, int pf) {
    super(desktop, listener, pf);
    btValidation.setText("Sélection");
    btCancel.setText(GemCommand.CLOSE_CMD);
  }

  @Override
  public Vector<Schedule> getSchedule(boolean all) {

    String query = " WHERE p.ptype=" + Schedule.MEMBER_SCHEDULE + " AND p.idper=" + idper;
    if (!all) {
      query += " AND jour BETWEEN '" + datePanel.getStartFr() + "' AND '" + datePanel.getEndFr() + "'";
    }
    query += " ORDER BY jour,debut";

    return ScheduleIO.find(query, dc);

  }
  
}
