/*
 * @(#)HourRangePanel.java	2.6.a 19/09/12
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

import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemLabel;

/**
 * Time range panel.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class HourRangePanel
        extends GemBorderPanel
        implements FocusListener
{

  private static final int MAX = 60 * 12;
  private static final String START = "10:00";
  private static final String END = "23:00";
  private HourField start;
  private HourField end;
  private Hour old;
  private int maxi;
  private CalendarDlg dlg;

  public HourRangePanel(int _maxi) {
    this();
    maxi = _maxi;
  }

  public HourRangePanel() {

    start = new HourField(START);
    end = new HourField(START);
    start.addFocusListener(this);
    end.addFocusListener(this);

    add(new GemLabel(BundleUtil.getLabel("Hour.From.label")));
    add(start);
    add(new GemLabel(BundleUtil.getLabel("Hour.To.label")));
    add(end);
  }

  public HourRangePanel(Hour h) {
    start.set(h);
    end.set(h);
  }

  public HourRangePanel(Hour hStart, Hour hEnd) {
    start.set(hStart);
    end.set(hEnd);
  }

  @Override
  public Insets getInsets() {
    return new Insets(0, 0, 0, 0);
  }

  public Hour get() {
    return start.get();
  }

  public Hour getStart() {
    return start.get();
  }

  public Hour getEnd() {
    return end.get();
  }

  public void setHour(Hour h) {
    start.set(h);
  }

  public void setStart(String s) {
    start.setText(s);
  }

  public void setStart(Hour h) {
    start.set(h);
  }

  public void setEnd(Hour e) {
    end.set(e);
  }

  public void setEnd(String s) {
    end.setText(s);
  }

  public void clear() {
    start.setText(START);
    end.setText(START);
  }

  @Override
  public void focusLost(FocusEvent evt) {
    if (evt.isTemporary()) {
      return;
    }

    if (evt.getSource() == start) {
      if (start.get().equals(old)) {
        return;
      }
      Hour d = start.get();
      start.set(old);
      validHour1(d); 
    } else if (evt.getSource() == end) {
      //if (end.get().equals(old))
      //	return;
      Hour f = end.get();
      end.set(old);
      validHour2(f);
    }
  }

  @Override
  public void focusGained(FocusEvent evt) {
    if (evt.getSource() == start) {
      old = new Hour(start.get());
    } else if (evt.getSource() == end) {
      old = new Hour(end.get());
    }
  }

  public void validHour1(Hour d) {
    Hour f = end.get();
    //int dif = f.toMinutes() - d.toMinutes();
    if (!d.before(f)) {
      end.set(d);
    } 
    /*else if (dif > maxi) {// désactivation @since 2.1.k
      String msg = "durée > à " + (maxi / 60) + " heures";
      if (JOptionPane.showConfirmDialog(null, msg, msg, JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
        return;
      }
    }*/
    start.set(d);
  }

  public void validHour2(Hour f) {
    Hour deb = start.get();
    int dif = f.toMinutes() - deb.toMinutes();
    if (f.before(deb)) {
      //JOptionPane.showMessageDialog(null, "heure end<debut", "heure end<debut", JOptionPane.ERROR_MESSAGE);
      end.set(deb);
      return;
    }
    /*else if (dif > maxi) {
      String msg = "durée > à " + (maxi / 60) + " heures";
      if (JOptionPane.showConfirmDialog(null, msg, msg, JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
        return;
      }
    }*/
    /*else if (dif == 0) {
      JOptionPane.showMessageDialog(null, "heure debut=end", "heure debut=end", JOptionPane.ERROR_MESSAGE);
      return;
    }*/
    end.set(f);
  }
}
