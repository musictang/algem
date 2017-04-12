/*
 * @(#) RuleFactory.java Algem 2.13.1 12/04/2017
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
package net.algem.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.algem.util.FrenchCharacterData;
import org.passay.CharacterRule;
import org.passay.LengthRule;
import org.passay.PasswordValidator;
import org.passay.Rule;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.1
 * @since 2.13.1 12/04/2017
 */
public class RuleFactory {

  public static final int LOW = 1;
  public static final int MEDIUM = 2;
  public static final int STRONG = 3;

  public static PasswordValidator getValidator(int rule) {
    switch (rule) {
      case LOW:
        List<Rule> rl = new ArrayList<>();
        rl.add(new LengthRule(8, 16));
        return new PasswordValidator(rl);
      case STRONG:
        return new PasswordValidator(Arrays.asList(
          // length between 8 and 16 characters
          new LengthRule(10, 16),
          // at least one upper-case character
          new CharacterRule(FrenchCharacterData.UpperCase, 1),
          // at least one lower-case character
          new CharacterRule(FrenchCharacterData.LowerCase, 1),
          // at least one digit character
          new CharacterRule(FrenchCharacterData.Digit, 1),
          // at least one symbol (special character)
          new CharacterRule(FrenchCharacterData.Special, 1))
        );
      default:
        return new PasswordValidator(Arrays.asList(
          new LengthRule(8, 16),
          new CharacterRule(FrenchCharacterData.UpperCase, 1),
          new CharacterRule(FrenchCharacterData.LowerCase, 1),
          new CharacterRule(FrenchCharacterData.Digit, 1))
        );
    }
  }

}
