/*
 * @(#)RoomRateCardCtrl.java	2.7.a 17/01/13
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
package net.algem.room;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import net.algem.util.*;
import net.algem.util.event.GemEvent;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * comment
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class RoomRateCardCtrl
        extends CardCtrl
{

  private GemDesktop desktop;
  private DataCache dataCache;
  private RoomRateView view;
  private RoomRate rate;
  private RoomRateIO dao;

  public RoomRateCardCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    this.dataCache = desktop.getDataCache();
    dao = (RoomRateIO) DataCache.getDao(Model.RoomRate);
    btPrev.setText(GemCommand.DELETE_CMD);
    btPrev.setActionCommand("Suppression");
    view = new RoomRateView();

    addCard("Fiche tarif", view);

    select(0);
    // réactivation après le select (désactivé si arg vaut 0)
    btPrev.setEnabled(true);
  }

  @Override
  public boolean next() {
    switch (step) {
      default:
        select(step + 1);
        break;
    }
    return true;
  }

  @Override
  public boolean cancel() {
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlAbandon"));
    }
    return true;
  }

  @Override
  public boolean prev() {
    if (MessagePopup.confirm(this, MessageUtil.getMessage("delete.rate.confirmation"),
            BundleUtil.getLabel("Warning.label"))) {
      return delete(getRate());
    }
    return false;
  }

  @Override
  public boolean validation() {
    RoomRate edited = getRate();
    if (edited.getLabel().isEmpty()) {
      return false;
    }
    if (edited.getId() == 0) {
      return create(edited);
    }
    if (rate.equals(edited)) {
      return true;
    }

    rate = edited;
    return update(rate);
  }

  public void clear() {
    view.clear();
  }

  @Override
  public boolean loadCard(Object o) {
    clear();
    if (o == null || !(o instanceof RoomRate)) {
      return false;
    }

    rate = (RoomRate) o;
    view.setRate(rate);

    return true;
  }

  @Override
  public boolean loadId(int id) {
    try {
//      return loadCard(RoomRateIO.findId(id, dc));
      return loadCard(DataCache.findId(id, Model.RoomRate));
    } catch (SQLException ex) {
      GemLogger.logException("Tarif salle found exception", ex);
      return loadCard(null);
    }
  }

  public RoomRate getRate() {
    return view.getRate();
  }

  public void disableSuppression() {
    btPrev.setEnabled(false);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    super.actionPerformed(evt);
    if ("Suppression".equals(evt.getActionCommand())) {
      prev();
    }
  }

  private boolean create(RoomRate roomRate) {
    try {
      dao.insert(roomRate);
      dataCache.add(roomRate);
      desktop.postEvent(new GemEvent(this, GemEvent.CREATION, GemEvent.ROOM_RATE, roomRate));
    } catch (Exception e1) {
      GemLogger.logException("insertion tarif salle", e1, this);
      return false;
    }
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
    }
    return true;
  }

  private boolean update(RoomRate roomRate) {
    try {
      dao.update(roomRate);
      dataCache.update(roomRate);
      desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.ROOM_RATE, roomRate));
    } catch (Exception e1) {
      GemLogger.logException("update tarif salle", e1, this);
      return false;
    }
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
    }
    return true;
  }

  private boolean delete(RoomRate roomRate) {

    if (dao.delete(roomRate.getId())) {
      dataCache.remove(roomRate);
      desktop.postEvent(new GemEvent(this, GemEvent.SUPPRESSION, GemEvent.ROOM_RATE, roomRate));
    }

    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
    }
    return true;
  }
  
}
