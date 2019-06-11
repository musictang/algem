/*
 * @(#) DeferRehearsalDlg.java Algem 2.15.8 26/03/2018
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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

package net.algem.planning.editing;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.algem.planning.PlanningService;
import net.algem.planning.ScheduleObject;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.8
 * @since 2.15.8 26/03/2018
 */
public class DeferRehearsalDlg
  extends ModifPlanDlg
{

  private DeferRehearsalView pv;
  private ScheduleObject schedule;

  public DeferRehearsalDlg(GemDesktop desktop, ScheduleObject _plan, PlanningService service, String titleKey) {
    super(desktop.getFrame());
    schedule = _plan;
    pv = new DeferRehearsalView(desktop.getDataCache().getList(Model.Room), service);

    pv.set(schedule);
    validation = false;
    dlg = new JDialog(desktop.getFrame(), true);
    dlg.setSize(400,300);
    addContent(pv, titleKey);

  }

  @Override
  public void show() {
    dlg.setVisible(true);
  }

  @Override
  public boolean isEntryValid() {
    ScheduleObject ns = pv.getSchedule();
    String error = MessageUtil.getMessage("invalid.time.slot");
    if (!ns.getEnd().after(ns.getStart())) {
      JOptionPane.showMessageDialog(dlg, MessageUtil.getMessage("hour.range.error"), error, JOptionPane.ERROR_MESSAGE);
      return false;
    }

    if (ns.getEnd().le(ns.getStart())) {
       JOptionPane.showMessageDialog(dlg, MessageUtil.getMessage("hour.range.error"), error, JOptionPane.ERROR_MESSAGE);
      return false;
    }

    return true;
  }

  @Override
  public boolean isValidate() {
    return validation;
  }

  ScheduleObject getSchedule() {
    return pv.getSchedule();
  }

}
