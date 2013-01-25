/*
 * @(#)GroupRehearsalCreateCtrl.java	2.7.a 03/12/12
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * Controller for group single rehearsal.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class GroupRehearsalCreateCtrl
        extends CardCtrl
{

  private DataCache dataCache;
  private GemDesktop desktop;
  private GroupRehearsalView rv;
  private Group group;
  private String label = MessageUtil.getMessage("rehearsal.band.entry");
  private GroupService service;

  public GroupRehearsalCreateCtrl(GemDesktop d) {
    desktop = d;
    addActionListener((ActionListener) d);
    dataCache = d.getDataCache();
    service = new GroupService(dataCache.getDataConnection());
  }

  public GroupRehearsalCreateCtrl(GemDesktop d, Group g) {
    this(d);
    this.group = g;
  }

  public void init() {
    rv = new GroupRehearsalView(dataCache);
    addCard(label, rv);
    select(0);

  }

  @Override
  public boolean prev() {
    select(step - 1);
    return true;
  }

  @Override
  public boolean next() {
    select(step + 1);
    return true;
  }

  @Override
  public boolean cancel() {
    clear();
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Abandon"));
    }
    return true;
  }

  public void clear() {
    rv.clear();
    select(0);
  }

  @Override
  public boolean loadCard(Object c) {
    return false;
  }

  @Override
  public boolean loadId(int id) {
    return false;
  }

  @Override
  public boolean validation() {
    String entryError = MessageUtil.getMessage("entry.error");
    String conflict = MessageUtil.getMessage("planning.conflict");

    DateFr date = rv.getDate();
    int room = rv.getRoom();
    Hour hdeb = rv.getStartingTime();
    Hour hfin = rv.getEndingTime();

    if (date.equals(DateFr.NULLDATE)) {
      JOptionPane.showMessageDialog(rv,
                                    MessageUtil.getMessage("date.entry.error"),
                                    entryError,
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (date.before(dataCache.getStartOfPeriod())
            || date.after(dataCache.getEndOfPeriod())) {
      JOptionPane.showMessageDialog(rv,
                                    MessageUtil.getMessage("date.out.of.period"),
                                    entryError,
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }

    if (hdeb.toString().equals("00:00")
            || hfin.toString().equals("00:00")
            || !(hfin.after(hdeb))) {
      JOptionPane.showMessageDialog(rv,
                                    MessageUtil.getMessage("hour.range.error"),
                                    entryError,
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }

    if (service.testRoomConflict(date.toString(), hdeb.toString(), hfin.toString(), room) > 0) {
      JOptionPane.showMessageDialog(null,
                                    MessageUtil.getMessage("busy.room.warning"),
                                    conflict,
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }

    if (service.testGroupConflict(date.toString(), hdeb.toString(), hfin.toString(), group.getId()) > 0) {
      JOptionPane.showMessageDialog(null,
                                    MessageUtil.getMessage("busy.band.warning"),
                                    conflict,
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }
    try {
      service.createRehearsal(date, hdeb, hfin, group, room);
      desktop.postEvent(new ModifPlanEvent(this, rv.getDate(), rv.getDate()));
      clear();// L'ORDRE EST IMPORTANT : clear() before d'afficher le message
      JOptionPane.showMessageDialog(this,
                                    MessageUtil.getMessage("planning.update.info"),
                                    label,
                                    JOptionPane.INFORMATION_MESSAGE);

    } catch (GroupException ex) {
      GemLogger.logException(ex.getMessage(), ex, this);
      MessagePopup.warning(this, ex.getMessage());
      return false;
    } 
    return true;
  }

}

