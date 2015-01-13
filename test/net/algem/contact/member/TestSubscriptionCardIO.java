/*
 * @(#)TestRehearsalCardIO.java 2.9.2 06/01/15
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
package net.algem.contact.member;

import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 */
public class TestSubscriptionCardIO
        extends TestCase
{

  private DataCache dataCache;
  private DataConnection dc;

  public TestSubscriptionCardIO(String testName) {
    super(testName);

  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dc = TestProperties.getDataConnection();
    dataCache = TestProperties.getDataCache(dc);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testInsertQuery() throws Exception {
    RehearsalPass card = new RehearsalPass("Ziktest", 22.0F, 300);
    card.setId(1);
    String query = RehearsalPassIO.getInsertQuery(card);
    String expected = "INSERT INTO " + RehearsalPassIO.TABLE + " VALUES(1, 'Ziktest', 22.0, 30, 300)";
    assertEquals(expected, query);
    card = new RehearsalPass("Ziktest", 0, 0);
    card.setId(1);
    query = RehearsalPassIO.getInsertQuery(card);
    expected = "INSERT INTO " + RehearsalPassIO.TABLE + " VALUES(1, 'Ziktest', 0.0, 30, 0)";
    assertEquals(expected, query);
  }

  public void testInsertWithNullValues() throws Exception {
    RehearsalPass card = new RehearsalPass("Ziktest", 0, 0);
    RehearsalPassIO.insert(card, dc);
    assertNotNull(card.getId());
    assertTrue(0.0 == card.getAmount());
    assertTrue(RehearsalPass.MIN_DEFAULT == card.getMin());

    // clean up
    RehearsalPassIO.delete(card.getId(), dc);
  }

  public void testInsertWithValidData() throws Exception {
    RehearsalPass card = new RehearsalPass("Ziktest", 22.0F, 600);
    RehearsalPassIO.insert(card, dc);
    assertNotNull(card.getId());
    assertTrue(22.0 == card.getAmount());
    assertTrue(RehearsalPass.MIN_DEFAULT == card.getMin());
    // clean up
    RehearsalPassIO.delete(card.getId(), dc);
  }

  public void testFindAll() {
  }
}
