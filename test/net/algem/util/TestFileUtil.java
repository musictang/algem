/*
 * @(#)TestFileUtil.java 2.9.3.2 13/03/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

import java.util.regex.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 */
public class TestFileUtil
{
  
  public TestFileUtil() {
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
  public void testFileMatching() {
    int id = 322;

    String file = "groupe 322.txt";
    String regex = "^(.*[^0-9])?" + id + "[ ]?\\..*$";
    Pattern p = Pattern.compile(regex);
    assertTrue(file.matches(regex));
    assertTrue(p.matcher(file).matches());

    file = "groupe 22.txt";
    assertFalse(file.matches(regex));
    assertFalse(p.matcher(file).matches());

    file = "groupe 322";
    assertFalse(file.matches(regex));
    assertFalse(p.matcher(file).matches());
    
    file = "groupe 322.txt";
    assertTrue(file.matches(regex));
    assertTrue(p.matcher(file).matches());

    file = "groupe 322 .txt";
    assertTrue(file.matches(regex));
    assertTrue(p.matcher(file).matches());

    file = "groupe 2.txt";
    assertFalse(file.matches(regex));
    assertFalse(p.matcher(file).matches());
    
    file = "groupe 0322.txt";
    assertFalse(file.matches(regex));
    assertFalse(p.matcher(file).matches());
    
    file = "0322.txt";
    assertFalse(file.matches(regex));
    assertFalse(p.matcher(file).matches());
    
    file = "1322.txt";
    assertFalse(file.matches(regex));
    assertFalse(p.matcher(file).matches());
    
    file = "22.txt";
    assertFalse(file.matches(regex));
    assertFalse(p.matcher(file).matches());
    
    file = "2.txt";
    assertFalse(file.matches(regex));
    assertFalse(p.matcher(file).matches());
    
    file = "322 bio.txt";
    assertFalse(file.matches(regex));
    assertFalse(p.matcher(file).matches());
    
    file = "322.txt";
    assertTrue(file.matches(regex));
    assertTrue(p.matcher(file).matches());
  }
}
