/*
 * @(#)TestAccountUtil.java 2.6.a 08/10/12
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
package net.algem.accounting;

import java.util.Vector;
import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.bank.Bic;
import net.algem.bank.BicIO;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TestAccountUtil
        extends TestCase
{

  private DataCache dataCache;
  private DataConnection dc;

  public TestAccountUtil(String testName) throws Exception {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dc = TestProperties.getDataConnection();
    dataCache = TestProperties.getDataCache(dc);
    dataCache.load(null);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

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

  public void testGetIntValue() {
    float montant = 28.599F;
    double res = AccountUtil.getIntValue(montant);
    assertTrue(2860 == res);

    double md = 15.0000000000001;
    res = AccountUtil.getIntValue(md);
    assertTrue(1500 == res);
  }

  /**
   * Test de ribs dont les comptes ne comportent que des chiffres.
   */
  public void testBicWithDigits() {
    /* String b = "45499"; // code banque String g = "06048"; // code guichet String c
     * = "00027230241"; // code compte String k = "81"; // clé rib */

    String b = "30002"; // code banque
    String g = "05948"; // code guichet
    String c = "00O00391696"; // code compte le 3è caractère est un O majuscule et non un 0 !
    String k = "25"; // clé rib

    StringBuilder rib = new StringBuilder();
    rib.append(b).append(g).append(c).append(k);
    assertTrue(AccountUtil.isBicOk(rib.toString()));

    // erreur code banque
    rib.replace(0, 5, "45498");
    assertFalse(rib.toString(), AccountUtil.isBicOk(rib.toString()));

    rib.replace(0, 5, b);
    // erreur code guichet
    rib.replace(5, 10, "06049");
    assertFalse(rib.toString(), AccountUtil.isBicOk(rib.toString()));

    rib.replace(5, 10, g);
    // erreur code compte
    rib.replace(10, 21, "00027230242");
    assertFalse(rib.toString(), AccountUtil.isBicOk(rib.toString()));

    rib.replace(10, 21, c);
    // erreur clé
    rib.replace(21, 23, "82");
    assertFalse(rib.toString(), AccountUtil.isBicOk(rib.toString()));

    // rib initial
    rib.replace(21, 23, k);
    assertTrue(rib.toString(), AccountUtil.isBicOk(rib.toString()));
  }

  /**
   * Test de ribs comportant des lettres dans le numéro de compte.
   */
  public void testBicWithLetters() {
    String b = "10011"; // code banque
    String g = "00020"; // code guichet
    String c = "0785039777F"; // code compte
    String k = "05"; // clé rib

    StringBuilder rib = new StringBuilder();
    rib.append(b).append(g).append(c).append(k);
    assertTrue(AccountUtil.isBicOk(rib.toString()));

    // erreur code banque
    rib.replace(0, 5, "10010");
    assertFalse(rib.toString(), AccountUtil.isBicOk(rib.toString()));

    rib.replace(0, 5, b);
    // erreur code guichet
    rib.replace(5, 10, "00010");
    assertFalse(rib.toString(), AccountUtil.isBicOk(rib.toString()));

    rib.replace(5, 10, g);
    // erreur code compte
    rib.replace(10, 21, "0785039777E");
    assertFalse(rib.toString(), AccountUtil.isBicOk(rib.toString()));

    rib.replace(10, 21, c);
    // erreur clé
    rib.replace(21, 23, "06");
    assertFalse(rib.toString(), AccountUtil.isBicOk(rib.toString()));

    // rib initial
    rib.replace(21, 23, k);
    assertTrue(rib.toString(), AccountUtil.isBicOk(rib.toString()));
  }

  /**
   * Test de ribs sur une sélection aléatoire de 10 ribs en base de données.
   */
  public void testBic() {
    String where = " ORDER BY random() LIMIT 10";
    Vector<Bic> ribs = BicIO.find(where, dc);
    if (ribs == null || ribs.isEmpty()) {
      fail(MessageUtil.getMessage("search.empty.list.status"));
    }
    for (Bic r : ribs) {
      assertTrue(r.toString(), AccountUtil.isBicOk(r.toString()));
    }
  }

  /**
   * Vérification de tous les ribs en BD.
   */
  /* public void testAllRIBs() { Vector<Bic> ribs = BicIO.find(dc,""); if (ribs ==
   * null || ribs.isEmpty()) {
   * fail(MessageUtil.getMessage("search.empty.list.status")); } for (Bic r :
   * ribs) { String s = r.toString(); //System.out.println(s);
   * assertTrue(r.toString(),AccountUtil.isBicOk(r.toString())); }
  } */
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
