/*
 * @(#)InvoiceComparator.java 2.5.d 25/07/12
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
package net.algem.billing;

import java.util.Comparator;

/**
 * Invoice comparator.
 * The comparison apply to the incremental number.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.5.d
 * @since 2.5.a 09/07/12
 */
public class InvoiceComparator
        implements Comparator<String> {

  @Override
  public int compare(String o1, String o2) {

    Integer s1 = Integer.parseInt(o1.substring(4));
    Integer s2 = Integer.parseInt(o2.substring(4));
    return s1.compareTo(s2);
  }
}

