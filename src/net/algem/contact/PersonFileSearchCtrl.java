/*
 * @(#)PersonFileSearchCtrl.java 2.8.t 15/05/14
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
package net.algem.contact;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Vector;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEventListener;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemDesktopCtrl;
import net.algem.util.ui.SearchCtrl;

/**
 * Search for contacts.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 1.0a 07/07/1999
 */
public class PersonFileSearchCtrl
        extends SearchCtrl
        implements Runnable
{

  private GemDesktop desktop;
  private String query;
  private String cquery = "DECLARE pc CURSOR FOR SELECT * FROM " + PersonIO.TABLE;
  private PersonFileEditor dossierPersonne;
  private Contact currentContact;
  private GemEventListener gemListener;

  public PersonFileSearchCtrl(GemDesktop desktop, String title) {
    super(desktop.getDataCache().getDataConnection(), title);
    this.desktop = desktop;
  }

  public PersonFileSearchCtrl(GemDesktop _desktop, String title, GemEventListener gl) {
    this(_desktop, title);
    gemListener = gl;

  }

  @Override
  public void init() {
    searchView = new PersonSearchView();
    searchView.addActionListener(this);

    list = new PersonListCtrl();
    list.addMouseListener(this);
    list.addActionListener(this);

    wCard.add("cherche", searchView);
    wCard.add("liste", list);

    ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
  }

  @Override
  public void search() {

    String org, name, firstname, telephone, email, site;
    int id = getId();
    if (id > 0) {
      query = "WHERE id = " + id;
    } else if ((org = searchView.getField(1)) != null) {
      query = "WHERE translate(lower(organisation),'" + TRANSLATE_FROM + "', '" + TRANSLATE_TO + "') ~* '"
              + TableIO.normalize(org) + "'";
    } else if ((name = searchView.getField(2)) != null) {
      query = "WHERE translate(lower(nom),'" + TRANSLATE_FROM + "', '" + TRANSLATE_TO + "') ~* '"
              + TableIO.normalize(name) + "'";
    } else if ((firstname = searchView.getField(3)) != null) {
      query = "WHERE translate(lower(prenom),'" + TRANSLATE_FROM + "', '" + TRANSLATE_TO + "') ~* '"
              + TableIO.normalize(firstname) + "'";
    } else if ((telephone = searchView.getField(4)) != null) {
      query = ", "+TeleIO.TABLE+" t WHERE id = t.idper AND t.numero ~ '"+telephone+"'";
    } //ajout 2.0g recherche email
    else if ((email = searchView.getField(5)) != null) {
      query = ", "+EmailIO.TABLE+" e WHERE id = e.idper AND e.email ~* '"+email+"'";
    } // ajout 2.1a recherche site web
    else if ((site = searchView.getField(6)) != null) {
      query = ", "+WebSiteIO.TABLE+" s WHERE id = s.idper AND s.url ~* '"+site+"'";
    } else {
      query = "";
    }

    query += query.length() > 0 ? " AND " : " WHERE ";
    query += "personne.ptype != " + Person.BANK + " AND personne.ptype != " + Person.ESTABLISHMENT;

    int nb = ContactIO.count(query, dc);

    if (nb == 0) {
      setStatus(EMPTY_LIST);
    } else if (nb == 1) {
      ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
      currentContact = ContactIO.findId(query, dc);
      ContactIO.complete(currentContact, dc);
      createModule();
    } else {
      setStatus(MessageUtil.getMessage("search.list.status", new Object[]{nb}));
      if (thread != null) {
        abort = true;
        try {
          thread.join();
        } catch (Exception ignore) {
        }
      }
      abort = false;
      thread = new Thread(this, "select");
      list.clear();

      thread.start();
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
    }
  }

  @Override
  public void run() {
    String url = dc.getUrl();
    Connection cnx;
    try {
      cnx = DriverManager.getConnection(url, dc.getConnectionProperties());
    } catch (SQLException e) {
      GemLogger.logException("ThreadTablePersonne: Erreur Connexion:", e);
      return;
    }

    String squery = cquery + " " + query + " ORDER BY nom,prenom";
    int cpt = 0;
    try {
      Vector<Contact> block; // vecteur de Contact
      Statement stmt = cnx.createStatement();
      stmt.executeUpdate("begin");
      stmt.executeUpdate(squery);
      do {
        block = new Vector<Contact>();
        ResultSet rs = stmt.executeQuery("FETCH 20 in pc");
        while (rs.next() && !abort) {
          Contact c = new Contact(PersonIO.getFromRS(rs));
          block.addElement(c);
          //barre.setValue(++row);
        }
        list.addBlock(block);// on ajoute les contacts à la list
        cpt++;
//                System.out.println("ThreadBrowser add block:"+cpt);
      } while (block.size() > 0 && !abort);
      stmt.executeUpdate("end");
      stmt.close();
    } catch (Exception ex) {
      GemLogger.logException(query, ex);
    }

    try {
      cnx.close();
    } catch (Exception ignore) {
      GemLogger.logException(ignore);
    }
    thread = null;
    query = "";
  }

  @Override
  public void load(int id) {
    currentContact = ContactIO.findId(id, dc);
    createModule();
  }

  public void createModule() {
    // selection contact for group and musician editor
    if (gemListener != null) {
      gemListener.postEvent(new ContactSelectEvent(this, currentContact));
      desktop.removeCurrentModule();
      return;
    }
    // Recherche si un module correspondant au contact est déjà ouvert.
    PersonFileEditor m = ((GemDesktopCtrl) desktop).getPersonFileEditor(currentContact.getId());

    if (m == null) {
      PersonFile pFile = new PersonFile(currentContact);
      try {
        // On complète le dossier avec les informations prof, adherent, banque/rib s'il y a lieu
        ((PersonFileIO) DataCache.getDao(Model.PersonFile)).complete(pFile);
      } catch (SQLException ex) {
        GemLogger.logException("complete dossier browser", ex);
      }
      PersonFileEditor editor = new PersonFileEditor(pFile);
      desktop.addModule(editor);
    } else {
      desktop.setSelectedModule(m);
    }

  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    super.actionPerformed(evt);
    String cmd = evt.getActionCommand();
    if (GemCommand.CREATE_CMD.equals(cmd)) {
      dossierPersonne = new PersonFileEditor();
      desktop.addModule(dossierPersonne);
    } else if (GemCommand.CANCEL_CMD.equals(cmd)) {
      desktop.removeCurrentModule();
    }

  }
}

