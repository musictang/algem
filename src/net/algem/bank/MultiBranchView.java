/*
 * @(#)MultiBranchView.java	2.6.a 14/09/12
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
import javax.swing.table.TableColumnModel;
import net.algem.contact.Address;
import net.algem.util.ui.*;

/**
 * Address view for banks with multiple branches.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class MultiBranchView
        extends GemBorderPanel
{

  private MultiBranchTableModel branchTableModel;
  private JTable branchTable;
  private Vector branchs;
  private GemField bankCode;
  private GemField branchCode;
  private ActionListener actionListener;

  public MultiBranchView(String bank, String branch) {
    bankCode = new GemField(5);
    bankCode.setText(bank);
    bankCode.setEditable(false);
    bankCode.setBackground(Color.lightGray);
    branchCode = new GemField(5);
    branchCode.setText(branch);
    branchCode.setEditable(false);
    branchCode.setBackground(Color.lightGray);

    branchTableModel = new MultiBranchTableModel();
    branchTable = new JTable(branchTableModel);
    branchTable.setAutoCreateRowSorter(true);
    branchTable.addMouseListener(new MouseAdapter()
    {

      public void mouseClicked(MouseEvent e) {
        int n = branchTable.convertRowIndexToModel(branchTable.getSelectedRow());
        if (actionListener != null) {
          actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Modifier"));
        }
      }
    });


    TableColumnModel cm = branchTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(80);
    cm.getColumn(1).setPreferredWidth(300);
    cm.getColumn(2).setPreferredWidth(250);


    JScrollPane pm = new JScrollPane(branchTable);

    GemPanel haut = new GemPanel();
    haut.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(haut);
    gb.add(new GemLabel("code banque"), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(bankCode, 1, 0, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(new GemLabel("code agence"), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(branchCode, 1, 1, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);

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

  public BankBranch getSelectedAgence() {
    int n = branchTable.getSelectedRow();
    return (BankBranch) branchs.elementAt(n);
  }

  public void addBranch(BankBranch a) {
    branchs.addElement(a);
    branchTableModel.addItem(a);
    //addAdresse(a.getAdresse());
  }

  public void loadBranches(Vector v) {
    branchs = v;
    Enumeration e = v.elements();
    while (e.hasMoreElements()) {
      BankBranch a = (BankBranch) e.nextElement();
      //addAdresse(a.getAdresse());//remplissage du table model
      branchTableModel.addItem(a);
    }
  }

  public void addAddress(Address adr) {
    branchTableModel.addItem(adr);
  }

  public void changeAddress(int i, Address adr) {
    branchTableModel.modItem(i, adr);
  }

  public void removeAddress(int i) {
    branchTableModel.deleteItem(i);
  }

  public void setBankCode(String s) {
    bankCode.setText(s);
  }

  public void setBranchCode(String s) {
    branchCode.setText(s);
  }

  public void clear() {
    bankCode.setText("");
    branchCode.setText("");
    branchTableModel.clear();
  }
}
