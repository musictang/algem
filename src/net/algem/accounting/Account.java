/*
 * @(#)Account.java	2.7.a 09/01/13
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

package net.algem.accounting;

import net.algem.config.ActivableParam;
import net.algem.config.Param;
import net.algem.util.model.GemModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 2.3.c 08/03/12
 */
public class Account
        extends ActivableParam
        implements GemModel
{

  private int id;

  public Account() {

  }

  public Account(int id) {
    this.id = id;
  }

  public Account(String number) {
    super(number);
    setActive(true);
  }

  public Account(Param p) {
    super(p.getKey(), p.getValue());
    setActive(true);
  }

  public Account(int id, String number, String value) {
    super(number, value);
    this.id = id;
  }

  public Account(int id, String key, String value, boolean active) {
    this(id, key, value);
    setActive(active);
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Alias for the method {@link net.algem.config.Param#getKey()}.
   * @return an account number
   */
  public String getNumber() {
    return super.getKey();
  }

  public String getLabel() {
    return super.getValue();
  }

  public void setNumber(String n) {
    super.setKey(n);
  }

  public void setLabel(String l) {
    super.setValue(l);
  }

  @Override
  public String toString() {
    return getLabel();
  }

}
