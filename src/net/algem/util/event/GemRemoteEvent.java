/*
 * @(#)GemRemoteEvent.java	2.9.4.13 05/11/15
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
package net.algem.util.event;

/**
 * Gem remote event.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class GemRemoteEvent
        implements java.io.Serializable
{

  private static final long serialVersionUID = -7210250040383415117L;
  
  private GemEvent event;
  private String remoteId;

  public GemRemoteEvent() {
  }

  public GemRemoteEvent(GemEvent _event, String _id) {
    event = _event;
    remoteId = _id;
  }

  public GemEvent getEvent() {
    return event;
  }

  public String getId() {
    return remoteId;
  }

  @Override
  public String toString() {
    return "GemRemoteEvent:" + event + " - " + remoteId;
  }
  
}
