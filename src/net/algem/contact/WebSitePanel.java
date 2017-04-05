/*
 * @(#)WebSitePanel.java	2.13.1 05/04/17
 *
 * Copyright (c) 1998-2017 Musiques Tangentes. All Rights Reserved.
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

package net.algem.contact;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.jdesktop.DesktopBrowseHandler;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.jdesktop.DesktopOpenHandler;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.1
 */
public class WebSitePanel extends InfoPanel implements ActionListener {

  private DesktopBrowseHandler browser;
  private short ptype;// temp value

  public WebSitePanel(DesktopBrowseHandler browser) {
//    super(v, false);
    addButton(BundleUtil.getLabel("See.label"), BundleUtil.getLabel("Website.view.tip"));
    this.browser = browser;
    iButton.addActionListener(this);
  }

  public WebSite getSite() {
    WebSite s = new WebSite();
    s.setType(iChoice.getKey());
    //s.setUrl(maybePrefixURL(iField.getText().trim()));
    s.setUrl(iField.getText().trim());
    return s;
  }

  public void setSite(WebSite s) {
    if (s.getType() >= 0) {
      iChoice.setKey(s.getType());
    }
    iField.setText(s.getUrl());
    ptype = s.getPtype();
  }

  /**
   * Adds the prefix http or https at the beginning of the url if not present.
   * @param url current url
   * @return formatted-url
   * @deprecated
   */
  private String maybePrefixURL(String url) {
    String val = iChoice.getValue().toLowerCase();
    if (url.trim().length() > 0 && !(url.toLowerCase().startsWith(WebSite.HTTP_PREFIX) || url.toLowerCase().startsWith(WebSite.HTTPS_PREFIX))) {
      switch (val) {
        case "facebook":
        case "myspace":
        case "twitter":
          url = WebSite.HTTPS_PREFIX + url;
          break;
        default:
          url = WebSite.HTTP_PREFIX + url;
      }
    }
    return url.toLowerCase();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String path = iField.getText();
    try {
      if (path.startsWith("file://")) {
        path = path.substring(7);
        new DesktopOpenHandler().open(path);
      } else {
        browser.browse(path);
      }
    } catch (DesktopHandlerException ex) {
      GemLogger.log(ex.getMessage());
    }
  }
}
