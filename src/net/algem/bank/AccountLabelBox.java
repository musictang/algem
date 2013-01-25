/*
 * @(#)AccountLabelBox.java	2.6.a 14/09/12
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
package net.algem.bank;

import javax.swing.JComboBox;

/**
 * Combo box pour les comptes comptables.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @deprecated
 */
public class AccountLabelBox
        extends JComboBox
{

  private Object[] codes;
  private Object[] labels;

  public AccountLabelBox(Object[] numbers, Object[] labels) {
    super(labels);
    this.codes = numbers;
    this.labels = labels;
  }

  public void setSelectedItem(String s) {
    for (int i = 0; i < codes.length; i++) {
      if (s.equals(codes[i])) {
        super.setSelectedItem(labels[i]);
        break;
      }
    }
  }

  @Override
  public Object getSelectedItem() {
    String s = (String) super.getSelectedItem();
    for (int i = 0; i < labels.length; i++) {
      if (s.equals(labels[i])) {
        return codes[i];
      }
    }
    return codes[0];
  }
}
