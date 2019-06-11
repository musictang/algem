/*
 * @(#)MultiBranchView.java	2.9.4.3 23/04/15
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
package net.algem.bank;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.*;

/**
 * Address view for banks with multiple branches.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.3
 */
public class MultiBranchView
        extends GemBorderPanel
{

  private MultiBranchTableModel branchTableModel;
  private JTable branchTable;
  private Vector<BankBranch> branches;
  private GemField bankCodeField;
  private GemField branchCodeField;
  private ActionListener actionListener;

  public MultiBranchView(String bankCode, String branchCode) {
    bankCodeField = new GemField(5);
    bankCodeField.setText(bankCode);
    bankCodeField.setEditable(false);
    bankCodeField.setBackground(Color.LIGHT_GRAY);
    branchCodeField = new GemField(5);
    branchCodeField.setText(branchCode);
    branchCodeField.setEditable(false);
    branchCodeField.setBackground(Color.LIGHT_GRAY);

    branchTableModel = new MultiBranchTableModel();
    branchTable = new JTable(branchTableModel);

    branchTable.setAutoCreateRowSorter(true);
    branchTable.addMouseListener(new MouseAdapter()
    {
      public void mouseClicked(MouseEvent e) {
        int n = branchTable.convertRowIndexToModel(branchTable.getSelectedRow());
        if (actionListener != null) {
          actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.MODIFY_CMD));
        }
      }
    });

    TableColumnModel cm = branchTable.getColumnModel();
    
    cm.getColumn(0).setPreferredWidth(80);
    cm.getColumn(1).setPreferredWidth(300);
    cm.getColumn(2).setPreferredWidth(200);
    cm.getColumn(3).setPreferredWidth(100);

    JScrollPane pm = new JScrollPane(branchTable);

    GemPanel haut = new GemPanel();
    haut.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(haut);
    gb.add(new GemLabel(BundleUtil.getLabel("Bank.code.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(bankCodeField, 1, 0, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(new GemLabel(BundleUtil.getLabel("Bank.branch.code.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(branchCodeField, 1, 1, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);

    this.setLayout(new BorderLayout());

    add(haut, BorderLayout.NORTH);
    add(pm, BorderLayout.CENTER);
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  BankBranch getSelectedBranch() {
    TableCellEditor tce = branchTable.getCellEditor();
    if (tce != null) {
      tce.stopCellEditing();
    }
    int n = branchTable.getSelectedRow();
    return (BankBranch) branches.elementAt(n);
  }

  void addBranch(BankBranch a) {
    branches.addElement(a);
    branchTableModel.addItem(a);
    //addAdresse(a.getAdresse());
  }

  void loadBranches(Vector<BankBranch> v) {
    branches = v;
    Enumeration<BankBranch> e = v.elements();
    while (e.hasMoreElements()) {
      BankBranch a = e.nextElement();
      //addAdresse(a.getAdresse());//remplissage du table model
      branchTableModel.addItem(a);
    }
  }

  void setBankCode(String s) {
    bankCodeField.setText(s);
  }

  void setBranchCode(String s) {
    branchCodeField.setText(s);
  }

  private void clear() {
    bankCodeField.setText("");
    branchCodeField.setText("");
    branchTableModel.clear();
  }
}
