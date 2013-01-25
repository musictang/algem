/*
 * @(#)GroupRehearsalHistoView.java 2.6.a 31/07/12
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
package net.algem.group;

import java.util.Vector;
import net.algem.planning.AbstractHistoRehearsal;
import net.algem.planning.Schedule;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class GroupRehearsalHistoView
        extends AbstractHistoRehearsal
{
  
  private GroupService service;

  public GroupRehearsalHistoView(GemDesktop desktop, GroupService service, int id) {
    super(desktop, null, id);
    this.service = service;
    btValidation.setText(BundleUtil.getLabel("Selection.label"));
    btCancel.setText(GemCommand.ERASE_CMD);
  }

  @Override
  public Vector<Schedule> getSchedule(boolean all) {
    return service.getRehearsalHisto(idper, datePanel.getStartFr(), datePanel.getEndFr(), all);
  }

}
