/*
 * @(#)RoomEvent.java	3.0.0 22/10/2021
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
package net.algem.room;

import java.util.Date;
import net.algem.util.event.GemEvent;

/**
 * Room creation event notification.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @author <a href="mailto:eric@productionlibre.fr">Eric</a>
 * @version 3.0.0
 */
public class RoomEvent
        extends GemEvent {

    private final Room room;
    private Date date;

    public RoomEvent(Object src, int type, Room room) {
        super(src, type, ROOM);
        this.room = room;
    }

    public RoomEvent(Object source, int type, Room room, Date d) {
        this(source, type, room);
        this.date = d;
    }

    public Room getRoom() {
        return room;
    }

    public Date getDate() {
        return date;
    }
}
