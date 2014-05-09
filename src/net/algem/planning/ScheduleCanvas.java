/*
 * @(#)ScheduleCanvas.java 2.8.t 08/05/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Vector;
import net.algem.config.AgeRange;
import net.algem.config.ColorPlan;
import net.algem.config.ColorPrefs;
import net.algem.config.GemParam;
import net.algem.contact.Person;
import net.algem.course.Course;
import net.algem.room.Room;
import net.algem.util.ui.GemPanel;

/**
 * Abstract class for planning layout.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 2.5.a 10/07/12
 */
public abstract class ScheduleCanvas
        extends GemPanel
        implements ScheduleView, MouseListener, Printable {

  protected static final int MARGEH = 30;
  protected static final int MARGED = 50;
  protected static final Font NORMAL_FONT = new Font("Helvetica", Font.PLAIN, 10);
  protected static final Font SMALL_FONT = new Font("Helvetica", Font.PLAIN, 9);
  protected static final Font X_SMALL_FONT = new Font("Helvetica", Font.PLAIN, 8);

  protected int pas_x;
  protected ActionListener listener;
  protected Schedule clickSchedule;
  protected Vector<ScheduleRangeObject> clickRange;
  protected int clickx;
  protected int clicky;
  protected Image img;
  protected ColorPrefs colorPrefs = new ColorPrefs();

  public void removeActionListener(ActionListener l) {
    listener = AWTEventMulticaster.remove(listener, l);
  }

  public void addActionListener(ActionListener l) {
    listener = AWTEventMulticaster.add(listener, l);
  }

  /**
   * Gets the background color.
   *
   * @param p schedule
   * @return a color
   */
  protected Color getScheduleColor(ScheduleObject p) {
    Color c = Color.white;
    switch (p.getType()) {
      case Schedule.COURSE_SCHEDULE:
        Room s = ((CourseSchedule) p).getRoom();
        Course cc = ((CourseSchedule) p).getCourse();
        if (s.isCatchingUp()) {
          //c = Color.black; // salles de rattrapages
          c = colorPrefs.getColor(ColorPlan.CATCHING_UP);
        } else {
          if (cc != null && !cc.isCollective()) {
            //c = DARK_GREEN; // couleur plannings cours individuels
            c = colorPrefs.getColor(ColorPlan.COURSE_INDIVIDUAL);
          } else {
            //c = Color.red; // couleur cours collectif
            if (cc.isInstCode()) {
              c = colorPrefs.getColor(ColorPlan.INSTRUMENT_CO);
            } else {
              c = colorPrefs.getColor(ColorPlan.COURSE_CO);
            }
          }
        }
        break;
      case Schedule.ACTION_SCHEDULE:
        //c = Color.green;
        c = colorPrefs.getColor(ColorPlan.ACTION);
        break;
      case Schedule.MEMBER_SCHEDULE:
        c = colorPrefs.getColor(ColorPlan.MEMBER_REHEARSAL);
        break;
      case Schedule.GROUP_SCHEDULE:
        //c= DARK_BLUE; // couleur groupe et repetiteurs
        c = colorPrefs.getColor(ColorPlan.GROUP_REHEARSAL);
        break;
      case Schedule.WORKSHOP_SCHEDULE:
        //c = Color.white; // couleur atelier ponctuel
        c = colorPrefs.getColor(ColorPlan.WORKSHOP);
        break;
      case Schedule.TRAINING_SCHEDULE:
        //c = Color.white; // couleur atelier ponctuel
        c = colorPrefs.getColor(ColorPlan.TRAINING);
        break;
    } // end switch couleurs
    return c;
  }

  /**
   * Gets the text color for headers.
   *
   * @param p schedule
   * @return a color
   */
  protected Color getTextColor(ScheduleObject p) {

    if (p instanceof CourseSchedule) {
      Room r = p.getRoom();
      if (r.isCatchingUp()) {
        return colorPrefs.getColor(ColorPlan.CATCHING_UP_LABEL);
      } else if (((CourseSchedule) p).getCourse().isCollective()) {
        return colorPrefs.getColor(ColorPlan.COURSE_CO_LABEL);
      } else {
        return colorPrefs.getColor(ColorPlan.COURSE_INDIVIDUAL_LABEL);
      }
    } else if (p instanceof WorkshopSchedule) {
      return colorPrefs.getColor(ColorPlan.WORKSHOP_LABEL);
    } else if (p.getType() == Schedule.TRAINING_SCHEDULE) {
      return colorPrefs.getColor(ColorPlan.TRAINING_LABEL);
    } else if (p instanceof GroupRehearsalSchedule) {
      return colorPrefs.getColor(ColorPlan.GROUP_LABEL);
    } else if (p instanceof MemberRehearsalSchedule) {
      return colorPrefs.getColor(ColorPlan.MEMBER_LABEL);
    } else {
      return colorPrefs.getColor(ColorPlan.LABEL);
    }

  }

  /**
   * Gets schedule range color.
   *
   * @param p schedule
   * @param c basic color
   * @return a color
   */
  protected Color getScheduleRangeColor(ScheduleObject p, Color c) {
    if (p instanceof ScheduleRangeObject) {
      Person a = ((ScheduleRangeObject) p).getMember();
      if (a == null) {
        c = Color.GRAY;
      } else if (a.getId() == 0) {
        c = c.darker();// break color
      }
    }
    return c;
  }

  /**
   * Gets the width of the colored range depending on number of students registered
   * and number of places allowed.
   *
   * @param p number of places
   * @param n number of participants
   * @return an integer
   */
  public int getScheduleRangeWidth(int p, int n) {

    if (p <= 0) {
      return 0;
    }
    if (n >= p) {
      return pas_x;
    }
    return (pas_x * n) / p;
  }

  /**
   * Gets planification code (level, status, age range).
   * @param p schedule
   * @return a 3 characters string prefixed by a space
   */
  protected String getCode(ScheduleObject p) {

    if (p instanceof CourseSchedule) {
      GemParam status = ((CourseSchedule) p).getAction().getStatus();
      GemParam level = ((CourseSchedule) p).getAction().getLevel();
      AgeRange t = ((CourseSchedule) p).getAction().getAgeRange();

      String n = GemParam.NONE;

      String c = (status == null ? " " + n : " " + status.getCode());
      c += (level == null ? n : level.getCode());
      c += (t == null || t.getCode() == null ? n : t.getCode());
      return (" " + n + n + n).equals(c) ? null : c;
    } else {
      return null;
    }

  }

  protected String getCodeDetail(ScheduleObject p) {
    String c = "";
    String n = GemParam.NONE;
    if (p instanceof CourseSchedule) {
      GemParam status = ((CourseSchedule) p).getAction().getStatus();
      GemParam level = ((CourseSchedule) p).getAction().getLevel();
      AgeRange t = ((CourseSchedule) p).getAction().getAgeRange();
      c += (status == null || status.getCode().equals(n) ? "" : " " + status.getLabel());
      c += (level == null || level.getCode().equals(n) ? "" : " " + level.getLabel());
      c += (t == null || t.getCode().equals(n) ? "" : " " + t.getLabel());
    }
    return c.isEmpty() ? null : c;

  }
  @Override
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
    if (pageIndex > 0) {
      // We have only one page, and 'page' is zero-based
      return NO_SUCH_PAGE;
    }

    if (img != null) {
      g.translate((int) pageFormat.getImageableX(), (int) pageFormat.getImageableY());
      if (img.getWidth(this) > pageFormat.getImageableWidth() || img.getHeight(this) > pageFormat.getImageableHeight()) {
        Image scaled = img.getScaledInstance(
                (int) pageFormat.getImageableWidth(),
                (int) pageFormat.getImageableHeight(),
                Image.SCALE_SMOOTH); // important ! SCALE_SMOOTH instead of SCALE_DEFAULT
        g.drawImage(scaled, 0, 0, this);
      } else {
        g.drawImage(img, 0, 0, this);
      }
    }

    return PAGE_EXISTS;
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
  public Schedule getSchedule() {
    return clickSchedule;
  }

  @Override
  public Vector<ScheduleRangeObject> getScheduleRanges() {
    return clickRange;
  }

  public java.util.List<ScheduleRangeObject> getPlagesCoursCoInst(java.util.List<ScheduleRangeObject> plages) {

    java.util.List<ScheduleRangeObject> vcc = new ArrayList<ScheduleRangeObject>();
    for (ScheduleRangeObject p : plages) {
      Course c = p.getCourse();
      if (c == null) {
        continue;
      }
      if (c.isCourseCoInst()) {
        vcc.add(p);
      }
    }
    return vcc.isEmpty() ? null : vcc;
  }

    @Override
  public void setBounds(int nx, int ny, int nw, int nh) {
    if (img != null) {
      img = null;
      repaint();
    }
    super.setBounds(nx, ny, nw, nh);
  }

  /**
   * Gest the schedule under click position.
   * @param v a list of schedules
   * @param hc time grid position
   * @return a schedule
   */
  protected ScheduleObject getClickedSchedule(Vector<ScheduleObject> v, Hour hc) {
    if (v != null) {
      for (int i = 0; i < v.size(); i++) {
        ScheduleObject p = v.elementAt(i);
        Hour hd = p.getStart();
        Hour hf = p.getEnd();
        if (hc.ge(hd) && hc.le(hf)) {
          return p;
        }
      }
    }
    return null;
  }

  /**
   * Gets the click position.
   * @return a point
   */
  @Override
  public Point getClickPosition() {
    Point p = getLocationOnScreen();

    p.x += clickx;
    p.y += clicky;

    return p;
  }

  /**
   * Global drawing.
   */
  protected abstract void drawBackground();

  /**
   * Displays a symbol for unpaid sessions.
   *
   * @param col
   * @param start beginning in minutes
   * @param end end in minutes
   * @param c display color
   */
  protected abstract void flagNotPaid(int col, int start, int end, Color c);
}
