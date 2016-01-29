/*
 * @(#)BookingGroupSchedule.java 2.9.4.0 29/01/2016
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 * 
 */

package net.algem.planning;

import net.algem.util.BundleUtil;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version Expression projectVersion is undefined on line 13, column 15 in Templates/Classes/Class.java.
 * @since Expression projectVersion is undefined on line 14, column 13 in Templates/Classes/Class.java. 29/01/2016
 */
class BookingGroupSchedule extends GroupRehearsalSchedule {

  public BookingGroupSchedule() {
  }

  @Override
  public String getScheduleLabel() {
    return getScheduleDetail();
  }

  @Override
  public String getScheduleDetail() {
    return BundleUtil.getLabel("Booking.label") + "\n" + group.getName();
  }

}
