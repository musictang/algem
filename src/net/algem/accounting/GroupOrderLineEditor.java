/*
 * @(#)GroupOrderLineEditor.java	2.7.k 01/03/2013
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
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import net.algem.config.Preference;
import net.algem.planning.DateRangePanel;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.k
 * @since 2.7.k 01/03/2013
 */
public class GroupOrderLineEditor 
  extends OrderLineEditor
{
  
  private DateRangePanel dateRange;
  private GemButton btDateRange;
  private JToggleButton btMembershipFilter;
  private JToggleButton btUnpaidFilter;

  public GroupOrderLineEditor(GemDesktop _desktop, OrderLineTableModel _tableModel) {
    super(_desktop, _tableModel);
    try {
      Preference p = AccountPrefIO.find(AccountPrefIO.MEMBER_KEY_PREF, dc);
      Account a = AccountPrefIO.getAccount(p, dc);
      if (a != null) {
        table.setMemberShipFilter(a.getLabel());
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }
  
  @Override
  public void init(){
    btCreate = new GemButton(GemCommand.ADD_CMD);
    btCreate.addActionListener(this);
    btModify = new GemButton(GemCommand.MODIFY_CMD);
    btModify.addActionListener(this);
    btSuppress = new GemButton(GemCommand.DELETE_CMD);
    btSuppress.addActionListener(this);

    btMembershipFilter = new JToggleButton(BundleUtil.getLabel("Membership.label"));
    btMembershipFilter.addActionListener(this);
    
    btUnpaidFilter = new JToggleButton(BundleUtil.getLabel("Payment.schedule.unpaid"));
    btUnpaidFilter.addActionListener(this);

    JPanel header = new JPanel();
    dateRange = new DateRangePanel(dataCache.getStartOfYear(), dataCache.getEndOfPeriod());
    btDateRange = new GemButton(BundleUtil.getLabel("Action.load.label"));
    btDateRange.addActionListener(this);
    header.add(dateRange);
    header.add(btDateRange);

    GemPanel footer = new GemPanel(new BorderLayout());
    GemPanel buttons = new GemPanel();

    buttons.add(btUnpaidFilter);
    buttons.add(btMembershipFilter);
    buttons.add(btModify);
    buttons.add(btCreate);
    buttons.add(btSuppress);

    GemPanel pTotal = new GemPanel();
    totalLabel = new JLabel(BundleUtil.getLabel("Total.label"));
    totalField = new GemField(10);
    totalField.setEditable(false);
    table.addListSelectionListener(totalField);

    pTotal.add(totalLabel);
    pTotal.add(totalField);
    footer.add(pTotal, BorderLayout.NORTH);
    footer.add(buttons, BorderLayout.CENTER);
    
    table.filterByPeriod(dateRange.getStartFr(), dateRange.getEndFr());

    setLayout(new BorderLayout());
    add(header, BorderLayout.NORTH);
    add(table, BorderLayout.CENTER);
    add(footer, BorderLayout.SOUTH);
    setLocation(70, 30);
    
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btDateRange) {
      table.filterByPeriod(dateRange.getStartFr(), dateRange.getEndFr());
      btMembershipFilter.setSelected(false);
      btUnpaidFilter.setSelected(false);
    } else if (evt.getSource() == btMembershipFilter) {
      if (btMembershipFilter.isSelected()) {
        table.filterByMemberShip(dateRange.getStartFr(), dateRange.getEndFr());
        btUnpaidFilter.setSelected(false);
      } else {
        table.filterByPeriod(dateRange.getStartFr(), dateRange.getEndFr());
      }
      
    } else if (evt.getSource() == btUnpaidFilter) {
      if (btUnpaidFilter.isSelected()) {
        table.filterByUnpaid();
        btMembershipFilter.setSelected(false);
      } else {
        table.filterByPeriod(dateRange.getStartFr(), dateRange.getEndFr());
      }
    } else {
      super.actionPerformed(evt);
    }
  }
}
