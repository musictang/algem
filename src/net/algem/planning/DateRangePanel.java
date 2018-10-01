/*
 * @(#)DateRangePanel.java 2.15.10 01/10/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import net.algem.util.BundleUtil;
import net.algem.util.ImageUtil;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 * Date range panel with calendar (F1 key).
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.10
 */
public class DateRangePanel
        extends GemPanel
        implements ActionListener, FocusListener
{

  //private static final long maxi = 60 * 60 * 24 * 365;
  public static final int SIMPLE_DATE = 1;
  public static final int RANGE_DATE = 2;

  public static final int FIRST_FIELD = 10;
  public static final int SECOND_FIELD = 20;
  public static final int ALL_FIELDS = 100;

  private static final Border DEFAULT_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
  private static final Insets NULL_INSETS = new Insets(0, 0, 0, 0);
  private int mode = SIMPLE_DATE;
  private DateFrField start;
  private DateFrField end;
  private Date old;
  private ImageIcon icon;
  private GemButton cal1;
  private GemButton cal2;
  private CalendarDlg dlg;
  private GemBorderPanel d1, d2;
  private ActionListener listener;

  /**
   *
   * @param mode constante déterminant s'il s'agit d'une simple date ou d'une plage
   * @param border si null, la bordure par défaut est utilisée
   */
  public DateRangePanel(int mode, Border border) {

    this.mode = mode;
    icon = ImageUtil.createImageIcon(ImageUtil.CAL_ICON);
    start = new DateFrField(new Date());

    String tip = BundleUtil.getLabel("Calendar.tip");
    cal1 = new GemButton(icon);
    cal1.setToolTipText(tip);
    cal1.setMargin(NULL_INSETS);
    cal1.addActionListener(this);
    if (border == null) {
      border = DEFAULT_BORDER;
    }
    d1 = new GemBorderPanel(border);
    d1.add(start);
    d1.add(cal1);

    setLayout(new BorderLayout());
    add(d1, BorderLayout.WEST);

    if (mode == RANGE_DATE) {
      start.addFocusListener(this);

      end = new DateFrField(start.getDate());
      end.addFocusListener(this);

      cal2 = new GemButton(icon);
      cal2.setToolTipText(tip);
      cal2.setMargin(NULL_INSETS);
      cal2.addActionListener(this);

      d2 = new GemBorderPanel(border);
      d2.add(new JLabel(BundleUtil.getLabel("Date.To.label")));
      d2.add(end);
      d2.add(cal2);

      add(d2, BorderLayout.EAST);

    }
    //dlg = new CalendarDlg(this, "calendrier");
  }

  public DateRangePanel() {
    this(new DateFr(new Date()), new DateFr(new Date()));
  }

  /**
   * Creates a panel for one date only.
   * @param _start
   */
  public DateRangePanel(DateFr _start) {
    this(SIMPLE_DATE, DEFAULT_BORDER);
    start.setDate(_start);
    //fin.setDate(_debut);
  }

  /**
   * Creates a panel for two dates.
   * @param _start
   * @param _end
   */
  public DateRangePanel(DateFr _start, DateFr _end) {
    this(RANGE_DATE, DEFAULT_BORDER);
    start.setDate(_start);
    end.setDate(_end);
  }

  /**
   * Creates a panel for one date only, with border.
   * @param _start
   * @param border
   */
  public DateRangePanel(DateFr _start, Border border) {
    this(SIMPLE_DATE, border);
    start.setDate(_start);
  }

  /**
   * Creates a panel for two dates, with border.
   * @param _start
   * @param _end
   * @param border
   */
  public DateRangePanel(DateFr _start, DateFr _end, Border border) {
    this(RANGE_DATE, border);
    start.setDate(_start);
    end.setDate(_end);
  }

  /**
   * Sets the inner and optionally the outer border of the current instance.
   * @param external external border or null if  none is required
   * @param internal internal border or null
   */
  public void setBorder(Border external, Border internal) {
//    if (external != null) {
      setBorder(external);
//    }
    d1.setBorder(internal);
    if (d2 != null) {
      d2.setBorder(internal);
    }
  }

  @Override
  public Insets getInsets() {
    return NULL_INSETS;
  }

  public Date getDate() {
    return start.getDate();
  }

  public DateFr get() {
    return start.get();
  }

  public Date getStart() {
    return start.getDate();
  }

  public DateFr getStartFr() {
    return start.get();
  }

  public DateFrField getStartField() {
    return start;
  }

  public Date getEnd() {
    return end.getDate();
  }

  public DateFr getEndFr() {
    return end.get();
  }

  public void setDate(Date d) {
    start.setDate(d);
  }

  public void setDate(DateFr d) {
    start.setDate(d);
  }

  public void setStart(String s) {
    start.setText(s);
  }

  public void setStart(DateFr d) {
    start.setDate(d);
  }

  public void setStart(Date d) {
    start.setDate(d);
  }

  public void setEnd(DateFr d) {
    end.setDate(d);
  }

  public void setEnd(Date d) {
    end.setDate(d);
  }

  public void setEnd(String s) {
    end.setText(s);
  }

  /**
   * Enable specific field in component.
   * If field number > 1, enabled status is applied to all fields.
   * @param enabled true if this field is enabled
   * @param field field number starting with 0
   */
  public void setEnabled(boolean enabled, int field) {
    switch (field) {
      case 0:
        start.setEditable(enabled);
        cal1.setEnabled(enabled);
        break;
      case 1:
        end.setEditable(enabled);
        cal2.setEnabled(enabled);
        break;
      default:
        start.setEditable(enabled);
        end.setEditable(enabled);
        cal1.setEnabled(enabled);
        cal2.setEnabled(enabled);
        break;
    }

  }

  public void clear() {
  }

  @Override
  public void focusLost(FocusEvent evt) {
    if (evt.isTemporary()) {
      return;
    }

    if (evt.getSource() == start) {
      if (start.get().equals(new DateFr(old))) {
        return;
      }
      Date newDate = start.getDate();
      start.setDate(old);
      validDate1(newDate);
    } else if (evt.getSource() == end) {
      if (end.get().equals(new DateFr(old))) {
        return;
      }
      Date newDate = end.getDate();
      end.setDate(old);
      validDate2(newDate);
    }
  }

  @Override
  public void focusGained(FocusEvent evt) {
    if (evt.getSource() == start) {
      old = start.getDate();
    } else if (evt.getSource() == end) {
      old = end.getDate();
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (dlg == null) {
      dlg = new CalendarDlg(this, BundleUtil.getLabel("Calendar.label"));
    }
    if (evt.getSource() == cal1) {
      dlg.setDate(start.getDate());
      dlg.open();
      if (dlg.isValidate()) {
        if (mode == RANGE_DATE) {
          validDate1(dlg.getDate());
        } else {
          start.setDate(dlg.getDate());
        }
      }
    } else if (evt.getSource() == cal2) {
      dlg.setDate(end.getDate());
      dlg.open();
      if (dlg.isValidate()) {
        if (mode == RANGE_DATE) {
          validDate2(dlg.getDate());
        }
      }
    }
  }

  @Override
  public String toString() {
    return mode == SIMPLE_DATE ? getStartFr().toString() : (getStartFr().toString() + "_" + getEndFr().toString());
  }

  private void validDate1(Date newDate) {
    Date dateFin = end.getDate();
    if (!newDate.before(dateFin)) {
      end.setDate(newDate);
    }
    start.setDate(newDate);
  }

  private void validDate2(Date newDate) {
    Date dateDebut = start.getDate();
    if (newDate.before(dateDebut)) {
      JOptionPane.showMessageDialog(null, "date fin<debut", "date fin<debut", JOptionPane.ERROR_MESSAGE);
      return;
    }
    end.setDate(newDate);
  }
}
