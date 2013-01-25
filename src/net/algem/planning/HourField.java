/*
 * @(#)HourField.java	2.6.a 25/09/12
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
package net.algem.planning;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;

/**
 * Text field for time representation.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class HourField
        extends JTextField
        implements KeyListener
{

  private int pos;
  private HourDocument buf;

  public HourField(String d) {
    addKeyListener(this);
    buf = new HourDocument(this, d);
    setDocument(buf);
  }

  public HourField() {
    this(Hour.NULL_HOUR);
  }

  public HourField(Hour d) {
    this(d.toString());
  }

  @Override
  public String toString() {
    return buf.toString();
  }

  public Hour get() {
    return buf.getHour();
  }

  public Hour getHour() {
    return get();
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
    /*
    char	c;
    if (e.isActionKey() && e.getKeyCode() == KeyEvent.VK_F1)
    {
    CalendrierDlg popCal = new CalendrierDlg(this,"calendrier");
    Hour d = new Hour(getText());
    popCal.setDate(d.getDate());
    popCal.saisie();
    if (popCal.isValider())
    {
    d = new Hour(popCal.getDate());
    buf.setDate(d.toString());
    }
    e.consume();
    }
     */
  }

  public void set(Hour d) {
    if (d != null) {
      buf = new HourDocument(this, d.toString());
      setDocument(buf);
      pos = 0;
    }
  }

  @Override
  public void setText(String s) {
    if (s != null) {
      buf = new HourDocument(this, s);
      setDocument(buf);
      pos = 0;
    }
  }
}
