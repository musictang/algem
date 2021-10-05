/*
 * @(#)CategoryOccup.java	2.9.4.13 05/11/15
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
package net.algem.config;

import net.algem.util.model.GenericTable;

/**
 * Occupational category.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 1.0a 07/07/1999
 */
public class CategoryOccup
        implements GenericTable
{

  private static final long serialVersionUID = 4362331151043829289L;
  private int id;
  private String label;

  public CategoryOccup() {
  }

  public CategoryOccup(int i) {
    id = i;
    label = "";
  }

  public CategoryOccup(int i, String l) {
    id = i;
    label = l;
  }

  @Override
  public String toString() {
    return label;
  }

  public boolean equals(Object o) {
              if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CategoryOccup c = (CategoryOccup) o;
    return (id == c.id);
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int i) {
    id = i;
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public void setLabel(String l) {
    label = l;
  }

  public String getKey() {
    return String.valueOf(id);
  }

  public void setKey(String l) {
    id = Integer.parseInt(l);
  }

  public String getValue() {
    return label;
  }

  public void setValue(String l) {
    label = l;
  }

}
