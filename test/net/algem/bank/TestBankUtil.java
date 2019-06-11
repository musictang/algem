/*
 * @(#)TestBankUtil.java	2.8.i 08/07/13
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
package net.algem.bank;

import java.text.ParseException;
import java.util.Vector;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import net.algem.TestProperties;
import net.algem.util.DataConnection;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.i
 * @since 2.8.i 20/06/13
 */
public class TestBankUtil
{

  private DataConnection dc;
  
  public TestBankUtil() {
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
  public void testRibToIbanConversion() {
    String defautltCountryCode = "FR";
    assertTrue(Character.digit('A', Character.MAX_RADIX) == 10);
    assertTrue(Character.digit('Z', Character.MAX_RADIX) == 35);
    String rib = "20041010126013811S03310";
    assertTrue(42 == BankUtil.getIbanKey(defautltCountryCode, rib));
    String iban = BankUtil.ribToIban(defautltCountryCode, rib);
    assertEquals("FR4220041010126013811S03310", iban);

    // exemples from wikipedia http://fr.wikipedia.org/wiki/ISO_13616
    String bbank = "BARC20658244971655";
    assertTrue(87 == BankUtil.getIbanKey("GB", bbank));

    bbank = "068999999501";
    assertTrue(43 == BankUtil.getIbanKey("BE", bbank));

    // jeu de tests CFONB
    String ribs[] = {
      "10011000201111111111U76",
      "10096180880001234567844",
      "11749000010002314670438",
      "12169000215079782901076",
      "12239000014464440100014",
      "14889000010458457031143",
      "20041000011000100W02017",
      "30001001060000A15727006",
      "3000100106H380000000737",
      "40031000010000308435E92",
      "400310000100008B499NU15",
      "40978000221214364V00169",
      "40978000221214361J00125",
      "14006000011234567890132",
      "19806000019876543210926",
      "19906000014567890123485",
      "30004000030001000365859",
      "30004008970000014729726",
      "30004000740001000056916",
      "30007000110001902133441",
      "30007000120001678943216"
    };

    int keys[] = {51, 76, 76, 76, 76, 76, 12, 80, 47, 31, 10, 13, 45, 76, 76, 76, 76, 76, 76, 76, 76};
    for (int i = 0; i < ribs.length; i++) {
      assertTrue(keys[i] == BankUtil.getIbanKey(defautltCountryCode, ribs[i]));
    }

  }
  
  @Test
  public void testRib() {
    Rib bic = new Rib(1234);
    
    String iban = "FR5110011000201111111111U76";
    bic.setIban(iban);
    bic.setRib(iban);
    assertEquals("10011", bic.getEstablishment());
    assertEquals("00020", bic.getBranch());
    assertEquals("1111111111U", bic.getAccount());
    assertEquals("76", bic.getRibKey());
    assertEquals("51", bic.getKey(iban));
    
    assertTrue(BankUtil.isIbanOk(iban));
  }
  
  @Test
  public void testMaskFormatter() throws ParseException {

    String value = "A1234B567Z";
    String mask = "A-AAAA-AAAA-A";

    javax.swing.text.MaskFormatter mf = new javax.swing.text.MaskFormatter(mask);
    mf.setValueContainsLiteralCharacters(false);// important
    String res = mf.valueToString(value);

    assertEquals("A-1234-B567-Z", res);
    JFormattedTextField jf = new JFormattedTextField(mf);
    jf.setValue(value);
    assertEquals("A1234B567Z", jf.getValue().toString());

  }
  
    /**
   * Test de ribs dont les comptes ne comportent que des chiffres.
   */
  @Test
  public void testBicWithDigits() {
    /* String b = "45499"; // code banque String g = "06048"; // code guichet String c
     * = "00027230241"; // code compte String k = "81"; // clé rib */

    String b = "30002"; // code banque
    String g = "05948"; // code guichet
    String c = "00O00391696"; // code compte le 3è caractère est un O majuscule et non un 0 !
    String k = "25"; // clé rib

    StringBuilder rib = new StringBuilder();
    rib.append(b).append(g).append(c).append(k);
    assertTrue(BankUtil.isRibOk(rib.toString()));

    // erreur code banque
    rib.replace(0, 5, "45498");
    assertFalse(rib.toString(), BankUtil.isRibOk(rib.toString()));

    rib.replace(0, 5, b);
    // erreur code guichet
    rib.replace(5, 10, "06049");
    assertFalse(rib.toString(), BankUtil.isRibOk(rib.toString()));

    rib.replace(5, 10, g);
    // erreur code compte
    rib.replace(10, 21, "00027230242");
    assertFalse(rib.toString(), BankUtil.isRibOk(rib.toString()));

    rib.replace(10, 21, c);
    // erreur clé
    rib.replace(21, 23, "82");
    assertFalse(rib.toString(), BankUtil.isRibOk(rib.toString()));

    // rib initial
    rib.replace(21, 23, k);
    assertTrue(rib.toString(), BankUtil.isRibOk(rib.toString()));
  }

  /**
   * Test de ribs comportant des lettres dans le numéro de compte.
   */
  @Test
  public void testBicWithLetters() {
    String b = "10011"; // code banque
    String g = "00020"; // code guichet
    String c = "0785039777F"; // code compte
    String k = "05"; // clé rib

    StringBuilder rib = new StringBuilder();
    rib.append(b).append(g).append(c).append(k);
    assertTrue(BankUtil.isRibOk(rib.toString()));

    // erreur code banque
    rib.replace(0, 5, "10010");
    assertFalse(rib.toString(), BankUtil.isRibOk(rib.toString()));

    rib.replace(0, 5, b);
    // erreur code guichet
    rib.replace(5, 10, "00010");
    assertFalse(rib.toString(), BankUtil.isRibOk(rib.toString()));

    rib.replace(5, 10, g);
    // erreur code compte
    rib.replace(10, 21, "0785039777E");
    assertFalse(rib.toString(), BankUtil.isRibOk(rib.toString()));

    rib.replace(10, 21, c);
    // erreur clé
    rib.replace(21, 23, "06");
    assertFalse(rib.toString(), BankUtil.isRibOk(rib.toString()));

    // rib initial
    rib.replace(21, 23, k);
    assertTrue(rib.toString(), BankUtil.isRibOk(rib.toString()));
  }

  /**
   * Test de ribs sur une sélection aléatoire de 10 ribs en base de données.
   */
  @Ignore
  public void testBic() {
    String where = " ORDER BY random() LIMIT 10";
    Vector<Rib> ribs = RibIO.find(where, dc);
    if (ribs == null || ribs.isEmpty()) {
      return;
//      throw new Exception(MessageUtil.getMessage("search.empty.list.status"));
    }
    for (Rib r : ribs) {
      assertTrue(r.toString(), BankUtil.isRibOk(r.toString()));
    }
  }
  
  @Test
  public void testFormatIban() throws ParseException {
    String iban = "FR5110011000201111111111U76";
    MaskFormatter mf = IbanField.mf;
    
    StringBuilder sb = new StringBuilder();

    assertEquals("FR51 1001 1000 2011 1111 1111 U76", mf.valueToString(iban));
    int i = 0;
    for (; i < iban.length()-3; i += 4) {
      sb.append(iban.substring(i, i+4)).append(' ');
    }
    sb.append(iban.substring(i));
    assertEquals("FR51 1001 1000 2011 1111 1111 U76",sb.toString());
  }
  
      /**
   * Vérification de tous les ribs en BD.
   */
  /* public void testAllRIBs() { Vector<Bic> ribs = RibIO.find(dc,""); if (ribs ==
   * null || ribs.isEmpty()) {
   * fail(MessageUtil.getMessage("search.empty.list.status")); } for (Rib r :
   * ribs) { String s = r.toString(); //System.out.println(s);
   * assertTrue(r.toString(),AccountUtil.isBicOk(r.toString())); }
   * } */
  
}