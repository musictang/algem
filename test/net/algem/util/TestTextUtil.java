/*
 * @(#) TestTextUtil.java Algem 2.15.5 07/11/17
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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Locale;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.5
 * @since 2.13.3 17/05/17
 */
public class TestTextUtil {

  public TestTextUtil() {
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
  public void testTruncate() {
    String s = null;
    assertTrue(TextUtil.truncate(s, 50) == null);

    s = "";
    assertTrue(TextUtil.truncate(s, 50).isEmpty());

  }

  @Test
  public void testCrop() {
    String s = null;

    BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    g.setFont(new Font(Font.SERIF, Font.PLAIN, 9));

    assertTrue(TextUtil.crop(s, g, 50) == null);

    s = "";
    assertTrue(TextUtil.crop(s, g, 50).isEmpty());

    s = "Lorem ipsum dolor sit amet, consectetur adipiscing elit";
    int length = s.length();
    String cropped = TextUtil.crop(s, g, 50);
    assertTrue(cropped != null);
    assertTrue(cropped.length() < length);
    assertTrue(cropped.charAt(cropped.length() - 1) == '.');
  }

  @Test
  public void replaceInsensitive() {
    String path = "myfile.TXT";
    if (path.toLowerCase().endsWith(".txt")) {
      path = path.replaceAll("(?i)\\.txt", ".pnm"); // insensitive
    }
    System.out.println(path);
    assertEquals(path, "myfile.pnm");
  }
  
  @Test
  public void testStringFormat() {
    String e = String.format(Locale.FRANCE, "%8.2f", 11.5d);
    //System.out.printf(e);
    assertEquals("   11,50", e);
  }
}
