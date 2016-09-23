/*
 * @(#) EstablishmentPref.java Algem 2.11.0 23/09/2016
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
 */

package net.algem.config;

import java.util.prefs.Preferences;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.0
 * @since 2.11.0 23/09/2016
 */
public class EstablishmentPref {
  
  private Preferences prefs;

  public EstablishmentPref() {
    prefs = Preferences.userRoot().node("/algem/establishment");
  }
  
  public boolean isActive(String key) {
    return prefs.getBoolean(key, true);
  }

  public void setActive(String id, boolean value) {
      prefs.putBoolean(id, value);
  }
  
}
