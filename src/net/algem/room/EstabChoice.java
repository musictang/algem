/*
 * @(#)EstabChoice.java	2.7.a 03/12/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights reserved.
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
package net.algem.room;

import net.algem.util.model.GemList;
import net.algem.util.ui.GemChoice;

/**
 * GemChoice extension for establishments.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class EstabChoice
        extends GemChoice
{

  private EstabChoiceModel estabChoiceModel;

  public EstabChoice() {
  }

  public EstabChoice(EstabChoiceModel model) {
    super(model);
    estabChoiceModel = model;
    if (model != null && model.getSize() > 0) {
      setSelectedIndex(0);
    }
  }

  public EstabChoice(GemList<Establishment> list) {
    this(new EstabChoiceModel(list));
  }

  @Override
  public int getKey() {
    return ((Establishment) getSelectedItem()).getId();
  }

  @Override
  public void setKey(int k) {
    setSelectedItem(estabChoiceModel.getEstablishment(k));
  }
  
}
