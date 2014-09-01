/*
 * @(#)GemView.java	2.8.w 27/08/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

package net.algem.util.module;

import java.awt.event.ActionListener;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.model.GemCloseVetoException;

/**
 * Algem view interface.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 1.0a 07/07/2002
 */
public interface GemView 
        extends ActionListener, GemEventListener
{

  @Override
  public void postEvent(GemEvent evt);

  public void addActionListener(ActionListener l) ;
    
  public void removeActionListener(ActionListener l);

  public void print();

  public void close() throws GemCloseVetoException ;

  public void setSelectedTab(int tabIndex);

}
