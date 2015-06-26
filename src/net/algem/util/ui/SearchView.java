/*
 * @(#)SearchView.java	2.9.4.8 24/06/15
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

package net.algem.util.ui;

import java.awt.AWTEventMulticaster;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import net.algem.util.GemCommand;

/**
 * Abstract class for search view.
 * Criterium panel must be implemented in subtype classes.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.8
 * @since 1.0a 07/07/1999
 */
public abstract class SearchView
        extends GemPanel
        implements ActionListener
{

  protected GemPanel criteriaPanel;
  protected GemField status;
  protected GemButton btCreate;
  protected GemButton btErase;
  protected GemButton btSearch;
  protected GemButton btCancel;
  protected ActionListener actionListener;

  public SearchView() {

    btCreate = new GemButton(GemCommand.CREATE_CMD);
    btCreate.addActionListener(this);
    criteriaPanel = init();

    status = new GemField();
    status.setEnabled(false);

    btSearch = new GemButton(GemCommand.SEARCH_CMD);
    btSearch.addActionListener(this);

    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));
    buttons.add(btCreate);
    buttons.add(btCancel);
    buttons.add(btSearch);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.add(criteriaPanel, 0, 0, 1, 1, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(status, 0, 1, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(buttons, 0, 2, 1, 1, GridBagHelper.HORIZONTAL, 1.0, 0.0);
  }

  public void setStatus(String message) {
    status.setText(message);
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  public abstract GemPanel init();

  public abstract String getField(int n);

  public abstract void clear();
}
