/*
 * @(#)GroupOrderLineEditor.java	2.9.4.8 23/06/15
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
import java.sql.SQLException;
import java.text.ParseException;
import javax.swing.*;
import net.algem.config.Preference;
import net.algem.group.GemGroupService;
import net.algem.group.GroupService;
import net.algem.planning.DateRangePanel;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.menu.MenuPopupListener;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.8
 * @since 2.7.k 01/03/2013
 */
public class GroupOrderLineEditor
  extends OrderLineEditor
{

  private DateRangePanel dateRange;
  private GemButton btDateRange;
  private JToggleButton btMembershipFilter;
  private JToggleButton btUnpaidFilter;
  private JMenuItem miGroupModif;
  private GroupService service;

  public GroupOrderLineEditor(GemDesktop desktop, OrderLineTableModel tableModel, GroupService service) {
    super(desktop, tableModel);
    this.service = service;
    try {
      Preference p = AccountPrefIO.find(AccountPrefIO.MEMBERSHIP, dc);
      Account a = AccountPrefIO.getAccount(p, dc);
      if (a != null) {
        tableView.setMemberShipFilter(a.getLabel());
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  @Override
  public void init(){
    
    JPopupMenu popup = new JPopupMenu();
    miGroupModif = new JMenuItem(BundleUtil.getLabel("Order.line.modify.group.action"));
    miGroupModif.addActionListener(this);
    popup.add(miGroupModif);
    
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
    GemPanel buttons = new GemPanel(new GridLayout(1,5));

    buttons.add(btUnpaidFilter);
    buttons.add(btMembershipFilter);
    buttons.add(btModify);
    buttons.add(btCreate);
    buttons.add(btSuppress);

    GemPanel pTotal = new GemPanel();
    totalLabel = new JLabel(BundleUtil.getLabel("Total.label"));
    totalField = new GemField(10);
    totalField.setEditable(false);
    tableView.addListSelectionListener(totalField);

    pTotal.add(totalLabel);
    pTotal.add(totalField);
    footer.add(pTotal, BorderLayout.NORTH);
    footer.add(buttons, BorderLayout.CENTER);

    tableView.filterByPeriod(dateRange.getStartFr(), dateRange.getEndFr());
    tableView.getTable().addMouseListener(new MenuPopupListener(tableView, popup));
    
    setLayout(new BorderLayout());
    add(header, BorderLayout.NORTH);
    add(tableView, BorderLayout.CENTER);
    add(footer, BorderLayout.SOUTH);
    setLocation(70, 30);

  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    if (src == btDateRange) {
      tableView.filterByPeriod(dateRange.getStartFr(), dateRange.getEndFr());
      btMembershipFilter.setSelected(false);
      btUnpaidFilter.setSelected(false);
    } else if (evt.getSource() == btMembershipFilter) {
      if (btMembershipFilter.isSelected()) {
        tableView.filterByMemberShip(dateRange.getStartFr(), dateRange.getEndFr());
        btUnpaidFilter.setSelected(false);
      } else {
        tableView.filterByPeriod(dateRange.getStartFr(), dateRange.getEndFr());
      }
    } else if (src == btUnpaidFilter) {
      if (btUnpaidFilter.isSelected()) {
        tableView.filterByUnpaid();
        btMembershipFilter.setSelected(false);
      } else {
        tableView.filterByPeriod(dateRange.getStartFr(), dateRange.getEndFr());
      }
    } else if (src == miGroupModif) {
      updateGroup();
    } else {
      super.actionPerformed(evt);
    }
  }
  
  /**
   * Updates the group number on selected order lines.
   */
  private void updateGroup() {
    int[] rows = tableView.getSelectedRows();
    if (rows.length == 0) {
      return;
    }
    OrderLine e = tableView.getElementAt(rows[0]);
    try {
      OrderLineView dlg = new OrderLineView(desktop.getFrame(), BundleUtil.getLabel("Order.line.modification"), dataCache, true);
      dlg.setOrderLine(e);
      dlg.setGroupEditable(true);
      dlg.setVisible(true);
      if (dlg.isValidation()) {
        dc.setAutoCommit(false);
        OrderLine u = dlg.getOrderLine();
        int oids [] = new int[rows.length];
        for (int i = 0; i < rows.length; i++) {
          OrderLine r = tableView.getElementAt(rows[i]);
          oids[i] = r.getId();
          r.setGroup(u.getGroup());
          tableView.setElementAt(r, rows[i]);
        }
        ((GemGroupService) service).updateOrderLine(oids, u.getGroup());
        dc.commit();
      }
      dlg.dispose();
    } catch (SQLException ex) {
      dc.rollback();
      GemLogger.logException(MessageUtil.getMessage("update.error"), ex, this);
    } catch (ParseException pe) {
      GemLogger.log(pe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }
}
