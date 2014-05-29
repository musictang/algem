/*
 * @(#)ModifPlanRangeDlg.java	2.8.a 24/04/13
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
 */
package net.algem.planning.editing;

import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.ScheduleObject;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 1.1b vendredi 27 mars 2009
 */
public class ModifPlanRangeDlg
        extends ModifPlanDlg
{

  private ScheduleObject plan;
  private ModifPlanRangeView pv;

  public ModifPlanRangeDlg(Frame f, String t, ScheduleObject plan) {
    super(f);
    pv = new ModifPlanRangeView();

    this.plan = plan;
    set(); // initialisation du titre, de la date, de la salle et de la plage horaire
    validation = false;

    dlg = new JDialog(f, true);
    addContent(pv, null);
  }

  @Override
  public void entry() {
    dlg.setVisible(true);
  }

  public void set() {
    setTitle(plan.getScheduleLabel());
    setDate(plan.getDate());
    setRoom(plan.getIdRoom());
    setHour(plan.getStart(), plan.getEnd());
  }

  /**
   * Checks dates and time ranges.
   * Start time must be lower than end time.
   *
   * @return false if not valid
   */
  @Override
  public boolean isEntryValid() {
    if (!pv.getNewHourEnd().after(pv.getNewHourStart())) {
      JOptionPane.showMessageDialog(dlg,
              BundleUtil.getLabel("Warning.label"),
              MessageUtil.getMessage("invalid.time.slot"),
              JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (!pv.getEnd().afterOrEqual(pv.getStart())) {
      JOptionPane.showMessageDialog(dlg,
              BundleUtil.getLabel("Warning.label"),
              MessageUtil.getMessage("date.entry.error"),
              JOptionPane.ERROR_MESSAGE);
      return false;
    }

    return true;
  }

  @Override
  public boolean isValidate() {
    return validation;
  }

  void setTitle(String t) {
    pv.setTitle(t);
  }

  /** Sets a day. */
  void setDate(DateFr d) {
    pv.setStart(d);
    pv.setEnd(d);
  }

  /** Sets a room. */
  void setRoom(int r) {
    pv.setRoomId(r);
  }

  /**
   * @return new start time
   */
  Hour getNewHourStart() {
    return pv.getNewHourStart();
  }

  /**
   * @return new end time
   */
  Hour getNewHourEnd() {
    return pv.getNewHourEnd();
  }

  DateFr getDateEnd() {
    return pv.getEnd();
  }

  /**
   * Sets start and end time.
   */
  void setHour(Hour start, Hour end) {
    pv.setHour(start, end);
  }
}
