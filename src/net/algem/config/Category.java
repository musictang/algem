/*
 * @(#)Category.java	2.6.a 20/09/12
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

package net.algem.config;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public enum Category {


  SITEWEB ("categorie_siteweb", "libelle"),
  TELEPHONE ("typetel", "id"),
  VACANCY("categorie_vacance", "id");

  private String table;
  private String column;

  /**
   *
   * @param table table name
   * @param column column name for sorting
   */
  private Category (String table, String column) {
    this.table = table;
    this.column = column;
  }

  public String getTable() {
    return table;
  }

  public String getCol() {
    return column;
  }

}
