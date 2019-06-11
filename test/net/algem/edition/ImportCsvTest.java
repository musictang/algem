/*
 * @(#) ImportCsvTest.java Algem 2.13.0 29/03/2017
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
package net.algem.edition;

import net.algem.util.TextUtil;
import net.algem.util.model.TableIO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @since 2.13.0 20/03/2017
 */
public class ImportCsvTest
{

  public ImportCsvTest() {
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
  public void testComment() {
    //test comment lines in csv
    String regex1 = "^(#|\"?sep=;).*$";
    String input1 = "# un test;";
    String input2 = "\"sep=;\"";
    String input3 = "sep=;";
    assertTrue(input1.matches(regex1));
    assertTrue(input2.matches(regex1));
    assertTrue(input3.matches(regex1));
  }

  @Test
  public void testNullString() {
    String s = null;
    String res = TableIO.escape(s);
    assertNull(res);
    res = escape(s);
    assertNull(res);
  }

  private String escape(String s) {
    return s;
  }

  @Test
  public void testDiacritics() {
    String s = "Gaëlle";
    String res = TextUtil.stripDiacritics(s);
    System.out.println(res);
    assertNotEquals(s, res);
  }

}
