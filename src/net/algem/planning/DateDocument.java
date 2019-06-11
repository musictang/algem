/*
 * @(#)DateDocument.java	2.17.0 20/03/2019
 *                              2.14.0 20/06/17
 * 
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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

import java.util.Date;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.0
 */
public class DateDocument
        extends PlainDocument
{

  private static int sep1 = 2, sep2 = 5;
  private JTextComponent textComponent;
  private int pos;
  private DateFr buf = new DateFr();
  private int jj, mm, aa;

  public DateDocument(JTextComponent tc) {
    textComponent = tc;
    try {
      insertString(0, DateFr.NULLDATE, null);
    } catch (Exception ignore) {
    }
  }

  public DateDocument(JTextComponent tc, String s) {
    this(tc);
    if (!s.equals(DateFr.NULLDATE)) {
      try {
        insertString(0, s, null);
      } catch (Exception ignore) {
      }
    }
  }

  @Override
  public void insertString(int offset, String s, AttributeSet attributs)
          throws BadLocationException {
    if (s.equals(DateFr.NULLDATE)) {
      super.insertString(offset, s, attributs);
      textComponent.setCaretPosition(0);
    } else if (s.length() == 10) {
      buf = new DateFr(s);
      super.remove(0, 10);
      super.insertString(0, buf.toString(), attributs);
    } else {
      try {
        Integer.parseInt(s);
      } catch (Exception ex) {
        char c = s.charAt(0);
        if (c == 'm') {
          buf.incMonth(1);
        } else if (c == 'M') {
          buf.decMonth(1);
        } else if (c == 'j') {
          buf.incDay(1);
        } else if (c == 'J') {
          buf.decDay(1);
        } else if (c == 'a') {
          buf.incYear(1);
        } else if (c == 'A') {
          buf.decYear(1);
        } else {
          return;
        }
        super.remove(0, 10);
        super.insertString(0, buf.toString(), attributs);
        //setCaretPosition(pos);
        return;
      }
      char c = s.charAt(0);
      pos = offset;
      if (pos > 9) {
        return;
      }

      if (atSeparator(offset)) {
        pos++;
        textComponent.setCaretPosition(pos);
      }

      if ((pos == 3 && c > '1')
              || (pos == 0 && c > '3')) {
        return;
      }
      if (pos == 1) {
        jj = buf.digitAt(0) * 10;
        jj += (c - '0');
        if (jj < 1 || jj > 31) {
          return;
        }
      }
      if (pos == 4) {
        mm = buf.digitAt(3) * 10;
        mm += (c - '0');
        if (mm < 1 || mm > 12) {
          return;
        }
        if (jj == 31
                && (mm == 4 || mm == 6
                || mm == 9 || mm == 11)) {
          return;
        }
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

  public DateFr getDateFr() {
//    return buf; //TODO ERIC ERROR retourne référence variable privée !
    return new DateFr(buf.toString()); 
  }

  public void setDate(String s) {
    try {
      super.remove(0, 10);
      super.insertString(0, s, null);
      buf = new DateFr(s);
    } catch (Exception ignore) {
    }
  }

  public Date getDate() {
    if (DateFr.NULLDATE.equals(buf.toString())) {
      return null;
    }
    return buf.getDate();
  }

  private boolean atSeparator(int offset) {
    return offset == sep1 || offset == sep2;
  }
}
