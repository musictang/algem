/*
 * @(#)VATView  2.14.0 07/06/17
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
package net.algem.billing;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Vector;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import net.algem.accounting.Account;
import net.algem.accounting.AccountChoice;
import net.algem.accounting.AccountUtil;
import net.algem.config.Param;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemLabel;
import net.algem.config.ParamView;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemNumericField;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.3.c 12/03/12
 */
public class VATView
  extends ParamView {

  private GemChoice account;
  private JFormattedTextField rate;

  public VATView(Vector<Account> accounts) {

    account = new AccountChoice(accounts);
    key = new GemNumericField(5);
    key.setEditable(false);
    rate = new JFormattedTextField(AccountUtil.getDefaultNumberFormat());
    rate.setColumns(5);

    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btModify = new GemButton(GemCommand.SAVE_CMD);
    btDelete = new GemButton(GemCommand.DELETE_CMD);
    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));
    buttons.add(btDelete);
    buttons.add(btCancel);
    buttons.add(btModify);

    GemPanel mask = new GemPanel();
    mask.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(mask);
    keyLabel = new GemLabel();
    valueLabel = new GemLabel();

    setLabels();

    gb.add(keyLabel, 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(valueLabel, 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Account.label")), 0, 2, 1, 1, GridBagHelper.WEST);

    gb.add(key, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(rate, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(account, 1, 2, 1, 1, GridBagHelper.WEST);

    setLayout(new BorderLayout());
    add(mask, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
  }

  @Override
  public void setLabels() {
    keyLabel = new GemLabel(BundleUtil.getLabel("Id.label"));
    valueLabel = new GemLabel(BundleUtil.getLabel("Rate.label"));
  }

  @Override
  public void set(Param p) {
    key.setText(String.valueOf(p.getId()));
    rate.setValue(Float.parseFloat(p.getKey()));
    if (p instanceof Vat) {
      Account a = ((Vat) p).getAccount();
      if (a != null) {
        account.setKey(a.getId());
      }
    }

  }

  @Override
  public Param get() {
    Vat t = new Vat();
    t.setId(key.getText().isEmpty() ? 0 : Integer.parseInt(key.getText()));
    t.setKey(String.valueOf(rate.getValue()));
    t.setAccount((Account) account.getSelectedItem());

    return t;

  }

  @Override
  public void clear() {
    key.setText("");
    rate.setValue(null);
    account.setSelectedIndex(0);
  }

}
