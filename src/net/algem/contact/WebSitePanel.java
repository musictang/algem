/*
 * @(#)WebSitePanel.java	2.9.3.2 10/03/15
 *
 * Copyright (c) 1998-2015 Musiques Tangentes. All Rights Reserved.
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;
import net.algem.config.Param;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.jdesktop.DesktopBrowseHandler;
import net.algem.util.jdesktop.DesktopHandlerException;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.3.2
 */
public class WebSitePanel extends InfoPanel implements ActionListener, ItemListener {

  private DesktopBrowseHandler browser;
  private short ptype;// temp value

  public WebSitePanel(Vector<Param> v, WebSite s, DesktopBrowseHandler browser) {
    super(v, false);
    setSite(s);
    iChoice.addItemListener(this);
    addButton(BundleUtil.getLabel("See.label"), BundleUtil.getLabel("Website.view.tip"));
    this.browser = browser;
    iButton.addActionListener(this);
  }

  public WebSite getSite() {
    WebSite s = new WebSite();
    s.setType(iChoice.getKey());
    s.setUrl(maybePrefixURL(iField.getText().trim()));
    return s;
  }

  private void setSite(WebSite s) {
    if (s.getType() >= 0) {
      iChoice.setKey(s.getType());
    }
    iField.setText(s.getUrl());
    ptype = s.getPtype();
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    iField.setText(maybePrefixURL(iField.getText()));
  }
  
  /**
   * Adds the prefix http or https at the beginning of the url if not present.
   * @param url current url
   * @return formatted-url
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
    try {
      browser.browse(iField.getText());
    } catch (DesktopHandlerException ex) {
      GemLogger.log(ex.getMessage());
    }
  }
}
