/*
 * @(#) LevelIO.java Algem 2.7.a 07/01/13
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
 */
package net.algem.config;

import java.sql.SQLException;
import java.util.List;
import net.algem.util.DataConnection;
import net.algem.util.model.Cacheable;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.5.a 20/06/2012
 */
public class LevelIO
        extends GemParamIO 
        implements Cacheable
{

  private final static String TABLE = "niveau";
  private final static String SEQUENCE = "niveau_id_seq";
 
  public LevelIO(DataConnection dc) {
    this.dc = dc;
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
    return find();
  }
  
}
