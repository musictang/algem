/*
 * @(#)BundleUtil.java 2.7.a 21/11/12
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

package net.algem.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import net.algem.util.ui.MessagePopup;

/**
 * Utility class for i18n labels.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class BundleUtil
{
  public static ResourceBundle DEFAULT_BUNDLE;
  static {
    try {
      DEFAULT_BUNDLE =  ResourceBundle.getBundle("algem");
    } catch(MissingResourceException e) {
      GemLogger.log(Level.SEVERE, e.getMessage());
      MessagePopup.error(null, MessageUtil.getMessage("file.not.found.exception", "algem.properties"));
    }
  }

  public static String getLabel(String key) {

    if (DEFAULT_BUNDLE == null) {
      return "nobundle:" + key;
    }
    return get(key, DEFAULT_BUNDLE);
  }

  public static String getLabel(String key, ResourceBundle bundle) {
    if (bundle == null) {
      return getLabel(key);
    }
    return get(key, bundle);

  }

  public static String getLabel(String key, Object[] args) {
    String l = getLabel(key);
    return MessageFormat.format(l, args);
  }


  private static String get(String key, ResourceBundle bundle) {
    String label = null;
    try {
      label = bundle.getString(key);
    } catch (MissingResourceException e) {
			GemLogger.log(e.getKey()+" : "+e.getMessage());
			if (key.endsWith("label")) {
				key = key.substring(0, key.lastIndexOf('.'));
			}	else {
				key = "!"+key+"!";
			}
    }

    return label == null ? key : label;
  }


}
