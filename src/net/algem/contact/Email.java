/*
 * @(#)Email.java	2.9.4.13 05/11/15
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
package net.algem.contact;

/**
 * Email model.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class Email
        implements java.io.Serializable, Cloneable
{

  private static final long serialVersionUID = -5025699614872440903L;
  
  private int idper;
  private String email;
  private boolean archive;
  private int idx;

  public Email() {
  }

  public Email(String _email, boolean _archive) {
    email = _email;
    archive = _archive;
  }

  public Email(int _idper, String _email) {
    idper = _idper;
    email = _email;
  }

  public void setIdx(int ix) {
    idx = ix;
  }

  public int getIdx() {
    return idx;
  }

  public void setIdper(int i) {
    idper = i;
  }

  public int getIdper() {
    return idper;
  }

  @Override
  public String toString() {
    return idper + " " + email;
  }

  public void setEmail(String s) {
    email = s;
  }

  public String getEmail() {
    if (email != null) {
      return email.trim();
    }
    return null;
  }

  public boolean isValid() {
    return email.length() > 1;
  }

  /**
   * 
   * @param arch
   */
  public void setArchive(boolean arch) {
    archive = arch;
  }

  /**
   *
   * @return true si archive, false sinon
   * @since 2.0jf
   */
  public boolean isArchive() {
    return archive;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 11 * hash + this.idper;
    hash = 11 * hash + (this.email != null ? this.email.hashCode() : 0);
    hash = 11 * hash + (this.archive ? 1 : 0);
    return hash;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Email) {
      return equals((Email) o);
    }
    return false;
  }

  public boolean equals(Email n) {
    return getIdper() == n.getIdper() && getEmail().equals(n.getEmail()) && isArchive() == n.isArchive();
  }
  
}
