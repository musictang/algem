/*
 * @(#)HistoSubscriptionCard.java 2.9.4.12 01/09/15
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
 */

package net.algem.contact.member;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabDialog;
import net.algem.util.ui.GemPanel;

/**
 * History of passes purchased by the member.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.12
 * @since 2.9.2 07/01/15
 */
public class HistoSubscriptionCard 
  extends FileTabDialog
{
  public static final String CLOSE_CMD = "Histo.pass.close";
  private static final String DETAIL_CMD = BundleUtil.getLabel("Detail.label");
  private HistoSubscriptionCardTableModel histoTableModel;
  private SubscriptionCardSessionTableModel sessionTableModel;
  private JTable histoTable, sessionTable;
  private final GemPanel histoPanel;
  private int idper;
  private boolean loaded;
  private boolean first = true;
  private MemberService service;
  private ActionListener listener;
    protected String[] histoToolTips = {
    null,
    BundleUtil.getLabel("Date.of.purchase"),
    BundleUtil.getLabel("Subscription.name.label"),
    MessageUtil.getMessage("subscription.rest.modification.tip")
  };

  public HistoSubscriptionCard(GemDesktop desktop, int idper, ActionListener listener, MemberService service) {
    super(desktop);
    this.idper = idper;
    this.listener = listener;
    this.service = service;
    
    histoPanel = new GemPanel(new CardLayout());
    
    histoTableModel = new HistoSubscriptionCardTableModel(service);
    histoTable = new JTable(histoTableModel) {
      //Implements table header tool tips.
      @Override
      protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel) {
          
          @Override
          public String getToolTipText(MouseEvent e) {
            java.awt.Point p = e.getPoint();
            int index = columnModel.getColumnIndexAtX(p.x);
            int realIndex = columnModel.getColumn(index).getModelIndex();
            return histoToolTips[realIndex];
          }
        };
      }
    };

    histoTable.setAutoCreateRowSorter(true);
    TableColumnModel cm = histoTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(10);
    
    sessionTableModel = new SubscriptionCardSessionTableModel();
    sessionTable = new JTable(sessionTableModel);
    JScrollPane sessionScroll = new JScrollPane(sessionTable);

    JScrollPane histoScroll = new JScrollPane(histoTable);
    histoPanel.add(histoScroll,0);
    histoPanel.add(sessionScroll,1);
    ((CardLayout) histoPanel.getLayout()).first(histoPanel);

    setLayout(new BorderLayout());
    add(histoPanel, BorderLayout.CENTER);
    
    GemPanel buttons = new GemPanel(new GridLayout(1,2));
    btValidation.setText(DETAIL_CMD);
    buttons.add(btValidation);
    btCancel.setText(GemCommand.CLOSE_CMD);
    buttons.add(btCancel);
    add(buttons, BorderLayout.SOUTH);
  }

  @Override
  public void validation() {
    int row = histoTable.getSelectedRow();
    if (row < 0) {
      return;
    }
    loadSessions(row);
    ((CardLayout) histoPanel.getLayout()).next(histoPanel);
    if (first) {
      btValidation.setText(GemCommand.BACK_CMD);
      first = false;
    } else {
      btValidation.setText(DETAIL_CMD);
      first = true;
    }
  }

  @Override
  public void cancel() {
    clear();
    if (listener != null) {
      listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, CLOSE_CMD));
    }
  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public void load() {
    clear();
    try {
      List<PersonSubscriptionCard> cards = service.getSubscriptions(idper, false);
      for (PersonSubscriptionCard pc : cards) {
       histoTableModel.addItem(pc);
      }
      ((CardLayout) histoPanel.getLayout()).first(histoPanel);
      loaded = cards.size() > 0;
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
    }
  }
  
  private void loadSessions(int row) {   
    PersonSubscriptionCard card = (PersonSubscriptionCard) histoTableModel.getItem(histoTable.convertRowIndexToModel(row));
    sessionTableModel.clear();
    try {
      List<SubscriptionCardSession> sessions = service.getSessions(card.getId());
      card.setSessions(sessions);
      for (SubscriptionCardSession s : sessions) {
        sessionTableModel.addItem(s);
      }
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
    }
  }
  
  private void clear() {
    histoTableModel.clear();
    sessionTableModel.clear();
    btValidation.setText(DETAIL_CMD);
    first = true;
    //btCancel.setText(GemCommand.CLOSE_CMD);
  }

}
