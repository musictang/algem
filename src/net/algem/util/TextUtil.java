/*
 * @(#)TextUtil.java	2.15.0 14/07/17
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

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.8.r 13/12/13
 */
public class TextUtil
{

  public static final int LEADING = 0;
  public static final int TRAILING = 1;
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");
  public static final Pattern DIACRITICS_AND_FRIENDS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

  /**
   * Truncate the string {@code s} to {@code size} characters.
   * If initial string length is lower than {@code size}, the string is returned without alterations.
   * @param s initial string
   * @param size final size
   * @return a formatted string
   */
  public static String truncate(String s, int size) {

    if (s != null && s.length() > size) {
      return s.substring(0, size);
    }

    return s;
  }

  public static String crop(String s, Graphics g, int width) {
    FontMetrics fm = g.getFontMetrics();
    String crop = "";
    if (s != null && fm.stringWidth(s) > width) {
      int w = 0;
      for (char c : s.toCharArray()) {
        int cw = fm.charWidth(c);
        if (w + cw < width) {
          w += cw;
          crop += c;
        } else {
          break;
        }
      }
    }

    if (crop.isEmpty()) {
      return s;
    }
    return crop.substring(0, crop.length()-1) + '.';
  }

  /**
   * Adds the character {@code c} at the start of the end of the string {@code s} until string length equals {@code size}.
   * @param s initial string
   * @param size final size
   * @param c the character used to fill
   * @param where filling location (leading or trailing)
   * return a formatted string
   */
  public static String pad(String s, int size, char c, int where) {

    if (s == null) {
      s = "";
    }

    String res = s;
    int numSpaces = size - s.length();
    if (numSpaces > 0) {
      for (int i = 0; i < numSpaces; i++) {
        if (where == TRAILING) {
          res += c;
        } else {
          res = c + res;
        }
      }
    }
    return res;
  }

  /**
   * Fills the end of the string {@code s} with spaces until its size equals {@code size}.
   * @param s initial string
   * @param size final size
   * @return a formatted string
   */
  public static String padWithTrailingSpaces(String s, int size) {
    return pad(s, size, ' ', TRAILING);
  }

  public static String padWithLeadingSpaces(String s, int size) {
    return pad(s, size, ' ', LEADING);
  }

  /**
   * Fills the end of the string {@code s} with "0" until its size equals {@code size}.
   *
   * @param s initial string
   * @param size final size
   * @return a formatted sring
   */
  public static String padWithTrailingZeros(String s, int size) {
    return pad(s, size, '0', TRAILING);
  }

  /**
   * Fills the start of the string {@code s} with "0" until its size equals {@code size}.
   * @param s initial string
   * @param size final size
   * @return a formatted string
   */
  public static String padWithLeadingZeros(String s, int size) {
    return pad(s, size, '0', LEADING);
  }

  /**
   * Converts a number {@code n} to string, filling its start with "0" until its size equals {@code size}.
   * @param n the number to convert
   * @param size final size
   * @return a formatted string
   */
  public static String padWithLeadingZeros(int n, int size) {
    return pad(String.valueOf(n), size, '0', LEADING);
  }

	/**
   * Removes accented characters from a string.
   * @param s initial string
   * @return a string without accents if any
   */
  public static String replaceChars(String s) {
    char str [] = s.toCharArray();
    char from [] = {'À','Â','É','È','Ê','Ë','Î','Ï','Ô','Ö','Ù','Ü','\''};
    char to [] = {'A','A','E','E','E','E','I','I','O','O','U','U',' '};
    for (int i = 0 ; i < str.length; i++) {
      for (int j = 0 ; j < from.length; j++) {
        if (str[i] == from[j]) {
          str[i] = to[j];
        }
      }
    }
    return new String(str);

  }

  public static String stripDiacritics(String str) {
    str = Normalizer.normalize(str, Normalizer.Form.NFD);
    str = DIACRITICS_AND_FRIENDS.matcher(str).replaceAll("");
    return str;
  }

}
