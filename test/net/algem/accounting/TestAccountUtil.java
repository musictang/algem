/*
 * @(#)TestAccountUtil.java 2.14.0 26/05/17
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
 *
 */
package net.algem.accounting;

import java.text.NumberFormat;
import java.util.Locale;
import net.algem.TestProperties;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.10
 */
public class TestAccountUtil
{

  private DataCache dataCache;
  private DataConnection dc;

  public TestAccountUtil() {
  }

  @Before
  public void setUp() throws Exception {
//    dc = TestProperties.getDataConnection();
//    dataCache = TestProperties.getDataCache(dc);
//    dataCache.load(null);
  }

 @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Ignore
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
  public void testRoundTotalFraction() {
    double f = 0.999999;
    double res = Math.rint(f);
    assertTrue(res == 1);
    f = 0.555657;
    res = Math.rint(f);
    assertTrue(res == 1);
    f = 0.495657;
    res = Math.rint(f);
    assertFalse(res == 1);
    f = 1.4933333;
    res = Math.rint(f);
    assertTrue(res == 1);
    f = 0.333333333 * 3;
    res = Math.rint(f);
    assertTrue(res == 1);
    f = 0.444444444 * 9;//3,999999996
    res = Math.rint(f);
    assertTrue(res == 4);

    double r = Math.rint(99*0.01);
    assertTrue("r = " +r, r *100 == 100);
    r = Math.rint(99/100d);
    assertTrue("r = " +r, r *100 == 100);

    r= Math.rint(14999.0*0.01);
    assertTrue("r = " +r, r*100 == 15000);
    OrderLine ol = new OrderLine();
    ol.setAmount(Math.rint(14999 * 0.01));
    assertTrue(ol.getAmount() == 15000);
  }

  @Ignore
  public void testGetIntValue() {
    float montant = 28.599F;
    double res = AccountUtil.getIntValue(montant);
    assertTrue(2860 == res);

    double md = 15.0000000000001;
    res = AccountUtil.getIntValue(md);
    assertTrue(1500 == res);

    double m = 284.03d;
    long d = Math.round(m * 100);
    long ee = 28403l;
    assertTrue("result = " +d, ee == d);
    String s = String.valueOf(d);
    assertEquals("28403", s);

    int i = new Long(ee).intValue();
    assertTrue("result = " +i, i == 28403);

    int di = (int) Math.round(m * 100);
    assertTrue("result = " +di, di == 28403);

    m = 284.02999999;
    di = (int) Math.round(m * 100);
    assertTrue("result = " +di, di == 28403);

    di = AccountUtil.getIntValue(m);
    assertTrue("result = " +di, di == 28403);

    //fails here
//    di = (int) (AccountUtil.round(m) * 100);
//    assertTrue("result = " +di, di == 28403);
  }


  @Ignore
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
  
  @Test
  /**
   *  test ISO 639-1-ISO_3166-1 sequences.
   */
  public void testBPCP() {
    NumberFormat nf1 = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-GB"));
    nf1.setMinimumFractionDigits(2);
    nf1.setMaximumFractionDigits(2);
    String res = nf1.format(123.45);
    System.out.println(res);
    assertEquals("£123.45",res);
    nf1 = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-US"));
    res = nf1.format(123.45);
    System.out.println(res);
    assertEquals("$123.45",res);
    nf1 = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("fr-FR"));
    res = nf1.format(123.45);
    System.out.println(res);
    assertEquals("123,45 €",res);
    nf1 = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("fr-NC"));//nouvelle-caledonie
    nf1.setMinimumFractionDigits(2);// force decimal
    nf1.setMaximumFractionDigits(2);
    res = nf1.format(123.45);
    System.out.println(res);
    assertEquals("123,45 XPF",res);
    nf1 = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-ES"));//espagne
    res = nf1.format(123.45);
    System.out.println(res);
    assertEquals("123,45 €",res);
    nf1 = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("de-DE"));//allemagne
    res = nf1.format(123.45);
    System.out.println(res);
    assertEquals("123,45 €",res);
    nf1 = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("uk-UA"));//ukraine
    res = nf1.format(123.45);
    System.out.println(res);
    assertEquals("123,45 грн.",res);
    nf1 = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-PT"));//portugal
    res = nf1.format(123.45);
    System.out.println(res);
    assertEquals("123,45 €",res);
    nf1 = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));//brésil
    res = nf1.format(123.45);
    System.out.println(res);
    assertEquals("R$ 123,45",res);
    nf1 = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("zh-CN"));//chine
    res = nf1.format(123.45);
    System.out.println(res);
    assertEquals("￥123.45",res);
  }
}
