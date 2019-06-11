/*
 * @(#)TelView.java	2.13.0 22/03/2017
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

import java.util.Vector;
import net.algem.config.Param;
import net.algem.util.BundleUtil;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 */
public class TelView
        extends InfoView
{

  private Vector<Param> vp;

  public TelView(Vector<Param> vp) {
    super(BundleUtil.getLabel("Telephones.label"), true);
    this.vp = vp;
  }

  public void setTels(Vector<Telephone> tels) {
    clearAll();
    if (tels != null && tels.size() > 0) {
      for (Telephone t : tels) {
        TelPanel tp = new TelPanel();
        tp.init(vp, false);
        tp.setTel(t);
        rows.add(tp);
        add(tp);
      }
      revalidate();
    } else {
      addRow();
    }

  }

  public Vector<Telephone> getTels() {
    Vector<Telephone> v = new Vector<Telephone>();
    for (InfoPanel pt : rows) {
      Telephone t = ((TelPanel) pt).getTel();
      if (t != null && !t.getNumber().isEmpty()) {
        v.add(t);
      }
    }
    if (v.size() > 0) {
      return v;
    }
    return null;
  }

  @Override
  public void setEditable(boolean val) {
    super.setEditable(val);
    for (InfoPanel p : rows) {
      ((TelPanel) p).setEditable(val);
    }

  }

  public void setLien(Vector<Telephone> tels) {
    clearAll();
    setTels(tels);
  }

  @Override
  protected void addRow() {
    Telephone t = new Telephone();
    t.setTypeTel(Telephone.DEFAULT_TYPE);
    TelPanel tp = new TelPanel();
    tp.init(vp, false);
    tp.setTel(t);
    rows.add(tp);
    add(tp);
    revalidate();
  }
}
