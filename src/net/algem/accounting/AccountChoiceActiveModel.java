/*
 * @(#)AccountChoiceActiveModel.java	2.7.a 14/01/13
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

package net.algem.accounting;

import net.algem.util.model.GemList;
import net.algem.util.model.GemModel;
import net.algem.util.ui.GemChoiceFilterModel;

/**
 * Active accounts filter.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.7.a 14/01/2013
 */
public class AccountChoiceActiveModel
  extends GemChoiceFilterModel
{

  public <T extends GemModel> AccountChoiceActiveModel(GemList<T> list) {
    super(list);
    load(list);
//    selected = this.list.getElementAt(indices.get(0));
  }
  
  
@Override
  public boolean isFilterOk(GemModel c) {
    return ((Account)c).isActive();
  }
}
