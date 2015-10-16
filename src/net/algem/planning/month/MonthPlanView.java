/*
 * @(#)MonthPlanView.java	2.9.4.13 15/10/15
 *
 * Copyright (cp) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning.month;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.DateFormatSymbols;
import java.util.*;
import net.algem.config.ColorPlan;
import net.algem.course.Course;
import net.algem.planning.*;
import net.algem.util.ui.GemField;

/**
 * Layout for month schedule.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class MonthPlanView
        extends ScheduleCanvas
        implements MouseMotionListener
{

  private static final int H_END = 1440;
  private static final ScheduleRangeComparator RANGE_COMPARATOR = new ScheduleRangeComparator();

  private Calendar cal;
  private String[] dayNames;
  private int lineHeight;
  private FontMetrics fm;
  private Graphics bg;
  private int year;
  private int month;
  private Vector<ScheduleObject> schedules;
  private Vector<ScheduleRangeObject> ranges;
  private GemField status;

  public MonthPlanView(GemField status) {

    this.status = status;
    cal = Calendar.getInstance(Locale.FRANCE);
    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    step_x = 10;
    dayNames = new DateFormatSymbols(Locale.FRANCE).getShortWeekdays();

    addMouseListener(this);
    addMouseMotionListener(this);
  }

  public void load(Date d, Vector<ScheduleObject> pl, Vector<ScheduleRangeObject> pg) {
    cal.setTime(d);
    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    this.schedules = pl;
    this.ranges = pg;
    clickSchedule = null;
    clickRange = null;
    img = null;
    repaint();
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    if (img == null) {
      Dimension d = getSize();
      img = createImage(d.width, d.height);
      step_y = (d.height - TOP_MARGIN) / GRID_Y;
      bg = img.getGraphics();
      bg.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,10));
      if (bg instanceof Graphics2D) {
        ((Graphics2D) bg).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      }   
      fm = bg.getFontMetrics();
      lineHeight = fm.getHeight() + 4;
      drawBackground();
    }
    g.drawImage(img, 0, 0, this);
  }

  @Override
  public void drawBackground() {

    drawGrid();
    if (schedules == null) {
      return;
    }

    drawSchedules(schedules);
    drawScheduleRanges(ranges);
    textRange(schedules); // OPTIONNEL
  }

  public void drawGrid() {
    int x = LEFT_MARGIN + 1;
    int y = lineHeight;
    cal.set(year, month, 1);
    cal.setTime(cal.getTime());
    int dow = cal.get(Calendar.DAY_OF_WEEK);
    int maxd = DateLib.daysInMonth(month, year);

    Dimension d = getSize();
    step_x = (d.width - LEFT_MARGIN) / maxd;
    step_y = ((d.height - TOP_MARGIN) / GRID_Y);

    for (int i = 1; i <= maxd; i++) {
      int w = fm.stringWidth(dayNames[dow]) + 10;
      //Day names header
      bg.drawString(dayNames[dow], x - (w - 10) / 2, y);
      // draw sunday column
      if (dow == Calendar.SUNDAY) {
        drawRange(i, H_START, H_END, Color.gray);
      }
      if (++dow > 7) {
        dow = 1;
      }

      // day of month numbers
      String dd = String.valueOf(i);
      w = fm.stringWidth(dd) + 10;
      bg.drawString(dd, x - (w - 10) / 2, y + 10);
      bg.drawLine(x - (step_x / 2), 2, x - (step_x / 2), TOP_MARGIN + (step_y * GRID_Y));
      x += step_x;
    }

    bg.drawLine(x - (step_x / 2), 2, x - (step_x / 2), TOP_MARGIN + (step_y * GRID_Y));
    x = 1;
//    y = TOP_MARGIN + step_y;
    y = TOP_MARGIN + (fm.getHeight() / 2);
    Hour hour = new Hour(H_START);
    // Half hours labels
    for (int i = 0; i < GRID_Y; i++) {
      bg.drawString(hour.toString(), x, y);
      hour.incMinute(30);
      y += step_y;
    }
    x = 5 + fm.stringWidth(hour.toString()) + 2;

    bg.drawLine(x, TOP_MARGIN + 1, LEFT_MARGIN + (step_x * maxd) - (step_x / 2), TOP_MARGIN + 1);
    bg.setColor(Color.gray);

//    y = TOP_MARGIN + (step_y * 2);
    y = TOP_MARGIN + (step_y);

    // horizontal lines (one by half hour)
    Graphics2D g2d = (Graphics2D) bg.create();
    Stroke dotted = new BasicStroke(0.1f,BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, new float[]{1f,3f}, 0);
    g2d.setStroke(dotted);

    int x2 = LEFT_MARGIN + (step_x * maxd) - (step_x / 2);
//    for (int i = 0; i < GRID_Y; i += 2, y += (step_y * 2)) {
    for (int i = 0; i < GRID_Y; i += 1, y += (step_y)) {
      if (0 == (i & 1)) {
        g2d.drawLine(x, y + 1, x2, y + 1);
      } else {
        bg.drawLine(x, y + 1, x2, y + 1);
      }
    }
  }

  private void drawSchedules(Vector<ScheduleObject> plans) {
    for (int i = 0; i < plans.size(); i++) {
      ScheduleObject p = (ScheduleObject) plans.elementAt(i);
      Color c = getScheduleColor(p);
      drawRange(p, c, step_x);
      if (p.getType() == Schedule.MEMBER || p.getType() == Schedule.GROUP) {
        if (p.getNote() == -1) {
          c = colorPrefs.getColor(ColorPlan.FLAG);
          flagNotPaid(p.getDate().getDay(), p.getStart().toMinutes(), p.getEnd().toMinutes(), c);
        }
      }
    }
  }

  private void drawScheduleRanges(Vector<ScheduleRangeObject> all) {
    if (all == null || all.isEmpty()) {
      return;
    }
    Collections.sort(all, RANGE_COMPARATOR);
    java.util.List<ScheduleRangeObject> regular = new ArrayList<ScheduleRangeObject>(all);
    java.util.List<ScheduleRangeObject> collective = getRangesCoursCoInst(regular);
    if (collective != null) {
      regular.removeAll(collective);
    }

    Color cp = colorPrefs.getColor(ColorPlan.RANGE);
    // tracé des plages de cours individuels
    for (ScheduleRangeObject p : regular) {
      Course cc = p.getCourse();
      if ((cc != null && !cc.isCollective()) || Schedule.ADMINISTRATIVE == p.getType()) {
        drawRange(p, cp, step_x);
      }
    }
    if (collective == null || collective.isEmpty()) {
      return;
    }
    int idp = collective.get(0).getScheduleId();
    int n = 0; // nombre de participants
    int w = 0; // largeur de plage occupée
    int idx = 0; // index plage
    // tracé des plages de cours collectifs
    for (int j = 0; j < collective.size(); j++) {
      ScheduleRangeObject p = collective.get(j);
      if (idp == p.getScheduleId()) {
        n++;
        idx = j;
        continue;
      }
      w = getScheduleRangeWidth(collective.get(idx).getAction().getPlaces(), n);
      drawRange(collective.get(idx), cp, w);
      idp = p.getScheduleId();
      n = 1;
      idx = j;
    }
    w = getScheduleRangeWidth(collective.get(idx).getAction().getPlaces(), n);
    drawRange(collective.get(idx), cp, w);
  }

  public void drawRange(DateFr j, Hour start, Hour end, Color c) {
    drawRange(j.getDay(), start.toMinutes(), end.toMinutes(), c);
  }

  public void drawRange(int jour, int start, int end, Color c) {
    int x = setX(jour, 2);
    int y = setY(start);
    int ht = setY(end) - y;
    bg.setColor(c);
    bg.fillRect(x, y, step_x - 1, ht - 1);
    bg.setColor(Color.black);
  }

  private void drawRange(ScheduleObject p, Color c, int w) {
    int pStart = p.getStart().toMinutes();
    int pEnd = p.getEnd().toMinutes();
    int day = p.getDate().getDay();

    int x = setX(day, 2);
    int y = setY(pStart);
    int ht = setY(pEnd) - y;

    bg.setColor(c);
    bg.fillRect(x, y, w - 1, ht - 1);
    bg.setColor(Color.black);
    // black line separator
    if (p instanceof CourseSchedule && p.getClass() != ScheduleRangeObject.class) {
      bg.drawLine(x, y - 1, (x + w) - 1, y - 1);
    }
    if (p.getType() == Schedule.MEMBER || p.getType() == Schedule.GROUP) {
      if (p.getNote() == -1) {
        flagNotPaid(p.getDate().getDay(), p.getStart().toMinutes(), p.getEnd().toMinutes(), c);
      }
    }
  }

  @Override
  protected int setX(int col, int spacing) {
    return LEFT_MARGIN + spacing + ((col - 1) * step_x) - (step_x / 2);
  }

  private void textRange(Vector<ScheduleObject> plans) {
    for (int i = 0; i < plans.size(); i++) {
      ScheduleObject p = (ScheduleObject) plans.elementAt(i);
      if (!(p instanceof CourseSchedule)) {
        continue;
      }
      if (((Course) p.getActivity()).isCollective()) {
        int x = LEFT_MARGIN + 0 + ((p.getDate().getDay() - 1) * step_x) - (step_x / 2);
        int y = setY(p.getStart().toMinutes());
        bg.setColor(getTextColor(p));
        bg.setFont(X_SMALL_FONT);
        showLabel(p, x, y);
      }
    }
  }

  private void showLabel(ScheduleObject p, int x, int y) {

    String code = getCode(p);
    int offset = (step_x / 2);
    if (code != null && !code.isEmpty()) {
      int w = fm.stringWidth(code) + 4;// largeur du texte
      while (w > step_x) {
        code = code.substring(0, code.length() - 1);// on enlève un caractère
        w = fm.stringWidth(code) + 4; // on réduit la largeur en fonction
      }
      bg.drawString(code, (x + offset) - (w / 2), y + 10);
    }
  }

  @Override
  public void flagNotPaid(int jour, int deb, int fin, Color c) {
    int x = setX(jour, 2);
    int y = setY(deb);
    bg.setColor(c);
    bg.drawString("$$$", x, y + 12);
    bg.setColor(Color.black);
  }

  @Override
  public void setBounds(int nx, int ny, int nw, int nh) {
    /*
     * if (nw != d.width || nh != d.height) { in = getInsets();
     * sb.setBounds(width+in.left, in.top, sbwidth, height); respace();
     */
    super.setBounds(nx, ny, nw, nh);
  }

  @Override
  public void processMouseEvent(MouseEvent e) {
    /*
     * if (e.isPopupTrigger()) { int	x = e.getX() - LEFT_MARGIN -2; int	y = e.getY()
     * - TOP_MARGIN -2;
     *
     * int	jj = ((x + (step_x)/2) / step_x) + 1; int	hh = ((y * 30)/pas_y)+540;
     * int	mm = hh % 60; hh /=	60; Date d = new
     * Date(annee-1900,mois-1,jj,hh,mm); popup.setLabel(d.toString());
     * popup.show(e.getComponent(),e.getX(),e.getY()); }
     */
    super.processMouseEvent(e);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    clickX = e.getX();
    clickY = e.getY();
    int x = clickX - LEFT_MARGIN - 2;
    int y = clickY - TOP_MARGIN - 2;

    int jj = ((x + (step_x) / 2) / step_x) + 1;
    int hh = ((y * 30) / step_y) + H_START;
    int mm = hh % 60;
    hh /= 60;

    Hour h = new Hour(hh, mm);
    Graphics g = getGraphics();
    // ecrase frame du dessus ??
    //g.clipRect(0,0,LEFT_MARGIN-(step_x/2)-1,TOP_MARGIN-1);
    g.setColor(getBackground());
    g.fillRect(0, 0, LEFT_MARGIN - (step_x / 2) - 1, TOP_MARGIN - 1);
    g.setColor(Color.black);

    g.setFont(new Font("Helvetica", Font.PLAIN, 10));
    g.drawString(jj + "-" + month, 1, 10);
    g.drawString(h.toString(), 1, 25);

    clickSchedule = null;
    cal.set(year, month, jj, hh, mm);
    Hour hc = new Hour(hh, mm);// heure clic

    clickSchedule = getSchedule(schedules, jj, hc);

    if (clickSchedule == null) {
      if (listener != null) {
        clickSchedule = new Schedule();//TODOGEM
        clickSchedule.setDate(cal.getTime());
        clickSchedule.setStart(hc);
        clickSchedule.setEnd(hc);
        //TODOGEM pp.setIdRoom();
        //TODOGEM pp.setIdPerson();
        //TODOGEM pp.setAction();
        listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "ClickDate"));
      }
      return;
    }

    clickRange = new Vector<ScheduleRangeObject>();

    for (int i = 0; ranges != null && i < ranges.size(); i++) {
      ScheduleRangeObject pg = ranges.elementAt(i);
      if (pg.getScheduleId() == clickSchedule.getId()) {
//      if (pg.getDate().bufferEquals(clickSchedule.getDate())
//              && pg.getIdAction() == clickSchedule.getIdAction()
//              && pg.getIdper().getId() == clickSchedule.getIdPerson() //ajout 1.1d
//              && (pg.getDateStart().ge(clickSchedule.getDateStart()) && pg.getDateEnd().le(clickSchedule.getDateEnd()))) //ajout 1.1d
//      {
        clickRange.add(pg);
      }
    }
    if (listener != null) {
      listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Click"));
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {

    int x = e.getX() - LEFT_MARGIN - 2;
    int y = e.getY() - TOP_MARGIN - 2;
    int jj = ((x + (step_x) / 2) / step_x) + 1;
    int hh = ((y * 30) / step_y) + H_START;
    int mm = hh % 60;
    hh /= 60;

    Hour h = new Hour(hh, mm);
    ScheduleObject p = getSchedule(schedules, jj, h);

    if (p != null) {
      String code = getCodeDetail(p);
      StringBuilder bl = new StringBuilder(((ScheduleObject) p).getScheduleDetail());
      if (code != null) {
        bl.append(" ").append(code);
      }
      status.setText(bl.toString());
    } else {
      status.setText(null);
    }
    /*
     * int	jj = ((x + (step_x)/2) / step_x) + 1; int	hh = ((y * 30)/pas_y)+600;
     * int	mm = hh % 60; hh /=	60;
     *
     * Hour h = new Hour(hh,mm); Graphics g = getGraphics(); // ecrase
     * //frame du dessus ?? //g.clipRect(0,0,LEFT_MARGIN-(step_x/2)-1,TOP_MARGIN-1);
     * g.setColor(getBackground()); g.fillRect(0,0,LEFT_MARGIN-(step_x/2)-1,TOP_MARGIN-1);
     * g.setColor(Color.black);
     *
     * g.setFont(new Font("Helvetica", Font.PLAIN, 10));
     * g.drawString(jj+"-"+mois,1,10); g.drawString(h.toString(),1,25);
     */
  }

  private ScheduleObject getSchedule(Vector<ScheduleObject> plans, int jj, Hour hc) {

    if (plans != null) {
      for (int i = 0; i < plans.size(); i++) {
        ScheduleObject p = plans.elementAt(i);
        DateFr d = p.getDate();
        if (d.getDay() != jj) {
          continue;
        }
        Hour hd = p.getStart();
        Hour hf = p.getEnd();
        if (hc.ge(hd) && hc.le(hf)) {
          return p;
        }
      }
    }
    return null;
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    //throw new UnsupportedOperationException("Not supported yet.");
  }
}
