/*
 * @(#)YearCanvas.java	2.6.a 19/09/12
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

import java.awt.AWTEventMulticaster;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import net.algem.util.ui.GemPanel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public abstract class YearCanvas
        extends GemPanel
        implements MouseListener
{

  protected int year = 1997;
  protected Calendar cal;
  private SimpleDateFormat df;
  protected String[] dayLabels;
  protected String[] monthLabels;
  protected Dimension dim;
  protected int pas_x;
  protected int pas_y;
  protected Font font;
  protected Font titleFont;
  protected ActionListener actionListener;

  public YearCanvas() {
    font = new Font("Helvetica", Font.PLAIN, 12);
    titleFont = new Font("Helvetica", Font.BOLD, 14);

    cal = Calendar.getInstance(Locale.FRANCE);
    df = new SimpleDateFormat("EEEEE dd MMMMM yyyy", Locale.FRANCE);

    addMouseListener(this);
  }

  public void setDate(Date d) {
    cal.setTime(d);
    repaint();
  }

  public Date getDate() {
    return cal.getTime();
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }
}
