/*
 * @(#)AccountMatchingCfg.java 2.9.4.13 07/10/15
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
package net.algem.accounting;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.algem.config.ParamChoice;
import net.algem.util.*;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Management of account matching between personal and revenue accounts.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.2.l 07/12/11
 */
public class AccountMatchingCfg
        extends GemPanel
        implements ActionListener
{
  private final GemDesktop desktop;
  private final DataConnection dc;
  private List<Account> personalAccounts;
  private List<Account> revenueAccounts;
  private GemButton btOk;
  private GemButton btClose;
  private JTable table;
  private JTableModel<AccountPref> tableModel;

  public AccountMatchingCfg(GemDesktop desktop) {
    this.desktop = desktop;
    dc = DataCache.getDataConnection();
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

  private void init() throws SQLException, NullAccountException {
    if (personalAccounts == null || personalAccounts.isEmpty()) {
      throw new NullAccountException(MessageUtil.getMessage("no.personal.account"));
    }
    if (revenueAccounts == null || revenueAccounts.isEmpty()) {
      throw new NullAccountException(MessageUtil.getMessage("no.revenue.account"));
    }
    tableModel = new AccountMatchingTableModel();
    table = new JTable(tableModel);
    table.setRowHeight(table.getRowHeight() + 3);

    GemChoice accountSelector = new AccountChoice(new Vector<Account>(personalAccounts));
    GemChoice revenueSelector = new ParamChoice(revenueAccounts);
    table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(accountSelector));
    table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(revenueSelector));
    
    set();
    
    setLayout(new BorderLayout());
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

    JScrollPane scroll = new GemScrollPane(table);

    add(scroll, BorderLayout.CENTER);
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
    List<AccountPref> prefs = tableModel.getData();
    for (AccountPref pref : prefs) {
      if (pref.getCostAccount() == null) {
        continue;
      }
      if (PersonalRevenueAccountIO.find(pref.getAccount().getId(), dc) == 0) {
        PersonalRevenueAccountIO.insert(pref.getAccount().getId(), pref.getCostAccount().getId(), dc);
      } else {
        PersonalRevenueAccountIO.update(pref.getAccount().getId(), pref.getCostAccount().getId(), dc);
      }
    }
  }

  private void set() throws SQLException {
    Map<Account, Account> prefs = PersonalRevenueAccountIO.find(dc);
    for (Account a : personalAccounts) {
      AccountPref p = new AccountPref();
      p.setAccount(a);
      if (prefs != null && prefs.get(a) != null) {
        p.setCostAccount(prefs.get(a));
      }
      tableModel.addItem(p);
    }
  }
  
}
