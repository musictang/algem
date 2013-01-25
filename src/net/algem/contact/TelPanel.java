/*
 * @(#)TelPanel.java	2.6.a 17/09/12
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

package net.algem.contact;

import java.awt.Color;
import java.util.Vector;
import net.algem.config.Param;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TelPanel extends InfoPanel
{

  public TelPanel(Vector<Param> params, Telephone tel) {
    super(params, false);
    setTel(tel);
  }

  public Telephone getTel() {
    Telephone t = new Telephone();
    t.setNumber(iField.getText().trim());
    t.setTypeTel(iChoice.getKey());
    return t;
  }

  public void setTel(Telephone tel) {
    if (tel != null) {
      if (tel.getTypeTel() > 0) {
        iChoice.setKey(tel.getTypeTel());
      }
      iField.setText(tel.getNumber());
    }
  }

  public void setEditable(boolean editable) {
    iChoice.setEnabled(editable);
    iField.setEditable(editable);
    iField.setBackground(editable ? Color.white : Color.lightGray);
  }
}
