/*
 * @(#)CourseChoiceActiveModel.java	2.7.a 28/11/12
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
package net.algem.course;

import java.util.Vector;
import net.algem.util.model.GemList;
import net.algem.util.model.GemModel;


/**
 * Model filter for active course.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class CourseChoiceActiveModel
        extends CourseChoiceFilterModel {

  private boolean active;

  public CourseChoiceActiveModel(GemList<Course> list, boolean active) {
    super(list);

    this.active = active;
    load(list);
    selected = this.list.getElementAt(indices.get(0));
  }

  public CourseChoiceActiveModel(Vector<Course> list, boolean active) {
    this(new GemList<Course>(list), active);
  }

  @Override
  public boolean isFilterOk(GemModel c) {
    return ((Course)c).isActive() == active;
  }
}
