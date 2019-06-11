/*
 * @(#)IntegerDocument.java	2.6.a 25/09/12
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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class IntegerDocument
        extends PlainDocument
{

  private JTextComponent textComponent;
  private int pos;

  public IntegerDocument(JTextComponent tc) {
    textComponent = tc;
    //try {
    //	insertString(0, DateFr.NULLDATE, null);
    //} catch (Exception ignore) {};
  }

  public IntegerDocument(JTextComponent tc, String s) {
    this(tc);
    try {
      insertString(0, s, null);
    } catch (Exception ignore) {
    }
  }

  @Override
  public void insertString(int offset, String s, AttributeSet attributs)
          throws BadLocationException {
    try {
      Integer.parseInt(s);
    } catch (Exception e) {
      //Toolkit.getDefaultToolkit().beep();
      return;
    }
    super.insertString(offset, s, attributs);
  }
}
