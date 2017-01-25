/*
 * @(#)StringUtils.java	2.11.5 25/01/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util;

import java.util.List;

/**
 *
 * @author alexandre
 * @since 2.9
 * @version 2.11.5
 */
public class StringUtils {

  /**
   * Converts a list of objects to string.
   *
   * @param <T>
   * @param list initial list
   * @param conjunction string used to separate the elements of the list
   * @return a string representation of the list
   */
  public static <T> String join(List<T> list, String conjunction) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Object item : list) {
      if (first) {
        first = false;
      } else {
        sb.append(conjunction);
      }
      sb.append(item);
    }
    return sb.toString();
  }
}
