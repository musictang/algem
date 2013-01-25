/*
 * @(#)WebSiteView.java	2.6.a 02/08/2012
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
package net.algem.contact;

import java.util.Vector;
import net.algem.config.Param;
import net.algem.util.jdesktop.DesktopBrowseHandler;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.1.b
 */
public class WebSiteView
        extends InfoView
{

  private Vector<Param> vp;
  private DesktopBrowseHandler browser;

  public WebSiteView(Vector<Param> vp) {
    super("Sites Web", true);
    this.vp = vp;
    browser = new DesktopBrowseHandler();
  }

  public void setSites(Vector<WebSite> sites) {

    if (sites != null && sites.size() > 0) {
      for (WebSite s : sites) {
        WebSitePanel ps = new WebSitePanel(vp, s, browser);
        rows.add(ps);
        add(ps);
      }
      revalidate();
    } else {
      addRow();
    }
  }

  public Vector<WebSite> getSites() {
    Vector<WebSite> v = new Vector<WebSite>();
    int i = 0;
    for (InfoPanel pi : rows) {
      WebSite s = ((WebSitePanel) pi).getSite();
      if (s != null && !s.getUrl().isEmpty()) {
        s.setIdx(i++);
        v.add(s);
      }
    }

    return v.isEmpty() ? null : v;
  }

  @Override
  protected void addRow() {
    WebSite s = new WebSite();
    s.setType(WebSite.DEFAULT_TYPE);
    WebSitePanel ps = new WebSitePanel(vp, s, browser);
    rows.add(ps);
    add(ps);
    revalidate();
  }
}
