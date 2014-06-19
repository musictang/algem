/*
 * @(#)StudioSchedule.java	2.8.v 16/06/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning;

import net.algem.config.GemParam;
import net.algem.group.Group;
import net.algem.util.BundleUtil;

/**
 * Abstract studio schedule.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.8.v 02/06/14
 */
public abstract class StudioSchedule
        extends ScheduleObject
{
  protected Group group;

  public StudioSchedule() {
  }

  public StudioSchedule(Schedule d) {
    super(d);
  }

  public void setGroup(Group g) {
    idper = g == null ? 0 : g.getId();
    group = g;
  }

  public Group getGroup() {
    return group;
  }

  public String getActivityLabel() {
    GemParam p = (GemParam) activity;
    return p == null || p.getId() == 0 || p.getLabel() == null || p.getLabel().isEmpty() ?
      BundleUtil.getLabel("Studio.label") : p.getLabel();
  }

   @Override
  public String getScheduleLabel() {
    return group.getName();
  }
}
