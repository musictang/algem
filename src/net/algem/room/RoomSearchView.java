/*
 * @(#)RoomSearchView.java	2.8.n 27/09/13
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
package net.algem.room;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.ui.*;

/**
 * Room search view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.n
 * @since 1.0a 07/07/1999
 */
public class RoomSearchView
        extends SearchView
{

  private GemNumericField number;
  private GemField name;
  private GemField estab;
  private GemPanel mask;

  public RoomSearchView() {
  }

  @Override
  public GemPanel init() {
    number = new GemNumericField(6);
    number.addActionListener(this);
    name = new GemField(15);
    name.addActionListener(this);
    estab = new GemField(6);
    estab.addActionListener(this);
    estab.setToolTipText(MessageUtil.getMessage("establishment.number.tip"));
    
    btErase = new GemButton(GemCommand.ERASE_CMD);
    btErase.addActionListener(this);

    mask = new GemPanel();
    mask.setLayout(new GridBagLayout());

    GridBagHelper gb = new GridBagHelper(mask);
    gb.insets = GridBagHelper.SMALL_INSETS;
    gb.add(new GemLabel(BundleUtil.getLabel("Number.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Name.label")), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Establishment.label")), 0, 2, 1, 1, GridBagHelper.EAST);

    gb.add(number, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(name, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(estab, 1, 2, 1, 1, GridBagHelper.WEST);
    
    gb.add(btErase, 2, 4, 1, 1, GridBagHelper.WEST);

    return mask;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (actionListener == null) {
      return;
    }
    if (source == number || source == name || source == estab) {
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
        s = name.getText();
        break;
      case 2:
        s = estab.getText();
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
    name.setText("");
    estab.setText("");
  }
}
