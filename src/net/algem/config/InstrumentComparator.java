/*
 * @(#)InstrumentComparator.java 2.0l 08/02/10 16:17
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

package net.algem.config;

import java.util.Comparator;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc</a>
 */
public class InstrumentComparator implements Comparator {
  @Override
	public int compare(Object o1, Object o2) {
		Instrument i1 = (Instrument)o1;
		Instrument i2 =(Instrument)o2;
		return ((Integer)i1.getId()).compareTo(((Integer)i2.getId()));
	}

}
