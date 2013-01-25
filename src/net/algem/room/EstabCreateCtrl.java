/*
 * @(#)EstabCreateCtrl.java 2.7.a 17/01/13
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
import net.algem.contact.ContactFileEditor;
import net.algem.contact.Person;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class EstabCreateCtrl
        extends CardCtrl
{

  private DataConnection dc;
  private GemDesktop desktop;
  private ContactFileEditor contact;

  public EstabCreateCtrl(GemDesktop _desktop) {
    desktop = _desktop;
    dc = _desktop.getDataCache().getDataConnection();
  }

  public void init() {

    contact = new ContactFileEditor(desktop);
    contact.filter(Person.ESTABLISHMENT);
    addCard("Coordonnées etablissement", contact);
    select(0);
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
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Abandon"));
    }
    return true;
  }

	@Override
  public boolean validation() {
    try {
      Establishment p = save();
      desktop.getDataCache().add(p);
      desktop.postEvent(new GemEvent(this, GemEvent.CREATION, GemEvent.ESTABLISHMENT, p));
    } catch (SQLException ex) {
      GemLogger.logException("Insertion etablissement", ex, this);
      return false;
    }
    clear();
    return true;
  }

  public void clear() {
    contact.clear();
    select(0);
  }

	@Override
  public boolean loadCard(Object o) {
    return false;
  }

	@Override
  public boolean loadId(int id) {
    return false;
  }

  Establishment save() throws SQLException {
    Establishment e = new Establishment();
    e.setPerson(contact.getPerson());
    e.setAddress(contact.getAddressAll());
    e.setTele(contact.getTele());
    e.setEmail(contact.getEmail());

    if (!e.isValid()) {
      MessagePopup.error(this, MessageUtil.getMessage("incomplete.entry.error"));
      return null;
    }

    EstablishmentIO.insert(e, Person.ESTABLISHMENT, dc);
    
    return e;
  }
}

