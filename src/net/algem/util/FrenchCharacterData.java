/*
 * @(#) FrenchCharacterData.java Algem 2.13.0 06/04/2017
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
 */

package net.algem.util;

import org.passay.CharacterData;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 * @since 2.13.0 06/04/2017
 */
/* See LICENSE for licensing and NOTICE for copyright. */


/**
 * French language character data.
 * Extension of EnglishCharacterData
 *
 * @author jm
 * @see org.passay.EnglishCharacterData
 */
public enum FrenchCharacterData implements CharacterData {

  /** Lower case characters. */
  LowerCase("INSUFFICIENT_LOWERCASE", "abcdefghijklmnopqrstuvwxyzàâäéèêëîïôöùûü"),

  /** Upper case characters. */
  UpperCase("INSUFFICIENT_UPPERCASE", "ABCDEFGHIJKLMNOPQRSTUVWXYZÀÂÉÈÊÎÔÙÛ"),

  /** Digit characters. */
  Digit("INSUFFICIENT_DIGIT", "0123456789"),

  /** Alphabetical characters (upper and lower case). */
  Alphabetical("INSUFFICIENT_ALPHABETICAL", UpperCase.getCharacters() + LowerCase.getCharacters()),

  /** Special characters. */
  Special(
    "INSUFFICIENT_SPECIAL",
    // ASCII symbols
    "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}`" +
    // Latin-1 symbols
    "\u00a1\u00a2\u00a3\u00a4\u00a5\u00a6\u00a7\u00a8\u00a9\u00aa\u00ab\u00ac\u00ad\u00ae\u00af" +
    "\u00b0\u00b1\u00b2\u00b3\u00b4\u00b5\u00b6\u00b7\u00b8\u00b9\u00ba\u00bb\u00bc\u00bd\u00be\u00bf" +
    // Latin-1 math
    "\u00d7\u00f7" +
    // Unicode symbols
    "\u2013\u2014\u2015\u2017\u2018\u2019\u201a\u201b\u201c\u201d\u201e\u2020\u2021\u2022\u2026\u2030\u2032\u2033" +
    "\u2039\u203a\u203c\u203e\u2044\u204a" +
    // Unicode currency
    "\u20a0\u20a1\u20a2\u20a3\u20a4\u20a5\u20a6\u20a7\u20a8\u20a9\u20aa\u20ab\u20ac\u20ad\u20ae\u20af" +
    "\u20b0\u20b1\u20b2\u20b3\u20b4\u20b5\u20b6\u20b7\u20b8\u20b9\u20ba\u20bb\u20bc\u20bd\u20be");


  /** Error code. */
  private final String errorCode;

  /** Characters. */
  private final String characters;


  /**
   * Creates a new instance from given parameters.
   *
   * @param  code  Error code.
   * @param  charString  Characters as string.
   */
  FrenchCharacterData(final String code, final String charString)
  {
    errorCode = code;
    characters = charString;
  }

  @Override
  public String getErrorCode()
  {
    return errorCode;
  }

  @Override
  public String getCharacters()
  {
    return characters;
  }
}