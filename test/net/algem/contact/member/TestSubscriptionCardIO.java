/*
 * @(#)TestRehearsalCardIO.java 2.6.a 08/10/12
 * 
 * Copyright (c) 1999-2011 Musiques Tangentes. All Rights Reserved.
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

import java.sql.SQLException;
import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.model.NullValueException;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
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
    RehearsalCard card = new RehearsalCard("Ziktest", 22.0F, 10, 30);
    card.setId(1);
    String query = RehearsalCardIO.getInsertQuery(card);
    String expected = "INSERT INTO " + RehearsalCardIO.TABLE + " VALUES(1,'Ziktest','22.0','10','30')";
    assertEquals(expected, query);
    card = new RehearsalCard("Ziktest", 0, 0, 0);
    card.setId(1);
    query = RehearsalCardIO.getInsertQuery(card);
    expected = "INSERT INTO " + RehearsalCardIO.TABLE + " VALUES(1,'Ziktest','0.0','0','0')";
    assertEquals(expected, query);
  }

  public void testInsertWithNullValues() throws Exception {
    RehearsalCard card = new RehearsalCard("Ziktest", 0, 0, 0);
    RehearsalCardIO.insert(card, dc);
    assertNotNull(card.getId());
    assertTrue(0.0 == card.getAmount());
    assertTrue(0 == card.getSessionsNumber());
    assertTrue(0 == card.getDuration());

    // clean up
    RehearsalCardIO.delete(card.getId(), dc);
  }

  public void testInsertWithValidData() throws Exception {
    RehearsalCard card = new RehearsalCard("Ziktest", 22.0F, 10, 60);
    RehearsalCardIO.insert(card, dc);
    assertNotNull(card.getId());
    assertTrue(22.0 == card.getAmount());
    assertTrue(10 == card.getSessionsNumber());
    assertTrue(60 == card.getDuration());

    // clean up
    RehearsalCardIO.delete(card.getId(), dc);
  }

  public void testInsertWithNullLibelle() throws SQLException {
    RehearsalCard card = new RehearsalCard(null, 22.0F, 10, 60);
    try {
      RehearsalCardIO.insert(card, dc);
      fail("SQLException should be thrown");
    } catch (NullValueException ex) {
      assertNotNull(ex.getMessage());
    }

  }

  public void testFindAll() {
  }
}
