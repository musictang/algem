/*
 * @(#)DesktopMailHandler.java	2.7.a 03/12/12
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;

/**
 * Sending mail manager.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.0k 11/01/10
 * @see java.awt.Desktop
 */
public class DesktopMailHandler
        extends DesktopHandler
{

  public DesktopMailHandler() {
  }

  public void send(String _to, String bcc) {
    URI uriMailto = null;
    String to = _to;
    String mailClient = BundleUtil.getLabel("Mail.client");
    assert (mailClient != null);
    if (isMailSupported() && !mailClient.equalsIgnoreCase("kmail")) { //XXX bug KDE retourne true pour Action.MAIL mais ne fonctionne pas
      try {
        if (to.length() > 0) {
          if (bcc != null && bcc.length() > 0) {
            to += "?bcc=" + bcc;
          }
          uriMailto = new URI("mailto", to, null);
          getDesktop().mail(uriMailto);
        } else {
          getDesktop().mail();
        }
      } catch (IOException ioe) {
        GemLogger.log(Level.WARNING, getClass().getName(), "send", ioe.getMessage());
        executeMailClient(_to, bcc);
      } catch (URISyntaxException use) {
        GemLogger.logException(use);
      }
    } else {
      //utiliser le runtime
      GemLogger.log("Desktop.Action.MAIL not supported");
      executeMailClient(to, bcc);
    }
  }

  private void executeMailClient(String to, String bcc) {
    try {
      String mailClient = BundleUtil.getLabel("Mail.client");
      Runtime.getRuntime().exec(mailClient + " " + formatMailto(to, bcc, mailClient));
    } catch (IOException e) {
      GemLogger.logException(e);
    }
  }

  /**
   * Gets a string including (if necessary) bcc options for mail agent editor.
   *
   * @param _to
   * @param bcc
   * @param app
   * @return
   */
  private String formatMailto(String _to, String bcc, String app) {
    String args = null;
    if (app.equalsIgnoreCase("kmail")) {
      if (bcc != null && bcc.length() > 0) {
        args = _to + " -b " + bcc;
      } else {
        args = _to;
      }
    } else if (app.equalsIgnoreCase("thunderbird")) {
      if (bcc != null && bcc.length() > 0) {
        args = " -compose to='" + _to + "',bcc='" + bcc + "'";
      } else {
        args = " -compose to='" + _to + "'";
      }
    }
    return args;
  }
}