/*
 * @(#)AgeRangeSearchView.java 2.6.a 18/09/12
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
 */

package net.algem.config;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.*;

/**
 *
 * @author <a href="mailto:nicolasnouet@gmail.com">Nicolas Nouet</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.3.a
 */

public class AgeRangeSearchView
	extends SearchView
{
  private GemField agemin;
  private GemField agemax;
  private GemPanel mask;

  public AgeRangeSearchView() {
  }

  @Override
  public GemPanel init() {
    agemin = new GemField(2);
    agemin.addActionListener(actionListener);
    agemax = new GemField(2);
    agemax.addActionListener(actionListener);


    btErase = new GemButton(GemCommand.ERASE_CMD); //@jm GemCommand.ERASE_CMD
    btErase.addActionListener(this);
    btCreate.setToolTipText("Créer une nouvelle tranche d'âge"); // @jm message à placer dans message.properties
    mask = new GemPanel();
    mask.setLayout(new GridBagLayout());

    GridBagHelper gb = new GridBagHelper(mask);
    gb.insets = GridBagHelper.SMALL_INSETS;
    gb.add(new GemLabel(BundleUtil.getLabel("Age.range.min.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Age.range.max.label")), 0, 1, 1, 1, GridBagHelper.EAST);

    gb.add(agemin, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(agemax, 1, 1, 1, 1, GridBagHelper.WEST);
    return mask;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (actionListener == null || !(actionListener instanceof AgeRangeSearchCtrl)) {
      return;
    }
    if (evt.getSource() == agemin || evt.getSource() == agemax) {
      ((AgeRangeSearchCtrl) actionListener).search(); // @jm GemCommand.SEARCH_CMD
    } else actionListener.actionPerformed(evt);
  }

  @Override
  public String getField(int n) {
    String s = null;
    switch (n) {
      case 0:
        s = agemin.getText();
        break;
      case 1:
        s = agemax.getText();
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
    agemin.setText("");
    agemax.setText("");
  }
}

