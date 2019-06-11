/*
 * @(#)ModulePricing.java	2.9.1 09/12/14
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
 * Enumeration of the different types of pricing for modules.
 * Relative to the type priperiod in database.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 * @since 2.9.1 07/11/14
 */
public enum PricingPeriod {
  /** Pricing is HOUR-based. */
  HOUR(BundleUtil.getLabel("Pricing.period.hour.label")),
  /** Pricing is MONTH-based. */
  MNTH(BundleUtil.getLabel("Pricing.period.month.label")),
  /** Pricing is QUARTER-based. */
  QTER(BundleUtil.getLabel("Pricing.period.quarter.label")),
  /** Pricing is SEMESTER-based. */
  BIAN(BundleUtil.getLabel("Pricing.period.biannual.label")),
  /** Pricing is YEAR-based. */
  YEAR(BundleUtil.getLabel("Pricing.period.year.label")),
  /** NULL default to YEAR. */
  NULL(BundleUtil.getLabel("Pricing.period.none.label"));
  
  /** Display name. */
  private String communName;
  
  private PricingPeriod(String localizedName) {
    this.communName = localizedName;
  }
  
  @Override
  public String toString() {
    return communName;
  }
  
}
