/*
 * @(#)MessagePopup.java	2.6.a 14/09/12
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
package net.algem.util.ui;

import java.awt.Component;
import javax.swing.JOptionPane;
import net.algem.util.BundleUtil;

/**
 * Utility class for displaying messages.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class MessagePopup
{

  public static boolean confirm(Component _parent, String _message) {
    return confirm(_parent, _message, BundleUtil.getLabel("Warning.label"));
  }

  /**
   * Confirmation popup.
   *
   * @param _parent
   * @param _message
   * @param _titre
   * @return un booléen
   */
  public static boolean confirm(Component _parent, String _message, String _titre) {

    Object[] options = {BundleUtil.getLabel("Yes.option.label"), BundleUtil.getLabel("No.option.label")};
    return (JOptionPane.showOptionDialog(_parent, _message, _titre, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]) == JOptionPane.YES_OPTION);
  }

  /**
   * Information popup.
   *
   * @param _parent
   * @param _message
   */
  public static void information(Component _parent, String _message) {
    JOptionPane.showMessageDialog(_parent, _message, BundleUtil.getLabel("Information.label"), JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Warning popup.
   *
   * @param c component
   * @param m message
   */
  public static void warning(Component c, String m) {
    JOptionPane.showMessageDialog(c, m, BundleUtil.getLabel("Warning.label"), JOptionPane.WARNING_MESSAGE);
  }

  public static void error(Component c, String m) {
    JOptionPane.showMessageDialog(c, m, BundleUtil.getLabel("Error.label"), JOptionPane.ERROR_MESSAGE);
  }
}
