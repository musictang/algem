/*
 * @(#)RehearsalCardSearchView.java 2.6.a 18/09/12
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
package net.algem.contact.member;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.ui.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @see net.algem.contact.member.RehearsalCard
 */
public class RehearsalCardSearchView
        extends SearchView
{

  private GemField label;
  private GemNumericField nbSessions;
  private GemNumericField minDuration;
  GemPanel mask;

  public RehearsalCardSearchView() {
  }

  @Override
  public GemPanel init() {

    label = new GemField(15);
    label.addActionListener(this);

    nbSessions = new GemNumericField(5);
    nbSessions.addActionListener(this);

    minDuration = new GemNumericField(5);
    minDuration.addActionListener(this);

    btErase = new GemButton(GemCommand.ERASE_CMD);
    btErase.addActionListener(this);

    btCreate.setToolTipText(MessageUtil.getMessage("card.create.tip"));
    mask = new GemPanel();
    mask.setLayout(new GridBagLayout());

    GridBagHelper gb = new GridBagHelper(mask);
    gb.insets = GridBagHelper.SMALL_INSETS;
    gb.add(new GemLabel(BundleUtil.getLabel("Label.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Sessions.number.label")), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(new GemLabel(BundleUtil.getLabel("Duration.min.label")), 0, 2, 1, 1, GridBagHelper.EAST);


    gb.add(label, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(nbSessions, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(minDuration, 1, 2, 1, 1, GridBagHelper.WEST);

    gb.add(btErase, 2, 3, 1, 1, GridBagHelper.WEST);

    return mask;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (actionListener == null) {
      return;
    }
    if (evt.getSource() == label
            || evt.getSource() == nbSessions
            || evt.getSource() == minDuration) {
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
        s = label.getText();
        break;
      case 1:
        s = nbSessions.getText();
        break;
      case 2:
        s = minDuration.getText();
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
    label.setText("");
    nbSessions.setText("");
    minDuration.setText("");
  }
}
