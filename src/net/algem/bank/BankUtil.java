/*
 * @(#)BankUtil.java	2.8.i 05/07/13
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

import java.math.BigDecimal;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.i
 * @since 2.8.i 20/06/13
 */
public class BankUtil {
   /**
   * Checking BIC.
   * @link  http://fr.wikipedia.org/wiki/Relev%C3%A9_d%27identit%C3%A9_bancaire#Algorithme_de_v.C3.A9rification_en_Java
   *
   * @param rib on 23 digits
   * @return true if rib % 97 = 0
   */
  public static boolean isRibOk(String rib) {
    StringBuilder extendedRib = new StringBuilder(rib.length());
    for (char currentChar : rib.toCharArray()) {
      //Works on base 36 (26 lettres + 10 chiffres)
      int currentCharValue = Character.digit(currentChar, Character.MAX_RADIX);
      //Convert character to simple digit
      extendedRib.append(currentCharValue < 10 ? currentCharValue : (currentCharValue + (int) StrictMath.pow(2, (currentCharValue - 10) / 9)) % 10);
    }

    return new BigDecimal(extendedRib.toString()).remainder(new BigDecimal(97)).intValue() == 0;
  }

  public static String ribToIban(String countryCode, String rib) {
    int key = getIbanKey(countryCode, rib);
    return countryCode + key + rib;
  }
  
  public static String ribToIban(String rib) {
    int key = getIbanKey(Rib.DEFAULT_COUNTRY_CODE, rib);
    return Rib.DEFAULT_COUNTRY_CODE + key + rib;
  }
  
  public static int getIbanKey(String countryCode, String rib) {
    
    StringBuilder tmpiban = new StringBuilder();
    String toConvert = rib + countryCode + "00";
    
    for (char currentChar : toConvert.toCharArray()) {
      tmpiban.append(Character.digit(currentChar, Character.MAX_RADIX));
    }
    BigDecimal k = new BigDecimal(tmpiban.toString()).remainder(new BigDecimal(97));
    return 98 - k.intValue();
  }
  
  public static boolean isIbanOk(String iban) {
    if (iban == null || iban.isEmpty()) {
      return true;
    }
    String currentKey = iban.substring(2,4);
    String bban = iban.substring(4);
    String ccode = iban.substring(0,2);
    int calculatedKey = getIbanKey(ccode, bban);
    
    return calculatedKey == Integer.parseInt(currentKey);   
  }
  
  public static boolean isBicOk(String bic) {
    int length = bic.length();
    if (length < 0 || length < 8 || length > 11) {
      return false;
    }
    char [] cars = bic.toCharArray();
    for (int i = 0 ; i < 6; i++) {
      if (!Character.isLetter(cars[i])) {
        return false;
      }
    }
    for (int j = 6 ; j < cars.length ; j++) {
      if (!Character.isLetterOrDigit(cars[j])) {
        return false;
      }
    }
    return true;
  }
  

}
