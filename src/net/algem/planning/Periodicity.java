/*
 * @(#)Periodicity.java	2.8.w 23/07/14
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

import net.algem.util.BundleUtil;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.7.p 22/07/2014
 */
public enum Periodicity 
{
  DAY(BundleUtil.getLabel("Periodicity.day.label")),
  FORTNIGHT(BundleUtil.getLabel("Periodicity.fortnight.label")),
  MONTH(BundleUtil.getLabel("Periodicity.month.label")),
  QUARTER(BundleUtil.getLabel("Periodicity.quarter.label")),
  SEMESTER(BundleUtil.getLabel("Periodicity.semester.label")),
  WEEK(BundleUtil.getLabel("Periodicity.week.label")),
  YEAR(BundleUtil.getLabel("Periodicity.year.label"));
  
  private String communName;

  private Periodicity(String communName) {
    this.communName = communName;
  }
  
  @Override
  public String toString() {
    return communName;
  }
          
}
