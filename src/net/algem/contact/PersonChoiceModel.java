/*
 * @(#)PersonChoiceModel.java	2.17.0 14/03/19
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
package net.algem.contact;

import net.algem.util.model.GemList;
import net.algem.util.ui.GemChoiceModel;

/**
 * ComboxBox choice model for teachers.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0
 * @since 2.17.0
 */
public class PersonChoiceModel
        extends GemChoiceModel {

  public PersonChoiceModel(GemList<Person> list) {
    super(list);
  }

  public Person getPerson(int id) {
    return (Person) list.getItem(id);
  }

}
