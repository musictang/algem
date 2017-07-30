/*
 * @(#)Person.java	2.15.0 30/07/2017
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
package net.algem.contact;

import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.util.model.GemModel;

/**
 * Generic model of contact.
 * A person is identified by his
 * <code>type</code> and may be a physical person, a room,
 * a bank or an establishment.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 *
 * @since 1.0a 07/07/1999
 */
public class Person
        implements GemModel, Comparable<Person>
{

  public static final short PERSON = 1;
  public static final short GROUP = 3;
  public static final short ROOM = 4;
  public static final short ESTABLISHMENT = 5;
  public static final short BANK = 6;
  public static final short ACTION = 7;
  protected static boolean nameFirst = ConfigUtil.getConf(ConfigKey.PERSON_SORT_ORDER.getKey()).equalsIgnoreCase("n");
  private static final long serialVersionUID = 4578641553339894755L;

  protected int id;
  protected short type = PERSON;
  protected String name;
  protected String firstName;
  protected String nickName;
  protected String gender;
  protected boolean imgRights;
  protected Organization organization;
  protected boolean partnerInfo;

  public Person() {
  }

  /**
   *
   * @param i id
   */
  public Person(int i) {
    this(i, "");
  }

  /**
   *
   * @param n name
   */
  public Person(String n) {
    this(0, n);
  }

  /**
   *
   * @param i id
   * @param n name
   */
  public Person(int i, String n) {
    this(i, n, "", "M");
  }

  /**
   *
   * @param i id
   * @param n name
   * @param f firstName
   * @param c civility
   */
  public Person(int i, String n, String f, String c) {
    id = i;
    name = n;
    firstName = f;
    gender = c;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Person other = (Person) obj;
    if (this.id != other.id) {
      return false;
    }
    if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
      return false;
    }
    if ((this.firstName == null) ? (other.firstName != null) : !this.firstName.equals(other.firstName)) {
      return false;
    }
    if ((this.gender == null) ? (other.gender != null) : !this.gender.equals(other.gender)) {
      return false;
    }
    if ((this.organization == null) ? (other.organization != null) : !this.organization.equals(other.organization)) {
      return false;
    }
    if ((this.nickName == null) ? (other.nickName != null) : !this.nickName.equals(other.nickName)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 23 * hash + this.id;
//    hash = 23 * hash + this.organization;
    hash = 23 * hash + (this.organization != null ? this.organization.hashCode() : 0);
    hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
    hash = 23 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
    hash = 23 * hash + (this.gender != null ? this.gender.hashCode() : 0);
    hash = 23 * hash + (this.nickName != null ? this.nickName.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    return type == PERSON ? (nameFirst ? getNameFirstname() : getFirstnameName()) : name;
  }

  public String getFirstnameName() {
    return ((firstName == null || firstName.isEmpty()) ? name : firstName + " " + name);
  }

  public String getNameFirstname() {
    return name + (firstName == null ? "" : " " + firstName);
  }

  /**
   * Abbreviation display of teacher name in plannings.
   *
   * @since 1.1e
   * @return la première lettre du prénom suivie du name (ex. B. Sagno pour Bernard Sagno)
   */
  public String getAbbrevFirstNameName() {

    String p = "";//prenom
    String n = "";//nom
    StringBuffer b;
    if (firstName != null && firstName.length() > 0) {
      p = firstName.charAt(0) + ".";
      if (firstName.contains("-")) { // si prénom contient un tiret
        p += firstName.charAt(firstName.indexOf('-') + 1) + ".";
      }
    }
    b = new StringBuffer(p);
    if (name != null && name.length() > 0) {
      n = name.charAt(0) + name.substring(1).toLowerCase();
    }
    return b.append(" ").append(n).toString();
  }


  public String getCommunName() {
    if (nickName != null) {
      return nickName.isEmpty() ? getAbbrevFirstNameName() : nickName;
    } else {
      return getAbbrevFirstNameName();
    }
  }

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public void setType(short i) {
    type = i;
  }

  public short getType() {
    return type;
  }

  @Override
  public void setId(int i) {
    id = i;
  }

  @Override
  public int getId() {
    return id;
  }

  public void setName(String s) {
    name = s;
  }

  public String getName() {
    return name;
  }

  public void setFirstName(String s) {
    firstName = s;
  }

  public String getFirstName() {
    return firstName;
  }

   public void setGender(String s) {
    gender = s;
  }

  public String getGender() {
    return gender;
  }

  public void setImgRights(boolean s) {
    imgRights = s;
  }

  public boolean hasImgRights() {
    return imgRights;
  }

  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

  public boolean isPartnerInfo() {
    return partnerInfo;
  }

  public void setPartnerInfo(boolean partnerInfo) {
    this.partnerInfo = partnerInfo;
  }

  public boolean isValid() {
    if (organization != null && organization.getId() > 0) {
      if (organization.getName() != null && organization.getName().length() > 1) {
        return true;
      }
    }
    return  name.length() > 1;
  }

  @Override
  public int compareTo(Person o) {
    return getFirstnameName().compareToIgnoreCase(o.getFirstnameName());
  }


  private boolean out(int n) {
    System.out.println("!Personne.equals " + n);
    return false;
  }

}
