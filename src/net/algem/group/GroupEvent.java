/*
 * @(#)GroupEvent.java 3.0.0 22/10/2021
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
package net.algem.group;

import net.algem.util.event.GemEvent;

/**
 * comment
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @author <a href="mailto:eric@productionlibre.fr">Eric</a>
 * @version 3.0.0
 */
public class GroupEvent
        extends GemEvent {

    private final Group group;

    public GroupEvent(Object src, int type, Group group) {
        super(src, type, GROUP); // source, operation, type event
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

}
