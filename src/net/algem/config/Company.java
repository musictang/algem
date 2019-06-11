/*
 * @(#) Company.java Algem 2.15.0 30/07/2017
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

package net.algem.config;

import net.algem.contact.Contact;
import net.algem.contact.Organization;
import net.algem.contact.Person;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 25/07/2017
 */
public class Company {

  private String domain;
  private byte[] logo;
  private byte[] stamp;

  private Organization org;
  private Contact contact;
  private Person referent;

  public Company() {
  }

  public Organization getOrg() {
    return org;
  }

  public void setOrg(Organization org) {
    this.org = org;
  }

  public Contact getContact() {
    return contact;
  }

  public Person getReferent() {
    return referent;
  }

  public void setReferent(Person referent) {
    this.referent = referent;
  }

  public void setContact(Contact contact) {
    this.contact = contact;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public byte[] getLogo() {
    return logo;
  }

  public void setLogo(byte[] logo) {
    this.logo = logo;
  }

  public byte[] getStamp() {
    return stamp;
  }

  public void setStamp(byte[] stamp) {
    this.stamp = stamp;
  }

}
