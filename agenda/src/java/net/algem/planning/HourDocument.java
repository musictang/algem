/*
 * @(#)HourDocument.java	1.0.0 11/02/13
 *
 * Copyright (c) 2013 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem Agenda.
 * Algem Agenda is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem Agenda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem Agenda. If not, see <http://www.gnu.org/licenses/>.
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
 * @version 1.0.0
 * @since 1.0.0 11/02/13
 */
public class HourDocument
        extends PlainDocument
{

  private static int sep1 = 2;
  private JTextComponent textComponent;
  private int pos;
  private Hour buf = new Hour();
  private int hh, mm;

  public HourDocument(JTextComponent tc) {
    textComponent = tc;
    try {
      insertString(0, Hour.NULL_HOUR, null);
    } catch (Exception ignore) {
    }
  }

  public HourDocument(JTextComponent tc, String s) {
    this(tc);
    if (!s.equals(Hour.NULL_HOUR)) {
      try {
        insertString(0, s, null);
      } catch (Exception ignore) {
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
      } catch (Exception ex) {
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

      if ((pos == 0 && c > '2')
              || (pos == 3 && c > '5')) {
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
    } catch (Exception ignore) {
    }
    
  }

  private boolean atSeparator(int offset) {
    return offset == sep1;
  }
}
