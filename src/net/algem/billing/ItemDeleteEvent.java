/*
 * @(#)ItemDeleteEvent.java 2.5.d 25/07/12
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

package net.algem.billing;

import net.algem.util.event.GemEvent;

/**
 * Invoice item delete event.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.5.d
 * @since 2.3.a 03/02/12
 */
public class ItemDeleteEvent 
        extends GemEvent
{

   private Item item;

  public ItemDeleteEvent(Object source, Item item) {
    super(source, SUPPRESSION, ORDER_ITEM); // source, operation, type event
    this.item = item;
  }

  public Item getArticle() {
    return item;
  }
  
}
