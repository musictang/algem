/*
 * @(#)ModuleService.java 2.6.a 03/08/12
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

package net.algem.course;

import java.sql.SQLException;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.model.Model;

/**
 * Service class for modules.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.5.a 03/07/12
 */
public class ModuleService 
{
  
  private DataConnection dc;

  public ModuleService(DataConnection dc) {
    this.dc = dc;
  }
  
  public void create(Module m) throws SQLException {
    ((ModuleIO) DataCache.getDao(Model.Module)).insert(m);
  }
  
  public void update(Module m) throws SQLException {
    ((ModuleIO) DataCache.getDao(Model.Module)).update(m);
  }
  
  public void delete(Module m) throws SQLException {
    ((ModuleIO) DataCache.getDao(Model.Module)).delete(m);
  }
}
