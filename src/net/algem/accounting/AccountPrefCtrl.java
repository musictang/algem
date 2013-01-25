/*
 * @(#)AccountPrefCtrl.java 2.6.a 12/09/12
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
package net.algem.accounting;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import net.algem.config.Param;
import net.algem.config.ParamChoice;
import net.algem.config.ParamTableIO;
import net.algem.config.Preference;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 * Management of default accounts : membership, enrolment, course subscription, rehearsal, etc.
 * On update only. Creation of new default account is not activated.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.1.i
 */
public class AccountPrefCtrl
        extends GemPanel implements ActionListener
{

  GemPanel choice = new GemPanel();
  String[] keys;
  GemChoice[] ccChoix;
  ParamChoice[] caChoix;
  
  private GemDesktop desktop;
  private DataConnection dc;
  private Vector<Account> accounts;
  private Vector<Param> costAccounts;
  private static String accountLabel = "Compte";
  private static String costLabel = "Analytique";
  private GemButton ok;
  private GemButton cancel;  

  public AccountPrefCtrl() {
  }

  /**
   * Initialisation du JPanel et préchargement de la liste des comptes comptables et analytiques.
   * @param desktop
   */
  public AccountPrefCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    dc = desktop.getDataCache().getDataConnection();
    try {
      accounts = AccountIO.find(true, dc);
      costAccounts = ParamTableIO.find(CostAccountCtrl.tableName, CostAccountCtrl.columnName, dc);
      init();
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName(), "ComptesPrefsCtrl", ex);
    }
  }

  public void init() throws SQLException {

    setLayout(new BorderLayout());

    choice.setLayout(new BoxLayout(choice, BoxLayout.Y_AXIS));
    choice.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JScrollPane sp = new JScrollPane(choice);
    sp.getVerticalScrollBar().setUnitIncrement(16);

    keys = AccountPrefIO.findKeys(dc);
    if (keys != null && keys.length > 0) {

      ccChoix = new AccountChoice[keys.length];
      caChoix = new ParamChoice[keys.length];

      for (int i = 0, max = keys.length; i < max; i++) {
        Preference pref = AccountPrefIO.find(keys[i], dc);
        Object[] values = pref.getValues();

        GemPanel p = new GemPanel();

        p.setBorder(BorderFactory.createTitledBorder(keys[i]));
        ccChoix[i] = new AccountChoice(accounts);
        ccChoix[i].setKey((Integer)values[0]);
        caChoix[i] = new ParamChoice(costAccounts);
        caChoix[i].setKey((String)values[1]);
        p.add(new GemLabel(accountLabel));
        p.add(ccChoix[i]);
        p.add(new GemLabel(costLabel));
        p.add(caChoix[i]);

        choice.add(p);
      }
    }

    GemPanel commandPanel = new GemPanel();
    commandPanel.setLayout(new GridLayout(1, 1));

    ok = new GemButton(GemCommand.VALIDATION_CMD);
    cancel = new GemButton(GemCommand.CANCEL_CMD);
    ok.addActionListener(this);
    cancel.addActionListener(this);

    commandPanel.add(ok);
    commandPanel.add(cancel);

    add(sp, BorderLayout.CENTER);
    add(commandPanel, BorderLayout.SOUTH);

  }

  @Override
  public void actionPerformed(ActionEvent e) {

    if (e.getSource() == ok) {
      try {
        for (int i = 0; i < keys.length; i++) {
          int p1 = ((Account) ccChoix[i].getSelectedItem()).getId();
          Param a = (Param) caChoix[i].getSelectedItem();
          if (a == null) {
            a = (Param) caChoix[i].getItemAt(0);
          }
          String p2 = (a == null ? "" : a.getKey());
          Preference pref = new Preference(keys[i], new String[]{String.valueOf(p1), p2});
          AccountPrefIO.update(pref, dc);
        }
      } catch (SQLException ex) {
        GemLogger.log(getClass().getName(), "actionPerformed", ex);
      }
    }
    close();
  }

  protected void close() {
    desktop.removeModule(GemModule.DEFAULT_ACCOUNT_KEY);
  }
}
