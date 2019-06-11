/*
 * @(#)GroupSearchCtrl.java	2.15.2 27/09/17
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

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;
import net.algem.config.MusicStyleIO;
import net.algem.contact.Person;
import net.algem.contact.WebSiteIO;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.model.TableIO;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.SearchCtrl;

/**
 * Search controller for groups.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.2
 * @since 1.0a 07/07/1999
 */
public class GroupSearchCtrl
        extends SearchCtrl
        implements GemEventListener
{

  public static final String GROUP_BROWSER_KEY = "Group.browser";
  private final GemDesktop desktop;
  private final GemGroupService service;

  public GroupSearchCtrl(GemDesktop _desktop) {
    super(DataCache.getDataConnection(), null);
    desktop = _desktop;
    desktop.addGemEventListener(this);
    service = new GemGroupService(dc);
  }

  @Override
  public void init() {
    searchView = new GroupSearchView();
    searchView.addActionListener(this);

    list = new GroupListCtrl();
    list.addMouseListener(this);
    list.addActionListener(this);

    wCard.add("cherche", searchView);
    wCard.add("liste", list);

    ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
  }

  @Override
  public void search() {

    String query = null;
    String name;
    String style;
    String site;
    int id = getId();
    if (id > 0) {
      query = "WHERE id = " + id;
    } else if ((name = searchView.getField(1)) != null) {
      query = "WHERE translate(lower(nom),'" + TRANSLATE_FROM + "', '" + TRANSLATE_TO + "') ~* '"
              + TableIO.normalize(name) + "'";
    } else if ((style = searchView.getField(2)) != null) {
      query = ", " + MusicStyleIO.TABLE + " ms WHERE ms.libelle ~* '" + TableIO.escape(style) + "' AND groupe.style = ms.id";
    } else if ((site = searchView.getField(3)) != null) {
      query = ", " + WebSiteIO.TABLE + " w WHERE w.url ~* '" + site + "' AND w.ptype = " + Person.GROUP + " AND groupe.id = w.idper";
    } else {
      query = "";
    }
    query += " ORDER BY nom";
    try {
      List<Group> v = service.find(query);
      if (v.isEmpty()) {
        setStatus(EMPTY_LIST);
      } else if (v.size() == 1) {
        loadGroupFileEditor(v.get(0));
      } else {
        setStatus(MessageUtil.getMessage("search.list.status", v.size()));
        ((CardLayout) wCard.getLayout()).show(wCard, "liste");
        list.loadResult(v);
      }
    } catch (SQLException ex) {
      GemLogger.logException(MessageUtil.getMessage("search.band.exception"), ex);
    }
  }

  @Override
  public void actionPerformed(ActionEvent ev) {
    if (GemCommand.CREATE_CMD.equals(ev.getActionCommand())) {
      create();
    } else if (GemCommand.CANCEL_CMD.equals(ev.getActionCommand())) {
      desktop.removeModule(GROUP_BROWSER_KEY);
      desktop.removeGemEventListener(this);
    } else {
      super.actionPerformed(ev);
    }
  }

  @Override
  public void load(int id) {
    Group g = ((GroupListCtrl) list).getGroupe();
    loadGroupFileEditor(g);
  }

  private void create() {
    loadGroupFileEditor(new Group(0));
  }

  private void loadGroupFileEditor(Group g) {
    GroupFileEditor editor = new GroupFileEditor(g);
    desktop.addModule(editor);
  }

  @Override
  public void postEvent(GemEvent evt) {
    if (list != null && !list.getData().isEmpty()) {
      if (evt instanceof GroupDeleteEvent) {
        list.deleteRow(((GroupDeleteEvent) evt).getGroup());
      } else if (evt instanceof GroupUpdateEvent) {
        list.updateRow(((GroupUpdateEvent) evt).getGroup());
      }
    }
  }

  @Override
  public String toString() {
    return GROUP_BROWSER_KEY;
  }
}
