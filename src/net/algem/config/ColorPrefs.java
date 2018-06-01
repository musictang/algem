/*
 * @(#)ColorPrefs.java	2.15.8 21/03/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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
 * @version 2.15.8
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

  /**
   * Automatically selecting a foreground color based on a background color.
   * @param bg background color
   * @return a color
   */
  public static Color getForeground(Color bg) {
    int r = bg.getRed();
    int g = bg.getGreen();
    int b = bg.getBlue();

    double luminance = r * 0.299 + g * 0.587 + b * 0.114;

    if (luminance <= 128) {
      return luminance <= 48 ? Color.WHITE.darker() : Color.WHITE;
    }
    return Color.BLACK;
  }

  /**
   * Lightens a color to enhance the foreground display.
   * @param c initial color
   * @return a color
   */
  public static Color brighten(Color c) {
    float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
    float h = hsb[0];
    float s = hsb[1];
    float b = hsb[2];
    if (b <= 0.85) {
      b += 0.15f;
    } else if (s >= 0.25) {
      s -= 0.25f;
    } else {
      return c.brighter();
    }
    return new Color(Color.HSBtoRGB(h, s, b));
  }

}
