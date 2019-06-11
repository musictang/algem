/*
 * @(#)DialogPanel.java	2.6.a 25/09/12
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

import java.awt.AWTEvent;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import net.algem.util.BundleUtil;

/**
 * Generic dialog panel.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class DialogPanel
        extends javax.swing.JDialog
        implements ActionListener
{

  private GemButton btQuit;
  private GemPanel panel;
  private static final String QUIT = BundleUtil.getLabel("Menu.quit.label");

  public DialogPanel(Frame _parent, String title, GemPanel _panneau) {
    super(_parent, title);
    panel = _panneau;
    init();
  }

  public DialogPanel(Dialog _parent, String title, GemPanel _panneau) {
    super(_parent, title);

    panel = _panneau;
    init();
  }

  public void init() {
    btQuit = new GemButton(QUIT);
    btQuit.addActionListener(this);

    getContentPane().add("Center", panel);
    getContentPane().add("South", btQuit);

    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    setSize(610, 450);
  }

  public boolean canClose() {
    return true;
  }

  public void close() {
    setVisible(false);
    dispose();
  }

  @Override
  public void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      if (canClose()) {
        super.processWindowEvent(e);
        close();
      }
    } else {
      super.processWindowEvent(e);
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals(QUIT)) {
      if (canClose()) {
        close();
      }
    }
  }
}
