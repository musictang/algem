/*
 * @(#) TestEstablishment.java Algem 2.11.3 17/11/2016
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
package net.algem.room;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.algem.TestProperties;
import net.algem.contact.Person;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.3
 * @since 2.11.3 17/11/16
 */
public class TestEstablishment {

  private DataConnection dc;
    private  DataCache dataCache;

  public TestEstablishment() {
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
    try {
      dataCache = TestProperties.getDataCache(dc);
    } catch (Exception ex) {
      Logger.getLogger(TestEstablishment.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @After
  public void tearDown() {
  }

   @Test
   public void testEquals() {

     Person p1 = new Person();
     p1.setName("E1");
     p1.setFirstName("");
     p1.setGender("");
     p1.setOrgName(null);
     p1.setImgRights(false);
     p1.setType(Person.ESTABLISHMENT);

     Person p2 = new Person();
     p2.setName("E2");
     p2.setFirstName("");
     p2.setGender("");
     p2.setOrgName(null);
     p2.setImgRights(false);
     p2.setType(Person.ESTABLISHMENT);

     Establishment e1 = new Establishment();
     e1.setPerson(p1);
     e1.setId(1);

     Establishment e2 = new Establishment();
     e2.setPerson(p2);
     e2.setId(2);

     assertNotEquals(e1, e2);
     e2.setPerson(null);
     assertNotEquals(e1, e2);
     e1.setPerson(null);
     assertEquals(e1, e2);
     e2.setPerson(p2);
     assertNotEquals(e1, e2);
     e1.setPerson(p2);
     assertEquals(e1, e2);
     e1.setPerson(p1);
     assertNotEquals(e1, e2);
   }
}
