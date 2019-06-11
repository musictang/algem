/*
 * @(#) TestLoginPassword.java Algem 2.13.1 12/04/17
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

import java.util.Arrays;
import net.algem.util.FrenchCharacterData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.passay.CharacterRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.RuleResultDetail;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.1
 * @since 2.13.1 06/04/2017
 */
public class TestLoginPassword {

  public TestLoginPassword() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testPasswordStrength() {
    PasswordValidator validator = new PasswordValidator(Arrays.asList(
      // length between 8 and 16 characters
      new LengthRule(8, 16),
      // at least one upper-case character
      new CharacterRule(FrenchCharacterData.UpperCase, 1),
      // at least one lower-case character
      new CharacterRule(FrenchCharacterData.LowerCase, 1),
      // at least one digit character
      new CharacterRule(FrenchCharacterData.Digit, 1),
      // at least one symbol (special character)
      new CharacterRule(FrenchCharacterData.Special, 1)
    // no whitespace
    //new WhitespaceRule())
    ));

//final char[] password = System.console().readPassword("Password: ");
    String pass = "bonjour";
    RuleResult result = validator.validate(new PasswordData(pass));
//    System.out.println(result.getDetails());
    System.out.println("pass 1");
    printDetail(result);
    assertFalse(result.isValid());

    pass = "Bonj0ur Monde";
    result = validator.validate(new PasswordData(pass));
    System.out.println("pass 2");
    printDetail(result);
    assertFalse(result.isValid());

    pass = "Bonj0ur?Monde";
    result = validator.validate(new PasswordData(pass));
    System.out.println("pass 3");
    printDetail(result);
    assertTrue(result.isValid());

    pass = "Éonj0ur?mélnde";
    result = validator.validate(new PasswordData(pass));
    System.out.println("pass 4");
    printDetail(result);
    assertTrue(result.isValid());

    pass = "écôutë pÈt1t!";
    result = validator.validate(new PasswordData(pass));
    System.out.println("pass 5");
    printDetail(result);
    assertTrue(result.isValid());

    /*if (result.isValid()) {
      System.out.println("Password is valid");
    } else {
      System.out.println("Invalid password:");
      for (String msg : validator.getMessages(result)) {
        System.out.println(msg);
      }
    }*/
  }

  @Test
  public void testInputVerifier() {
    char p1 [] = {'f','o', 'o','p','a','s','s'};
    char p2 [] = {'f','o', 'o','p','a','s','s'};
    assertNotEquals(p1.toString(), p2.toString());
    assertTrue(Arrays.equals(p1, p2));
  }

  private void printDetail(RuleResult result) {
    if (!result.isValid()) {
      for (RuleResultDetail detail : result.getDetails()) {
        System.out.println(detail.getErrorCode());
      }
      System.out.println();
    }
  }
}
