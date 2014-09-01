/*
 * @(#)MessagePopup.java	2.8.w 21/07/14
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
package net.algem.util.ui;

import java.awt.Component;
import javax.swing.JOptionPane;
import net.algem.util.BundleUtil;

/**
 * Utility class for displaying messages.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 */
public class MessagePopup
{

  /**
   * Displays a confirmation dialog box with default warning title.
   * @param parent parent component
   * @param message message to display
   * @return true if confirmation
   */
  public static boolean confirm(Component parent, String message) {
    return confirm(parent, message, BundleUtil.getLabel("Warning.label"));
  }

  /**
   * Displays a confirmation dialog box with custom title.
   *
   * @param parent parent component
   * @param message message to display
   * @param title custom title
   * @return true if confirmation
   */
  public static boolean confirm(Component parent, String message, String title) {

    Object[] options = {BundleUtil.getLabel("Yes.option.label"), BundleUtil.getLabel("No.option.label")};
    return (JOptionPane.showOptionDialog(parent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]) == JOptionPane.YES_OPTION);
  }

  /**
   * Displays an information dialog box with default title.
   *
   * @param parent parent component
   * @param message message to display
   */
  public static void information(Component parent, String message) {
    JOptionPane.showMessageDialog(parent, message, BundleUtil.getLabel("Information.label"), JOptionPane.INFORMATION_MESSAGE);
  }
  
  /**
   * Displays an information dialog box with custom title.
   *
   * @param parent parent component
   * @param message message to display
   * @param title custom title
   */
  public static void information(Component parent, String message, String title) {
    JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Displays a warning dialog box with default title.
   *
   * @param c parent component
   * @param m message to display
   */
  public static void warning(Component c, String m) {
    JOptionPane.showMessageDialog(c, m, BundleUtil.getLabel("Warning.label"), JOptionPane.WARNING_MESSAGE);
  }

  /**
   * Displays an error dialog box with default title.
   * @param c parent component
   * @param m message to display
   */
  public static void error(Component c, String m) {
    JOptionPane.showMessageDialog(c, m, BundleUtil.getLabel("Error.label"), JOptionPane.ERROR_MESSAGE);
  }
  
  /**
   * Displays an error dialog box with custom title.
   * @param c parent component
   * @param m message to display
   * @param title custom title
   */
  public static void error(Component c, String m, String title) {
    JOptionPane.showMessageDialog(c, m, title, JOptionPane.ERROR_MESSAGE);
  }
}
