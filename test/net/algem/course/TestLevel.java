/*
 * @(#)TestLevel.java 2.7.a 22/11/12
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
package net.algem.course;

import java.sql.SQLException;
import net.algem.TestProperties;
import net.algem.config.GemParam;
import net.algem.planning.ActionService;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.5.a 27/06/12
 */
public class TestLevel {
  
  private DataConnection dc;
  private DataCache dataCache;
  private ActionService service;
  
  public TestLevel() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
    
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }
  
  @Before
  public void setUp() throws Exception {
    dc = TestProperties.getDataConnection();
    dataCache = TestProperties.getDataCache(dc);
    dataCache.load(null);
    service = new ActionService(dataCache);
  }
  
  @After
  public void tearDown() {
  }

  @Test
  public void verify() throws SQLException {
    /* Un niveau avec id != 0 et code "-" doit exister en base de données */
    GemParam n = new GemParam(0, "-", "Aucun");

    assertTrue(service.verifyLevel(n) == null);
    /* Un niveau avec code "D" doit exister en base de données */
    n.setCode("D");
    assertTrue(service.verifyLevel(n) != null);
    /* Un niveau avec code "M" doit exister en base de données */
    n.setCode("M");
    assertTrue(service.verifyLevel(n) != null);
    n.setCode("Z");
    assertTrue(service.verifyLevel(n) == null);
  }
  
  @Test
  public void testAlphaNum() {
    String regex = "^\\p{Alnum}$";
    String m = "0";
    assertTrue(m.matches(regex));
    m = "D";
    assertTrue(m.matches(regex));
    m = "01az2";
    assertFalse(m.matches(regex));
    m = ".";
    assertFalse(m.matches(regex));
    m = "_";
    assertFalse(m.matches(regex));
    assertTrue(m.matches("^\\w+$"));
  }
}
