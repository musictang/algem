/*
 * @(#)SubstituteTeacherChoice.java	2.6.a 19/09/12
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
package net.algem.contact.teacher;

import net.algem.util.ui.GemChoice;

/**
 * comment
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class SubstituteTeacherChoice
        extends GemChoice
{

  public SubstituteTeacherChoice(SubstituteTeacherChoiceModel profs) {
    super(profs);
  }

  public SubstituteTeacherChoice(SubstituteTeacherList profs) {
    this(new SubstituteTeacherChoiceModel(profs));
  }

  @Override
  public int getKey() {
    return ((SubstituteTeacher) getSelectedItem()).getSubstitute().getId();
  }

  @Override
  public void setKey(int k) {
    setSelectedItem(((SubstituteTeacherChoiceModel) getModel()).getTeacher(k));
  }
}
