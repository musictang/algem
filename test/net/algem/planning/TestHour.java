/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.algem.planning;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.7.a 11/06/2013
 */
public class TestHour {

    public TestHour() {
    }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void testParseAndDisplayTime() {
      Hour h = new Hour(360);
      
      assertTrue(6 == h.getHour());
      assertEquals("06:00", h.toString());
      
      String [] hs = {"25:00:00", "40:00:00"};
      int total = 0;
      h = new Hour();
      for (String hh : hs) {
         h.set(hh);
         total += h.toMinutes();
         
      }
      assertTrue(total == 3900);
//      Hour t = new Hour(total);
//      System.out.print(t.toString() + " " + t.toMinutes());
      assertTrue(65 == total/60);
      
      String s = "09:45:00";
      int hh = Integer.parseInt(s.substring(0, 2));
      int hm = Integer.parseInt(s.substring(3, 5));
      
      assertTrue(9 == hh);
      assertTrue(45 == hm);
      
      s = "25:30:00";
      h.set(s);
      assertEquals("25:30", h.toString());
      s = "250:30:00";
      int idx = s.indexOf(":");
      hh = Integer.parseInt(s.substring(0, idx));
      hm = Integer.parseInt(s.substring(idx +1 , idx + 3));
      assertEquals("250:30", hh + ":" + hm);
    }

}