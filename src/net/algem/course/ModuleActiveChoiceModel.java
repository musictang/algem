/*
 * @(#) ModuleActiveChoiceModel.java Algem 2.12.0 14/03/17
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
 */

package net.algem.course;

import java.util.Vector;
import net.algem.util.model.GemList;
import net.algem.util.model.GemModel;
import net.algem.util.ui.GemChoiceFilterModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 * @since 2.12.0 14/03/17
 */
public class ModuleActiveChoiceModel extends GemChoiceFilterModel
{
  private boolean active;

  public ModuleActiveChoiceModel(GemList<Module> list, boolean active) {
    super(list);
    this.active = active;
    load(list);
  }

  public ModuleActiveChoiceModel(Vector<Module> list, boolean active) {
    this(new GemList<Module>(list), active);
  }

  @Override
  public boolean isFilterOk(GemModel m) {
    return ((Module) m).isActive() == active;
  }

  public Module getModule(int id) {
    return (Module) list.getItem(id);
  }

}
