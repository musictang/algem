/*
 * @(#)BranchSearchView.java	2.9.4.13 15/10/15
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

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.*;

/**
 * Search bank view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.9.4.13
 * @since 1.0a 07/07/1999
 */
public class BranchSearchView
        extends SearchView
{

  private GemNumericField number;
  private GemField bankCode;
  private GemField bankName;
  private GemField branchCode;
  private GemPanel maskPanel;

  public BranchSearchView() {
  }

  @Override
  public GemPanel init() {
    number = new GemNumericField(6);
    number.addActionListener(this);
    bankCode = new GemField(5);
    bankCode.addActionListener(this);
    bankName = new GemField(30);
    bankName.addActionListener(this);
    branchCode = new GemField(5);
    branchCode.addActionListener(this);

    btCreate.setText(null);
    btCreate.setEnabled(false);
    btErase = new GemButton(GemCommand.ERASE_CMD);
    btErase.addActionListener(this);

    maskPanel = new GemPanel();
    maskPanel.setLayout(new GridBagLayout());

    GridBagHelper gb = new GridBagHelper(maskPanel);
    gb.insets = GridBagHelper.SMALL_INSETS;
    gb.add(new GemLabel(BundleUtil.getLabel("Number.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Bank.code.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Bank.name.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Bank.branch.code.label")), 0, 3, 1, 1, GridBagHelper.WEST);

    gb.add(number, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(bankCode, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(bankName, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(branchCode, 1, 3, 1, 1, GridBagHelper.WEST);

    return maskPanel;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (actionListener == null) {
      return;
    }
    if (evt.getSource() == bankCode
            || evt.getSource() == branchCode
            || evt.getSource() == number) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.SEARCH_CMD));
    } else {
      actionListener.actionPerformed(evt);
    }
  }

  @Override
  public String getField(int n) {
    String s = null;
    switch (n) {
      case 0:
        s = number.getText();
        break;
      case 1:
        s = bankCode.getText();
        break;
      case 2:
        s = bankName.getText();
        break;
      case 3:
        s = branchCode.getText();
        break;
    }
    if (s != null && s.length() > 0) {
      return s;
    } else {
      return null;
    }
  }

  @Override
  public void clear() {
    number.setText("");
    bankCode.setText("");
    branchCode.setText("");
  }
}
