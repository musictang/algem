/*
 * @(#)GroupRehearsalHistoView.java 2.9.3 27/02/15
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
package net.algem.group;

import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import net.algem.planning.AbstractHistoRehearsal;
import net.algem.planning.Schedule;
import net.algem.util.BundleUtil;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.3
 */
public class GroupRehearsalHistoView
        extends AbstractHistoRehearsal
{
  
  private GemGroupService service;

  public GroupRehearsalHistoView(GemDesktop desktop, GemGroupService service, int id) {
    super(desktop, null, id);
    this.service = service;
    btValidation.setText(BundleUtil.getLabel("Period.label"));
    btValidation.setToolTipText(BundleUtil.getLabel("Rehearsal.list.selection.tip"));
    btCancel.setText(BundleUtil.getLabel("Action.current.month.label"));
  }

  @Override
  public List<Schedule> getSchedule(boolean all) {
    if (all) {
      return service.getRehearsalHisto(idper,null,null);
    }
    return service.getRehearsalHisto(idper, datePanel.getStartFr(), datePanel.getEndFr());
  }
  
  private void setCurrentMonth() {
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.set(Calendar.DAY_OF_MONTH, 1);
    datePanel.setStart(c.getTime());
    c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
    datePanel.setEnd(c.getTime());
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btCancel) {
      clear();
      setCurrentMonth();
      load();
    } else {
      super.actionPerformed(evt);
    }
  }

}
