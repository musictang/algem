/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.algem.accounting;

import java.text.NumberFormat;
import java.util.Locale;
import net.algem.TestProperties;
import net.algem.util.DataConnection;
import net.algem.util.TextUtil;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.7.a 17/12/2013
 */
public class TestExportFormat {

  private DataConnection dc;
    public TestExportFormat() {
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
    public void testFormat() {
      //AccountExportService exportService = new ExportCiel(dc);
      NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
      nf.setGroupingUsed(false);
      nf.setMinimumFractionDigits(2);
      nf.setMaximumFractionDigits(2); 
      long m = 15020050L;
      String formatted = TextUtil.padWithLeadingSpaces(nf.format(m / 100.0), 13);
      assertEquals("    150200.50", formatted);
      m = 30015020050L;
      formatted = TextUtil.padWithLeadingSpaces(nf.format(m / 100.0), 13);
      assertEquals(" 300150200.50", formatted);
      m = 153;
      formatted = TextUtil.padWithLeadingSpaces(nf.format(m / 100.0), 13);
      assertEquals("         1.53", formatted);
    }

}