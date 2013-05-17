/*
 * @(#)EstabCardCtrl.java	2.8.a 28/03/13
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
import java.util.Vector;
import net.algem.contact.*;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.event.GemEvent;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 */
public class EstabCardCtrl
        extends CardCtrl
{

  private GemDesktop desktop;
  private DataCache dataCache;
  private DataConnection dc;
  private ContactFileEditor contactEditor;
  private EstabListView listView;
  private Establishment estab;
  private Establishment oldEstab;

  public EstabCardCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    this.dataCache = desktop.getDataCache();
    dc = desktop.getDataCache().getDataConnection();
    contactEditor = new ContactFileEditor(desktop);
    contactEditor.filter(Person.ESTABLISHMENT);
    listView = new EstabListView();

    addCard("etablissement", contactEditor);
    addCard("salles", listView);
    select(0);
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
    switch (step) {
      default:
        select(step - 1);
        break;
    }
    return true;
  }

  @Override
  public boolean validation() {
    try {
      estab = get();
      EstablishmentIO.update(oldEstab, estab, dc);//TODO refresh estab list table model
      dataCache.update(estab);
      desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.ESTABLISHMENT, estab));
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
    listView.clear();
  }

  @Override
  public boolean loadCard(Object o) {
    clear();
    if (o == null || !(o instanceof Establishment)) {
      return false;
    }
    estab = (Establishment) o;
    oldEstab = estab;
    try {
      contactEditor.setPerson(estab.getPerson());
      listView.load(estab.getId());
      if (estab.getAddress() != null) {
        contactEditor.setAddress(estab.getAddress());
      }
      if (estab.getTele() != null) {
        contactEditor.setTele(estab.getTele());
      }
      if (estab.getEmail() != null) {
        contactEditor.setEmail(estab.getEmail());
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
      return loadCard(dataCache.findId(id, Model.Establishment));
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

    Vector<Address> va = contactEditor.getAddressAll();
    if (va != null) {
      for (Address a : va) {
        a.setId(e.getId());
      }
      e.setAddress(va);
    }

    Vector<Telephone> telephones = contactEditor.getTele();
    int i = 0;
    if (telephones != null) {
      for (Telephone t : telephones) {
        t.setIdper(e.getId());
        t.setIdx(i);
        i++;
      }
      e.setTele(telephones);
    }

    Vector<Email> emails = contactEditor.getEmail();
    i = 0;
    if (emails != null) {
      for (Email em : emails) {
        em.setIdper(e.getId());
        em.setIdx(i);
        i++;
      }
      e.setEmail(emails);
    }
    return e;
  }
}
