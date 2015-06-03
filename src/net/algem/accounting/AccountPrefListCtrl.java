/*
 * @(#)AccountPrefListCtrl.java 2.9.4.6 01/06/15
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 * 
 */
package net.algem.accounting;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.algem.config.Param;
import net.algem.config.ParamChoice;
import net.algem.config.Preference;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.ListCtrl;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.6
 * @since 2.9.4.6 01/06/15
 */
public class AccountPrefListCtrl
        extends ListCtrl
        implements ActionListener
{

  private static final String DEFAULT_ACCOUNT_KEY = "Menu.default.account";
  private GemDesktop desktop;
  private AccountingService service;
  private GemButton btValidate;
  
  public AccountPrefListCtrl(GemDesktop desktop, AccountingService service, List<Account> accounts, List<Param> costAccounts) {
    super(false);
    this.desktop = desktop;
    this.service = service;
    this.tableModel = new AccountPrefTableModel();
    
    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setRowHeight(table.getRowHeight() + 3);
    TableColumnModel cm = table.getColumnModel();
    GemChoice accountSelector = new AccountChoice(new Vector<Account>(accounts));
    GemChoice costAccountSelector = new ParamChoice(costAccounts);
    TableColumn accountCol = cm.getColumn(1);
    TableColumn costAccountCol = cm.getColumn(2);
    accountCol.setCellEditor(new DefaultCellEditor(accountSelector));
    costAccountCol.setCellEditor(new DefaultCellEditor(costAccountSelector));

    JScrollPane p = new JScrollPane(table);
    p.setBorder(new BevelBorder(BevelBorder.LOWERED));
    
    add(p, BorderLayout.CENTER);
    btValidate = new GemButton(GemCommand.VALIDATION_CMD);
    btValidate.addActionListener(this);
    
    GemButton btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);
    GemPanel buttons = new GemPanel(new GridLayout(1, 2));
    buttons.add(btValidate);
    buttons.add(btCancel);
    add(buttons, BorderLayout.SOUTH);
    
  }
  
  public void load(String[] keys) {
    try {
      
      List<AccountPref> prefs = new ArrayList<AccountPref>();
      if (keys != null && keys.length > 0) {
        for (int i = 0, max = keys.length; i < max; i++) {
          Preference pref = AccountPrefIO.find(keys[i], DataCache.getDataConnection());
          Object[] values = pref.getValues();
          
          AccountPref p = new AccountPref();
          
          p.setKey(keys[i]);
          if ((int) values[0] > 0) {
            p.setAccount((Account) DataCache.findId((int) values[0], Model.Account));
          }
          if (values[1] != null) {
            p.setCostAccount(DataCache.getCostAccount((String) values[1]));
          }
          prefs.add(p);
        }
      }
      loadResult(prefs);
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName(), "ComptesPrefsCtrl", ex);
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btValidate) {
      stopCellEditing();
      List<AccountPref> prefs = tableModel.getData();
      for (AccountPref p : prefs) {
        try {
          Account c = p.getAccount();
          Param ca = p.getCostAccount();
          if (c != null && ca != null) {
            Preference pref = new Preference(p.getKey(), new String[]{String.valueOf(c.getId()), ca.getKey()});
            service.updateAccountPref(pref);
          }
        } catch (SQLException ex) {
          GemLogger.log(getClass().getName(), "actionPerformed", ex);
        }
      }
    }
    close();
  }
  
  protected void close() {
    desktop.removeModule(DEFAULT_ACCOUNT_KEY);
  }
}
