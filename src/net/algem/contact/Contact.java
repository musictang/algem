/*
 * @(#)Contact.java 2.7.a 16/01/13
 *
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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

import java.util.Vector;

/**
 * Contact model.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class Contact
        extends Person
        implements Cloneable
{

  private boolean complete;
  private Vector<Address> addresses;
  private Vector<Telephone> telephones;
  private Vector<Email> emails;
  private Vector<WebSite> sites;

  public Contact() {

  }

  public Contact(Person pp) {
    this();
    id = pp.getId();
    firstName = pp.getFirstName();
    name = pp.getName();
    civility = pp.getCivility();
    type = pp.getType();
    //note = pp.getNote();
    imgRights = pp.getImgRights();
  }

  public Address getAddress() {
    if (addresses != null && addresses.size() > 0) {
      return (Address) addresses.elementAt(0);
    } else {
      return null;
    }
  }

  public Vector<Address> getAddressAll() {
    if (addresses != null && addresses.isEmpty()) {
      return null;
    }
    return addresses;
  }

  public void setAddress(Vector<Address> v) {
    addresses = v;
    for (int i = 0; addresses != null && i < addresses.size(); i++) {
      Address adr = (Address) addresses.elementAt(i);
      adr.setId(getId());
    }
  }

  public void setAddress(Address adr) {
    if (adr != null) {
      adr.setId(getId());
      addresses = new Vector();
      addresses.addElement(adr);
    } else {
      addresses = null;
    }
  }

  public Vector<Telephone> getTele() {
    if (telephones != null && telephones.size() > 0) {
      return telephones;
    }
    return null;
  }

  public void setTele(Vector<Telephone> v) {
    telephones = v;
    for (int i = 0; telephones != null && i < telephones.size(); i++) {
      Telephone tel = (Telephone) telephones.elementAt(i);
      tel.setIdper(getId());
    }
  }

  public void addTele(Telephone t) {
    if (telephones == null) {
      telephones = new Vector();
    }

    t.setIdper(getId());
    telephones.addElement(t);
  }

  public void setEmail(Vector<Email> ve) {
    emails = ve;
    if (emails != null) {
      for (Email e : emails) {
        e.setIdper(getId());
      }
    }
  }

  public Vector<Email> getEmail() {
    if (emails != null && emails.size() > 0) {
      return emails;
    } else {
      return null;
    }
  }

  public void setWebSites(Vector<WebSite> sites) {
    this.sites = sites;
  }

  public Vector<WebSite> getSites() {
    if (sites != null && sites.size() > 0) {
      return sites;
    } else {
      return null;
    }
  }

  public void setComplete(boolean b) {
    complete = b;
  }

  public boolean isComplete() {
    return complete;
  }

  // DEBUG
  public boolean out(int test) {
//    System.out.println("Contact.equiv sortie:" + test);
    return false;
  }

  @Override
  public boolean equals(Object o) {

    if (o == null || !(o instanceof Contact)) {
      return out(1);
    }
    // superclass Person : comparison id, name, firstName, civility
    if (!super.equals(o)) {
      return out(2);
    }
    Contact c = (Contact) o;

    if (getImgRights() != c.getImgRights()) {
      return out(3);
    }

    if (!telsEqual(c)) {
      return out(4);
    }

    if (!emailsEqual(c)) {
      return out(5);
    }

    if (!addressesEqual(c)) {
      return out(6);
    }

    if (!sitesEqual(sites, c.getSites())) {
      return out(7);
    }
    
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + (this.addresses != null ? this.addresses.hashCode() : 0);
    hash = 29 * hash + (this.telephones != null ? this.telephones.hashCode() : 0);
    hash = 29 * hash + (this.emails != null ? this.emails.hashCode() : 0);
    hash = 29 * hash + (this.sites != null ? this.sites.hashCode() : 0);
    return hash;
  }

  public boolean telsEqual(Contact c) {

    if ((telephones == null || telephones.isEmpty()) && c.getTele() == null) {
      return true;
    }
    if (telephones == null && c.getTele() != null || (telephones != null && c.getTele() == null)) {
      return false;
    }
    if (telephones.size() != c.getTele().size()) {
      return false;
    }
    for (int i = 0; i < telephones.size(); i++) {
      if (!telephones.elementAt(i).equals(c.getTele().elementAt(i))) {
        return false;
      }
    }
    return true;
  }

  public boolean emailsEqual(Contact c) {

    if ((emails == null || emails.isEmpty()) && c.getEmail() == null) {
      return true;
    }
    if (emails == null && c.getEmail() != null || emails != null && c.getEmail() == null) {
      return false;
    }
    if (emails.size() != c.getEmail().size()) {
      return false;
    }
    for (int i = 0; i < emails.size(); i++) {
      if (!emails.elementAt(i).equals(c.getEmail().elementAt(i))) {
        return false;
      }
    }
    return true;

  }

  public boolean addressesEqual(Contact c) {
    if (addresses != null) {
      if (c.getAddressAll() == null) {
        if (addresses.size() > 0) {
          return false;
        }
      } else {
        if (addresses.size() != c.getAddressAll().size()) {
          return false;
        }
        for (int i = 0; i < addresses.size(); i++) {
          if (!addresses.elementAt(i).equals(c.getAddressAll().elementAt(i))) {
            return false;
          }
        }
      }
    } else { // si l'adresse de oldcontact n'est pas nulle
      if (c.getAddressAll() != null && c.getAddressAll().size() > 0) {
        return false;
      }
    }
    return true;
  }

  public static boolean sitesEqual(Vector<WebSite> sites, Vector<WebSite> orig) {
    if (sites != null) {
      if (orig == null) {
        if (sites.size() > 0) {
          // un ou plusieurs sites ont été créés pour le contact
          return false;
        }
      } else {
        // le nombre de sites diffère
        if (sites.size() != orig.size()) {
          return false;
        }
        for (int i = 0; i < sites.size(); i++) {
          if (!sites.elementAt(i).equiv(orig.elementAt(i))) {
            // un ou plusieurs sites ont été modifiés
            return false;
          }
        }
      }
    } else {
      if (orig != null && orig.size() > 0) {
        return false;
      }
    }
    return true;

  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    Contact c = (Contact) super.clone();
    c.setAddress((Address) getAddress().clone());
    if (getTele() != null && getTele().size() > 0) {
      Vector<Telephone> v = new Vector<Telephone>();
      for (int i = 0, size = getTele().size(); i < size; i++) {
        v.add((Telephone) getTele().elementAt(i).clone());
      }
      c.setTele(v);
    }
    if (getEmail() != null && getEmail().size() > 0) {
      Vector<Email> ve = new Vector<Email>();
      for (int i = 0, size = getEmail().size(); i < size; i++) {
        ve.add((Email) getEmail().elementAt(i).clone());
      }
      c.setEmail(ve);
    }
    if (getSites() != null && getSites().size() > 0) {
      Vector<WebSite> vs = new Vector<WebSite>();
      for (int i = 0, size = getSites().size(); i < size; i++) {
        vs.add((WebSite) getSites().elementAt(i).clone());
      }
      c.setWebSites(vs);
    }
    return c;
  }
}
