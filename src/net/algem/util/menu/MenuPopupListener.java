/*
 * @(#)MenuPopupListener.java	2.3.a 14/02/12
 *
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights reserved.
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
package net.algem.util.menu;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 * Menu popup listener (generally right click).
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.3.a
 * @since 2.3.a 13/02/12
 */
public class MenuPopupListener
        extends MouseAdapter
{

  protected JPopupMenu popup;
  protected Component component;

  public MenuPopupListener(Component component, JPopupMenu popup) {
    this.component = component;
    this.popup = popup;
  }

  @Override
  public void mousePressed(MouseEvent e) {
    maybeShowPopup(e);
  }

  /**
   *
   * In Microsoft Windows, the user by convention brings up
   * a popup menu by releasing the right mouse button while
   * the cursor is over a component that is popup-enabled.
   * @param e
   */
  @Override
  public void mouseReleased(MouseEvent e) {
    maybeShowPopup(e);
  }

  protected void maybeShowPopup(MouseEvent e) {
    if (e.isPopupTrigger()) {
      popup.show(component, e.getX(), e.getY());
    }
  }
}
