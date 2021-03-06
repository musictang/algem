/*
 * @(#)Establishment.java	2.11.3 17/11/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
package net.algem.room;

import java.util.List;
import java.util.Objects;
import java.util.Vector;
import net.algem.contact.Address;
import net.algem.contact.Email;
import net.algem.contact.Person;
import net.algem.contact.Telephone;
import net.algem.contact.WebSite;
import net.algem.util.model.GemModel;

/**
 * School establishment.
 * An establishment is a person of type {@link net.algem.contact.Person#ESTABLISHMENT}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.3
 */
public class Establishment
        implements GemModel {

  private static final long serialVersionUID = -7929093636684679404L;

  private Person p;
  private Vector<Address> a;
  private Vector<Telephone> t;
  private Vector<Email> emails;
  private List<WebSite> sites;
  private boolean active;

  public Establishment() {
  }

  public Establishment(String pp) {
    this.p = new Person(1, pp);
  }

  public Establishment(Person pp) {
    this.p = pp;
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
    final Establishment other = (Establishment) obj;
    if (this.getPerson() == null) {
      return other.getPerson() == null;
    }
    return (other.getPerson() == null ? false : this.getPerson().getId() == other.getPerson().getId());

  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 73 * hash + (this.getPerson() == null ? 0 : this.getPerson().getId());
    //hash = 73 * hash + (this.active ? 1 : 0);
    return hash;
  }

  @Override
  public String toString() {
    return p.toString();
  }

  public Person getPerson() {
    return p;
  }

  public void setPerson(Person v) {
    p = v;
  }

  public Address getAddress() {
    if (a != null && a.size() > 0) {
      return a.elementAt(0);
    }
    return null;
  }

  public Vector<Address> getAddressAll() {
    return a;
  }

  public void setAddress(Vector<Address> v) {
    a = v;
  }

  public Vector<Telephone> getTele() {
    return t;
  }

  public void setTele(Vector<Telephone> v) {
    t = v;
  }

  public Vector<Email> getEmail() {
    return emails;
  }

  public void setEmail(Vector<Email> _emails) {
    emails = _emails;
  }

  public List<WebSite> getSites() {
    return sites;
  }

  public void setSites(List<WebSite> sites) {
    this.sites = sites;
  }

  @Override
  public void setId(int id) {
    p.setId(id);
  }

  @Override
  public int getId() {
    return p.getId();
  }

  public String getName() {
    return p.getName();
  }

  public boolean isValid() {
    return p.getName() != null && ! p.getName().isEmpty();
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

}
