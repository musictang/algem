/*
 * @(#)AccountMatchingCfg.java 2.8.v 13/06/14
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
 *
 */
package net.algem.accounting;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import net.algem.util.*;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Management of account matching between personal and revenue accounts.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.2.l 07/12/11
 */
public class AccountMatchingCfg
        extends GemPanel implements ActionListener
{

  private GemDesktop desktop;
  private DataConnection dc;
  private Vector<Account> personalAccounts;
  private Vector<Account> revenueAccounts;
  private static String personalAccountLabel = BundleUtil.getLabel("Personal.account.label");
  private static String revenueAccountLabel = BundleUtil.getLabel("Revenue.account.label");
  private GemButton btOk;
  private GemButton btClose;
  private GemPanel mainPanel = new GemPanel();
  private AccountChoice[] personal;
  private AccountChoice[] revenue;

  public AccountMatchingCfg(GemDesktop desktop) {
    this.desktop = desktop;
    dc = desktop.getDataCache().getDataConnection();
    try {
      String where = " WHERE (numero LIKE '" + AccountUtil.PERSONAL_ACCOUNT_FIRST_DIGIT + "%' OR numero LIKE '" + AccountUtil.CUSTOMER_ACCOUNT_FIRST_LETTER + "%') AND actif = true";
      personalAccounts = AccountIO.find(where, dc);
      where = " WHERE numero LIKE '" + AccountUtil.REVENUE_ACCOUNT_FIRST_DIGIT + "%' AND actif = true";
      revenueAccounts = AccountIO.find(where, dc);

      init();
    } catch (SQLException ex) {
      GemLogger.logException("Config comptes exception", ex);
    } catch (NullAccountException na) {
      MessagePopup.warning(this, na.getMessage());
    }
  }

  public void init() throws SQLException, NullAccountException {

    setLayout(new BorderLayout());

    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JScrollPane sp = new GemScrollPane(mainPanel);

    if (personalAccounts == null || personalAccounts.isEmpty()) {
      throw new NullAccountException(MessageUtil.getMessage("no.personal.account"));
    }
    if (revenueAccounts == null || revenueAccounts.isEmpty()) {
      throw new NullAccountException(MessageUtil.getMessage("no.revenue.account"));
    }

    int max = personalAccounts.size();
    personal = new AccountChoice[max];
    revenue = new AccountChoice[max];

    for (int i = 0; i < max; i++) {
      GemPanel p = new GemPanel(new GridLayout(2,2,10,0));
      p.setBorder(BorderFactory.createTitledBorder(String.valueOf(i + 1)));

      personal[i] = new AccountChoice(personalAccounts);
      revenue[i] = new AccountChoice(revenueAccounts);

      p.add(new GemLabel(personalAccountLabel));
      p.add(new GemLabel(revenueAccountLabel));
      p.add(personal[i]);
      p.add(revenue[i]);

      mainPanel.add(p);
    }
    set();

    GemPanel commandPanel = new GemPanel();
    commandPanel.setLayout(new GridLayout(1, 1));

    btOk = new GemButton(GemCommand.VALIDATION_CMD);
    btOk.setToolTipText(BundleUtil.getLabel("Save.and.close.tip"));
    btClose = new GemButton(GemCommand.CANCEL_CMD);
    btClose.setToolTipText(BundleUtil.getLabel("Close.without.saving.tip"));
    btOk.addActionListener(this);
    btClose.addActionListener(this);

    commandPanel.add(btOk);
    commandPanel.add(btClose);

    add(sp, BorderLayout.CENTER);
    add(commandPanel, BorderLayout.SOUTH);

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == btOk) {
      try {
        save();
      } catch (SQLException ex) {
        GemLogger.logException("Update correspondance comptes", ex);
      }
    }

    desktop.removeCurrentModule();

  }

  private void save() throws SQLException {

    for (int i = 0; i < personal.length; i++) {
      int t = ((Account) personal[i].getSelectedItem()).getId();
      int p = ((Account) revenue[i].getSelectedItem()).getId();
      if (PersonalRevenueAccountIO.find(t, dc) == 0) {
        PersonalRevenueAccountIO.insert(t, p, dc);
      } else {
        PersonalRevenueAccountIO.update(t, p, dc);
      }
    }
  }

  private void set() throws SQLException {

    Map<Account, Account> cm = PersonalRevenueAccountIO.find(dc);
    if (cm != null) {
      Account keys[] = new Account[cm.size()];
      Account values[] = new Account[cm.size()];
      int j = 0;
      for (Account c : cm.keySet()) {
        keys[j++] = c;
      }
      j = 0;
      for (Account c : cm.values()) {
        values[j++] = c;
      }

      for (int i = 0; i < cm.size(); i++) {
        personal[i].setSelectedItem(keys[i]);
        revenue[i].setSelectedItem(values[i]);
      }
    }

  }
}
