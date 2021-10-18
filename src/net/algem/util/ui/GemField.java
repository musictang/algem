/*
 * @(#)GemField.java	2.9.4.13 01/10/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * JTextField extension.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.12
 * @since 1.0a 07/07/1999
 *
 */
public class GemField
        extends javax.swing.JTextField
        implements KeyListener, FocusListener
{

  protected static int MAXHISTO = 20;
  protected String oldval = "";
  protected int idx = 0;
  protected List<String> histo;
  protected int limit;

  /**
   * Main constructor.
   * If {@code _histo} is true, a focusListener is added.
   * By default, history contains 20 elements.
   * @param _histo history flag
   * @param length number of columns
   */
  public GemField(boolean _histo, int length) {
    super(length);
    setDragEnabled(true);
    if (_histo) {
      histo = new ArrayList<>(20);
      addFocusListener(this);
    }
    addKeyListener(this);
  }

  /** 
   * Constructs a field with or without history management and with default length.
   * @param _histo history flag
   */
  public GemField(boolean _histo) {
    this(_histo, 5);
  }

  /**
   * Constructs a field without history and with default length.
   */
  public GemField() {
    this(false, 5);
  }

  /**
   * Constructs a field of some length and without history.
   * @param length number of columns
   */
  public GemField(int length) {
    this(false, length);
  }

  /**
   * Constructs a field with max input length of {@code maxChars}.
   *
   * @param length number of columns
   * @param maxChars maximum number of chars
   * @since 2.6.d
   */
  public GemField(int length, final int maxChars) {
    this(false, length);
    setMaxCharsDocument(maxChars);
  }

  /**
   * Constructs a field of some length with predefined content and without history.
   * @param text predefined content
   * @param length number of columns
   */
  public GemField(String text, int length) {
    this(false, length);
    setText(text);
    //setFont(new Font("Helvetica",Font.PLAIN,12));
  }

  /**
   * Constructs a field of some length with predefined content.
   * @param _histo history flag
   * @param text predefined content
   * @param length number of columns
   */
  public GemField(boolean _histo, String text, int length) {
    this(_histo, length);
    setText(text);
    //setFont(new Font("Helvetica",Font.PLAIN,12));
  }

  /**
   * Constructs a field with predefined content and without history.
   * The number of columns equals text length.
   * @param text predefined content
   */
  public GemField(String text) {
    this(false, text.length());
    setText(text);
  }

  /**
   * Constructs a field with predefined content.
   * Length of the field depends on the {@code text} length.
   *
   * @param _histo history flag
   * @param text predefined content
   */
  public GemField(boolean _histo, String text) {
    this(_histo, text.length());
    setText(text);
  }

  /**
   * Adds a text with history management.
   * @param text
   */
  @Override
  public void setText(String text) {

    if (text != null && histo != null && !oldval.equals(text) && text.trim().length() > 0) {
      if (histo.size() > MAXHISTO) {
        histo.remove(0);
      }
      histo.add(text);
      idx = histo.size();
      oldval = text;
    }
    super.setText(text);

  }
  
  /**
   * Sets the document rule to limit the number of input characters.
   * @param max maximum number of characters
   */
  public void setMaxChars(final int max) {
    setMaxCharsDocument(max);
  }
  
  /**
   * Sets the maximum number of characters authorized.
   * @param max maximum number of input characters allowed
   */
  private void setMaxCharsDocument(final int max) {
    setDocument(new PlainDocument()
    {
      @Override
      public void insertString(int offs, String str, AttributeSet a)
              throws BadLocationException {
        if (getLength() + str.length() <= max) {
          super.insertString(offs, str, a);
        }
      }
    });
  }

  @Override
  public void focusGained(FocusEvent event) {
    oldval = getText();
  }

  @Override
  public void focusLost(FocusEvent event) {
    if (!oldval.equals(getText())) {
      if (histo.size() > MAXHISTO) {
        histo.remove(0);
      }
      histo.add(getText());
      idx = histo.size() - 1; //last index
    }
  }

  @Override
  public void keyTyped(KeyEvent keyevent) {
  }

  @Override
  public void keyReleased(KeyEvent keyevent) {
  }

  /**
   * History recall when key pressed (UP, DOWN).
   * With down, last element is retrieved.
   * @param keyevent
   */
  @Override
  public void keyPressed(KeyEvent keyevent) {
    if (!keyevent.isActionKey()) {
      return;
    }
    if (histo == null) {
      return;
    }

    if (keyevent.getKeyCode() == 38) {//up  
      if (idx > 0) {
        idx--;
        super.setText((String) histo.get(idx));
      }
    } else if (keyevent.getKeyCode() == 40) { //down
      if (idx < (histo.size())) {
        super.setText((String) histo.get(idx));
        idx++;
      }
    }
  }

  public static Border getDefaultBorder() {
    JTextField tx = new JTextField();
    return tx.getBorder();
  }

  public static Color getDefaultBorderColor() {
    return new JTextField().getBackground().darker();
  }
}
