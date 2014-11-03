/*
 * @(#)ModifPlanRoomDlg.java	2.8.w 02/09/14
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

import java.awt.Frame;
import java.util.Date;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 1.0a 07/07/1999
 */
public class ModifPlanRoomDlg
        extends ModifPlanDlg
{

  private int room;
  private ModifPlanRoomView pv;

  public ModifPlanRoomDlg(Frame f, String t, DataCache dataCache) {
    super(f);
    pv = new ModifPlanRoomView(dataCache, t);
    validation = false;
    dlg = new JDialog(f, true);
    
    addContent(pv, "Schedule.room.modification.title");
    dlg.setSize(400,280);
  }

  @Override
  public void show() {
    dlg.setVisible(true);
  }

  @Override
  public boolean isEntryValid() {
    if (room == pv.getId()) {
      JOptionPane.showMessageDialog(dlg,
                                    MessageUtil.getMessage("same.room"),
                                    BundleUtil.getLabel("Warning.label"),
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (pv.getEnd().before(pv.getStart())) {
      JOptionPane.showMessageDialog(dlg,
                                    MessageUtil.getMessage("break.end.date.error"),
                                    BundleUtil.getLabel("Warning.label"),
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

  void setDate(Date d) {
    pv.setStart(d);
    pv.setEnd(d);
  }

  DateFr getStart() {
    return pv.getStart();
  }

  DateFr getEnd() {
    return pv.getEnd();
  }

  int getNewRoom() {
    return pv.getId();
  }

  void setRoom(int r) {
    room = r;
    pv.setId(r);
  }

}

