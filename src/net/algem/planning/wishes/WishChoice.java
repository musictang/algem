/*
 * @(#)WishChoice.java	2.17.0 27/03/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning.wishes;

import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemChoiceLabel;
import net.algem.util.ui.GemChoiceModel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.17.0
 * @since 2.17.0
 */
public class WishChoice
        extends GemChoice
{

  /**
   * 
   */
  public WishChoice(GemChoiceModel m) {
    super(m);
  }

  public void deleteKey(int k) {
      setSelectedIndex(k > 0 ? k-1 : 0);
  }
  @Override
  public int getKey() {
    return ((GemChoiceLabel) getSelectedItem()).getId();
  }

  @Override
  public void setKey(int k) {
      for (int i=0; i < getItemCount(); i++) {
          GemChoiceLabel l = (GemChoiceLabel)getItemAt(i);
          if (l.getId() == k) {
              setSelectedItem(l);
              return;
          }
      }
      return;
  }
}
