/*
 * @(#)EstabCtrl.java	2.11.0 22/09/16
 * 
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
import java.util.List;
import net.algem.contact.*;
import net.algem.util.*;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * Establishment controller.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 */
public class EstabCtrl
        extends CardCtrl
{

  private final GemDesktop desktop;
  private final DataCache dataCache;
  private final DataConnection dc;
  private ContactFileEditor contactEditor;
  private EstabRoomListView roomListView;
  private Establishment estab;
  private Establishment oldEstab;

  public EstabCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    this.dataCache = desktop.getDataCache();
    this.dc = DataCache.getDataConnection();
    contactEditor = new ContactFileEditor(desktop);
    contactEditor.setCodePostalCtrl(new CodePostalCtrl(dc));
    contactEditor.filter(Person.ESTABLISHMENT);
    roomListView = new EstabRoomListView();

    addCard("etablissement", contactEditor);
    addCard("salles", roomListView);
    select(0);
  }

  @Override
  public boolean next() {
    switch (step) {
      default:
        if (step >= 0) {
           btPrev.setText(GemCommand.PREVIOUS_CMD);
           btPrev.setActionCommand(GemCommand.PREVIOUS_CMD);
        }
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
    //    removeGemEventListener();
    return true;
  }

  @Override
  public boolean prev() {
    switch (step) {
      case 0:
        Establishment e = get();
        try {
          if (dataCache.authorize("Establishment.suppression.auth")) {
            EstablishmentIO.delete(e, dc);
            dataCache.remove(e);
            desktop.postEvent(new GemEvent(this, GemEvent.SUPPRESSION, GemEvent.ESTABLISHMENT, e));
          } else {
            throw new EstablishmentException(MessageUtil.getMessage("action.authorization.error", dataCache.getUser().getLogin()));
          }
//          close();
            if (actionListener != null) {
              actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
            }
        } catch (EstablishmentException cex) {
          MessagePopup.warning(this, cex.getMessage());
          return false;
        }
      default:
         if (step == 1) {
           btPrev.setText(GemCommand.DELETE_CMD);
           btPrev.setActionCommand(GemCommand.DELETE_CMD);
        }
        select(step - 1);
        break;
    }
    return true;
  }

  @Override
  public boolean validation() {
    try {
      estab = get();
      if (estab.isValid()) {
        EstablishmentIO.update(oldEstab, estab, dc);
        RoomIO roomIO = (RoomIO) DataCache.getDao(Model.Room);
        for (Room r : roomListView.getData()) {   
          roomIO.updateStatus(r);
          dataCache.update(r);
        }
        dataCache.update(estab);
        desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.ESTABLISHMENT, estab));
      } else {
        MessagePopup.error(this, MessageUtil.getMessage("establishment.empty.name.exception"));
        select(0);
        return false;
      }
    } catch (SQLException e1) {
      GemLogger.logException("update etablissement", e1, this);
      return false;
    }
    
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
    }
    return true;
  }

  public void clear() {
    contactEditor.clear();
    roomListView.clear();
  }

  @Override
  public boolean loadCard(Object o) {
    clear();
    if (o == null || !(o instanceof Establishment)) {
      return false;
    }
    estab = (Establishment) o;
    oldEstab = estab;
    if (estab.getId() > 0) {
      btPrev.setText(GemCommand.DELETE_CMD);
      btPrev.setActionCommand(GemCommand.DELETE_CMD);
    } else {
      btPrev.setText("");
    }
    try {
      contactEditor.setPerson(estab.getPerson());
      roomListView.load(estab.getId());
      if (estab.getAddress() != null) {
        contactEditor.setAddress(estab.getAddress());
      }
      if (estab.getTele() != null) {
        contactEditor.setTele(estab.getTele());
      }
      if (estab.getEmail() != null) {
        contactEditor.setEmail(estab.getEmail());
      }
      if (estab.getSites()!= null) {
        contactEditor.setSites(estab.getSites());
      }
      select(0);
    } catch (Exception e) {
      GemLogger.logException("lecture fiche etablissement", e, this);
      return false;
    }
    return true;
  }

  @Override
  public boolean loadId(int id) {
    try {
      // return loadCard(DataCache.findId(id, Model.Establishment));
      return loadCard(EstablishmentIO.findId(id, dc)); // do not use the cache here
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      return false;
    }
  }

  private Establishment get() {
    Establishment e = new Establishment();
    Person p = contactEditor.getPerson();
    if (p != null) {
      p.setType(Person.ESTABLISHMENT);
    }
    e.setPerson(p);

    List<Address> va = contactEditor.getAddressAll();
    if (va != null) {
      for (Address a : va) {
        a.setId(e.getId());
      }
      e.setAddress(va);
    }

    List<Telephone> telephones = contactEditor.getTele();
    int i = 0;
    if (telephones != null) {
      for (Telephone t : telephones) {
        t.setIdper(e.getId());
        t.setIdx(i);
        i++;
      }
      e.setTele(telephones);
    }

    List<Email> emails = contactEditor.getEmail();
    i = 0;
    if (emails != null) {
      for (Email em : emails) {
        em.setIdper(e.getId());
        em.setIdx(i);
        i++;
      }
      e.setEmail(emails);
    }
   
    i = 0;
    List<WebSite> sites = contactEditor.getSites();
    if (sites != null) {
      for (WebSite w : sites) {
        w.setIdper(e.getId());
        w.setIdx(i);
        w.setPtype(Person.ESTABLISHMENT);
        i++;
      }
      e.setSites(sites);
    }
    
     return e;
  }
  
  @Override
  public void addGemEventListener(GemEventListener l) {
    gemListener = l;
    desktop.addGemEventListener(l);
  }
  
  @Override
  public void removeGemEventListener() {
    desktop.removeGemEventListener(gemListener);
  }
}
