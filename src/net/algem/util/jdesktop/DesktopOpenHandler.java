/*
 * @(#)DesktopOpenHandler.java 2.6.a 03/08/12
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

import java.io.File;

/**
 * Java desktop handler for file opening.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class DesktopOpenHandler
        extends DesktopHandler
{

  public DesktopOpenHandler() {
  }

  public void open(String... paths) throws DesktopHandlerException {
    if (!isOpenSupported()) {
      throw new DesktopHandlerException("Desktop Open not supported");
    }
    try {
      File f = null;
      for (String path : paths) {
        f = new File(path);
        getDesktop().open(f);
      }
    } catch (Exception e) {
      throw new DesktopHandlerException(e.getMessage() + " : Desktop Open Exception");
    }
  }
}
