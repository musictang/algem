/*
 * @(#)DateDayBar.java	2.9.4.8 18/06/15
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
package net.algem.planning;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.InsetsUIResource;
import net.algem.util.ImageUtil;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.9.4.8
 * @since 1.0a 07/07/1999
 */
public class DateDayBar
        extends GemPanel
        implements ActionListener
{

  private static final String dowLabelColor = "#4c4c4c";
  private String[] monthLabels;
  private JButton[] dayButtons;
  private GemButton[] monthButtons;
  private GemButton btPrevYear;
  private GemButton btNextYear;
  private Calendar cal;
  private ActionListener actionListener;
  private JButton daySelected;
  private GemButton monthSelected;
  private int currentMonth;
  private int currentDay;

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
    dayButtons = new JButton[31];
    int maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

    for (int i = 0; i < 31; i++) {
      cal.set(Calendar.DAY_OF_MONTH, i + 1);
//      dayButtons[i] = new JButton("<html><font color=" + dowLabelColor + ">" + cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.FRANCE) + "</font><br />" + String.valueOf(i + 1) + "</html>");
      dayButtons[i] = new JButton(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.FRANCE));
      /* */
      dayButtons[i].setFont(new Font("Helvetica", Font.PLAIN, 10));
      dayButtons[i].setMargin(new Insets(0, 0, 0, 0));
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
    btPrevYear = new GemButton("<<<");
    btPrevYear.setMargin(new Insets(2, 2, 2, 2));
    monthPanel.add(btPrevYear);
    btPrevYear.addActionListener(this);

    /** The 12 months. */
    monthButtons = new GemButton[12];
    for (int i = 0; i < 12; i++) {
      monthButtons[i] = new GemButton(monthLabels[i]);
      monthButtons[i].setFont(new Font("Helvetica", Font.PLAIN, 10));
      monthButtons[i].setMargin(new Insets(2, 2, 2, 2));
      monthPanel.add(monthButtons[i]);
      monthButtons[i].addActionListener(this);
    }
    btNextYear = new GemButton(">>>");
    btNextYear.setMargin(new Insets(2, 2, 2, 2));
    monthPanel.add(btNextYear);
    btNextYear.addActionListener(this);

    setLayout(new BorderLayout());
    add(dayPanel, BorderLayout.NORTH);
    add(monthPanel, BorderLayout.SOUTH);

    currentMonth = cal.get(Calendar.MONTH);
    colorSelected(currentDay-1, currentMonth);

  }

  public void setDate(Date d) {
    cal.setTime(d);
    colorSelected(cal.get(Calendar.DAY_OF_MONTH)-1, cal.get(Calendar.MONTH));
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
    if (c == btPrevYear) { // on recule d'une année
      cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
      //
      cal.set(Calendar.MONTH, 11); // calé sur le dernier mois de l'année
      setDayLabel(); // calé sur le dernier jour du mois
      colorSelected(30,11);
    } else if (c == btNextYear) {
      cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
      //
      cal.set(Calendar.MONTH, 0); // calé sur le premier mois
      setDayLabel();
      cal.set(Calendar.DAY_OF_MONTH, 1);
      colorSelected(0,0);
    } else if (c instanceof JButton) {
        boolean isMonth = false;
        for (int i = 0; i < 12; i++) {
          if (c == monthButtons[i]) {
            cal.set(cal.get(Calendar.YEAR), i, 1); // le premier jour du mois
            setDayLabel();
            cal.set(cal.get(Calendar.YEAR), i, 1); // on réinitialise au premier jour du mois
            colorSelected(0,i);
            isMonth = true;
            break;
          }
        }
        // recherche du jour sélectionné
        if (!isMonth) {
          for (int i = 0; i < 31; i++) {
            if (c == dayButtons[i]) {
              setSelected(i);
              break;
            }
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
      dayButtons[j].setText("<html><font color="+dowLabelColor+">"+day+"</font><br /><center>"+(j + 1)+"</center></html>");
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
   * @param d day index
   * @param m month index
   */
  public void colorSelected(int d, int m) {
    // reset previous selected button
    if (daySelected != null) {
      daySelected.setBackground(null);
      daySelected.setContentAreaFilled(true);
      daySelected.setOpaque(false);
    }
    if (monthSelected != null) {
      monthSelected.setBackground(null);
      monthSelected.setContentAreaFilled(true);
      monthSelected.setOpaque(false);
    }
    daySelected = dayButtons[d];
    //windows L&F workaround
    daySelected.setContentAreaFilled(false);
    daySelected.setOpaque(true);
    //
    daySelected.setBackground(DateBar.CALENDAR_BAR_SELECTED);
    
    monthSelected = monthButtons[m];
    monthSelected.setContentAreaFilled(false);
    monthSelected.setOpaque(true);
    monthSelected.setBackground(DateBar.CALENDAR_BAR_SELECTED);
  }
  
  /**
   * Sets time to the selected index.
   * @param i selected index
   */
  private void setSelected(int i) {
    cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), i + 1);
    colorSelected(i,cal.get(Calendar.MONTH));
  }
  
}
