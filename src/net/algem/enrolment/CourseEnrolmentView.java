/*
 * @(#)CourseEnrolmentView.java	2.10.0 13/05/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
package net.algem.enrolment;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import net.algem.group.Musician;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;

/**
 * View the list of enrolled members.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 */
public class CourseEnrolmentView
        extends MemberEnrolmentView
{

  public CourseEnrolmentView(GemDesktop desktop, EnrolmentService service) {
     super(desktop, service);
  }

  /**
   * Load the list of members between {@code start} and {@code end}.
   * @param id course id
   * @param start start date
   * @param end end date
   */
  @Override
  protected void load(int id, Date start, Date end) {
    if (id == 0) {
      return;
    }
    try {
      List<Musician> vm = service.findCourseMembers(id, start, end);
      for (Musician m : vm) {
        membersTableModel.addItem(m);
        total.setText(String.valueOf(vm.size()));
      }
    } catch (SQLException e) {
      GemLogger.logException(getClass().getName() + "#load", e);
    }
  }

}
