/*
 * @(#)AgeRangeSearchCtrl.java 2.8.w 08/07/14
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
 */
package net.algem.config;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.MessagePopup;
import net.algem.util.ui.SearchCtrl;

/**
 *
 * @author <a href="mailto:nicolasnouet@gmail.com">Nicolas Nouet</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.3.a
 */
public class AgeRangeSearchCtrl
        extends SearchCtrl
{

  private final DataCache dataCache;
  private final GemDesktop desktop;
  private final AgeRangeIO dao;

  public AgeRangeSearchCtrl(GemDesktop _desktop) {
    super(DataCache.getDataConnection(), MessageUtil.getMessage("age.range.edition.label"));
    this.desktop = _desktop;
    dataCache = _desktop.getDataCache();
    dao = (AgeRangeIO) DataCache.getDao(Model.AgeRange);
  }

  @Override
  public void init() {
    try {
      //    searchView.addActionListener(this);
      //    searchView.addActionListener(this);

      list = new AgeRangeListCtrl(false);
      list.addMouseListener(this);
      list.addActionListener(this);

      mask = new AgeRangeCardCtrl();
      mask.addActionListener(this);

      //    wCard.add("cherche", searchView);
      wCard.add("masque", mask);
      wCard.add("liste", list);

      String query = " ORDER BY agemin";
      Vector<AgeRange> v = dao.find(query);
      if (v == null || v.isEmpty()) {
        setStatus(EMPTY_LIST);
      } /*else if (v.size() == 1) {
        ((CardLayout) wCard.getLayout()).show(wCard, "masque");
        mask.loadCard(v.elementAt(0));
      }*/ else {
        ((CardLayout) wCard.getLayout()).show(wCard, "liste");
        list.loadResult(v);
      }
      //((CardLayout) wCard.getLayout()).show(wCard, "cherche");
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  @Override
  public void search() {
    String agemin = searchView.getField(0);
    String agemax = searchView.getField(1);

    String query = "";

    if (agemin != null) {
      query = "WHERE agemin >= " + agemin;
      if (agemax != null) {
        query += " AND agemax <= " + agemax;
      }
    } else if (agemax != null) {
      query = "WHERE agemax <= " + agemax;
    }

    query += " ORDER BY agemin";

    try {
      Vector<AgeRange> v = dao.find(query);
      if (v == null || v.isEmpty()) {
        setStatus(EMPTY_LIST);
      } else if (v.size() == 1) {
        ((CardLayout) wCard.getLayout()).show(wCard, "masque");
        mask.loadCard(v.elementAt(0));
      } else {
        ((CardLayout) wCard.getLayout()).show(wCard, "liste");
        list.loadResult(v);
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    super.actionPerformed(evt);
    String cmd = evt.getActionCommand();
    if (GemCommand.CLOSE_CMD.equals(cmd)) {
      desktop.removeModule("Menu.age.range");
    } else if (GemCommand.CREATE_CMD.equals(cmd)) {
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      mask.loadCard(null);
    } else if (GemCommand.APPLY_CMD.equals(cmd)) {
      AgeRange r = ((AgeRangeCardCtrl) evt.getSource()).getRange();
      try {
        /* AgeRange t = AgeRangeIO.check(r, dc);
         * if (t != null) {
         * throw new AgeRangeException(MessageUtil.getMessage("age.range.error.validation5", t.getLabel()));
         * } */
        if (r.getId() == 0) {
          dao.insert(r);
          list.addRow(r);
          dataCache.add(r);
          desktop.postEvent(new GemEvent(this, GemEvent.CREATION, GemEvent.AGE_RANGE, r));
        } else {
          dao.update(r);
          list.updateRow(r);
          dataCache.update(r);
          desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.AGE_RANGE, r));
        }
      } catch (AgeRangeException ex) {
        MessagePopup.error(this, ex.getMessage());
      }
    } else if (GemCommand.DELETE_CMD.equals(cmd)) {
      AgeRange d = ((AgeRangeCardCtrl) evt.getSource()).getRange();
      try {
        dao.delete(d.getId());
        list.deleteRow(d);
        dataCache.remove(d);
        desktop.postEvent(new GemEvent(this, GemEvent.SUPPRESSION, GemEvent.AGE_RANGE, d));
      } catch (AgeRangeException ex) {
        MessagePopup.error(this, ex.getMessage());
      }
    }
  }
}
