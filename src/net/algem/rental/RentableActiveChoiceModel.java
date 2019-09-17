/*
 * @(#)RentableActiveChoiceModel.java	2.17.1 28/09/2019
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
package net.algem.rental;

import java.util.Vector;
import net.algem.util.model.GemList;
import net.algem.util.model.GemModel;
import net.algem.util.ui.GemChoiceFilterModel;

/**
 * Filter model for active room.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.7.1
 * @since 2.7.1  28/09/2019
 *
 */
public class RentableActiveChoiceModel
        extends GemChoiceFilterModel {

  private boolean active;

  public RentableActiveChoiceModel(GemList<RentableObject> list, boolean active) {
    super(list);
    this.active = active;
    load(list);
    if (indices.size() > 0)
        selected = this.list.getElementAt(indices.get(0));
  }

  public RentableActiveChoiceModel(Vector<RentableObject> list, boolean active) {
    this(new GemList<RentableObject>(list), active);
  }

  @Override
  public boolean isFilterOk(GemModel s) {
    return ((RentableObject) s).isActif() == active;
  }
}
