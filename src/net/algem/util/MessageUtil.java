
/*
 * @(#)MessageUtil.java 2.1.j 01/06/11
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
import javax.swing.text.MaskFormatter;

/**
 * Utility class for displaying messages.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @since 2.1.j 01/06/11
 */
public class MessageUtil
{

  private static final String BUNDLE = "net/algem/messages";

  private MessageUtil() {
  }

  /**
   * Gets a message from a default properties file.
   * @param key
   * @return a string
   */
  public static String getMessage(String key) {
    String m = null;
    try {
      m = ResourceBundle.getBundle(BUNDLE).getString(key);
      if (m == null) {
        m = "!" + key + "!";
      }
    } catch (MissingResourceException e) {
      GemLogger.logException(e);
      return "!!" + key + "!!";
    }
    return m;
  }

  /**
   * Gets a formatted message from a default properties file.
   * @param key
   * @param args an array of objects
   * @return a string
   */
  public static String getMessage(String key, Object[] args) {
    String m = getMessage(key);
    return MessageFormat.format(m, args);
  }
	
	public static String getMessage(String key, Object arg) {
    String m = getMessage(key);
    return MessageFormat.format(m, arg);
  }
	

  public static MaskFormatter createFormatter(String s) {
    MaskFormatter formatter = null;
    try {
      formatter = new MaskFormatter(s);
    } catch (java.text.ParseException exc) {
      GemLogger.logException("formatter is bad", exc);
    }
    return formatter;
  }

}
