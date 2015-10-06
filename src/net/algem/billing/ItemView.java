/*
 * @(#)ItemView.java 2.9.4.13 02/10/2015
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

package net.algem.billing;

import java.awt.GridBagLayout;
import java.util.logging.Level;
import net.algem.accounting.AccountChoice;
import net.algem.config.Param;
import net.algem.config.ParamChoice;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.ui.*;

/**
 * Invoice item view.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.n
 * @since 2.3.a 03/02/12
 */
public class ItemView
        extends GemBorderPanel
{

  protected GemNumericField id;
  protected GemField designation;
  protected GemDecimalField price;
  protected GemChoice account;
  protected ParamChoice vat;
  //protected JCheckBox standard;
  protected GridBagHelper gb;
  protected boolean standard;

  public ItemView(final BillingService service) {

    id = new GemNumericField(5);
    id.setEditable(false);
    designation = new GemField(30, ItemIO.MAX_LENGH);
    price = new GemDecimalField();

    price.setColumns(10);
    account = new AccountChoice(service.getAccounts());
    
    vat = new ParamChoice(service.getVat());

    setLayout(new GridBagLayout());
    gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;
    gb.add(new GemLabel("Id"), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Invoice.item.description.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Invoice.item.price.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Account.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Invoice.item.vat.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(id, 1, 0, 3, 1, GridBagHelper.WEST);
    gb.add(designation, 1, 1, 3, 1, GridBagHelper.WEST);
    gb.add(price, 1, 2, 3, 1, GridBagHelper.WEST);
    gb.add(account, 1, 3, 3, 1, GridBagHelper.WEST);
    gb.add(vat, 1, 4, 3, 1, GridBagHelper.WEST);

  }

  /**
   * Returns an item (possibly updated in the view).
   * @return an item
   */
  public Item get() {

    int i = 0;
    String idString = id.getText();
    if (idString == null || idString.isEmpty()) {
      idString = "0";
    }
    try {
      i = Integer.parseInt(idString);
    } catch (NumberFormatException nfe) {
      GemLogger.log(Level.WARNING, nfe.getMessage());
    }
    Item a = new Item(i);
    a.setDesignation(designation.getText().trim());
    a.setPrice(((Number) price.getValue()).doubleValue());
    a.setAccount(account.getKey());
    a.setVat((Param) vat.getSelectedItem());
    //a.setStandard(standard.isSelected());
    a.setStandard(standard);

    return a;
  }

  /**
   * Sets an item in the view.
   * @param a current item
   */
  public void set(Item a) {
    
    id.setText(String.valueOf(a.getId()));
    designation.setText(a.getDesignation());
    price.setValue(a.getPrice());
    account.setKey(a.getAccount());
    Param t = a.getVat();
    if (t != null) {
      vat.setKey(a.getVat().getKey());
    }
    standard = a.isStandard();
    //standard.setSelected(a.isStandard());
  }

  /**
   * Resets the view.
   */
  public void clear() {
    
    id.setText(null);
    designation.setText(null);
    price.setValue(0.0);
    account.setSelectedIndex(0);
    vat.setSelectedIndex(0);
    //standard.setSelected(false);
  }

}
