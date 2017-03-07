/*
 * @(#)AbstractComponentCtrl.java	2.12.0 06/03/17
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
 *
 */

package net.algem.util.ui;

import java.awt.event.ActionEvent;

/**
 * Abstract controller based on GemComponentCtrl interface.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 * @since 2.8.v 21/05/14
 */
public abstract class AbstractComponentCtrl 
  extends GemPanel
  implements GemComponentCtrl

{
  
  protected GemButton plus;
  protected GemButton minus;

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == plus) {
      add();
    } else if (src == minus) {
      remove();
    }
  }

}
