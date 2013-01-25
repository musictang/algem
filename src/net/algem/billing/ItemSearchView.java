/*
 * @(#)ItemSearchView.java  2.6.a 03/10/12
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

package net.algem.billing;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import javax.swing.JFormattedTextField;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.ui.*;

/**
 * Search view for standard invoice items.
 * Only standard items are took into account.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.3.a 30/01/12
 */
public class ItemSearchView
        extends SearchView
{

  private GemField designation;

  /** Account number */
  private GemField account;

  /** VAT percent. */
  private JFormattedTextField vat;
  
  private GemPanel mask;

  public ItemSearchView(DataConnection dc) {
    super(); // call init() 
  }

  @Override
  public GemPanel init() {
    
    designation = new GemField(20);
    account = new GemField(10);
    vat = new JFormattedTextField(NumberFormat.getNumberInstance());
    vat.setColumns(5);
    designation.addActionListener(this);
    account.addActionListener(this);
    vat.addActionListener(this);

    btErase = new GemButton(GemCommand.ERASE_CMD);
    btErase.addActionListener(this);

    mask = new GemPanel();
    mask.setLayout(new GridBagLayout());

    GridBagHelper gb = new GridBagHelper(mask);
    gb.insets = GridBagHelper.SMALL_INSETS;
    gb.add(new GemLabel(BundleUtil.getLabel("Invoice.item.description.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Account.label")), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Invoice.item.vat.label")), 0, 2, 1, 1, GridBagHelper.EAST);

    gb.add(designation, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(account, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(vat, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(btErase,2, 3, 1, 1, GridBagHelper.WEST);
    
    return mask;
  }

  @Override
  public String getField(int n) {

    switch (n) {
      case 0:
        return designation.getText();
      case 1:
        return account.getText();
      case 2:
        Number t = ((Number) vat.getValue());
        return  (t == null) ? null : String.valueOf(t.doubleValue());
      default:
        return null;
    }

  }

  @Override
  public void clear() {
    designation.setText(null);
    account.setText(null);
    vat.setValue(null);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (actionListener == null) {
      return;
    }
    if (e.getSource() == designation
            || e.getSource() == account
            || e.getSource() == vat) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.SEARCH_CMD));
    } else {
        actionListener.actionPerformed(e);
    }
  }


}
