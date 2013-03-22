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
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
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
  private ModuleIO moduleIO;
  private CourseIO courseIO;

  public ModuleService(DataConnection dc) {
    this.dc = dc;
    moduleIO = (ModuleIO) DataCache.getDao(Model.Module);
    courseIO = (CourseIO) DataCache.getDao(Model.Course);
  }

  public void create(Module m) throws SQLException {
    moduleIO.insert(m);
  }

  public void update(Module m) throws SQLException {
    moduleIO.update(m);
  }

  public void delete(Module m) throws SQLException {
    moduleIO.delete(m);
  }

  public void create(Course c) throws SQLException {
    courseIO.insert(c);
  }

  public void update(Course c) throws SQLException {
    courseIO.update(c);
  }

  public void delete(Course c) throws CourseException, SQLException {
    String where = ", action a WHERE p.ptype = " + Schedule.COURSE_SCHEDULE + " AND p.action = a.id AND a.cours = " + c.getId();
    if (ScheduleIO.findCourse(where, dc).size() > 0) {
      throw new CourseException(MessageUtil.getMessage("course.suppression.warning"));
    }
    courseIO.delete(c);
  }
  
}
