/*
 * @(#)HistoRehearsalView.java 2.9.3 27/02/15
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
 * 
 */
package net.algem.contact.member;

import java.awt.event.ActionListener;
import java.util.List;
import net.algem.planning.AbstractHistoRehearsal;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;

/**
 * Rehearsal history tab.
 * All the rehearsals are loaded by default.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.3
 */
public class HistoRehearsalView 
  extends AbstractHistoRehearsal
{

  public HistoRehearsalView(GemDesktop desktop, ActionListener listener, int pf) {
    super(desktop, listener, pf);
    btValidation.setText(BundleUtil.getLabel("Period.label"));
    btValidation.setToolTipText(BundleUtil.getLabel("Rehearsal.list.selection.tip"));
    btCancel.setText(GemCommand.CLOSE_CMD);
  }

  @Override
  public List<Schedule> getSchedule(boolean all) {

    String query = " WHERE p.ptype=" + Schedule.MEMBER + " AND p.idper=" + idper;
    if (!all) {
      query += " AND jour BETWEEN '" + datePanel.getStartFr() + "' AND '" + datePanel.getEndFr() + "'";
    }
    query += " ORDER BY jour DESC,debut";

    return ScheduleIO.find(query, dc);

  }
  
}
