/*
 * @(#)AccountMatchingCfg.java 2.6.a 02/08/2012
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
import java.util.Map;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import net.algem.util.module.GemDesktop;
import net.algem.util.*;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * Management of account matching between personal and revenue accounts.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.2.l 07/12/11
 */
public class AccountMatchingCfg
        extends GemPanel implements ActionListener
{

  private GemDesktop desktop;
  private DataConnection dc;
  private Vector<Account> comptesDeTiers;
  private Vector<Account> comptesDeProduits;
  private static String compteDeTiersLabel = BundleUtil.getLabel("Personal.account.label");
  private static String comptesDeProduitsLabel = BundleUtil.getLabel("Revenue.account.label");
  private GemButton ok;
  private GemButton close;
  private GemPanel choix = new GemPanel();
  private AccountChoice[] ctChoix;
  private AccountChoice[] cpChoix;

  public AccountMatchingCfg(GemDesktop desktop) {
    this.desktop = desktop;
    dc = desktop.getDataCache().getDataConnection();
    try {
      String where = " WHERE (numero LIKE '" + AccountUtil.PERSONAL_ACCOUNT_FIRST_DIGIT + "%' OR numero LIKE '" + AccountUtil.CUSTOMER_ACCOUNT_FIRST_LETTER + "%') AND actif = true";
      comptesDeTiers = AccountIO.find(where, dc);
      where = " WHERE numero LIKE '" + AccountUtil.REVENUE_ACCOUNT_FIRST_DIGIT + "%' AND actif = true";
      comptesDeProduits = AccountIO.find(where, dc);

      init();
    } catch (SQLException ex) {
      GemLogger.logException("Config comptes exception", ex);
    } catch (NullAccountException na) {
      MessagePopup.warning(this, na.getMessage());
    }
  }

  public void init() throws SQLException, NullAccountException {

    setLayout(new BorderLayout());

    choix.setLayout(new BoxLayout(choix, BoxLayout.Y_AXIS));
    choix.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JScrollPane sp = new JScrollPane(choix);
    sp.getVerticalScrollBar().setUnitIncrement(16);

    if (comptesDeTiers == null || comptesDeTiers.size() == 0) {
      throw new NullAccountException(MessageUtil.getMessage("no.personal.account"));
    }
    if (comptesDeProduits == null || comptesDeProduits.size() == 0) {
      throw new NullAccountException(MessageUtil.getMessage("no.revenue.account"));
    }

    int max = comptesDeTiers.size();
    ctChoix = new AccountChoice[max];
    cpChoix = new AccountChoice[max];

    for (int i = 0; i < max; i++) {
      GemPanel p = new GemPanel(new GridLayout(2,2,10,0));
      p.setBorder(BorderFactory.createTitledBorder(String.valueOf(i + 1)));

      ctChoix[i] = new AccountChoice(comptesDeTiers);
      cpChoix[i] = new AccountChoice(comptesDeProduits);

      p.add(new GemLabel(compteDeTiersLabel));
      p.add(new GemLabel(comptesDeProduitsLabel));
      p.add(ctChoix[i]);    
      p.add(cpChoix[i]);
      
      choix.add(p);
    }
    set();

    GemPanel commandPanel = new GemPanel();
    commandPanel.setLayout(new GridLayout(1, 1));

    ok = new GemButton(GemCommand.VALIDATION_CMD);
    ok.setToolTipText(BundleUtil.getLabel("Save.and.close.tip"));
    close = new GemButton(GemCommand.CANCEL_CMD);
    close.setToolTipText(BundleUtil.getLabel("Close.without.saving.tip"));
    ok.addActionListener(this);
    close.addActionListener(this);

    commandPanel.add(ok);
    commandPanel.add(close);

    add(sp, BorderLayout.CENTER);
    add(commandPanel, BorderLayout.SOUTH);

  }

  @Override
  public void actionPerformed(ActionEvent e) { 
    if (e.getSource() == ok) {
      try {
        save();
      } catch (SQLException ex) {
        GemLogger.logException("Update correspondance comptes", ex);
      }
    }

    desktop.removeCurrentModule();

  }

  private void save() throws SQLException {

    for (int i = 0; i < ctChoix.length; i++) {
      int t = ((Account) ctChoix[i].getSelectedItem()).getId();
      int p = ((Account) cpChoix[i].getSelectedItem()).getId();
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
        ctChoix[i].setSelectedItem(keys[i]);
        cpChoix[i].setSelectedItem(values[i]);
      }
    }

  }
}
