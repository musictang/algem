/*
 * @(#)DesktopBrowseHandler.java	2.6.a 02/08/2012
 *
 * Copyright (c) 1998-2011 Musiques Tangentes. All Rights Reserved.
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

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class DesktopBrowseHandler extends DesktopHandler
{

  public DesktopBrowseHandler() {
  }

  public void browse(String url) {
    URI uri = null;
    try {  
      if (isBrowseSupported() && url != null && !url.isEmpty()) {
        uri = new URI(url);
        getDesktop().browse(uri);
      }
    } catch (URISyntaxException e) {
      System.out.println(e.getMessage());
    } catch (IOException ex) {
      System.out.println("io exception " + ex.getMessage());
      System.out.println("Desktop.Action.BROWSE not supported");
      executeInternetClient(uri.getPath());
    }
  }

  private void executeInternetClient(String url) {
    try {
      Runtime.getRuntime().exec(BundleUtil.getLabel("Internet.client") + " " + url);
    } catch (IOException ex) {
      System.out.println("erreur ouverture navigateur :" + ex);
    }
  }
}
