/*
 * @(#)PayFrequency.java	2.9.1 07/11/14
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

package net.algem.enrolment;

import net.algem.util.BundleUtil;

/**
 * Enumeration of the available frequencies of payment.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 * @since 2.8.w 22/07/14
 */
public enum PayFrequency {
  
  MONTH(BundleUtil.getLabel("Payment.frequency.month.label"), "MOIS"),
  QUARTER(BundleUtil.getLabel("Payment.frequency.quarter.label"), "TRIM"),
  SEMESTER(BundleUtil.getLabel("Payment.frequency.semester.label"), "SMST"),
  YEAR(BundleUtil.getLabel("Payment.frequency.year.label"), "ANNU"),
  NONE(BundleUtil.getLabel("Payment.frequency.none.label"), "NULL");
  
  /**
   * Display names.
   */
  private String communName;
  
  /**
   * 4-characters catalog names.
   * These names are used for persistence.
   */
  private String catalogName;

  private PayFrequency(String localizedName, String catalogName) {
    this.communName = localizedName;
    this.catalogName = catalogName;
  }

  public String getName() {
    return catalogName;
  }
  
  @Override
  public String toString() {
    return communName;
  }
  
  public static PayFrequency getValue(String s) {
    switch (s) {
      case "MOIS":
        return MONTH;
      case "TRIM":
        return QUARTER;
      case "SMST":
        return SEMESTER;
      case "ANNU":
        return YEAR;
      default:
        return NONE;
    }
  }

}
