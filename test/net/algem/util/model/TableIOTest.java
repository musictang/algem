/*
 * @(#)TableIOTest.java	2.9.2.1 20/02/15
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

package net.algem.util.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JTextField;
import net.algem.TestProperties;
import net.algem.util.DataConnection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2.1
 * @since 2.9.1 12/11/14
 */
public class TableIOTest {

  private DataConnection dc;
  public TableIOTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
    dc = TestProperties.getDataConnection();
    
  }

  @After
  public void tearDown() {
  }

  /**
   * Test of escape method, of class TableIO.
   */
  @Ignore
  public void testEscape() {
    fail("The test case is a prototype.");
  }

  /**
   * Test of unEscape method, of class TableIO.
   * @throws java.sql.SQLException
   */
  @Test
  public void testUnEscape() throws SQLException {
    JTextField tx = new JTextField("c:\\Algem\\Journaux");
    String s = tx.getText();
    String se = TableIO.escape(s);
    String query = "UPDATE config SET valeur = '" + se + "' WHERE clef = 'LogPath'";
    dc.executeUpdate(query);
    query = "SELECT valeur FROM config WHERE clef = 'LogPath'";
    ResultSet rs = dc.executeQuery(query);
    String e = "";
    while(rs.next()) {
      e = rs.getString(1); 
    }
    assertEquals("c:\\Algem\\Journaux", e);
    s = "Un' deux ' trois";
    se = TableIO.escape(s);
    query = "UPDATE config SET valeur = '" + se + "' WHERE clef = 'LogPath'";
    dc.executeUpdate(query);
    query = "SELECT valeur FROM config WHERE clef = 'LogPath'";
    rs = dc.executeQuery(query);
    e = "";
    while(rs.next()) {
      e = rs.getString(1); 
    }
    assertEquals("Un' deux ' trois", e);
  }

  /**
   * Test of normalize method, of class TableIO.
   */
  @Ignore
  public void testNormalize() {
    System.out.println("normalize");
    String s = "";
    String expResult = "";
    String result = TableIO.normalize(s);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of nextId method, of class TableIO.
   */
  @Ignore
  public void testNextId() throws Exception {
    System.out.println("nextId");
    String seq_name = "";
    DataConnection dc = null;
    int expResult = 0;
    int result = TableIO.nextId(seq_name, dc);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getColumnNames method, of class TableIO.
   */
  @Ignore
  public void testGetColumnNames() throws Exception {
    System.out.println("getColumnNames");
    String tableName = "";
    DataConnection dc = null;
    String[] expResult = null;
    String[] result = TableIO.getColumnNames(tableName, dc);
    assertArrayEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

}
