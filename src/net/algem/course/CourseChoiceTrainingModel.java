/*
 * @(#)CourseChoiceTrainingModel.java	2.8.t 11/04/14
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
 * GemChoice model for training courses.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 2.8.t 11/04/14
 */
public class CourseChoiceTrainingModel
  extends CourseChoiceTypeModel {

  public CourseChoiceTrainingModel(GemList<Course> list, boolean collective) {
    super(list, collective);
  }

  public CourseChoiceTrainingModel(Vector<Course> list, boolean collective) {
    this(new GemList<Course>(list), collective);
  }

  @Override
  public boolean isFilterOk(GemModel c) {
    return ((Course)c).isCollective() == collective
      && ((Course)c).getCode() == CourseCodeType.STG.getId();
  }
}
