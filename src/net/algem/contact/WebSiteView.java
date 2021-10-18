/*
 * @(#)WebSiteView.java	2.13.0 22/03/17
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

import java.util.List;
import java.util.ArrayList;
import net.algem.config.Param;
import net.algem.util.BundleUtil;
import net.algem.util.jdesktop.DesktopBrowseHandler;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 * @since 2.1.b
 */
public class WebSiteView
        extends InfoView
{

  private List<Param> vp;
  private DesktopBrowseHandler browser;

  public WebSiteView(List<Param> vp) {
    this(vp, true);
  }

  public WebSiteView(List<Param> vp, boolean border) {
    super(BundleUtil.getLabel("Website.label"), border);
    this.vp = vp;
    browser = new DesktopBrowseHandler();
  }

  public void setSites(List<WebSite> sites) {
    clearAll();
    if (sites != null && sites.size() > 0) {
      for (WebSite s : sites) {
        WebSitePanel ps = new WebSitePanel(browser);
        ps.init(vp, false);
        ps.setSite(s);
        rows.add(ps);
        add(ps);
      }
      revalidate();
    } else {
      addRow();
    }
  }

  public List<WebSite> getSites() {
    List<WebSite> v = new ArrayList<>();
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
    WebSitePanel ps = new WebSitePanel(browser);
    ps.init(vp, false);
    ps.setSite(s);
    rows.add(ps);
    add(ps);
    revalidate();
  }
}
