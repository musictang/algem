/*
 * @(#)DesktopBrowseHandler.java	2.8.t 16/05/14
 *
 * Copyright (c) 1998-2014 Musiques Tangentes. All Rights Reserved.
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;

/**
 * Java desktop handler for browsing urls.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 */
public class DesktopBrowseHandler extends DesktopHandler
{

  public DesktopBrowseHandler() {
  }

  /**
   * Tries to open {@code url} using the system browser.
   * 
   * @param url
   * @throws DesktopHandlerException 
   */
  public void browse(String url) throws DesktopHandlerException {

    if (url == null || url.isEmpty()) {
      return;
    }

    if (isBrowseSupported()) {
      try {
        getDesktop().browse(new URI(url));
      } catch (URISyntaxException e) {
        GemLogger.log(e.getMessage());
      } catch (IOException ex) {
        GemLogger.log("io exception " + ex.getMessage());
        executeInternetClient(url);
      }
    } else {
      GemLogger.log("Desktop.Action.BROWSE not supported");
      executeInternetClient(url);
    }

  }

   /**
   * System-level execution alternative.
   * 
   * @param url
   */
  private void executeInternetClient(String url) throws DesktopHandlerException {
    try {
      Runtime.getRuntime().exec(BundleUtil.getLabel("Internet.client") + " " + url);
    } catch (IOException ex) {
      throw new DesktopHandlerException("Erreur ouverture navigateur :" + ex);
    }
  }
}
