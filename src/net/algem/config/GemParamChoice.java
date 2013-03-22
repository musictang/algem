/*
 * @(#)GemParamChoice.java 2.6.a 18/09/12
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

package net.algem.config;

import java.util.Vector;
import javax.swing.ComboBoxModel;
import net.algem.util.model.GemList;
import net.algem.util.model.GemModel;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemChoiceModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.5.a 22/06/2012
 */
public class GemParamChoice 
  extends GemChoice {

  public GemParamChoice() {
  }

  public <T extends Object> GemParamChoice(Vector<T> v) {
    super(v);
  }
  
  public GemParamChoice(ComboBoxModel m) {
    super(m);
  }
  
  public GemParamChoice(GemList list) {
    this(new GemChoiceModel(list));
  }

  @Override
  public int getKey() {
    return ((GemModel)getSelectedItem()).getId();
  }

  @Override
  public void setKey(int k) {
     ((GemChoiceModel) getModel()).setSelectedItem(k);
  }
  
}
