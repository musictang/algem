/*
 * @(#)RehearsalPassCtrl.java 2.13.2 09/05/17
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
package net.algem.contact.member;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 * Controller for rehearsal cards.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.2
 */
public class RehearsalPassCtrl
  extends JDialog implements ActionListener
{

  private final DataConnection dc;
  private final RehearsalPassView view;
  private RehearsalPass card;
  private GemButton btValidate;
  private GemButton btCancel;
  private boolean validation;

  public RehearsalPassCtrl(GemDesktop desktop, boolean modal) {
    super(desktop.getFrame(), modal);
    this.dc = DataCache.getDataConnection();
    view = new RehearsalPassView();
    setLayout(new BorderLayout());
    add(view, BorderLayout.CENTER);
    GemPanel buttons = new GemPanel(new GridLayout(1,2));
    btValidate = new GemButton(GemCommand.VALIDATE_CMD);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btValidate.addActionListener(this);
    btCancel.addActionListener(this);
    
    buttons.add(btValidate);
    buttons.add(btCancel);
    add(buttons, BorderLayout.SOUTH);
    setLocationRelativeTo(desktop.getFrame());
    pack();
  }

  public boolean loadCard(Object o) {
    view.clear();
    if (o == null || !(o instanceof RehearsalPass)) {
      return false;
    }
    card = (RehearsalPass) o;
    view.setCard(card);

    return true;
  }

  public RehearsalPass getCard() {
    return view.getCard();
  }
  
  boolean isValidation() {
    return validation;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == btValidate) {
      validation = view.check();
      setVisible(!validation);
    } else {
      validation = false;
      setVisible(false);
    }
    
  }

}
