/*
 * @(#)PostitCreateCtrl.java	2.9.4.6 02/06/15
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
package net.algem.util.postit;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import net.algem.security.UserService;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktopCtrl;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;

/**
 * Postit creation controller.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.6
 */
public class PostitCreateCtrl
        extends GemPanel
        implements ActionListener
{

  public static final String POSTIT_CREATE_KEY="Postit.create";
  private GemDesktopCtrl desktop;
  private PostitCreateView postit;
  private GemField status;
  private GemButton btCancel;
  private GemButton btValidate;
  private ActionListener actionListener;
  private UserService service;

  public PostitCreateCtrl(GemDesktopCtrl desktop, UserService service) {

    this.setLayout(new BorderLayout());
    this.desktop = desktop;
    this.service = service;
    status = new GemField();
    status.setEditable(false);

    postit = new PostitCreateView(desktop.getDataCache().getUser().getId(), service.getRegisteredUsers());

    GemPanel p1 = new GemPanel();
    p1.setLayout(new GridLayout(1, 2));

    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);
    btValidate = new GemButton(GemCommand.VALIDATE_CMD);
    btValidate.addActionListener(this);
    p1.add(btCancel);
    p1.add(btValidate);

    add(postit, BorderLayout.CENTER);
    add(p1, BorderLayout.SOUTH);
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

  public boolean cancel() {
    postit.clear();
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
    }
    return true;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    if (cmd.equals(GemCommand.CANCEL_CMD)) {
      cancel();
    } else if (cmd.equals(GemCommand.VALIDATE_CMD)) {
      Postit p = postit.get();
      try {
        service.create(p);
        desktop.getPostit().addPostit(p);
//        if (p.getReceiver() == 0) { //public (filter at reception)
          desktop.postEvent(new CreatePostitEvent(this, p));
//        }
      } catch (SQLException e) {
        GemLogger.logException("Insertion postit", e, this);
      } finally {
        cancel();
      }
    }
  }
}

