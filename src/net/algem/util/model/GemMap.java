/*
 * @(#)GemMap   3.0.0 25/10/2021
 *
 * Copyright (c) 2021 eric@productionlibre.fr. All Rights Reserved.
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
package net.algem.util.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

/**
 * Base class for map of GemModel objects.
 *
 * @author <a href="mailto:eric@productionlibre.fr">Eric</a>
 * @version 3.0.0
 * @param <K,T>
 * @since 3.0 25/10/2021
 */
public class GemMap<K extends String, T extends GemModel> extends HashMap<K, T> {

    private PropertyChangeSupport propertySupport;

    public GemMap() {
        propertySupport = new PropertyChangeSupport(this);
    }

    public synchronized GemModel getId(int id) {
        for (Map.Entry<K, T> set : this.entrySet()) {
            T module = set.getValue();
            if (module.getId() == id) {
                return module;
            }
        }
        return null;
    }

    public synchronized T addElement(K key, T module) {
        T old = put(key, module);
        propertySupport.firePropertyChange("addElement", old, module);
        return old;
    }

    public synchronized T removeElement(K key) {
//        T old = this.removeElement(key, module);
        T old = this.remove(key);
        propertySupport.firePropertyChange("removeElement", old, null);
        return old;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
}

