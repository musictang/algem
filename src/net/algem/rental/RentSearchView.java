/*
 * @(#)RentSearchView.java	2.17.0t 29/08/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
package net.algem.rental;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.*;

/**
 * Course search view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0t
 * @since 2.17.0t 29/08/2019
 */
public class RentSearchView
        extends SearchView
{

  private GemNumericField number;
  private GemField type;
  private GemField marque;
  private GemPanel mask;

  public RentSearchView() {
    super();
  }

	@Override
  public GemPanel init() {
    mask = new GemPanel();
    mask.setLayout(new GridBagLayout());

    number = new GemNumericField(6);
    number.addActionListener(this);
    type = new GemField(24);
    type.addActionListener(this);
    marque = new GemField(24);
    marque.addActionListener(this);

    //actif = new JCheckBox();
    //actif.setBorder(null);

    btCreate.setText(GemCommand.CREATE_CMD);
    btCreate.setEnabled(true);

    btErase = new GemButton(GemCommand.ERASE_CMD);
    btErase.addActionListener(this);

    GridBagHelper gb = new GridBagHelper(mask);
    gb.add(new GemLabel(BundleUtil.getLabel("Number.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Instrument.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Rentable.brand.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    //gb.add(new GemLabel(BundleUtil.getLabel("Active.label")), 0, 3, 1, 1, GridBagHelper.WEST);

    gb.add(number, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(type, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(marque, 1, 2, 1, 1, GridBagHelper.WEST);
    //gb.add(actif, 1, 3, 1, 1, GridBagHelper.WEST);

    return mask;
  }


  @Override
  public void actionPerformed(ActionEvent evt) {
    if (actionListener == null) {
      return;
    }
    Object src = evt.getSource();
    if (src == number || src == type || src == marque) {
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
        s = type.getText();
        break;
      case 2:
        s = marque.getText();
        break;
//      case 3:
//        s = actif.isSelected() ? "t" : "f";
//        break;
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
    type.setText("");
    marque.setText("");
    //actif.setSelected(false);
  }
}
