/*
 * @(#)TextUtil.java	2.8.r 13/12/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.r
 * @since 2.8.r 13/12/13
 */
public class TextUtil
{

  public static final int LEADING = 0;
  public static final int TRAILING = 1;

  /**
   * Tronque la chaine de caractères au nombre de caractères
   * fourni par
   * <CODE>taille</CODE>. Si la chaîne a une taille
   * supérieure à l'argument
   * <CODE>taille</CODE> alors renvoie
   * la chaîne d'origine sans altération
   */
  public static String truncate(String chaine, int taille) {

    if (chaine.length() > taille) {
      return chaine.substring(0, taille);
    }

    return chaine;
  }

  /**
   * Ajoute le caractère
   * <CODE>c</CODE> au début ou à la fin de la chaine
   * de telle manière que la chaine soit de
   * <CODE>size</CODE> caractères
   */
  public static String pad(String chaine, int size, char c, int where) {

    if (chaine == null) {
      chaine = "";
    }

    String resultat = chaine;
    int numSpaces = size - chaine.length();
    if (numSpaces > 0) {
      for (int i = 0; i < numSpaces; i++) {
        if (where == TRAILING) {
          resultat += c;
        } else {
          resultat = c + resultat;
        }
      }
    }
    return resultat;
  }

  /**
   * Remplit d'espaces une chaîne de caractères selon la taille fixée par
   * <code>size</code>.
   */
  public static String padWithTrailingSpaces(String chaine, int size) {
    return pad(chaine, size, ' ', TRAILING);
  }

  /**
   * Remplit de zéros une chaîne de caractères selon la taille fixée par size.
   *
   * @param chaine
   * @param size
   * @return une chaîne
   */
  public static String padWithTrailingZeros(String chaine, int size) {
    return pad(chaine, size, '0', TRAILING);
  }

  public static String padWithLeadingZeros(String chaine, int size) {
    return pad(chaine, size, '0', LEADING);
  }

  public static String padWithLeadingZeros(int chiffre, int size) {
    return pad(String.valueOf(chiffre), size, '0', LEADING);
  }
  
  public static String padWithLeadingSpaces(String chaine, int size) {
    return pad(chaine, size, ' ', LEADING);
  }
}
