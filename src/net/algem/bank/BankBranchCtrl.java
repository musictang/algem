/*
 * @(#)BankBranchCtrl.java	2.9.4.13 15/10/15
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

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import net.algem.contact.AddressIO;
import net.algem.contact.CodePostalCtrl;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * Branch of bank management.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13 15/10/
 */
public class BankBranchCtrl
        extends CardCtrl
{

  private DataConnection dc;
  private BranchView branchView;
  private BankBranch branch;
  private BankBranchIO bankBranchIO;

  public BankBranchCtrl(DataConnection dc, BankBranchIO branchIO) {
    this.dc = dc;
    bankBranchIO = branchIO;
    branchView = new BranchView();
    branchView.setBankCodeCtrl(new BankCodeCtrl(this.dc, branchIO));
    branchView.setPostalCodeCtrl(new CodePostalCtrl(this.dc));

    addCard(BundleUtil.getLabel("Bank.branch.label"), branchView);
    btPrev.setText(GemCommand.DELETE_CMD);
    btPrev.setActionCommand(GemCommand.DELETE_CMD);
    select(0);
  }

  @Override
  public boolean next() {
    switch (step) {
      default:
        select(step + 1);
        break;
    }
    return true;
  }

  @Override
  public boolean cancel() {
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlAbandon"));
    }
    return true;
  }

  @Override
  public boolean prev() {
//    switch (step) {
//      default:
//        select(step - 1);
//        break;
//    }
//    return true;
    delete();
    return cancel();
  }

  @Override
  public boolean validation() {
    if (branch == null) {
      return false;
    }

    try {
      BankBranch a = branchView.getBankBranch();
      if (!a.isValid()) {
        MessagePopup.error(this, MessageUtil.getMessage("incomplete.entry.error"));
        return false;
      }
      bankBranchIO.update(branch, a);

    } catch (Exception e) {
      GemLogger.logException("update guichet", e, this);
      return false;
    }
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
    }
    return true;
  }

  public void clear() {
    branchView.clear();
    select(0);
  }
  
  private void delete() {
//    BankBranch a = branchView.getBankBranch();
    
    try {
      Vector<Rib> ribs = BranchIO.getRibs(branch.getId(), dc);
      if (ribs != null && ribs.size() > 0) {
        StringBuilder sb = new StringBuilder();
        for (Rib r : ribs) {
          sb.append(r.getId()).append(',');
        }
        sb.deleteCharAt(sb.length() -1);
        throw new Exception(MessageUtil.getMessage("bank.branch.delete.exception", sb.toString()));
      }
      bankBranchIO.delete(branch);
    } catch (Exception ex) {
      GemLogger.log(Level.WARNING, ex.getMessage());
      MessagePopup.error(this, ex.getMessage());
    }
  }
  

  @Override
  public boolean loadCard(Object o) {
    clear();
    if (o == null || !(o instanceof BankBranch)) {
      return false;
    }

    branch = (BankBranch) o;
    if (!branch.isComplete()) {
      try {
        branch.setAddress(AddressIO.findId(branch.getId(), dc));
      } catch (SQLException ex) {
        GemLogger.log(this.toString(), "loadCard", ex.getMessage());
      }
    }
    branchView.setBankBranch(branch);

    return true;
  }

  @Override
  public boolean loadId(int id) {
    return loadCard(bankBranchIO.findId(id));
  }
}
