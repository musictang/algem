/*
 * @(#)CourseScheduleChoice.java	2.17.0 15/05/19
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
package net.algem.course;

import net.algem.config.GemParamChoice;
import net.algem.planning.CourseSchedulePrintDetail;
import net.algem.util.model.GemList;
import net.algem.util.ui.GemChoiceModel;

/**
 * Combo box for course choice.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @since 2.17.0
 * @version 2.17.0
 */
public class CourseScheduleChoice
        extends GemParamChoice
{

/*  public CourseScheduleChoice(GemChoiceModel<GemModel> model) {
    super(model);
  }
*/
    
  public CourseScheduleChoice(GemList<CourseSchedulePrintDetail> list) {
    super(new GemChoiceModel<CourseSchedulePrintDetail>(list));
  }

}
