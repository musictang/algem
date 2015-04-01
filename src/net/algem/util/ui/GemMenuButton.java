/*
 * @(#)GemMenuButton.java	2.9.4.0 31/03/15
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
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;

/**
 * Button menu model.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.0
 */
public class GemMenuButton
        extends GemButton
{

  private Object object;
  private boolean popup;
  private Color back = getBackground();
  private Color fore = getForeground();
  // couleur au survol des boutons (cd PlanLienDetailCtrl)
  private Color overColor = new Color(255, 225, 255);
  private ActionListener al;

  /**
   * 
   * @param s label
   * @param l action listener
   * @param k action command string
   */
  public GemMenuButton(String s, ActionListener l, String k) {
    super(s);

    al = l;
    setActionCommand(k);
    addActionListener(l);

    setFont(new Font("Helvetica", Font.PLAIN, 10));
    setMargin(new Insets(0, 0, 0, 0));

    //setFocusPainted(true);
    //setRolloverEnabled(true);

    addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent evt) {
        /*
        System.out.println("CLICK:"+evt);
        if (!(evt.getButton() == MouseEvent.BUTTON1))
        {
        popup = true;
        //doClick();
        al.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"LienAdherent"));
        }
        else
        popup = false;
         */
      }

      /**
       * Modifies the background color.
       */
      @Override
      public void mouseEntered(MouseEvent evt) {
        setBackground(overColor);
      }

      @Override
      public void mouseExited(MouseEvent evt) {
        setBackground(back);
        setForeground(fore);
      }
    });

  }

  public GemMenuButton(String s, ActionListener l, String k, Object o) {
    this(s, l, k);
    object = o;
    setHorizontalAlignment(SwingConstants.LEFT);
  }

  public Object getObject() {
    return object;
  }

  public boolean isPopup() {
    return popup;
  }
}
