/*
 * @(#)MonthPlanView.java	2.8.p 13/11/13
 *
 * Copyright (cp) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
 * @version 2.8.p
 */
public class MonthPlanView
        extends ScheduleCanvas
        implements MouseMotionListener
{

  private static final int GRID_Y = 30;// orig : 28 (subdivisions)
  private static final int H_DEB = 540; // -> 9:00 (orig : 600)
  private static final int H_FIN = 1440;

  private Calendar cal;
  private String[] dayNames;
  private int pas_y = 12;
  private int th;
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
    month = cal.get(Calendar.MONTH) + 1;
    pas_x = 10;
    dayNames = new DateFormatSymbols(Locale.FRANCE).getShortWeekdays();

    addMouseListener(this);
    addMouseMotionListener(this);
    //enableEvents(AWTEvent.MOUSE_EVENT_MASK);
  }

  public void load(Date d, Vector<ScheduleObject> pl, Vector<ScheduleRangeObject> pg) {
    cal.setTime(d);
    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH) + 1;
    schedules = pl;
    ranges = pg;
    clickSchedule = null;
    clickRange = null;
    //System.out.println("MonthPlanView load pl="+pl.size()+" pg="+pg.size());
    img = null;
    repaint();
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    if (img == null) {
      Dimension d = getSize();
      img = createImage(d.width, d.height);
      bg = img.getGraphics();
      bg.setFont(new Font("Helvetica", Font.PLAIN, 8));
      fm = bg.getFontMetrics();
      th = fm.getHeight() + 4;
      drawBackground();
    }
//    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    //g.drawImage(img, in.left, in.top, this);
    g.drawImage(img, 0, 0, this);
//    setCursor(Cursor.getDefaultCursor());
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
    int x = MARGED + 1;
    int y = th;
    cal.set(year, month - 1, 1);
    cal.setTime(cal.getTime());
    int jj = cal.get(Calendar.DAY_OF_WEEK);
    int maxd = DateLib.daysInMonth(month, year);

    Dimension d = getSize();
    pas_x = (d.width - MARGED) / maxd;
    pas_y = ((d.height - MARGEH) / GRID_Y);

    for (int i = 1; i <= maxd; i++) {
      int w = fm.stringWidth(dayNames[jj]) + 10;
      bg.drawString(dayNames[jj], x - (w - 10) / 2, y);
      if (jj == Calendar.SUNDAY) {
        drawRange(i, H_DEB, H_FIN, Color.gray); // de 10h à 24h
      }
      if (++jj > 7) {
        jj = 1;
      }

      String dd = String.valueOf(i);
      w = fm.stringWidth(dd) + 10;
      bg.drawString(dd, x - (w - 10) / 2, y + 10);
      bg.drawLine(x - (pas_x / 2), 2, x - (pas_x / 2), MARGEH + (pas_y * GRID_Y));
      x += pas_x;
    }
    bg.drawLine(x - (pas_x / 2), 2, x - (pas_x / 2), MARGEH + (pas_y * GRID_Y));
    x = 5;
    y = MARGEH + pas_y;
    Hour heure = new Hour(H_DEB); // orig : "10:00"
    for (int i = 0; i < GRID_Y; i++) {
      bg.drawString(heure.toString(), x, y);
      heure.incMinute(30);
      y += pas_y;
    }
    bg.drawLine(2, MARGEH + 1, MARGED + (pas_x * maxd) - (pas_x / 2), MARGEH + 1);
    bg.setColor(Color.gray);

    y = MARGEH + (pas_y * 2);
    for (int i = 0; i < GRID_Y; i += 2, y += (pas_y * 2)) {
      bg.drawLine(2, y + 1, MARGED + (pas_x * maxd) - (pas_x / 2), y + 1);
    }
  }

  private void drawSchedules(Vector<ScheduleObject> plans) {
    for (int i = 0; i < plans.size(); i++) {
      ScheduleObject p = (ScheduleObject) plans.elementAt(i);
      Color c = getScheduleColor(p);
      drawRange(p, c, pas_x);
      if (p.getType() == Schedule.MEMBER
              || p.getType() == Schedule.GROUP) {
        if (p.getNote() == -1) {
          c = colorPrefs.getColor(ColorPlan.FLAG);
          flagNotPaid(p.getDate().getDay(), p.getStart().toMinutes(), p.getEnd().toMinutes(), c);
        }
      }
    }
  }

  private void drawScheduleRanges(Vector<ScheduleRangeObject> vpl) {
    if (vpl == null || vpl.isEmpty()) {
      return;
    }
    Collections.sort(vpl, new ScheduleRangeComparator());
    java.util.List<ScheduleRangeObject> vp = new ArrayList<ScheduleRangeObject>(vpl);
    java.util.List<ScheduleRangeObject> vpci = getPlagesCoursCoInst(vp);
    if (vpci != null) {
      vp.removeAll(vpci);
    }

    Color cp = colorPrefs.getColor(ColorPlan.RANGE);
    // tracé des plages de cours individuels
    for (ScheduleRangeObject p : vp) {
      Course cc = p.getCourse();
      if (!cc.isCollective()) {
        drawRange(p, cp, pas_x);
      }
    }
    if (vpci == null || vpci.isEmpty()) {
      return;
    }
    int idp = vpci.get(0).getScheduleId();
    int n = 0; // nombre de participants
    int w = 0; // largeur de plage occupée
    int idx = 0; // index plage
    // tracé des plages de cours collectifs
    for (int j = 0; j < vpci.size(); j++) {
      ScheduleRangeObject p = vpci.get(j);
      if (idp == p.getScheduleId()) {
        n++;
        idx = j;
        continue;
      }
      w = getScheduleRangeWidth(vpci.get(idx).getAction().getPlaces(), n);
      drawRange(vpci.get(idx), cp, w);
      idp = p.getScheduleId();
      n = 1;
      idx = j;
    }
    w = getScheduleRangeWidth(vpci.get(idx).getAction().getPlaces(), n);
    drawRange(vpci.get(idx), cp, w);
  }

  public void drawRange(DateFr j, Hour deb, Hour fin, Color c) {
    drawRange(j.getDay(), deb.toMinutes(), fin.toMinutes(), c);
  }

  public void drawRange(int jour, int deb, int fin, Color c) {
    int x = MARGED + 2 + ((jour - 1) * pas_x) - (pas_x / 2);
    int y = MARGEH + 2 + (((deb - H_DEB) * pas_y) / GRID_Y);
    int ht = ((fin - deb) * pas_y) / GRID_Y;
    bg.setColor(c);
    bg.fillRect(x, y, pas_x - 1, ht - 1);
    bg.setColor(Color.black);
  }

  public void drawRange(ScheduleObject p, Color c, int w) {
    int deb = p.getStart().toMinutes();
    int fin = p.getEnd().toMinutes();
    int jour = p.getDate().getDay();

    int x = MARGED + 2 + ((jour - 1) * pas_x) - (pas_x / 2);
    int y = MARGEH + 2 + (((deb - H_DEB) * pas_y) / GRID_Y);
    int ht = ((fin - deb) * pas_y) / GRID_Y;
    bg.setColor(c);
    bg.fillRect(x, y, w - 1, ht - 1);
    bg.setColor(Color.black);
    // trait noir séparateur
    if (p instanceof CourseSchedule && p.getClass() != ScheduleRangeObject.class) {
      bg.drawLine(x, y - 1, (x + w) - 1, y - 1);
    }
    if (p.getType() == Schedule.MEMBER || p.getType() == Schedule.GROUP) {
      if (p.getNote() == -1) {
        flagNotPaid(p.getDate().getDay(), p.getStart().toMinutes(), p.getEnd().toMinutes(), c);
      }
    }
  }

  private void textRange(Vector<ScheduleObject> plans) {
    for (int i = 0; i < plans.size(); i++) {
      ScheduleObject p = (ScheduleObject) plans.elementAt(i);
      if (!(p instanceof CourseSchedule)) {
        continue;
      }
      if (((Course) p.getActivity()).isCollective()) {
        int x = MARGED + 0 + ((p.getDate().getDay() - 1) * pas_x) - (pas_x - 8);
        int y = MARGEH + 0 + (((p.getStart().toMinutes() - H_DEB) * pas_y) / GRID_Y);
        bg.setColor(getTextColor(p));
        bg.setFont(X_SMALL_FONT);
        showLabel(p, x, y);
      }
    }
  }

  private void showLabel(ScheduleObject p, int x, int y) {

    String code = getCode(p);
    int offset = (pas_x / 2);
    if (code != null && !code.isEmpty()) {
      int w = fm.stringWidth(code) + 4;// largeur du texte
      while (w > pas_x) {
        code = code.substring(0, code.length() - 1);// on enlève un caractère
        w = fm.stringWidth(code) + 4; // on réduit la largeur en fonction
      }
      bg.drawString(code, x + offset - (w - 4) / 2, y + 10);
    }
  }

  @Override
  public void flagNotPaid(int jour, int deb, int fin, Color c) {
    int x = MARGED + 2 + ((jour - 1) * pas_x) - (pas_x / 2);
    int y = MARGEH + 2 + (((deb - H_DEB) * pas_y) / GRID_Y);
//    int ht = ((end - deb) * pas_y) / 30;
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
     * if (e.isPopupTrigger()) { int	x = e.getX() - MARGED -2; int	y = e.getY()
     * - MARGEH -2;
     *
     * int	jj = ((x + (pas_x)/2) / pas_x) + 1; int	hh = ((y * 30)/pas_y)+540;
     * int	mm = hh % 60; hh /=	60; Date d = new
     * Date(annee-1900,mois-1,jj,hh,mm); popup.setLabel(d.toString());
     * popup.show(e.getComponent(),e.getX(),e.getY()); }
     */
    super.processMouseEvent(e);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    clickx = e.getX();
    clicky = e.getY();
    int x = clickx - MARGED - 2;
    int y = clicky - MARGEH - 2;

    int jj = ((x + (pas_x) / 2) / pas_x) + 1;
    int hh = ((y * 30) / pas_y) + H_DEB;
    int mm = hh % 60;
    hh /= 60;

    Hour h = new Hour(hh, mm);
    Graphics g = getGraphics();
    // ecrase frame du dessus ??
    //g.clipRect(0,0,MARGED-(pas_x/2)-1,MARGEH-1);
    g.setColor(getBackground());
    g.fillRect(0, 0, MARGED - (pas_x / 2) - 1, MARGEH - 1);
    g.setColor(Color.black);

    g.setFont(new Font("Helvetica", Font.PLAIN, 10));
    g.drawString(jj + "-" + month, 1, 10);
    g.drawString(h.toString(), 1, 25);

    clickSchedule = null;
    cal.set(year, month - 1, jj, hh, mm);
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
//              && pg.getTeacher().getId() == clickSchedule.getIdPerson() //ajout 1.1d
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

    int x = e.getX() - MARGED - 2;
    int y = e.getY() - MARGEH - 2;
    int jj = ((x + (pas_x) / 2) / pas_x) + 1;
    int hh = ((y * 30) / pas_y) + H_DEB;
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
     * int	jj = ((x + (pas_x)/2) / pas_x) + 1; int	hh = ((y * 30)/pas_y)+600;
     * int	mm = hh % 60; hh /=	60;
     *
     * Hour h = new Hour(hh,mm); Graphics g = getGraphics(); // ecrase
     * //frame du dessus ?? //g.clipRect(0,0,MARGED-(pas_x/2)-1,MARGEH-1);
     * g.setColor(getBackground()); g.fillRect(0,0,MARGED-(pas_x/2)-1,MARGEH-1);
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
