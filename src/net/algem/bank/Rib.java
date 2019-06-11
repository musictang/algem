/*
 * @(#)Rib.java	2.9.4.13 05/11/15
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
package net.algem.bank;

/**
 * Bank Identifier Code  (RIB in french).
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 1.0a 07/07/1999
 */
public class Rib
        implements java.io.Serializable
{
  private static final long serialVersionUID = -6264160832031981319L;
  
  public static String DEFAULT_COUNTRY_CODE = "FR";

  /** Payer id. */
  private int idper;
  
  /** BBAN. Basic Bank Account Number.
    * France et Monaco (23 caractères) Format : BBBBBGGGGGCCCCCCCCCCCKK
    * B = code banque (5 chiffres),
    * G = code guichet (5 chiffres),
    * C = numéro de compte (11 chiffres et/ou lettres),
    * K = clé RIB (2 chiffres entre 01 et 97)
    * Belgique (12 chiffres) Format : BBBCCCCCCCKK
    * KK = BBBCCCCCCC modulo 97
    * Allemagne (18 chiffres) Format BBBBBBBBCCCCCCCCCC
   */
  private String establishment;
  private String branch;
  private String account;
  /** Rib key check. */
  private String ribKey;
  
  /** IBAN : International Bank Account Number. */
  private String iban;
  
  /** 2-character country code. */
  private String countryCode;

  /** Branch id. */
  private int branchId;

  public Rib(int _id) {
    idper = _id;
    countryCode = DEFAULT_COUNTRY_CODE;
  }

  @Override
  public String toString() {
    return establishment + branch + account + ribKey;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Rib other = (Rib) obj;
    if ((this.establishment == null) ? (other.establishment != null) : !this.establishment.equals(other.establishment)) {
      return false;
    }
    if ((this.branch == null) ? (other.branch != null) : !this.branch.equals(other.branch)) {
      return false;
    }
    if ((this.account == null) ? (other.account != null) : !this.account.equals(other.account)) {
      return false;
    }
    if ((this.ribKey == null) ? (other.ribKey != null) : !this.ribKey.equals(other.ribKey)) {
      return false;
    }
    if ((this.iban == null) ? (other.iban != null) : !this.iban.equals(other.iban)) {
      return false;
    }
    if (this.branchId != other.branchId) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 41 * hash + (this.establishment != null ? this.establishment.hashCode() : 0);
    hash = 41 * hash + (this.branch != null ? this.branch.hashCode() : 0);
    hash = 41 * hash + (this.account != null ? this.account.hashCode() : 0);
    hash = 41 * hash + (this.ribKey != null ? this.ribKey.hashCode() : 0);
    hash = 41 * hash + (this.iban != null ? this.iban.hashCode() : 0);
    hash = 41 * hash + this.branchId;
    return hash;
  }

  public void setId(int i) {
    idper = i;
  }

  public int getId() {
    return idper;
  }

  public void setEstablishment(String s) {
    establishment = s.trim();
  }

  public String getEstablishment() {
    return establishment;
  }

  public void setBranch(String s) {
    branch = s.trim();
  }

  public String getBranch() {
    return branch;
  }

  /**
   * Sets account number.
   * @param s 
   */
  public void setAccount(String s) {
    account = s.trim();
  }

  /**
   * Gets account number
   * @return a string
   */
  public String getAccount() {
    return account;
  }

  /**
   * Sets the rib's key.
   * @param s 
   */
  public void setRibKey(String s) {
    ribKey = s.trim();
  }

  /**
   * Gets rib's key.
   * @return a 2-character code
   */
  public String getRibKey() {
    return ribKey;
  }

  /**
   * Sets branch id.
   * @param i 
   */
  public void setBranchId(int i) {
    branchId = i;
  }

  /**
   * Gets branch id.
   * @return an id
   */
  public int getBranchId() {
    return branchId;
  }

  /**
   * Sets IBAN from one string.
   * @param iban 
   */
  public void setIban(String iban) {
    this.iban = iban;
  }
  
  /**
   * Sets iban from country code, key check and bban information.
   * @param countryCode a 2-character country code (UPPERCASE - ex. : FR)
   * @param key a 2-character code
   * @param bban basic bank account number (depending on the country) 
   */
  public void setIban(String countryCode, String key, String bban) {
    this.iban = countryCode + key + bban;
  }
  
  /**
   * Gets IBAN country code.
   * @param iban
   * @return the 2-characters country code
   */
  public String getCountryCode(String iban) {
    return iban.substring(0,2);
  }
  
  /**
   * Gets IBAN code check.
   * @param iban
   * @return the 2-characters key check 
   */
  public String getKey(String iban) {
    return iban.substring(2,4);
  }
  
  /**
   * Gets bban code.
   * @return the bbank part of iban
   */
  public String getBban() {
    return iban.substring(4);
  }

  /**
   * Sets rib from IBAN information.
   * @param bban 
   */
  void setRib(String iban) {
    if (countryCode.equals(DEFAULT_COUNTRY_CODE)) {
      establishment = iban.substring(4, 9);
      branch = iban.substring(9, 14);
      account = iban.substring(14, 25);
      ribKey = iban.substring(25);
    }
  }

  /**
   * Gets IBAN code.
   * @return a string code
   */
  public String getIban() {
    return iban;
  }
  
  /**
   * Checks the length of french rib.
   * @return true if length of each part is correct
   */
  public boolean hasCorrectLength() {
    // TODO EG menage sql rib compte à blanc
    return establishment.length() == 5
            && branch.length() == 5
            && account.length() == 11
            && ribKey.length() == 2;
            
  }

  /**
   * Checks if french rib is empty.
   * @return true if all parts of the rib are empty
   */
  public boolean isEmpty() {
    return establishment.length() == 0
            && branch.length() == 0
            && account.length() == 0
            && ribKey.length() == 0;
  }
  
}
