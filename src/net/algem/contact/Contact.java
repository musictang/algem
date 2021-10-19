/*
 * @(#)Contact.java 2.15.0 26/07/2017
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

import java.util.ArrayList;
import java.util.List;

/**
 * Contact model.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 1.0a 07/07/1999
 */
public class Contact
        extends Person
        implements Cloneable
{

  private static final long serialVersionUID = 4651926516240269103L;

  private boolean complete;
  private List<Address> addresses;
  private List<Telephone> telephones;
  private List<Email> emails;
  private List<WebSite> sites;

  public Contact() {

  }

  public Contact(Person pp) {
    id = pp.getId();
    firstName = pp.getFirstName();
    name = pp.getName();
    gender = pp.getGender();
    type = pp.getType();
    //note = pp.getNote();
    imgRights = pp.hasImgRights();
    partnerInfo = pp.isPartnerInfo();
    organization = pp.getOrganization();
    nickName = pp.getNickName();
  }

  public Address getAddress() {
    if (addresses != null && addresses.size() > 0) {
      return (Address) addresses.get(0);
    } else {
      return null;
    }
  }

  public List<Address> getAddressAll() {
    if (addresses != null && addresses.isEmpty()) {
      return null;
    }
    return addresses;
  }

  public void setAddress(List<Address> v) {
    addresses = v;
    for (int i = 0; addresses != null && i < addresses.size(); i++) {
      Address adr = (Address) addresses.get(i);
      adr.setId(getId());
    }
  }

  public void setAddress(Address adr) {
    if (adr != null) {
      adr.setId(getId());
      addresses = new ArrayList<>();
      addresses.add(adr);
    } else {
      addresses = null;
    }
  }

  public List<Telephone> getTele() {
    if (telephones != null && telephones.size() > 0) {
      return telephones;
    }
    return null;
  }

  public void setTele(List<Telephone> v) {
    telephones = v;
    for (int i = 0; telephones != null && i < telephones.size(); i++) {
      Telephone tel = (Telephone) telephones.get(i);
      tel.setIdper(getId());
    }
  }

  public void addTele(Telephone t) {
    if (telephones == null) {
      telephones = new ArrayList<>();
    }

    t.setIdper(getId());
    telephones.add(t);
  }

  public void setEmail(List<Email> ve) {
    emails = ve;
    if (emails != null) {
      for (Email e : emails) {
        e.setIdper(getId());
      }
    }
  }

  public List<Email> getEmail() {
    if (emails != null && emails.size() > 0) {
      return emails;
    } else {
      return null;
    }
  }

  public void setWebSites(List<WebSite> sites) {
    this.sites = sites;
  }

  public List<WebSite> getSites() {
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
    // superclass Person
    if (!super.equals(o)) {
      return out(2);
    }
    Contact c = (Contact) o;

    if (hasImgRights() != c.hasImgRights()) {
      return out(3);
    }

    if (isPartnerInfo() != c.isPartnerInfo()) {
      return out(4);
    }

    if (!telsEqual(c)) {
      return out(5);
    }

    if (!emailsEqual(c)) {
      return out(6);
    }

    if (!addressesEqual(c)) {
      return out(7);
    }

    if (!sitesEqual(sites, c.getSites())) {
      return out(8);
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

    if (telephones == null) {
        if (c.getTele() == null)
            return true;
        else if (c.getTele().isEmpty())
            return true;
        else
            return false;
    }            
    if (telephones.isEmpty() && c.getTele().isEmpty()) {
      return true;
    }
    if (telephones.size() != c.getTele().size()) {
      return false;
    }
    for (int i = 0; i < telephones.size(); i++) {
      if (!telephones.get(i).equals(c.getTele().get(i))) {
        return false;
      }
    }
    return true;
  }

  public boolean emailsEqual(Contact c) {

    if (emails == null) {
        if(c.getEmail() == null)
            return true;
        else if (c.getEmail().isEmpty())
            return true;
        else
            return false;
    }
    if (emails.isEmpty() && c.getEmail().isEmpty()) {
      return true;
    }
    if (emails.size() != c.getEmail().size()) {
      return false;
    }
    for (int i = 0; i < emails.size(); i++) {
      if (!emails.get(i).equals(c.getEmail().get(i))) {
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
          if (!addresses.get(i).equals(c.getAddressAll().get(i))) {
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

  public static boolean sitesEqual(List<WebSite> sites, List<WebSite> orig) {
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
          if (!sites.get(i).equiv(orig.get(i))) {
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
      List<Telephone> v = new ArrayList<>();
      for (int i = 0, size = getTele().size(); i < size; i++) {
        v.add((Telephone) getTele().get(i).clone());
      }
      c.setTele(v);
    }
    if (getEmail() != null && getEmail().size() > 0) {
      List<Email> ve = new ArrayList<>();
      for (int i = 0, size = getEmail().size(); i < size; i++) {
        ve.add((Email) getEmail().get(i).clone());
      }
      c.setEmail(ve);
    }
    if (getSites() != null && getSites().size() > 0) {
      List<WebSite> vs = new ArrayList<>();
      for (int i = 0, size = getSites().size(); i < size; i++) {
        vs.add((WebSite) getSites().get(i).clone());
      }
      c.setWebSites(vs);
    }
    return c;
  }

}
