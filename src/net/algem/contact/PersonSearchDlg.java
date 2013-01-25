/*
 * @(#)PersonSearchDlg.java	2.6.a 18/09/12
 * 
 * Copyright (c) 1999-2003 Musiques Tangentes. All Rights Reserved.
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JDialog;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.ui.*;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class PersonSearchDlg
        extends JDialog
        implements ActionListener
{

  protected DataCache dataCache;
  JDialog dlg;
  protected ContactCtrl contactCard;
  protected ListCtrl list;
  protected SearchView searchView;
  protected GemPanel wCard;
  protected GemLabel title;
  protected ActionListener actionListener;
  protected boolean btValidation;
  protected Contact contact;

  public PersonSearchDlg(Frame _parent, DataCache _cache) {
    super(_parent, "Recherche contact", true);
    //super(_parent,"Recherche contact",false);//Modif 1.1c anciennement à true mais bug avec false
    init(_cache);
  }

  public PersonSearchDlg(Dialog _parent, DataCache _cache) {
    super(_parent, "Recherche contact", true);
    //super(_parent,"Recherche contact",false);//Modif 1.1c anciennement à true mais bug avec false
    init(_cache);
  }

  /* Ajout 1.1a */
  public PersonSearchDlg(Frame _parent, DataCache _cache, ContactCtrl _fc) {
    super(_parent, "Vue contact", false);
    init(_cache, _fc);
  }

  public void init(DataCache dc) {

    dataCache = dc;

    title = new GemLabel("Recherche d'un contact");
    title.setFont(new Font("Helvetica", Font.PLAIN, 18));

    wCard = new GemPanel();
    wCard.setLayout(new CardLayout());

    getContentPane().setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(getContentPane());

    Insets in = new Insets(0, 0, 0, 0);

    gb.add(title, 0, 0, 1, 1, in, GridBagHelper.CENTER);
    gb.add(wCard, 0, 1, 1, 1, in, GridBagHelper.BOTH, 1.0, 1.0);

    setSize(650, 450);
    setLocation(70, 30);

    searchView = new PersonSearchView();
    searchView.addActionListener(this);

    list = new PersonListCtrl();
    list.addMouseListener(new MouseAdapter()
    {

      public void mouseClicked(MouseEvent e) {
        int id = list.getSelectedID();
        if (id > 0) {
          contactCard.loadId(id);
        }
        ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      }
    });
    list.addActionListener(this);

    contactCard = new ContactCtrl(dataCache.getDataConnection());
    contactCard.addActionListener(this);

    wCard.add("cherche", searchView);
    wCard.add("masque", contactCard);
    wCard.add("liste", list);

    ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
  }

  /* test 1.1a */
  public void init(DataCache dc, ContactCtrl fc) {
    dataCache = dc;

    title = new GemLabel("Fiche du contact");
    title.setFont(new Font("Helvetica", Font.PLAIN, 18));

    wCard = new GemPanel();
    wCard.setLayout(new CardLayout());

    getContentPane().setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(getContentPane());

    Insets in = new Insets(0, 0, 0, 0);

    gb.add(title, 0, 0, 1, 1, in, GridBagHelper.CENTER);
    gb.add(wCard, 0, 1, 1, 1, in, GridBagHelper.BOTH, 1.0, 1.0);

    setSize(650, 450);
    setLocation(70, 30);

    searchView = new PersonSearchView();
    searchView.addActionListener(this);

    list = new PersonListCtrl();
    list.addMouseListener(new MouseAdapter()
    {

      public void mouseClicked(MouseEvent e) {
        int id = list.getSelectedID();
        if (id > 0) {
          contactCard.loadId(id);
        }
        ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      }
    });
    list.addActionListener(this);
    /* modification ici */
    contactCard = fc;
    contactCard.addActionListener(this);

    wCard.add("cherche", searchView);
    wCard.add("masque", contactCard);
    wCard.add("liste", list);

    ((CardLayout) wCard.getLayout()).show(wCard, "masque");
  }

  public void search() {
    String query;
    int id = 0;
    String name;
    String firstname;

    try {
      id = Integer.parseInt(searchView.getField(0));
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    if (id > 0) {
      query = "WHERE id=" + id;
    } else if ((name = searchView.getField(1)) != null) {
      query = "WHERE nom ~* '^"
              + name + "'";
      //+name.toUpperCase()+"'";
    } else if ((firstname = searchView.getField(2)) != null) {
      query = "WHERE prenom ~* '^"
              + firstname + "'";
    } else {
      query = "";
    }

    query += query.length() > 0 ? " AND " : " WHERE ";
    query += "ptype=" + Person.PERSON;

    Vector<Contact> v = searchContact(query);
    if (v.isEmpty()) {
      setStatus("Aucun enregistrement trouvé");
    } else if (v.size() == 1) {
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      contactCard.loadCard(v.elementAt(0));
    } else {
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
      list.loadResult(v);
    }
  }

  private Vector<Contact> searchContact(String query) {
    return ContactIO.find(query, false, dataCache.getDataConnection());
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  private void setStatus(String message) {
    searchView.setStatus(message);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    //System.out.println("PersonSearchDlg actionPerformed:"+evt);
    String cmd = evt.getActionCommand();
    if (cmd.equals(GemCommand.SEARCH_CMD)) {
      setCursor(new Cursor(Cursor.WAIT_CURSOR));
      searchView.setStatus("");
      list.clear();
      search();
      setCursor(Cursor.getDefaultCursor());
    } else if (cmd.equals(GemCommand.NEW_SEARCH_CMD)) {
      ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
    } else if (cmd.equals("CtrlAbandon")) {
      if (list.nbLines() > 1) {
        ((CardLayout) wCard.getLayout()).show(wCard, "liste");
      } else {
        ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
      }
    } else if (cmd.equals(GemCommand.CANCEL_CMD)) {
      btValidation = false;
      setVisible(false);
      dispose();
    } else if (cmd.equals("CtrlValider")) {
      btValidation = true;
      contact = contactCard.get();
      setVisible(false);
      dispose();
    } else if (cmd.equals(GemCommand.ERASE_CMD)) {
      searchView.clear();
    }
  }

  public Contact getContact() {
    return contact;
  }

  public boolean isValidation() {
    return btValidation;
  }
}

