/*
 * @(#)RoomSearchCtrl.java	2.9.4.6 02/06/15
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
import java.awt.event.ActionEvent;
import java.util.Vector;
import net.algem.contact.Contact;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.SearchCtrl;

/**
 * Search controller for rooms.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.6
 * @since 2.2.b
 */
public class RoomSearchCtrl
        extends SearchCtrl
{

  public static final String ROOM_BROWSER_KEY = "Room.browser";
  private final GemDesktop desktop;

  public RoomSearchCtrl(GemDesktop desktop) {
    super(DataCache.getDataConnection(), null);
    this.desktop = desktop;
  }

  @Override
  public void init() {
    searchView = new RoomSearchView();
    searchView.addActionListener(this);

    list = new RoomListCtrl();
    list.addMouseListener(this);
    list.addActionListener(this);

    wCard.add("cherche", searchView);
    wCard.add("liste", list);

    ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
  }

  @Override
  public void search() {

    String query = "";
    String name;
    String estab;
    String contact;

    int id = getId();
    if (id > 0) {
      query = "WHERE id = " + id;
    } else if ((name = searchView.getField(1)) != null) {
      query = "WHERE translate(lower(nom),'" + TRANSLATE_FROM + "', '" + TRANSLATE_TO + "') ~* '" + TableIO.normalize(name) + "'";
    } else if ((estab = searchView.getField(2)) != null) {
      query = "WHERE etablissement = " + estab;
    } else if ((contact = searchView.getField(3)) != null) {
      query = "WHERE idper = " + contact;
    }
    query += " ORDER BY nom";

    Vector<Room> v = ((RoomIO) DataCache.getDao(Model.Room)).find(query);
    if (v == null || v.isEmpty()) {
      setStatus(EMPTY_LIST);
    } else if (v.size() == 1) {
      loadRoomFile(v.elementAt(0));
    } else {
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
      list.loadResult(v);
    }
  }

  @Override
  public void actionPerformed(ActionEvent ev) {
    if (GemCommand.CREATE_CMD.equals(ev.getActionCommand())) {
      create();
    } else if (GemCommand.CANCEL_CMD.equals(ev.getActionCommand())) {
      desktop.removeModule(ROOM_BROWSER_KEY);
    } else {
      super.actionPerformed(ev);
    }
  }

  private void create() {
    Room s = new Room(0);
    s.setContact(new Contact());
    loadRoomFile(s);
  }

  @Override
  public void load(int id) {
    Room s = ((RoomIO) DataCache.getDao(Model.Room)).findId(id);
    loadRoomFile(s);
  }

  private void loadRoomFile(Room r) {
    RoomFileEditor roomFile = new RoomFileEditor(r);
    desktop.addModule(roomFile);
  }

  @Override
  public String toString() {
    return ROOM_BROWSER_KEY;
  }
}

