/*
 * @(#)GroupRehearsalCreateCtrl.java	2.13.1 20/04/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.RehearsalEvent;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.room.RoomService;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * Controller for group single rehearsal.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.1
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
  private GemGroupService service;

  public GroupRehearsalCreateCtrl(GemDesktop d) {
    desktop = d;
    addActionListener((ActionListener) d);
    dataCache = d.getDataCache();
    service = new GemGroupService(DataCache.getDataConnection());
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
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
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
    if (date.bufferEquals(DateFr.NULLDATE)) {
      MessagePopup.error(rv,MessageUtil.getMessage("date.entry.error"), entryError);
      return false;
    }
    if (date.before(dataCache.getStartOfPeriod()) || date.after(dataCache.getEndOfPeriod())) {
      MessagePopup.error(rv,MessageUtil.getMessage("date.out.of.period"), entryError);
      return false;
    }
    
    int room = rv.getRoom();
    Hour hStart = rv.getStartingTime();
    Hour hEnd = rv.getEndingTime();
    
    if (hStart.toString().equals("00:00")
            || hEnd.toString().equals("00:00")
            || !(hEnd.after(hStart))) {
      MessagePopup.error(rv,MessageUtil.getMessage("hour.range.error"), entryError);
      return false;
    }
    
    if (!RoomService.isOpened(room, date, hStart, hEnd)) {
      return false;
    }
    
    if (service.testRoomConflict(date.toString(), hStart.toString(), hEnd.toString(), room) > 0) {
      MessagePopup.error(rv,MessageUtil.getMessage("busy.room.warning"), conflict);
      return false;
    }

    if (service.testGroupConflict(date.toString(), hStart.toString(), hEnd.toString(), group.getId()) > 0) {
      MessagePopup.error(rv,MessageUtil.getMessage("busy.group.warning"), conflict);
      return false;
    }
    try {
      service.createRehearsal(date, hStart, hEnd, group, room);
      desktop.postEvent(new ModifPlanEvent(this, rv.getDate(), rv.getDate()));
      clear();// L'ORDRE EST IMPORTANT : clear() avant d'afficher le message
      MessagePopup.information(rv,MessageUtil.getMessage("planning.update.info"), label);
      if (gemListener != null) {
        gemListener.postEvent(new RehearsalEvent(this, GemEvent.CREATION, GemEvent.REHEARSAL));
      }
    } catch (GroupException ex) {
      GemLogger.logException(ex.getMessage(), ex, contentPane);
      MessagePopup.warning(contentPane, ex.getMessage());
      return false;
    }
    return true;
  }

}

