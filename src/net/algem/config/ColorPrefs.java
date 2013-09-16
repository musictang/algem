/*
 * @(#)ColorPrefs.java	2.8.m 04/09/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.config;

import java.awt.Color;
import java.util.prefs.Preferences;

/**
 * Management of planning colors.
 * When system type, preferences are common for all users.
 * They are saved in node /algem/colors.
 * Under linux, when preferences are declared at system level, file
 * path may be changed at start with the option -Djava.util.prefs.systemRoot.
 * Under Windows, preferences are saved in registry. At system level, only
 * users with administrative rights may apply modifications.
 * For this reason, current preferences are declared at user level.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.m
 */
public class ColorPrefs
{

  public static final Color ERROR_BG_COLOR = Color.decode("#ff4a4a");
  private Preferences prefs;

  public ColorPrefs() {
    //prefs = Preferences.systemRoot().node("/algem/colors");
    prefs = Preferences.userRoot().node("/algem/colors");
  }

  /**
   * Gets the color corresponding to enumeration {@code colors}.
   * @param colors
   * @return an object of type awt.Color
   */
  public Color getColor(ColorPlan colors) {
    return getColor(colors.getKey(), colors.getDefaultColor());
  }

  /**
   * Gets the preferred color for preferences key.
   * @param key key name
   * @param defaultColor default color if none is found
   * @return an object of type awt.Color
   */
  public Color getColor(String key, int defaultColor) {
    int rgb = prefs.getInt(key, defaultColor);
    return new Color(rgb);
  }

  /**
   * Adds or modify a color in preferences file.
   * @param key
   * @param c the color
   */
  public void setColor(ColorPlan key, Color c) {
    if (key != null) {
      prefs.putInt(key.getKey(), c.getRGB());
    }
  }

  /**
   * Adds or modify a color in preferences file.
   * @param key
   * @param c the color in RGB format
   */
  public void setColor(ColorPlan key, int c) {
    if (key != null) {
      prefs.putInt(key.getKey(), c);
    }
  }
}
