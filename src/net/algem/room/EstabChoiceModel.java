/*
 * @(#)EstabChoiceModel.java	2.7.a 03/12/12
 * 
 * Copyright (c) 1998 Musiques Tangentes. All Rights Reserved.
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

import java.util.Vector;
import net.algem.util.model.GemList;
import net.algem.util.ui.GemChoiceModel;

/**
 * ComboxBox model for establishments.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class EstabChoiceModel
        extends GemChoiceModel
{

  /**
   * 
   * @param list 
   */
  public EstabChoiceModel(GemList<Establishment> list) {
    super(list);
  }

  public EstabChoiceModel(Vector<Establishment> list) {
    this(new GemList<Establishment>(list));
  }

  public Establishment getEstablishment(int id) {
    return (Establishment) list.getItem(id);
  }
}
