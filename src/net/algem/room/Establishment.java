/*
 * @(#)Establishment.java	2.6.a 24/09/12
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
package net.algem.room;

import java.util.Vector;
import net.algem.contact.Address;
import net.algem.contact.Email;
import net.algem.contact.Person;
import net.algem.contact.Telephone;
import net.algem.util.model.GemModel;

/**
 * School establishment.
 * An establishment is a person of type {@link net.algem.contact.Person#ESTABLISHMENT}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class Establishment
        implements GemModel {

  private Person p;
  private Vector<Address> a;
  private Vector<Telephone> t;
  private Vector<Email> emails;

  public Establishment() {
  }

  public Establishment(String pp) {
    p = new Person(1, pp);
  }

  public Establishment(Person pp) {
    p = pp;
  }

  @Override
  public boolean equals(Object e) {
    return (e != null && e instanceof Establishment
            && p.equals(((Establishment) e).p));
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 47 * hash + (this.p != null ? this.p.hashCode() : 0);
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
    return true;
  }

}
