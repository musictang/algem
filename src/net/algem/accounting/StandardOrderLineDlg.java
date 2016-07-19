/*
 * @(#) StandardOrderLineDlg.java Algem 2.10.0 19/05/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
package net.algem.accounting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import net.algem.planning.DateFr;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.10.0 18/05/16
 */
public class StandardOrderLineDlg
  extends JDialog
  implements ActionListener {

  private GemDesktop desktop;
  private AccountingService service;
  private OrderLineView dlg;
  private OrderLineTableModel model;
  private OrderLineTableView view;
  private GemButton btDelete;
  private GemButton btAdd;
  private GemButton btModify;
  private GemButton btCancel;

  public StandardOrderLineDlg() {
  }

  public StandardOrderLineDlg(GemDesktop desktop, AccountingService service, String title) {
    super(desktop.getFrame(), title);
    this.desktop = desktop;
    this.service = service;
  }

  public void createUI() {
    model = new OrderLineTableModel();
    view = new OrderLineTableView(model, this);
    disableCols(0,1,2,3,10,11,12);
    setLayout(new BorderLayout());
    JTextPane info = new JTextPane();
    info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    info.setText(MessageUtil.getMessage("standard.order.line.info"));
    info.setEditable(false);
    add(info, BorderLayout.NORTH);
    add(view, BorderLayout.CENTER);

    GemPanel buttons = new GemPanel(new GridLayout(1, 4));

    btDelete = new GemButton(GemCommand.DELETE_CMD);
    btAdd = new GemButton(GemCommand.ADD_CMD);
    btModify = new GemButton(GemCommand.MODIFY_CMD);
    btCancel = new GemButton(GemCommand.CLOSE_CMD);
    btDelete.addActionListener(this);
    btAdd.addActionListener(this);
    btModify.addActionListener(this);
    btCancel.addActionListener(this);
    buttons.add(btDelete);
    buttons.add(btAdd);
    buttons.add(btModify);
    buttons.add(btCancel);

    add(buttons, BorderLayout.SOUTH);
    setSize(905, 240);

    setVisible(true);

  }

  public void load() throws SQLException {
    List<OrderLine> lines = service.findStandardOrderLines();
    for (OrderLine line : lines) {
      model.addElement(line);
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    if (src == btDelete) {
      delete();
    } else if (src == btAdd) {
      add();
    } else if (src == btModify) {
      modify();
    } else if ("orderline.view.validate".equals(evt.getActionCommand())) {
      if (dlg.isValidation()) {
        try {
          OrderLine e = dlg.getOrderLine();
          if (e.getId() == 0) {
            create(e);
          } else {
            update(e);
          }
        } catch (ParseException ex) {
          GemLogger.log(ex.getMessage());
        } finally {
          dlg.setVisible(false);
          dlg.dispose();
        }
      }
    } else if ("orderline.view.cancel".equals(evt.getActionCommand())) {
      dlg.setVisible(false);
      dlg.dispose();
    } else {
      setVisible(false);
      dispose();
    }

  }

  private void modify() {
    int n = view.getSelectedRow();
    if (n < 0) {
      JOptionPane.showMessageDialog(this,
        OrderLineEditor.NO_PAYMENT_SELECTED,
        OrderLineEditor.PAYMENT_UPDATE_EXCEPTION,
        JOptionPane.ERROR_MESSAGE);
      return;
    }
    OrderLine e = view.getElementAt(n);
    e.setDate(new Date());

    try {
      dlg = new OrderLineView(null, MessageUtil.getMessage("payment.update.label"), desktop.getDataCache(), false);
      dlg.addActionListener(this);
      dlg.setOrderLine(e);
      dlg.setStandardEditable();
      dlg.setVisible(true);
    } catch (Exception ex) {
      GemLogger.logException(OrderLineEditor.PAYMENT_UPDATE_EXCEPTION, ex, this);
    }
  }

  private void add() {
    OrderLine e = null;
    int n = view.getSelectedRow();
    if (n >= 0) {
      e = new OrderLine(view.getElementAt(n));
      e.setId(0);
      e.setDate(new Date());
    } else {
      e = getDefault();
    }

    try {
      dlg = new OrderLineView(null, MessageUtil.getMessage("payment.update.label"), desktop.getDataCache(), false);
      dlg.addActionListener(this);
      dlg.setOrderLine(e);
      dlg.setStandardEditable();
      dlg.setVisible(true);
    } catch (Exception ex) {
      GemLogger.logException(OrderLineEditor.PAYMENT_UPDATE_EXCEPTION, ex, this);
    }
  }

  private void delete() {
    int n = view.getSelectedRow();
    if (n < 0) {
      JOptionPane.showMessageDialog(this,
        OrderLineEditor.NO_PAYMENT_SELECTED,
        OrderLineEditor.PAYMENT_UPDATE_EXCEPTION,
        JOptionPane.ERROR_MESSAGE);
      return;
    }
    OrderLine e = view.getElementAt(n);
    try {
      service.deleteStandardOrderLine(e);
      model.removeElementAt(n);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  private OrderLine getDefault() {
    OrderLine e = new OrderLine();
    e.setLabel("");
    e.setDocument("");
    e.setDate(new DateFr(new Date()));
    e.setAccount(null);
    e.setCostAccount(null);
    e.setPaid(false);
    e.setTransfered(false);
    e.setModeOfPayment(ModeOfPayment.CHQ.name());
    e.setCurrency("E");
    e.setInvoice(null);

    return e;
  }

  private void update(OrderLine e) {
    try {
      service.updateStandardOrderLine(e);
      int n = view.getRowIndexByModel(e);
      if (n >= 0) {
        view.setElementAt(e, n);
      }
    } catch (SQLException ex) {
      GemLogger.logException(OrderLineEditor.PAYMENT_UPDATE_EXCEPTION, ex, this);
    }
  }

  private void create(OrderLine e) {
    try {
      service.createStandardOrderLine(e);
      model.addElement(e);
    } catch (SQLException ex) {
      GemLogger.logException(OrderLineEditor.PAYMENT_UPDATE_EXCEPTION, ex, this);
    }
  }

  private void disableCols(int... cols) {
    DefaultTableCellRenderer r = new GrayedOutCellRenderer();
    for (int c : cols) {
      TableColumn column = view.getTable().getColumnModel().getColumn(c);
      column.setCellRenderer(r);
    }
  }

  class GrayedOutCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
      boolean editable = table.getModel().isCellEditable(row, column);
      setBackground(editable ? UIManager.getColor("Label.background") : Color.LIGHT_GRAY);
      return this;
    }
  }

}
