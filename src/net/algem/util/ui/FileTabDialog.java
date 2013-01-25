/*
 * @(#)FileTabDialog.java	2.6.a 01/08/2012
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
package net.algem.util.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public abstract class FileTabDialog
        extends FileTab
        implements ActionListener
{

  protected GemButton btValidation;
  protected GemButton btCancel;
  protected GemPanel buttons;

  public FileTabDialog(GemDesktop _desktop) {
    super(_desktop);

    btValidation = new GemButton(GemCommand.VALIDATION_CMD);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btValidation.addActionListener(this);
    btCancel.addActionListener(this);

    buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 2));
    buttons.add(btValidation);
    buttons.add(btCancel);
  }

  public abstract void validation();

  public abstract void cancel();

  @Override
  public void actionPerformed(ActionEvent _evt) {
    Object src = _evt.getSource();
    if (src == btValidation) {
      validation();
    } else if (src == btCancel) {
      cancel();
    }
  }
  
}
