/*
 * @(#)ModuleService.java 2.9.4.12 29/09/15
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
package net.algem.course;

import java.sql.SQLException;
import java.util.Vector;
import net.algem.enrolment.ModuleOrder;
import net.algem.enrolment.ModuleOrderIO;
import net.algem.planning.DateFr;
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
 * @version 2.9.4.12
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

  public void delete(Module m) throws SQLException, ModuleException {
    Vector<ModuleOrder> vm = ModuleOrderIO.find("AND m.id = " + m.getId(), dc);
    if (vm != null && vm.size() > 0) {
      if (vm.size() < 10) {
        StringBuilder sb = new StringBuilder();
        for (ModuleOrder mo : vm) {
          sb.append("\n").append(mo.getIdOrder());
        }
        throw new ModuleException(MessageUtil.getMessage("module.delete.exception2", sb.toString()));
      } else {
        throw new ModuleException(MessageUtil.getMessage("module.delete.exception", vm.size()));
      }
    }
    moduleIO.delete(m);
  }
  
  boolean isUsed(int moduleId, DateFr start) throws SQLException {
    Vector<ModuleOrder> vm = ModuleOrderIO.find("AND m.id = " + moduleId + " AND cm.debut >= '" + start.toString() + "'", dc);
    return vm != null && vm.size() > 0;
  }

  public void create(Course c) throws SQLException {
    courseIO.insert(c);
  }

  public void update(Course c) throws SQLException {
    courseIO.update(c);
  }

  public void delete(Course c) throws CourseException, SQLException {
    String where = ", action a WHERE p.ptype = " + Schedule.COURSE + " AND p.action = a.id AND a.cours = " + c.getId();
    if (ScheduleIO.findCourse(where, dc).size() > 0) {
      throw new CourseException(MessageUtil.getMessage("course.suppression.warning"));
    }
    courseIO.delete(c);
  }
  
}
