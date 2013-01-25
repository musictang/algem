/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.algem.config;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import net.algem.TestProperties;
import net.algem.util.DataConnection;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.7.a 04/01/2013
 */
public class TestInstrumentIO {

  private DataConnection dc;
    public TestInstrumentIO() {
    }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

    @Before
    public void setUp() {
      dc = TestProperties.getDataConnection();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testTeacherInstrumentCache() throws SQLException {
      Hashtable<Integer, List<Integer>> cache = InstrumentIO.load(dc);
      assertTrue(cache != null);
      assertTrue(cache.get(16094).size() == 3);
      assertTrue(cache.get(16094).get(0) == 28);
      assertTrue(cache.get(16094).get(1) == 14);
      assertTrue(cache.get(16094).get(2) == 11);
    }
}