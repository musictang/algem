/*
 * @(#)EmailField.java 2.6.a 17/09/12
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
package net.algem.contact;

import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.algem.util.ui.GemField;

/**
 * Email field with format verification.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class EmailField
        extends GemField
{

  /**
   * To exclude a sequence of characters from an ensemble, we may use
   * &&[^ô] (we exclue the ô here).
   */
  public final static Pattern emailRGX = Pattern.compile("^[A-Za-z0-9]+([\\.\\-_]?[A-Za-z0-9])*@[A-Za-z0-9]+([\\.\\-_]?[A-Za-z0-9]+)*[.][A-Za-z]{2,}$");

  public EmailField(int size) {
    super(size);
  }

  /**
   * Email verification.
   * @param email
   * @return true if format is correct
   */
  public static boolean check(String email) {
    if (email != null && email.length() > 0) {
      Matcher m = emailRGX.matcher(email);
      return m.matches();
    }
    return true;
  }

  public void keyPressed(KeyEvent keyevent) {
    /*
     * if(keyevent.isActionKey() && keyevent.getKeyCode() == 112) { String s =
     * getText(); System.out.println("lance client mail "+s);
     * keyevent.consume(); try { Runtime.getRuntime().exec("kmail "+s); } catch
     * (Exception e) { } }
     */
  }
}
