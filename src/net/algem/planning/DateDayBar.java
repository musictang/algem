/*
 * @(#)DateDayBar.java	2.6.a 21/09/12
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 */
public class DateDayBar
        extends GemPanel
        implements ActionListener
{

  private String[] monthLabels;
  private GemButton[] dayButtons;
  private GemButton[] mounthButtons;
  private GemButton btBefore;
  private GemButton btAfter;
  private Calendar cal;
  private ActionListener actionListener;
  private GemButton btSelected;
  private GemButton monthSelected;
  private int currentMonth;
  private int currentDay;

  /**
   * Color for selection in buttons bar (for day and month).
   */
  private static final Color selectionColor = new Color(255, 225, 255);
  private static final String dowColor = "#4c4c4c";

  public DateDayBar() {
    this(new Date());
  }

  public DateDayBar(Date d) {

    cal = Calendar.getInstance(Locale.FRANCE);
    cal.setTime(d);
    currentDay = cal.get(Calendar.DAY_OF_MONTH);
    GemPanel dayPanel = new GemPanel();
    GridLayout gl = new GridLayout(1, 31);
    gl.setHgap(0);
    dayPanel.setLayout(gl);

    /** The 31 days. */
    dayButtons = new GemButton[31];
    int maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    for (int i = 0; i < 31; i++) {
      /* ajout 2.0j jean-marc initiales des jours */
      cal.set(Calendar.DAY_OF_MONTH, i + 1);
      dayButtons[i] = new GemButton("<html><center><font color=" + dowColor + ">" + cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.FRANCE) + "</font><br />" + String.valueOf(i + 1) + "</center></html>");
      /* */
      dayButtons[i].setFont(new Font("Helvetica", Font.PLAIN, 10));

      /*bjours[i].setContentAreaFilled(false);// nimbus workaround
      bjours[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
      //bjours[i].setBorder(null);*/

      dayButtons[i].setMargin(new Insets(0, 1, 0, 1));
      dayPanel.add(dayButtons[i]);
      dayButtons[i].addActionListener(this);
    }
    cal.set(Calendar.DAY_OF_MONTH, currentDay);//reset du jour courant
    for (int j = (maxDaysInMonth); j < 31; j++) {
      dayButtons[j].setEnabled(false);
    }

    GemPanel monthPanel = new GemPanel();
    monthPanel.setLayout(new GridLayout(1, 14));

    monthLabels = new DateFormatSymbols(Locale.FRANCE).getShortMonths();
    btBefore = new GemButton("<<<");
    btBefore.setMargin(new Insets(2, 2, 2, 2));
    monthPanel.add(btBefore);
    btBefore.addActionListener(this);

    /** The 12 months. */
    mounthButtons = new GemButton[12];
    for (int i = 0; i < 12; i++) {
      mounthButtons[i] = new GemButton(monthLabels[i]);
      mounthButtons[i].setFont(new Font("Helvetica", Font.PLAIN, 10));
      mounthButtons[i].setMargin(new Insets(2, 2, 2, 2));
      monthPanel.add(mounthButtons[i]);
      mounthButtons[i].addActionListener(this);
    }
    btAfter = new GemButton(">>>");
    btAfter.setMargin(new Insets(2, 2, 2, 2));
    monthPanel.add(btAfter);
    btAfter.addActionListener(this);

    setLayout(new BorderLayout());
    add(dayPanel, BorderLayout.NORTH);
    add(monthPanel, BorderLayout.SOUTH);

    currentMonth = cal.get(Calendar.MONTH);
    colorSelected(currentDay-1);
    
    /*initSelectionMois();*/
    //initSelectionJour();
  }

  /*public void initSelectionJour() {
    if (btSelected == null) {
      for (int j = 0; j < bjours.length; j++) {
        if (j == currentDay - 1) {
          btSelected = bjours[j];
          btSelected.setBackground(selectionColor);
          break;
        }
      }
    }
  }*/

  /*public void initSelectionMois() {
    if (moisSelect == null) {
      for (int i = 0; i < bmois.length; i++) {
        if (i == moisCourant) {
          moisSelect = bmois[i];
          moisSelect.setBackground(selectionColor);
          break;
        }
      }
    }
  }*/

  public void setDate(Date d) {
    cal.setTime(d);
    colorSelected(cal.get(Calendar.DAY_OF_MONTH)-1);
    setDayLabel();
  }

  public Date getDate() {
    return cal.getTime();
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object c = evt.getSource();
    if (c == btBefore) { // on recule d'une année
      cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
      //
      cal.set(Calendar.MONTH, 11); // calé sur le dernier mois de l'année
      setDayLabel(); // calé sur le dernier jour du mois
      colorSelected(30);
    } else if (c == btAfter) {
      cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
      //
      cal.set(Calendar.MONTH, 0); // calé sur le premier mois
      setDayLabel();
      cal.set(Calendar.DAY_OF_MONTH, 1);
      colorSelected(0);
    } else if (c instanceof GemButton) {
        for (int i = 0; i < 12; i++) {
          if (c == mounthButtons[i]) {
            cal.set(cal.get(Calendar.YEAR), i, 1); // le premier jour du mois
            setDayLabel();
            cal.set(cal.get(Calendar.YEAR), i, 1); // on réinitialise au premier jour du mois
            colorSelected(0);
            break;
          }
        }
        // recherche du jour sélectionné
        for (int i = 0; i < 31; i++) {
          if (c == dayButtons[i]) {
            setSelected(i);
            break;
          }
        }
    }
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "date"));
    }
  }

  /**
   * Displays the abbrev name of days of week.
   */
  public void setDayLabel() {
    int maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    int j;
    String day;
    for (j = 0; j < maxDaysInMonth; j++) {
      cal.set(Calendar.DAY_OF_MONTH, j + 1);
      day = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.FRANCE);
      dayButtons[j].setText("<html><font color="+dowColor+">"+day+"</font><br /><center>"+(j + 1)+"</center></html>");
      if (!dayButtons[j].isEnabled()) {
        dayButtons[j].setEnabled(true);
      }
    }
    // Désactivation des jours non applicables pour le mois en cours
    if (maxDaysInMonth < 31) {
      for (int k = j; k < 31; k++) {
        dayButtons[k].setEnabled(false);
      }
    }
  }

  /**
   * Emphasize the selected day.
   * @param i day index
   */
  public void colorSelected(int i) {
    if (btSelected != null) {
      btSelected.setBackground(null);
    }
    btSelected = dayButtons[i];
    btSelected.setBackground(selectionColor);
  }
  
  private void setSelected(int i) {
    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), i + 1);
    colorSelected(i);
  }

  
}
