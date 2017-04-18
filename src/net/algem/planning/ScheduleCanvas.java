/*
 * @(#)ScheduleCanvas.java 2.13.1 18/04/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
import javax.swing.RootPaneContainer;
import net.algem.config.AgeRange;
import net.algem.config.ColorPrefs;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.config.GemParam;
import net.algem.course.Course;
import net.algem.util.GemLogger;
import net.algem.util.ui.GemPanel;

/**
 * Abstract class for planning layout.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.1
 * @since 2.5.a 10/07/12
 */
public abstract class ScheduleCanvas
        extends GemPanel
        implements ScheduleView, MouseListener, Printable {

  public static final int DAY_COL_WIDTH = 100;
  protected static final int TOP_MARGIN = 30;
  protected static final int LEFT_MARGIN = 50;
  protected static final Font NORMAL_FONT = new Font("Helvetica", Font.PLAIN, 10);
  protected static final Font SMALL_FONT = new Font("Helvetica", Font.PLAIN, 9);
  protected static final Font X_SMALL_FONT = new Font("Helvetica", Font.PLAIN, 8);
  protected static final Color CLOSED_COLOR = Color.decode("#cccccc");
  // !IMPORTANT non final static variable : authorize opening time modification without rebooting Algem
  /** Global starting time in minutes. */
  protected int GLOBAL_START_TIME;
  {
    String start = ConfigUtil.getConf(ConfigKey.START_TIME.getKey());
    int s = 0;
    try {
      s = Integer.parseInt(start.substring(0, start.indexOf(':'))) * 60;
    } catch(NumberFormatException nfe) {
      GemLogger.log(nfe.getMessage());
    }
    GLOBAL_START_TIME = s;
  }
  protected int GRID_Y = (1440 - GLOBAL_START_TIME) /30;

  protected int colOffset;
  protected int step_x;
  protected int step_y;
  protected ActionListener listener;
  protected Schedule clickSchedule;
  protected Vector<ScheduleRangeObject> clickRange;
  protected int clickX;
  protected int clickY;
  protected Image img;
  protected ColorPrefs colorPrefs = new ColorPrefs();
//  protected Cacheable actionIO = DataCache.getDao(Model.Action);
  protected ScheduleColorizer colorizer = new StandardScheduleColorizer(colorPrefs);

  public void removeActionListener(ActionListener l) {
    listener = AWTEventMulticaster.remove(listener, l);
  }

  public void addActionListener(ActionListener l) {
    listener = AWTEventMulticaster.add(listener, l);
  }

  public void setStepX(int step_x) {
    this.step_x = step_x;
  }

  /**
   * Gets the background color.
   *
   * @param p schedule
   * @return a color
   */
  protected Color getScheduleColor(ScheduleObject p) {
    return colorizer.getScheduleColor(p);
  }

  /**
   * Gets the text color for headers.
   *
   * @param p schedule
   * @return a color
   */
  protected Color getTextColor(ScheduleObject p) {
    return colorizer.getTextColor(p);
  }

  /**
   * Gets schedule range color.
   *
   * @param p schedule
   * @param c basic color
   * @return a color
   */
  protected Color getScheduleRangeColor(ScheduleObject p, Color c) {
    return ((StandardScheduleColorizer) colorizer).getScheduleRangeColor(p, c);
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
      return step_x;
    }
    return (step_x * n) / p;
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

  public java.util.List<ScheduleRangeObject> getRangesCoursCoInst(java.util.List<ScheduleRangeObject> ranges) {

    java.util.List<ScheduleRangeObject> vcc = new ArrayList<ScheduleRangeObject>();
    for (ScheduleRangeObject p : ranges) {
      Course c = p.getCourse();
      if (c == null) {
        continue;
      }
//      if (c.isCourseCoInst()) {
      if (c.isCollective()) {
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

   protected int setX(int col, int spacing) {
    return LEFT_MARGIN + spacing + ((col - colOffset) * step_x);
  }

  protected int setY(int start) {
    int offset = start % 30;
    int y = TOP_MARGIN  + 2 + ((start - offset - GLOBAL_START_TIME) / 30) * step_y;
    return y + (offset * step_y / 30);
  }

  /**
   * Gets the click position.
   * @return a point
   */
  @Override
  public Point getClickPosition() {
    Point p = getLocationOnScreen();

    p.x += clickX;
    p.y += clickY;

    return p;
  }

  protected void setWaitCursor() {
    RootPaneContainer root = ((RootPaneContainer) getTopLevelAncestor());
    root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    root.getGlassPane().setVisible(true);
  }

  protected void setDefaultCursor() {
    RootPaneContainer root = ((RootPaneContainer) getTopLevelAncestor());
    root.getGlassPane().setCursor(Cursor.getDefaultCursor());
    root.getGlassPane().setVisible(false);
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
