/*
 * @(#)AbstractHelpDlg.java 2.8.w 10/09/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

package net.algem.util.help;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.JDialog;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemButton;

/**
 * Abstract dialog help controller.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.8.w 10/09/14
 */
public abstract class AbstractHelpDlg
  extends JDialog
  implements ActionListener
{

  /** Default help resource directory. */
  protected static final String RES_PREFIX = "/resources/doc/html/";

  protected GemButton okBt;

  public AbstractHelpDlg(Frame owner, String title) {
    super(owner, title);
    okBt = new GemButton(GemCommand.CLOSE_CMD);
    okBt.addActionListener(this);
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, getContent());
    add(BorderLayout.SOUTH, okBt);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == okBt) {
      okBt.removeActionListener(this);
      setVisible(false);
      dispose();
    }
  }

  /**
   * Gets a URL linking to a resource.
   * @param res resource link as string
   * @return a URL
   */
  protected URL getResource(String res) {
    return getClass().getResource(RES_PREFIX + res);
  }

  /**
   * Gets the main content.
   * @return a component
   */
  protected abstract Component getContent();

}
