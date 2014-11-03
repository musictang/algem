/*
 * @(#)ModifPlanHourDlg.java	2.6.a 21/09/12
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
package net.algem.planning.editing;

import java.awt.Frame;
import java.util.Date;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class ModifPlanHourDlg
        extends ModifPlanDlg

{

  private DataCache dc;
  private ModifPlanHourView pv;

  public ModifPlanHourDlg(Frame f, String label, DataCache dc) {
    super(f);
    this.dc = dc;
    pv = new ModifPlanHourView(this.dc, label);
    validation = false;

    dlg = new JDialog(f, true);
    addContent(pv, "Schedule.hour.modification.title");

  }

  @Override
  public void show() {
    dlg.setVisible(true);
  }

  @Override
  public boolean isEntryValid() {
    if (!pv.getHourEnd().after(pv.getHourStart())
            ) {
      JOptionPane.showMessageDialog(dlg,
                                    BundleUtil.getLabel("Warning.label"),
                                    MessageUtil.getMessage("invalid.time.slot"),
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (pv.getEnd().before(pv.getStart())) {
      JOptionPane.showMessageDialog(dlg,
                                    BundleUtil.getLabel("Warning.label"),
                                    MessageUtil.getMessage("end.date.invalid.choice"),
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  @Override
  public boolean isValidate() {
    return validation;
  }

  void setTitle(String _titre) {
    pv.setTitle(_titre);
  }

  void setDate(Date _date) {
    pv.setStart(_date);
    pv.setEnd(_date);
  }

  DateFr getStart() {
    return pv.getStart();
  }

  DateFr getEnd() {
    return pv.getEnd();
  }

  Hour getNewHourStart() {
    return pv.getHourStart();
  }

  Hour getNewHourEnd() {
    return pv.getHourEnd();
  }

  void setHour(Hour _debut, Hour _fin) {
    pv.setHour(_debut, _fin);
  }

}

