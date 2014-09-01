/*
 * @(#)AddParticipantDlg.java	2.8.w 08/07/14
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

package net.algem.planning.editing;

import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import net.algem.contact.EmployeePanel;
import net.algem.contact.Person;
import net.algem.planning.ConflictQueries;
import net.algem.planning.ScheduleIO;
import net.algem.planning.ScheduleObject;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.8.v 06/06/14
 */
public class AddParticipantDlg
  extends ModifPlanDlg
{
  
  private EmployeePanel selector;
  private ScheduleObject plan;
  private final DataCache dataCache;
  
  public AddParticipantDlg(GemDesktop desktop, ScheduleObject plan, List<Person> persons) {
    super(desktop.getFrame());
    this.dataCache = desktop.getDataCache();

    this.plan = plan;
    selector = new EmployeePanel(persons);
    dlg = new JDialog(desktop.getFrame(), true);
    GemPanel gp = new GemPanel(new FlowLayout(FlowLayout.LEFT));
    
    gp.add(new GemLabel(BundleUtil.getLabel("New.label")));
    gp.add(selector);
    gp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    addContent(gp, "Schedule.add.participant.label");
    dlg.setSize(GemModule.XS_SIZE);
  }
  

  @Override
  public boolean isEntryValid() {
    
    int per = selector.getId();

    String query = ConflictQueries.getMemberScheduleSelection(plan.getDate().toString(), plan.getStart().toString(), plan.getEnd().toString(), per);
    if (per < 1 || ScheduleIO.count(query, DataCache.getDataConnection()) > 0) {
      MessagePopup.warning(dlg, MessageUtil.getMessage("busy.person.warning"));
      return false;
    }
    validation = true;
    return true;
  }

  @Override
  public boolean isValidate() {
    return validation;
  }

  int getParticipant() {
    return selector.getId();
  }

}
