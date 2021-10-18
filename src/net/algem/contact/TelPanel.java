/*
 * @(#)TelPanel.java	2.13.0 22/03/2017
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;
import net.algem.config.Param;
import net.algem.config.ParamChoice;
import static net.algem.contact.InfoPanel.RIGHT_SPACING;
import net.algem.util.ui.GemField;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 */
public class TelPanel extends InfoPanel
{

  public TelPanel() {
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

  @Override
  protected void init(List<Param> v, boolean withArchive) {
    iChoice = new ParamChoice(v);
    iField = new GemField();
    gb.add(iChoice, 0,0,1,1, RIGHT_SPACING, GridBagConstraints.HORIZONTAL, 0.0, 0.0);
    gb.add(iField, 1,0,3,1, new Insets(0,0,0,0), GridBagConstraints.HORIZONTAL, 1.0, 0.0);
  }
}
