/*
 * @(#)GemChoice.java	2.8.v 28/05/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util.ui;

import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import net.algem.util.model.GemList;
import net.algem.util.model.GemModel;

/**
 * Generic combo box.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.v
 */
public abstract class GemChoice
        extends JComboBox {

  public GemChoice() {
  }

  public GemChoice(ComboBoxModel m) {
    super(m);
  }

  public <T extends Object> GemChoice(Vector<T> v) {
    super(v);
  }

  public <T extends GemModel> void set(GemList<T> l) {
    if (getModel() instanceof GemChoiceModel) {
      int k = getKey();
      ((GemChoiceModel) getModel()).list = l;
      setKey(k);
    }
  }

  abstract public int getKey();

  abstract public void setKey(int k);

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
