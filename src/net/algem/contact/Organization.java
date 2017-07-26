/*
 * @(#) Organization.java Algem 2.15.0 26/07/2017
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
 */
package net.algem.contact;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 25/07/2017
 */
public class Organization {

  private int id;
  private String name;
  private String companyName;
  private String siret;
  private String nafCode;

    /**
   * Professional training code.
   * Code formation professionnelle.
   */
  private String fpCode;

  /**
   * Intra-community VAT code.
   * Code TVA intra-communautaire.
   * In France, it is composed of the letters FR, completed by a two-digits (or two-letters) key
   * assigned by the tax office from the place of exercise of the company
   * It ends by the SIREN 9-digits number.
   * VAT key = [ 12 + 3 * ( SIREN modulo 97 ) ] modulo 97
   * Pour la France, il est composé des lettres FR, complétées d'une clé de deux chiffres
   * ou lettres attribuée par le centre des impôts du lieu d'exercice de l'entreprise,
   * et du numéro SIREN à 9 chiffres. Par exemple pour l'entreprise déjà citée : FR 83 404 833 048.
   * La clé française suit la règle suivante :
   * Clé TVA = [ 12 + 3 * ( SIREN modulo 97 ) ] modulo 97
   */
  private String vatCode;

  public Organization() {
  }

  public Organization(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getSiret() {
    return siret;
  }

  public void setSiret(String siret) {
    this.siret = siret;
  }

  public String getNafCode() {
    return nafCode;
  }

  public void setNafCode(String nafCode) {
    this.nafCode = nafCode;
  }

  public String getFpCode() {
    return fpCode;
  }

  public void setFpCode(String fpCode) {
    this.fpCode = fpCode;
  }

  public String getVatCode() {
    return vatCode;
  }

  public void setVatCode(String vatCode) {
    this.vatCode = vatCode;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 89 * hash + this.id;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Organization other = (Organization) obj;
    if (this.id != other.id) {
      return false;
    }
    return true;
  }

}
