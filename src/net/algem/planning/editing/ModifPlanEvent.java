/*
 * @(#)ModifPlanEvent.java	2.6.a 21/09/12
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
package net.algem.planning.editing;

import net.algem.planning.DateFr;
import net.algem.util.event.GemEvent;

/**
 * Planning modification event.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class ModifPlanEvent
        extends GemEvent
{

  private DateFr start;
  private DateFr end;

  public ModifPlanEvent(Object _source, DateFr _start, DateFr _end) {
    super(_source, MODIFICATION, PLANNING);
    start = _start;
    end = _end;
  }

  public DateFr getStart() {
    return start;
  }

  public DateFr getEnd() {
    return end;
  }

  public void setStart(DateFr d) {
    start = d;
  }

  public void setEnd(DateFr d) {
    end = d;
  }

  @Override
  public String toString() {
    return "ModifPlanEvent:" + type + "," + operation + " " + start + "-" + end;
  }
}
