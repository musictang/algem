/*
 * @(#)EstabSearchCtrl.java	2.9.4.6 28/05/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

import java.awt.CardLayout;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.contact.*;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.SearchCtrl;

/**
 * Search controller for establisments.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.6
 * @since 1.0a 07/07/1999
 */
public class EstabSearchCtrl
        extends SearchCtrl
{

  private final GemDesktop desktop;

  public EstabSearchCtrl(GemDesktop _desktop, String title) {
    super(DataCache.getDataConnection(), title);
    desktop = _desktop;
  }

  @Override
  public void init() {
    searchView = new EstabSearchView();
    searchView.addActionListener(this);

    list = new EstabListCtrl();
    list.addMouseListener(this);
    list.addActionListener(this);

    mask = new EstabCtrl(desktop);
    mask.addActionListener(this);
    mask.addGemEventListener((EstabListCtrl) list);

    wCard.add("cherche", searchView);
    wCard.add("masque", mask);
    wCard.add("liste", list);

//    ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
  }

  @Override
  public void search() {

    String query = "WHERE ptype = " + Person.ESTABLISHMENT;
    String name;
    int id = getId();
    if (id > 0) {
      query += " AND id = " + id;
    } else if ((name = searchView.getField(1)) != null) {
      query += " AND nom ~* '" + name + "' AND ptype =" + Person.ESTABLISHMENT;
    }

    Vector<Person> v = PersonIO.find(query, dc);
    if (v.isEmpty()) {
      setStatus(EMPTY_LIST);
    } else if (v.size() == 1) {// ne fonctionnait pas
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      Establishment estab = new Establishment((Person) v.elementAt(0));
      try {
        estab.setAddress(AddressIO.findId(estab.getId(), dc));
        estab.setTele(TeleIO.findId(estab.getId(), dc));
        estab.setEmail(EmailIO.find(estab.getId(), dc));
      } catch (SQLException e) {
        GemLogger.logException(e);
      }
      mask.loadCard(estab);
    } else {
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
      list.loadResult(v);
    }
  }
  
  public void load() {
    String query = "WHERE ptype = " + Person.ESTABLISHMENT;
    Vector<Person> v = PersonIO.find(query, dc);
    ((CardLayout) wCard.getLayout()).show(wCard, "liste");
    list.loadResult(v);
  }
  
}
