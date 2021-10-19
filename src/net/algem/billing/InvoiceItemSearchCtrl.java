/*
 * @(#)InvoiceItemSearchCtrl.java 2.9.4.6 01/06/15
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
 */

package net.algem.billing;

import java.awt.CardLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.util.BundleUtil;
import net.algem.util.event.GemEventListener;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.6
 * @since 2.3.a 15/02/12
 */
public class InvoiceItemSearchCtrl
        extends ItemSearchCtrl

{

  static final String INVOICE_ITEM_BROWSER_KEY = BundleUtil.getLabel("Invoice.item.search.label");
  
  public InvoiceItemSearchCtrl(GemDesktop desktop) {
    super(desktop);
  }

  @Override
  public void init() {
    searchView = new ItemSearchView(dc);
    searchView.addActionListener(this);

    list = new ItemListCtrl(new InvoiceItemTableModel(), true);
    list.addMouseListener(this);
    list.addActionListener(this);

    mask = new InvoiceItemCtrl(dc, service);
    mask.addActionListener(this);
    
    wCard.add("cherche", searchView);
    wCard.add("masque", mask);
    wCard.add("liste", list);

    ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
  }

  @Override
  public void loadResult(String query) throws SQLException {

    List<Item> v = service.getItems(query);

    if (v == null) {
      setStatus(EMPTY_LIST);
    } else if (v.size() == 1) {
      InvoiceItem af = new InvoiceItem(v.get(0));
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      mask.loadCard(af);
    } else {
      List<InvoiceItem> vf = new ArrayList<>();
      for (Item a : v) {
        vf.add(new InvoiceItem(a));
      }
      list.loadResult(vf);
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
    }
  }

  public void addGemEventListener(GemEventListener g) {
    mask.addGemEventListener(g);
  }

  public void close() {
    desktop.removeModule(INVOICE_ITEM_BROWSER_KEY);
  }

}
