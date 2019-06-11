/*
 * @(#)UserException.java	2.11.0 27/09/16
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
package net.algem.security;

/**
 * User exception.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 * @since 2.8.p 30/10/13
 */
public class UserException
        extends Exception
{

  private String type;

  /**
   * Creates a new instance of
   * <code>UserException</code> without detail message.
   */
  public UserException() {
  }

  /**
   * Constructs an instance of
   * <code>UserException</code> with the specified detail message.
   *
   * @param msg the detail message.
   * @param type
   */
  public UserException(String msg, String type) {
    super(msg);
    this.type = type;
  }

  public String getMessageKey() {
    switch (type) {
      case "CREATION":
        return "user.creation.failure";
      case "ENCRYPTION":
        return "user.pass.creation.failure";
      case "MODIFICATION":
        return "user.modification.failure";
      default:
        return "user.modification.failure";
    }
  }

}
