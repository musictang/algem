/*
 * @(#)ItemSearchCtrl 2.7.a 14/01/13
 *
 * Copyright (c) 2012 Musiques Tangentes All Rights Reserved.
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
package net.algem.billing;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.accounting.AccountIO;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.SearchCtrl;

/**
 * Search for standard invoice items.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.3.a 30/01/12
 */
public class ItemSearchCtrl
        extends SearchCtrl
        implements GemEventListener
{

  protected BasicBillingService service;
  protected GemDesktop desktop;

  public ItemSearchCtrl(GemDesktop desktop) {
    super(desktop.getDataCache().getDataConnection(), MessageUtil.getMessage("invoice.item.edition.label"));
    this.desktop = desktop;
    this.desktop.addGemEventListener(this);
    service = new BasicBillingService(desktop.getDataCache());
  }

  @Override
  public void init() {
    searchView = new ItemSearchView(dc);
    searchView.addActionListener(this);

    list = new ItemListCtrl();
    list.addMouseListener(this);
    list.addActionListener(this);

    mask = new ItemCtrl(service);
    mask.addActionListener(this);
    mask.addGemEventListener(this);

    wCard.add("cherche", searchView);
    wCard.add("masque", mask);
    wCard.add("liste", list);

    ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
  }

  @Override
  public void search() {

    String designation = searchView.getField(0);
    String account = searchView.getField(1);
    String vat = searchView.getField(2);

    String query = "WHERE standard = true ";
    if (designation != null && !designation.isEmpty()) {
      query += "AND designation ~* '" + designation + "'";
    } else if (account != null && !account.isEmpty()) {
      query += "AND compte IN (SELECT id FROM " + AccountIO.TABLE + " WHERE libelle ~* '" + account + "')";
    } else if (vat != null && !vat.isEmpty()) {
      query += "AND id_tva IN (SELECT id FROM " + ItemIO.TVA_TABLE + " WHERE pourcentage = " + vat + ")";
    }
    query += " ORDER BY designation";

    try {
      loadResult(query);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    super.actionPerformed(e);
    if (GemCommand.CREATE_CMD.equals(e.getActionCommand())) {
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      mask.loadCard(null);
    }
  }

  @Override
  public void postEvent(GemEvent evt) {
    try {
      if (evt instanceof ItemDeleteEvent) {
        list.deleteRow(((ItemDeleteEvent) evt).getArticle());
      } else if (evt instanceof ItemUpdateEvent) {
        list.updateRow(((ItemUpdateEvent) evt).getItem());
      }
    } catch (IndexOutOfBoundsException idx) {
      GemLogger.logException(idx);
    }
  }

  protected void loadResult(String query) throws SQLException {

    Vector<Item> v = service.getItems(query);
    if (v == null) {
      setStatus(EMPTY_LIST);
    } else if (v.size() == 1) {
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      mask.loadCard(v.elementAt(0));
    } else {
      list.loadResult(v);
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
    }

  }
}
