/*
 * @(#)TestAccountUtil.java 2.8.i 03/07/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.accounting;

import net.algem.TestProperties;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.i
 */
public class TestAccountUtil
{

  private DataCache dataCache;
  private DataConnection dc;

  public TestAccountUtil() {
  }

  @Before
  public void setUp() throws Exception {
    dc = TestProperties.getDataConnection();
    dataCache = TestProperties.getDataCache(dc);
    dataCache.load(null);
  }

 @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Test
  public void testRound() {
    float montant = 28.599F;
    double res = AccountUtil.round(montant);
    assertTrue(28.60 == res);

    montant = 2860.5355555F;
    res = AccountUtil.round(montant);
    assertTrue(2860.54 == res);

    montant = 2860.533333F;
    res = AccountUtil.round(montant);
    assertTrue(2860.53 == res);

    double md = 15;
    res = AccountUtil.round(md);
    assertTrue(15.0 == res);

    md = 15.0000000000001;
    res = AccountUtil.round(md);
    assertTrue(15.0 == res);
  }

  @Test
  public void testGetIntValue() {
    float montant = 28.599F;
    double res = AccountUtil.getIntValue(montant);
    assertTrue(2860 == res);

    double md = 15.0000000000001;
    res = AccountUtil.getIntValue(md);
    assertTrue(1500 == res);
  }


  @Test
  public void testIsPersonalAccount() {
    Account c1 = new Account("4111111111");
    Account c2 = new Account("41100001");
    Account c3 = new Account("7110000000");
    Account c4 = new Account("C19000000000");
    Account c5 = new Account("c190000000");
    Account c6 = new Account("F190000000");

    assertTrue(AccountUtil.isPersonalAccount(c1));
    assertTrue(AccountUtil.isPersonalAccount(c2));
    assertTrue(AccountUtil.isPersonalAccount(c4));
    assertTrue(AccountUtil.isPersonalAccount(c5));
    assertFalse(AccountUtil.isPersonalAccount(c3));
    assertFalse(AccountUtil.isPersonalAccount(c6));
    assertFalse(AccountUtil.isPersonalAccount(null));
    assertFalse(AccountUtil.isPersonalAccount(new Account("")));
  }
}
