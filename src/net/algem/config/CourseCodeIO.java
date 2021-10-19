/*
 * @(#) CourseCodeIO.java Algem 2.15.6 29/11/17
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
package net.algem.config;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.algem.util.DataConnection;
import net.algem.util.model.Cacheable;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.6
 * @since 2.8.a 14/03/2013
 */
public class CourseCodeIO
  extends GemParamIO
        implements Cacheable
{

  private static final String TABLE = "module_type";
  private static final String SEQUENCE = "idmoduletype";
  private final Comparator<GemParam> comparator;

  public CourseCodeIO(DataConnection dc) {
    this.dc = dc;
    this.comparator = new Comparator<GemParam>() {
      @Override
      public int compare(GemParam o1, GemParam o2) {
        return o1.getValue().compareTo(o2.getValue());
      }
    };
  }

  @Override
  protected String getSequence() {
    return SEQUENCE;
  }

  @Override
  protected String getTable() {
    return TABLE;
  }

  @Override
  public List<GemParam> load() throws SQLException {
    List<GemParam> codes = find();
    Collections.sort(codes, comparator);
    return codes;
  }

}
