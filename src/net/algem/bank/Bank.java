/*
 * @(#)Bank.java	2.9.4.13 05/11/15
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
 * Bank object model.
 * A bank is a person of type {@link net.algem.contact.Person#BANK}.
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class Bank
        implements java.io.Serializable
{

  private static final long serialVersionUID = 626018583577705700L;
  
  private String code;
  private String name;
  /** Multi branch. */
  private boolean multi = false;

  public Bank() {
  }

  public Bank(String _code, String _name) {
    code = _code;
    name = _name;
  }

  public boolean equals(Bank b) {
    return (b != null
            && code.equals(b.code)
            && name.equals(b.name)
            && multi == b.multi);
  }

  @Override
  public String toString() {
    return code + " " + name;
  }

  public boolean isMulti() {
    return multi;
  }

  public void setMulti(boolean b) {
    multi = b;
  }

  public void setCode(String s) {
    code = s;
  }

  public String getCode() {
    return code;
  }

  public void setName(String s) {
    name = s;
  }

  public String getName() {
    return name;
  }

  public boolean isValid() {
    return name.length() > 1 && code.length() == 5;
  }
  
}
