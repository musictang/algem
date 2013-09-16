/*
 * @(#)DayPlanView.java 2.7.a 30/11/12
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
package net.algem.planning.day;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.print.Printable;
import java.util.*;
import net.algem.config.ColorPlan;
import net.algem.course.Course;
import net.algem.planning.*;

/**
 * Day schedule layout.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class DayPlanView
        extends ScheduleCanvas
        implements Printable
{

  private static final int H_START = 540;
  private static final int GRID_Y = 30; // orig : 28
  
  private int ncols = 5;
  private int pas_y;
  private int th;
  private FontMetrics fm;
  private Dimension dim;
  private Graphics bg;
  private Date date;
  private Calendar cal;
  private Vector<DayPlan> cols;
  private int top;

  public DayPlanView(Date d) {
    cols = new Vector<DayPlan>();
    date = d;
    cal = Calendar.getInstance(Locale.FRANCE);
    cal.setTime(d);
    pas_x = 100;

    addMouseListener(this);
    
  }

  public DayPlanView() {
    this(new Date());
  }

  public void addCol(DayPlan p) {
    cols.addElement(p);
  }

  public void setDate(Date d) {
    cal.setTime(d);
    date = d;
  }

  public Date getDate() {
    return date;
  }

  public void load(Date d, Vector<DayPlan> cols) {
    cal.setTime(d);
    date = d;
    this.cols = cols;
    img = null;
    repaint();
  }

  public void clear() {
    cols = new Vector<DayPlan>();
    img = null;
  }

  /*public void print() {
    Component c = this;
    while (c.getParent() != null) {
      c = c.getParent();
    }
    if (c instanceof Frame && img != null) {
      PrintJob prn = Toolkit.getDefaultToolkit().getPrintJob((Frame) c, "edition", null);
      Graphics g = prn.getGraphics();
      g.drawImage(img, 0, 0, this);
      prn.end();
    }
  }*/

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    if (img == null) {
      dim = getSize();
      pas_y = (dim.height - MARGEH) / GRID_Y;
      img = createImage(dim.width, dim.height);
      bg = img.getGraphics();
      bg.setFont(NORMAL_FONT);
      fm = bg.getFontMetrics();
      th = fm.getHeight() + 4;
      drawBackground();
    }
//		g.drawImage(img, in.left, in.top, this);
    g.drawImage(img, 0, 0, this);
  }

  /**
   * Draws the time grid and schedules.
   */
  @Override
  public void drawBackground() {
    //Dimension d = getSize();
    //pas_x = 100;
    ncols = dim.width / pas_x;
    drawGrid();

    if (cols == null) {
      return;
    }

    for (int i = top; i < top + ncols && i < cols.size(); i++) {
      DayPlan pj = cols.elementAt(i);
      drawSchedules(i, pj.getSchedule());
      drawScheduleRanges(i, pj.getScheduleRange());
    }

    for (int i = top; i < top + ncols && i < cols.size(); i++) {
      DayPlan pj = (DayPlan) cols.elementAt(i);
      Vector<ScheduleObject> v = pj.getSchedule();
      ScheduleObject prv = new CourseSchedule();
      prv.setActivity(new Course(""));
      prv.setIdPerson(0);
      for (int j = 0; j < v.size(); j++) {
        ScheduleObject p = v.elementAt(j);
        ScheduleObject prev = (j == 0) ? prv : v.elementAt(j - 1);
        textRange(i, p, prev); // ajout des textes sur les plannings
      }

    }
  }

  private void drawGrid() {
    int x = MARGED + (pas_x / 2) + 1;
    int y = th;
    // Dessin des entetes de colonne sur la partie visible
    for (int i = top; i < top + ncols && i < cols.size(); i++) {
      DayPlan p = cols.elementAt(i);
      String s = p.getLabel();
      int w = fm.stringWidth(s) + 4;
      while (w > pas_x) {
        s = s.substring(0, s.length() - 1);
        w = fm.stringWidth(s) + 4;
      }
      bg.drawString(s, x - (w - 4) / 2, y);
      bg.drawLine(x - (pas_x / 2), 0, x - (pas_x / 2), dim.height);
      x += pas_x;
    }
    bg.drawLine(x - (pas_x / 2), 0, x - (pas_x / 2), dim.height);
    bg.drawLine(0, MARGEH, dim.width, MARGEH);
    x = 5;
    y = MARGEH + pas_y;
    Hour heure = new Hour(H_START);
    // tracé des libellés des heures sur la colonne de gauche
    for (int i = 0; i < GRID_Y; i++) {
      bg.drawString(heure.toString(), x, y);
      heure.incMinute(30);
      y += pas_y;
    }
    bg.setColor(Color.gray);
    x = 5;
    y = MARGEH + (pas_y * 2);
    // tracé des lignes de séparation
    for (int i = 0; i < GRID_Y; i += 2, y += (pas_y * 2)) {
      bg.drawLine(2, y + 1, dim.width, y + 1);
    }
  }

   /**
   * Schedule coloring.
   *
   * @param i column number
   * @param v schedule list
   */
  protected void drawSchedules(int i, Vector<ScheduleObject> v) {
    for (int j = 0; j < v.size(); j++) {
      ScheduleObject p = v.elementAt(j);
      Color c = getScheduleColor(p);
      drawRange(i, p, c, pas_x); // dessin des plannings p comme ScheduleObject
      if (p.getType() == Schedule.MEMBER_SCHEDULE
              || p.getType() == Schedule.GROUP_SCHEDULE) {
        if (p.getNote() == -1) {
          flagNotPaid(i, p.getStart().toMinutes(), p.getEnd().toMinutes(), c);
        }
      }
    }
  }
  
  /**
   * Schedule range coloring. 
   *
   * @param i
   * @param vpl
   */
  private void drawScheduleRanges(int i, Vector<ScheduleRangeObject> vpl) {
    if (vpl == null || vpl.isEmpty()) {
      return;
    }
    Collections.sort(vpl, new ScheduleRangeComparator());
    java.util.List<ScheduleRangeObject> vp = new ArrayList<ScheduleRangeObject>(vpl);
    java.util.List<ScheduleRangeObject> vpci = getPlagesCoursCoInst(vp);
    if (vpci != null) {
      vp.removeAll(vpci);
    }
    Color c = colorPrefs.getColor(ColorPlan.RANGE);
    // tracé des plages de cours individuels
    for (ScheduleRangeObject p : vp) {
      Course cc = p.getCourse();
      if (!cc.isCollective()) {
      drawRange(i, p, c, pas_x);
      }
    }
    if (vpci == null || vpci.isEmpty()) {
      return;
    }
    int n = 0; // nombre de participants
    int w = 0; // largeur de plage occupée
    int idx = 0; // index plage
    int idp = vpci.get(0).getScheduleId();
    // tracé des plages de cours collectifs
    for (int j = 0 ; j < vpci.size(); j++) {
      ScheduleRangeObject p = vpci.get(j);
      if (idp == p.getScheduleId()) {
        n++;
        idx = j;
        continue;
      }
      w = getScheduleRangeWidth(vpci.get(idx).getAction().getPlaces(), n);
      drawRange(i, vpci.get(idx), c, w);
      idp = p.getScheduleId();
      n = 1;
      idx = j;
    }
    w = getScheduleRangeWidth(vpci.get(idx).getAction().getPlaces(), n);
    drawRange(i, vpci.get(idx), c, w);
  }

  protected void drawRange(int col, ScheduleObject p, Color c, int w) {
    int deb = p.getStart().toMinutes();
    int fin = p.getEnd().toMinutes();

    int x = MARGED + 2 + ((col - top) * pas_x);
    int y = MARGEH + 2 + (((deb - H_START) * pas_y) / GRID_Y);
    int ht = ((fin - deb) * pas_y) / GRID_Y;
   
    bg.setColor(getScheduleRangeColor(p, c));
    //bg.fillRect(x, y, pas_x - 2, ht - 1);
    bg.fillRect(x, y, w - 1, ht - 1);
    bg.setColor(Color.black);
    // trait noir séparateur
    if (p instanceof CourseSchedule && p.getClass() != ScheduleRangeObject.class) {
      bg.drawLine(x, y-1, (x + w) -1,y-1);
    }
  }

  /**
   * Text coloring.
   *
   * @param colonne
   * @param p
   */
  public void textRange(int colonne, ScheduleObject p, ScheduleObject prev) {
    int deb = p.getStart().toMinutes();

    String nomProf = "";
    String lib = null;

    int x = MARGED + 1 + ((colonne - top) * pas_x);
    int y = MARGEH + 1 + (((deb - H_START) * pas_y) / GRID_Y);// MARGEH + 1  (orig : MARGEH + 2)

    bg.setColor(getTextColor(p));
    bg.setFont(NORMAL_FONT);
    
    showLabel(p, prev, x, y);
    showTeacher(p, prev, x, y);
   
  }
  
  private void showLabel(ScheduleObject p, ScheduleObject prev, int x, int y) {
    String code = getCode(p);
    String label = null;
    int offset = (pas_x / 2);
    if (!p.getScheduleLabel().equals(prev.getScheduleLabel())) {
      label = p.getScheduleLabel() + (code == null ? "" : code);
    } else {
      label = code;
      offset = (pas_x - 15);
    }
    if (label != null && !label.isEmpty()) {
      int w = fm.stringWidth(label) + 4;// largeur du texte

      while (w > pas_x) {
        label = label.substring(0, label.length() - 1);// on enlève un caractère
        w = fm.stringWidth(label) + 4; // on réduit la largeur en fonction
      }

      //bg.drawString(lib, x + (pas_x / 2) - (w - 4) / 2, y + 10);
      bg.drawString(label, x + offset - (w - 4) / 2, y + 10);
    }
  }
  
  private void showTeacher(ScheduleObject p, ScheduleObject prev, int x, int y) {
    
    String teacherName = null;
    if (p.getIdPerson() != prev.getIdPerson()) {
      int duree = p.getStart().getLength(p.getEnd());
      if ((p instanceof CourseSchedule || p instanceof WorkshopSchedule) && duree > 30) {
        teacherName = p.getPerson().getAbbrevFirstNameName();
        if (teacherName != null) {
          bg.setFont(SMALL_FONT);
          int w = fm.stringWidth(teacherName) + 4;
          while (w > pas_x) {
            teacherName = teacherName.substring(0, teacherName.length() - 1);
            w = fm.stringWidth(teacherName) + 4;
            //System.out.println("w = "+w);
          }
          bg.drawString(teacherName, x + (pas_x / 2) - (w - 4) / 2, y + 18);
        }
      }
    }
  }

  @Override
  public void flagNotPaid(int colonne, int deb, int fin, Color c) {
    int x = MARGED + 2 + ((colonne - top) * pas_x);
    int y = MARGEH + 2 + (((deb - H_START) * pas_y) / GRID_Y);
    //int ht = ((end - deb) * pas_y) / 30;
    bg.setColor(colorPrefs.getColor(ColorPlan.FLAG));
    bg.drawString("$$$", x, y + 12);
    bg.setColor(Color.black);
  }



  @Override
  public void processMouseEvent(MouseEvent e) {
    /*
     * if (e.isPopupTrigger()) { int	x = e.getX() - MARGED -2; int	y = e.getY()
     * - MARGEH -2;
     *
     * int	jj = ((x + (pas_x)/2) / pas_x) + 1; int	hh = ((y * 30)/12)+540; int
     * mm = hh % 60; hh /=	60; Date d = new Date(annee-1900,mois-1,jj,hh,mm);
     * popup.setLabel(d.toString());
     * popup.show(e.getComponent(),e.getX(),e.getY()); }
     */
    super.processMouseEvent(e);
  }

  public int getColId() {
    int x = clickx - MARGED - 2;
    int y = clicky - MARGEH - 2;

    int col = x / pas_x;
    col += top;
//		int	col = ((x + (pas_x)/2) / pas_x) + 1;
    if (col >= 0 && col < cols.size()) {
      return ((DayPlan) cols.elementAt(col)).getId();
    } else {
      return 0;
    }
  }

  /**
   * Runs when a schedule is clicked.
   *
   * @param e
   */
  @Override
  public void mouseClicked(MouseEvent e) {

    clickx = e.getX();
    clicky = e.getY();
    int x = clickx - MARGED - 2;
    int y = clicky - MARGEH - 2;

    int col = x / pas_x; // le numéro de la colonne
//		int	jj = ((x + (pas_x)/2) / pas_x) + 1;
    int hh = ((y * 30) / pas_y) + H_START; //heure
    int mm = hh % 60; // minute
    hh /= 60;

    Calendar c = (Calendar) cal.clone();
    c.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), hh, mm);

    if (col + top >= cols.size()) {
      return;
    }

    DayPlan pj = cols.elementAt(col + top);
    Vector<ScheduleObject> v = pj.getSchedule();
    clickSchedule = null;
    Hour hc = new Hour(hh, mm);
    for (int i = 0; i < v.size(); i++) {
      ScheduleObject p = v.elementAt(i);
      Hour hd = p.getStart();
      Hour hf = p.getEnd();
      if (hc.ge(hd) && hc.le(hf)) {
        clickSchedule = p;
        break;
      }
    }
    if (clickSchedule == null) {
      return;
    }

    clickRange = new Vector<ScheduleRangeObject>();
    Vector<ScheduleRangeObject> vpl = pj.getScheduleRange();
    
    // ajout des plages
    for (int i = 0; vpl != null && i < vpl.size(); i++) {
      ScheduleRangeObject pg = vpl.elementAt(i);
      Course cc = ((CourseSchedule) pg).getCourse();
      if (cc.isCollective()) { // les plages affichées sont restreintes aux limites des plannings
          if (pg.getScheduleId() == clickSchedule.getId()) {
            clickRange.add(pg);
          }
      } else {
        // les plages de plusieurs plannings peuvent être ajoutées si elles font
        // référence au même prof
        if (pg.getIdAction() == clickSchedule.getIdAction()
                && pg.getTeacher().getId() == clickSchedule.getIdPerson()) {
          clickRange.add(pg);
        }
      }

    }
    if (listener != null) {
      listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Click"));
    }
  }

  public void setTop(int t) {
    top = t;
    img = null;
    repaint();
  }

  public Rectangle computeScroll() {
    int r = ncols;
    int c = cols.size();
    return new Rectangle(top, r == 0 ? 1 : r, 0, c < 0 ? 0 : c);
  }

}
