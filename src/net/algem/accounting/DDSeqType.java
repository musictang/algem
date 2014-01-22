/*
 * @(#)DDSeqType.java	2.8.r 20/01/14
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
package net.algem.accounting;

import net.algem.util.BundleUtil;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.r
 * @since 2.8.r 24/12/13
 */
public enum DDSeqType
{

  FRST(BundleUtil.getLabel("Direct.debit.FRST.label")),
  RCUR(BundleUtil.getLabel("Direct.debit.RCUR.label")),
  FNAL(BundleUtil.getLabel("Direct.debit.FNAL.label")),
  OOFF(BundleUtil.getLabel("Direct.debit.OOFF.label")),
  LOCK(BundleUtil.getLabel("Direct.debit.LOCK.label"));
  private final String label;

  DDSeqType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
  
  @Override
  public String toString() {
    return label;
  }
}
