/*
 * @(#)DesktopHandler.java	2.6.a 02/08/2012
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

package net.algem.util.jdesktop;

import java.awt.Desktop;

/**
 * Java desktop handler.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 05/10/10 2.1a
 */
public class DesktopHandler {


	private Desktop desktop;

	public DesktopHandler() {
//		cache = dc;
		setDesktop();
	}

	public Desktop getDesktop() {
		return desktop;
	}

	public void setDesktop() {
		if (Desktop.isDesktopSupported()) {
			desktop = Desktop.getDesktop();
		}
	}

  public boolean isSupported() {
    return desktop != null;
  }

  public boolean isMailSupported() {
    if (isSupported()) {
      return desktop.isSupported(Desktop.Action.MAIL);
			/*System.out.println("Desktop.Action.BROWSE :"+desktop.isSupported(Desktop.Action.BROWSE));
			System.out.println("Desktop.Action.EDIT :"+desktop.isSupported(Desktop.Action.EDIT));
			System.out.println("Desktop.Action.OPEN :"+desktop.isSupported(Desktop.Action.OPEN));
			System.out.println("Desktop.Action.PRINT :"+desktop.isSupported(Desktop.Action.PRINT));*/

		}
		//System.out.println("Desktop not supported");
		return false;
	}

  public boolean isOpenSupported() {
    if (isSupported()) {
      return desktop.isSupported(Desktop.Action.OPEN);
    }
    return false;
  }

  public boolean isBrowseSupported() {
    if (isSupported()) {
      return desktop.isSupported(Desktop.Action.BROWSE);
    }
    return false;
  }

  
}
