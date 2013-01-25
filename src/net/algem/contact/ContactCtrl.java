/*
 * @(#)ContactCtrl.java	2.6.a 17/09/12
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
package net.algem.contact;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.ErrorDlg;
import net.algem.util.ui.GemBar;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ContactCtrl
        extends CardCtrl {

  private DataConnection dc;
  private ContactView contactView;
  private Contact contact;
  private Note note;

  public ContactCtrl(DataConnection _dc) {
    dc = _dc;

    contactView = new ContactView(dc);
    contactView.setCodePostalCtrl(new CodePostalCtrl(dc));

    addCard("fiche", contactView);
    bar = new GemBar();
    bar.addIcon(BundleUtil.getLabel("Contact.note.icon"), "Notes", BundleUtil.getLabel("Contact.note.tip"));
    bar.addButtonListener(this);

    add(bar, BorderLayout.NORTH);
    select(0);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals("Notes")) {
      NoteDlg nd = new NoteDlg(this, dc);
      nd.loadNote(note, contact);
      nd.show();
      contactView.setNote(nd.getNote());
    } else {
      super.actionPerformed(evt);
    }
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
      Contact c = get();
      if (!c.isValid()) {
        new ErrorDlg(this, MessageUtil.getMessage("incomplete.entry.error"));
        return false;
      }

      new ContactIO(dc).update(contact, c);

    } catch (Exception e1) {
      GemLogger.logException("Update contact", e1, this);
      return false;
    }
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
    }
    return true;
  }

  public void clear() {
    contactView.clear();
  }

  @Override
  public boolean loadCard(Object o) {
    clear();
    select(0);
    if (o == null || !(o instanceof Contact)) {
      return false;
    }

    contact = (Contact) o;
    if (!contact.isComplete()) {
      try {
        contact.setAddress(AddressIO.findId(contact.getId(), dc));
        contact.setTele(TeleIO.findId(contact.getId(), dc));
        contact.setEmail(EmailIO.find(contact.getId(), dc));
      } catch (SQLException e) {
        GemLogger.logException(e);
      }
    }
    contactView.setPerson(contact);
    if (contact.getAddress() != null) {
      contactView.setAddress(contact.getAddress());
    }
    if (contact.getTele() != null) {
      contactView.setTele(contact.getTele());
    }
    if (contact.getEmail() != null) {
      contactView.setEmail(contact.getEmail());
    }
    try {
      note = NoteIO.findId(contact.getId(), contact.getType(), dc);
    } catch (NoteException ex) {
      GemLogger.logException(ex);
    }
    if (note != null) {
      contactView.setNote(note);
    }
    return true;
  }

  @Override
  public boolean loadId(int id) {
    return loadCard(new ContactIO(dc).findId(id, dc));
  }

  public Contact get() {
    Contact c = new Contact(contactView.getPerson());
    c.setAddress(contactView.getAddress());
    c.setTele(contactView.getTele());
    c.setEmail(contactView.getEmail());
    c.setWebSites(contactView.getSites());

    return c;
  }
}
