/*
 * @(#)DefaultGemParamService.java 2.9.3 24/02/2015
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

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.3
 * @since 2.9.3 24/02/2015
 */
class DefaultGemParamService implements GemParamService {

  private GemParamIO paramIO;
  
  public DefaultGemParamService() {
    
  }
  
  @Override
  public void setDAO(GemParamIO dao) {
    this.paramIO = dao;
  }

  @Override
  public List<GemParam> load() throws SQLException {
    return paramIO.find();
  }

  @Override
  public void insert(GemParam n) throws SQLException {
    paramIO.insert(n);
  }

  @Override
  public void update(GemParam n) throws SQLException {
    paramIO.update(n);
  }

  @Override
  public void delete(GemParam n) throws SQLException {
    paramIO.delete(n);
  }

}
