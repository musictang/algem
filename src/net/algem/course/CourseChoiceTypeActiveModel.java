/*
 * @(#)CourseChoiceTypeActiveModel.java	2.8.t 15/04/14
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
package net.algem.course;

import java.util.Vector;
import net.algem.util.model.GemList;
import net.algem.util.model.GemModel;


/**
 * Model filter for active and collective course.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 */
public class CourseChoiceTypeActiveModel
        extends CourseChoiceFilterModel {

  protected boolean collective;
  protected boolean active;

  public CourseChoiceTypeActiveModel(GemList<Course> list, boolean collective, boolean active) {
    super(list);
    this.collective = collective;
    this.active = active;

    load(list);

    if (list != null && list.getSize() > 0) {
      if (indices.size() > 0) {
        selected = this.list.getElementAt(indices.get(0));
      }
    }
  }

  public CourseChoiceTypeActiveModel(Vector<Course> liste, boolean collective, boolean active) {
    this(new GemList<Course>(liste), collective, active);
  }

  @Override
  public boolean isFilterOk(GemModel c) {
    return ((Course)c).isCollective() == collective && ((Course)c).isActive() == active;
  }
}
