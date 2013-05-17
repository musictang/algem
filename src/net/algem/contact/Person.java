/*
 * @(#)Person.java	2.8.a 11/04/13
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
package net.algem.contact;

import net.algem.util.model.GemModel;

/**
 * Generic model of contact.
 * A person is identified by his
 * <code>type</code> and may be a physical person, a room,
 * a bank or an establishment.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
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
  protected int id;
  protected short type = PERSON;
  protected String name;
  protected String firstName;
  protected String gender;
  //protected int note;
  protected boolean imgRights;
  protected String organization;

  public Person() {
  }

  public Person(int i) {
    this(i, "");
  }

  public Person(String n) {
    this(0, n);
  }

  public Person(int i, String n) {
    this(i, n, "", "M");
  }

  public Person(int i, String n, String p, String c) {
    id = i;
    name = n;
    firstName = p;
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
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 23 * hash + this.id;
    hash = 23 * hash + (this.organization != null ? this.organization.hashCode() : 0);
    hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
    hash = 23 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
    hash = 23 * hash + (this.gender != null ? this.gender.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    if (type == PERSON) {
      return getFirstnameName();
    } else {
      return name;
    }
  }

  public String getFirstnameName() {
    if (firstName == null || firstName.isEmpty()) {
      return name;
    } else {
      StringBuilder b = new StringBuilder(firstName);
      return b.append(" ").append(name).toString();
    }
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

  public String getNameFirstname() {
    StringBuilder b = new StringBuilder(name);
    return b.append(" ").append(firstName).toString();
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

  public String getIdentity() {
    return name + " " + firstName;
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

  public boolean getImgRights() {
    return imgRights;
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public boolean isValid() {
    return organization != null && !organization.isEmpty()
            ? organization.length() > 1 : name.length() > 1;
  }

  private boolean out(int n) {
    System.out.println("!Personne.equals " + n);
    return false;
  }

  @Override
  public int compareTo(Person o) {
    return getFirstnameName().compareToIgnoreCase(o.getFirstnameName());
  }
}
