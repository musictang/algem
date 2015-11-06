/*
 * @(#)Vat  2.9.4.13 05/11/15
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

package net.algem.billing;

import net.algem.config.Param;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 2.7.a 17/01/2013
 */
public class Vat 
  extends Param
{

  private static final long serialVersionUID = 3078588727265655506L;
  
  public Vat(String _key, String _value) {
    super(_key, _value);
  }
  
  public Vat(Param p) {
    this(p.getKey(), p.getValue());
  }
  
  @Override
  public int getId() {
    return Integer.parseInt(key);
  }

  @Override
  public void setId(int id) {
    throw new UnsupportedOperationException();
  }

}
