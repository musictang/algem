/*
 * @(#)CostAccount.java	2.8.d 16/05/13
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
 * @version 2.8.d
 * @since 2.7.a 16/01/2013
 */
public class CostAccount 
  extends ActivableParam
  implements GemModel
{


  public CostAccount() {
  }

  public CostAccount(Param ap) {
    this.key = ap.getKey();
    this.value = ap.getValue();
  }
  
  public CostAccount(ActivableParam ap) {
    super(ap.getKey(), ap.getValue(), ap.isActive());
  }
  
  @Override
  public int getId() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setId(int id) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
