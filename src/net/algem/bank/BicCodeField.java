/*
 * @(#)BicCodeField.java	2.8.i 05/07/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

import net.algem.util.ui.GemField;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.i
 * @since 2.8.i 05/07/13
 */
public class BicCodeField
  extends GemField 
{

  /** Max BIC code length. */
  private static final int MAX_BIC_LENGTH = 11;
  
  public BicCodeField() {
    super(MAX_BIC_LENGTH, MAX_BIC_LENGTH);
  }
  
}
