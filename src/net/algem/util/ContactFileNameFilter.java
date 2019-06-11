/*
 * @(#)ContactFileNameFilter.java 2.9.3.2 13/03/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * File name filter by contact's id.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.3.2
 * @since 2.8.p 08/11/13
 */
class ContactFileNameFilter
        implements FilenameFilter
{

  private String regex;
  
  public ContactFileNameFilter(int id) {
    regex = "^(.*[^0-9])?" + id + "[ ]?\\..*$";
  }

  @Override
  public boolean accept(File dir, String name) {
    return name.matches(regex);
  }

}
