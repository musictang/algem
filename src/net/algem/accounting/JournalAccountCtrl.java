/*
 * @(#)JournalAccountCtrl.java	2.3.c 09/03/12
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

import java.util.Vector;
import javax.swing.table.TableColumnModel;
import net.algem.util.ui.ErrorDlg;
import net.algem.util.ui.GenericTableCtrl;
import net.algem.util.ui.MessagePopup;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.model.ModelException;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.BundleUtil;
import net.algem.util.ui.JTableModel;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemChoice;

/**
 * Management of journal.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.3.c
 * @since 2.2.a
 */
public class JournalAccountCtrl
        extends GenericTableCtrl
{

  private JournalAccountService service;
  private GemChoice account;
  private GemField code;
  private GemField label;
  private String id;
  private GemDesktop desktop;

  public JournalAccountCtrl(JTableModel model, JournalAccountService service, GemDesktop desktop) {
    super(model);
    this.desktop = desktop;
    this.service = service;
    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(30);
    cm.getColumn(1).setPreferredWidth(150);
    cm.getColumn(2).setPreferredWidth(300);

    load();
    addControl();
  }

  @Override
  protected void select(int n) {

    JournalAccount j = (JournalAccount) tableModel.getItem(n);
    id = j.getKey();
    code.setText(j.getValue());
    label.setText(j.getLabel());
    account.setSelectedItem(j.getAccount());
  }

  @Override
  protected void load() {
    Vector<JournalAccount> v = null;
    try {
      v = service.find();
    } catch (ModelException ex) {
      return;
    }
    if (v == null) {
      return;
    }
    for (int i = 0; i < v.size(); i++) {
      tableModel.addItem(v.elementAt(i));
    }
  }

  @Override
  protected void addControl() {

    code = new GemField(5);
    label = new GemField(10);
    account = new AccountChoice(service.getAccounts());
    p1.add(new GemLabel(BundleUtil.getLabel("Code.label")));
    p1.add(code);
    p1.add(new GemLabel(BundleUtil.getLabel("Label.label")));
    p1.add(label);
    p1.add(new GemLabel(BundleUtil.getLabel("Account.label")));
    p1.add(account);
  }

  @Override
  public void insertion() {
    JournalAccount j = getSelected();
    if (j != null) {
      try {
        service.create(j);
        tableModel.addItem(j);
        clear();
      } catch (ModelException ex) {
        MessagePopup.warning(this, ex.getMessage());
      }

    }

  }

  @Override
  public void modification(int n) {
    JournalAccount j = getSelected();
    if (j != null) {
      try {
        service.update(j);
        tableModel.modItem(n, j);
        clear();
      } catch (ModelException ex) {
        MessagePopup.warning(this, ex.getMessage());
      }

    }
  }

  @Override
  public void suppression(int n) {
    JournalAccount j = getSelected();
    if (j != null) {
      try {
        service.delete(j);
        tableModel.deleteItem(n);
        clear();
      } catch (ModelException ex) {
        MessagePopup.warning(this, ex.getMessage());
      }

    }
  }

  @Override
  public void clear() {
    code.setText("");
    label.setText("");
    account.setSelectedIndex(0);
    id = null;
  }

  @Override
  public void close() {
    clear();
    desktop.removeModule(GemModule.BOOKING_JOURNAL_KEY);
  }

  private JournalAccount getSelected() {
    String c = code.getText().trim();
    String l = label.getText().trim();
//    int k = ((Account) compte.getSelectedItem()).getId();
    Account cj = (Account) account.getSelectedItem();
    if (c.length() == 0 || l.length() == 0) {
      new ErrorDlg(this, MessageUtil.getMessage("entry.error"));
      return null;
    }
    JournalAccount j = new JournalAccount(id, c);
    j.setLabel(l);
    j.setAccount(cj);
    return j;
  }

}
