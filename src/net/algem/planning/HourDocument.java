/*
 * @(#)HourDocument.java	2.9.1 12/11/14
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
package net.algem.planning;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 */
public class HourDocument
        extends PlainDocument
{

  /** Position of the first separator. */
  private static int sep1 = 2;

  /** Internal component. */
  private JTextComponent textComponent;

  private int pos;

  private Hour buf = new Hour();

  /**
   * Longer than 24 hours.
   * If is true, due to the implementation, time length remains below 100 hours.
   */
  private boolean extended;

  private int hh, mm;

  /**
   * Creates a document with optional extended time length.
   * @param tc text component
   * @param extended timeout option
   */
  public HourDocument(JTextComponent tc, boolean extended) {
    this.extended = extended;
    this.textComponent = tc;
    try {
      insertString(0, Hour.NULL_HOUR, null);
    } catch (BadLocationException ignore) {
    }
  }

  public HourDocument(JTextComponent tc, String s) {
    this(tc, false);
    if (!s.equals(Hour.NULL_HOUR)) {
      try {
        insertString(0, s, null);
      } catch (BadLocationException ignore) {
      }
    }
  }

  public HourDocument(JTextComponent tc, String s, boolean extended) {
    this(tc, extended);
    if (!s.equals(Hour.NULL_HOUR)) {
      try {
        insertString(0, s, null);
      } catch (BadLocationException ignore) {
      }
    }
  }

  @Override
  public void insertString(int offset, String s, AttributeSet attributs)
          throws BadLocationException {
    if (s.equals(Hour.NULL_HOUR)) {
      super.insertString(offset, s, attributs);
      textComponent.setCaretPosition(0);
    } else if (s.length() == 5) {
      buf = new Hour(s);
      super.remove(0, 5);
      super.insertString(0, buf.toString(), attributs);
    } else {
      try {
        Integer.parseInt(s);
      } catch (NumberFormatException ex) {
        char c = s.charAt(0);
        if (c == 'm') {
          buf.incMinute(1);
        } else if (c == 'M') {
          buf.decMinute(1);
        } else if (c == 'h') {
          buf.incHour(1);
        } else if (c == 'H') {
          buf.decHour(1);
        } else {
          return;
        }
        super.remove(0, 5);
        super.insertString(0, buf.toString(), attributs);
        //setCaretPosition(pos);
        return;
      }
      char c = s.charAt(0);
      pos = offset;
      if (pos >= 5) {
        return;
      }

      if (atSeparator(offset)) {
        pos++;
        textComponent.setCaretPosition(pos);
      }

      if ((pos == 0 && c > '2' && !extended) || (pos == 3 && c > '5')) {
        return;
      }
      buf.setDigit(pos, c);

      super.remove(pos, 1);
      super.insertString(pos, s, attributs);
    }
  }

  @Override
  public void remove(int offset, int length) {
    if (atSeparator(offset)) {
      textComponent.setCaretPosition(offset - 1);
    } else {
      textComponent.setCaretPosition(offset);
    }
  }

  public Hour getHour() {
    return buf;
  }

  public void setHour(String s) {
    try {
      super.remove(0, 5);
      super.insertString(0, s, null);
      buf = new Hour(s);
    } catch (BadLocationException ignore) {
    }

  }

  private boolean atSeparator(int offset) {
    return offset == sep1;
  }
}
