/*
 * @(#)BranchCreateCtrl.java 2.8.i 05/07/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
import net.algem.contact.CodePostalCtrl;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.i
 */
public class BranchCreateCtrl
        extends CardCtrl
{

  private DataConnection dc;
  private BranchView branchView;
	private BankBranchIO bankBranchIO; 

  public BranchCreateCtrl(DataConnection dc) {
    this.dc = dc;
    bankBranchIO = new BankBranchIO(dc);
  }

  public void init() {
    branchView = new BranchView();
    branchView.setPostalCodeCtrl(new CodePostalCtrl(dc));
    branchView.setBankCodeCtrl(new BankCodeCtrl(true, dc, bankBranchIO));

    addCard("fiche guichet bancaire", branchView);

    select(0);
  }

  @Override
  public boolean prev() {
    select(step - 1);
    return true;
  }

  @Override
  public boolean next() {
    select(step + 1);
    return true;
  }

  @Override
  public boolean cancel() {
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
    }
    return true;
  }

  @Override
  public boolean validation() {
    if (branchView.isNewBank()) {
      Bank b = branchView.getBank();
      if (b.isValid()) {
        try {
          Bank bk = BankIO.findCode(b.getCode(), dc);
          if (bk == null) {
            BankIO.insert(b, dc);
          }
        } catch (Exception e) {
          GemLogger.logException("insert banque", e, this);
        }
      }
    }
    try {
      BankBranch a = branchView.getBankBranch();

      if (!a.isValid()) {
        MessagePopup.error(this, MessageUtil.getMessage("incomplete.entry.error"));
        return false;
      }
      bankBranchIO.insert(a);
    } catch (Exception ex) {
      GemLogger.logException("insert guichet", ex, this);
      return false;
    }
    clear();
    return true;
  }

  public void clear() {
    branchView.clear();
    select(0);
  }

  @Override
  public boolean loadCard(Object p) {
    return false;
  }

  @Override
  public boolean loadId(int id) {
    return false;
  }
}

