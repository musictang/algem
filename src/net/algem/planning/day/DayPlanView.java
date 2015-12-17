/*
 * @(#)DayPlanView.java 2.9.4.14 17/12/15
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
package net.algem.planning.day;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.print.Printable;
import java.util.*;
import java.util.List;
import net.algem.config.ColorPlan;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.Note;
import net.algem.course.Course;
import net.algem.planning.*;
import net.algem.room.DailyTimes;
import net.algem.util.DataCache;

/**
 * Day schedule layout.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.14
 * @since 1.0a 07/07/1999
 */
public class DayPlanView
        extends ScheduleCanvas
        implements Printable
{

  private int visibleCols = 5;
  private int lineHeight;
  private FontMetrics fm;
  private Dimension dim;
  private Graphics bg;
  private Date date;
  private Calendar cal;
  private Vector<DayPlan> cols;
  private boolean showRangeNames;

  /** Schedule type info used to differenciate label.
   *  @see Schedule
   */
  private int type;
  private ActionService actionService;

  public DayPlanView(Date d) {
    cols = new Vector<DayPlan>();
    this.date = d;
    cal = Calendar.getInstance(Locale.FRANCE);
    cal.setTime(d);
    step_x = 100;
    showRangeNames = ConfigUtil.getConf(ConfigKey.SCHEDULE_RANGE_NAMES.getKey()).equals("t");
    addMouseListener(this);
    this.actionService = new ActionService();
  }

  public DayPlanView() {
    this(new Date());
  }

  public void addCol(DayPlan p) {
    cols.addElement(p);
  }

  public void setDate(Date d) {
    cal.setTime(d);
    this.date = d;
  }

  public Date getDate() {
    return date;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public void load(Date d, Vector<DayPlan> cols) {
    cal.setTime(d);
    this.date = d;
    this.cols = cols;
    img = null;
    repaint();
  }

  public void clear() {
    cols = new Vector<DayPlan>();
    img = null;
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    if (img == null) {
      dim = getSize();
      step_y = (dim.height - TOP_MARGIN) / GRID_Y;
      img = createImage(dim.width, dim.height);
      bg = img.getGraphics();
      bg.setFont(NORMAL_FONT);
      if (bg instanceof Graphics2D) {
        ((Graphics2D) bg).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      }
      fm = bg.getFontMetrics();
      lineHeight = fm.getHeight() + 4;
      drawBackground();
    }
    g.drawImage(img, 0, 0, this);
  }

  /**
   * Draws the time grid and schedules.
   */
  @Override
  public void drawBackground() {
    visibleCols = dim.width / step_x;
    drawGrid();
    if (cols == null) {
      return;
    }
    ScheduleObject dummy = new ScheduleObject()
    {
      @Override
      public String getScheduleLabel() {
        return null;
      }

      @Override
      public String getScheduleDetail() {
        return null;
      }
    };
    int dow = cal.get(Calendar.DAY_OF_WEEK);
    for (int i = colOffset; i < colOffset + visibleCols && i < cols.size(); i++) {
      DayPlan pj = cols.elementAt(i);
      DailyTimes dt = pj.getDailyTime(dow);
      if (dt != null) {
        drawClosed(i, dt, dummy);
      }
      drawSchedules(i, pj.getSchedule());
      drawScheduleRanges(i, pj.getScheduleRange());
      drawScheduleFlag(i, pj.getSchedule());// must be the last
    }

    for (int i = colOffset; i < colOffset + visibleCols && i < cols.size(); i++) {
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

  protected void drawClosed(int col, DailyTimes dt, ScheduleObject dummy) {
    Hour start = dt.getOpening();
    Hour end = dt.getClosing();

    Hour first = new Hour(H_START);
    Hour last = new Hour("24:00");

    if (start != null && start.toMinutes() > H_START) {
      dummy.setStart(first);
      dummy.setEnd(start);
      drawRange(col, dummy, CLOSED_COLOR, step_x);
    }
    if (end != null && end.toMinutes() < 1440) {
      dummy.setStart(end.toString().equals(Hour.NULL_HOUR) ? first : end);
      dummy.setEnd(last);
      drawRange(col, dummy, CLOSED_COLOR, step_x);
    }
  }

  private void drawGrid() {
    int x = LEFT_MARGIN + (step_x / 2) + 1;
    int y = lineHeight;
    // Column headers
    for (int i = colOffset; i < colOffset + visibleCols && i < cols.size(); i++) {
      DayPlan p = cols.elementAt(i);
      String s = p.getLabel();
      int w = fm.stringWidth(s) + 4;
      // fits string in column
      while (w > step_x) {
        s = s.substring(0, s.length() - 1);
        w = fm.stringWidth(s) + 4;
      }
      bg.drawString(s, x - (w - 4) / 2, y);
      bg.drawLine(x - (step_x / 2), 0, x - (step_x / 2), dim.height);
      x += step_x;
    }
    // first vertical line
    bg.drawLine(x - (step_x / 2), 0, x - (step_x / 2), dim.height);
    // first horizontal line
    bg.drawLine(5 + fm.stringWidth("00:00") + 5, TOP_MARGIN, dim.width, TOP_MARGIN);

    x = 5;
    y = TOP_MARGIN + (fm.getHeight() / 2);

    Hour hour = new Hour(H_START);

    // Hour labels on left column
    for (int i = 0; i < GRID_Y; i++) {
      bg.drawString(hour.toString(), x, y);
      hour.incMinute(30);
      y += step_y;
    }
    bg.setColor(Color.gray);

    x = 5 + fm.stringWidth(hour.toString()) + 5;
    y = TOP_MARGIN + step_y;
    // Half hours lines (odd lines are dotted)

    Graphics2D g2d = (Graphics2D) bg.create();
    Stroke dotted = new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, new float[]{1f, 3f}, 0);
    g2d.setStroke(dotted);
    for (int i = 0; i < GRID_Y; i++, y += step_y) {
      if (0 == (i & 1)) {
        g2d.drawLine(x, y + 1, dim.width, y + 1);
      } else {
        bg.drawLine(x, y + 1, dim.width, y + 1);
      }
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
      drawRange(i, p, c, step_x); // dessin des plannings p comme ScheduleObject
      if (p.getType() == Schedule.MEMBER || p.getType() == Schedule.GROUP) {
        if (p.getNote() == -1) {
          flagNotPaid(i, p.getStart().toMinutes(), p.getEnd().toMinutes(), c);
        }
      }
    }
  }

  /**
   * Draw a flag inside each schedule with a memo.
   * @param i col
   * @param v list of schedules
   */
  private void drawScheduleFlag(int i, Vector<ScheduleObject> v) {
    for (int j = 0; j < v.size(); j++) {
      ScheduleObject p = v.elementAt(j);
      if (hasNoteForAction(p)) {
        drawActionNoteFlag(i, p.getStart().toMinutes(), Color.BLACK);
      }
    }
  }

  /**
   * Checks if this schedule has a planning memo.
   *
   * @param p schedule
   * @return true if a note exists
   */
  private boolean hasNoteForAction(ScheduleObject p) {
    Note n = DataCache.ACTION_MEMO_CACHE.get(p.getIdAction());
    if (n == null) {
      n = actionService.getMemo(p.getIdAction());
      if (n != null) {
        DataCache.ACTION_MEMO_CACHE.put(p.getIdAction(), n);
      }
    }
    return n != null;
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
    java.util.List<ScheduleRangeObject> vpci = getRangesCoursCoInst(vp);
    if (vpci != null) {
      vp.removeAll(vpci);
    }
    Color c = null;
    // individual schedule ranges
    for (ScheduleRangeObject p : vp) {
      Course cc = p.getCourse();
      c = getScheduleColor(p);
      if (cc != null && !cc.isCollective() || Schedule.ADMINISTRATIVE == p.getType()) {
        drawRange(i, p, c, step_x);
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
    for (int j = 0; j < vpci.size(); j++) {
      ScheduleRangeObject p = vpci.get(j);
      if (idp == p.getScheduleId()) {
        n++;
        idx = j;
        continue;
      }
      w = getScheduleRangeWidth(vpci.get(idx).getAction().getPlaces(), n);
      c = getScheduleColor(vpci.get(idx));// important! previous range color
      drawRange(i, vpci.get(idx), c, w);
      idp = p.getScheduleId();
      n = 1;
      idx = j;
    }
    // last schedule of the column
    c = getScheduleColor(vpci.get(idx));
    w = getScheduleRangeWidth(vpci.get(idx).getAction().getPlaces(), n);
    drawRange(i, vpci.get(idx), c, w);
  }

  protected void drawAgenda(int i, Vector<ScheduleRangeObject> vpl) {
    for (ScheduleRangeObject p : vpl) {
        drawRange(i, p, colorPrefs.getColor(ColorPlan.ADMINISTRATIVE).darker(), step_x);
    }
  }

  protected void drawRange(int col, ScheduleObject p, Color c, int w) {
    int pStart = p.getStart().toMinutes();
    int pEnd = p.getEnd().toMinutes();

    int x = setX(col, 2);
    int y = setY(pStart);
    int ht = setY(pEnd) - y;

    bg.setColor(getScheduleRangeColor(p, c));
    bg.fillRect(x, y, w - 1, ht - 1);
    bg.setColor(Color.black);
    // black line separator
    if (p instanceof CourseSchedule && p.getClass() != ScheduleRangeObject.class) {
      bg.drawLine(x, y - 1, (x + w) - 1, y - 1);
    }
    if (showRangeNames || Schedule.ADMINISTRATIVE == p.getType()) {
      textSubRange(p, x);
    }
  }

  private void drawActionNoteFlag(int col, int start, Color c){
    int x = setX(col, 2);
    int y = setY(start);
    bg.setColor(c);
    bg.fillPolygon(new int[]{x,x+6,x},new int[]{y,y,y+6},3);
  }

  private void textSubRange(ScheduleObject p, int x) {
    if (p instanceof CourseSchedule && p instanceof ScheduleRangeObject) {
        Course crs = ((CourseSchedule) p).getCourse();
        // Displays the member's name if the time range is not shared between several persons.
        if (crs != null && (!crs.isCollective() || ((ScheduleRangeObject) p).getAction().getPlaces() < 2) || Schedule.ADMINISTRATIVE == p.getType()) {
          showSubSubLabel((ScheduleRangeObject) p, x);
        }
      }

  }

  /**
   * Text coloring.
   *
   * @param col
   * @param p
   * @param prev
   */
  private void textRange(int col, ScheduleObject p, ScheduleObject prev) {
    int pStart = p.getStart().toMinutes();

    int x = setX(col, 1);
    int y = setY(pStart);

    bg.setColor(getTextColor(p));
    bg.setFont(NORMAL_FONT);

    showLabel(p, prev, x, y);
    showSubLabel(p, prev, x, y);
  }

  private void showLabel(ScheduleObject p, ScheduleObject prev, int x, int y) {
    String code = getCode(p);
    String label = null;
    int offset = (step_x / 2);
    if (p instanceof AdministrativeSchedule && Schedule.ADMINISTRATIVE == type) {
      label = ((AdministrativeSchedule) p).getRoomLabel();
    } else {
      if (!p.getScheduleLabel().equals(prev.getScheduleLabel())) {
        label = p.getScheduleLabel() + (code == null ? "" : code);
      } else {
        label = code;
        offset = (step_x - 15);
      }
    }

    if (label != null && !label.isEmpty()) {
      int w = fm.stringWidth(label) + 4;// largeur du texte

      while (w > step_x) {
        label = label.substring(0, label.length() - 1);// on enlève un caractère
        w = fm.stringWidth(label) + 4; // on réduit la largeur en fonction
      }
      bg.drawString(label, x + offset - (w - 4) / 2, y + 10);
    }
  }

  /**
   * Subtitle info on schedule.
   * @param p
   * @param prev
   * @param x
   * @param y
   */
  private void showSubLabel(ScheduleObject p, ScheduleObject prev, int x, int y) {

    String subLabel = null;
    if (p.getIdPerson() != prev.getIdPerson() || prev.getIdPerson() == 0) {
      int length = p.getStart().getLength(p.getEnd());
      if (length > 30 && (p instanceof CourseSchedule || p instanceof WorkshopSchedule || p instanceof StudioSchedule)) {
        if (p instanceof GroupStudioSchedule) {
          subLabel = ((GroupStudioSchedule) p).getActivityLabel();
        } else if (p instanceof TechStudioSchedule) {
          subLabel = ((TechStudioSchedule) p).getTechnicianLabel();
        } else {
          subLabel = p.getPerson().getAbbrevFirstNameName();
        }

        if (subLabel != null) {
          bg.setFont(SMALL_FONT);
          drawSubLabel(subLabel, x, y + (18), step_x);
          //supplement info line
          if (p instanceof TechStudioSchedule) {
            subLabel = ((TechStudioSchedule) p).getActivityLabel();
            drawSubLabel(subLabel, x, y + (28), step_x);
          }
        }
      }
    }
  }

  /**
   *
   * @param p range object
   * @param x horizontal position
   */
  private void showSubSubLabel(ScheduleRangeObject p, int x) {
    int length = p.getStart().getLength(p.getEnd());
    if (length > 56) {
      int y = setY(p.getEnd().toMinutes()) - (fm.getHeight() /2);
      String subSubLabel = null;
      if (Schedule.ADMINISTRATIVE == p.getType()) {
        subSubLabel = String.valueOf(p.getNoteValue());
      } else {
        subSubLabel = p.getMember() != null ? p.getMember().getCommunName() : "";
      }
      drawSubLabel(subSubLabel, x, y , step_x);
    }
  }

  private void drawSubLabel(String subLabel, int x, int y, int step_x) {
    int w = fm.stringWidth(subLabel) + 4;
    while (w > step_x) {
      subLabel = subLabel.substring(0, subLabel.length() - 1);
      w = fm.stringWidth(subLabel) + 4;
    }
    bg.drawString(subLabel, x + (step_x / 2) - (w - 4) / 2, y);
  }

  @Override
  public void flagNotPaid(int colonne, int deb, int fin, Color c) {
    int x = LEFT_MARGIN + 2 + ((colonne - colOffset) * step_x);
    int y = setY(deb);
    bg.setColor(colorPrefs.getColor(ColorPlan.FLAG));
    bg.drawString("$$$", x, y + 12);
    bg.setColor(Color.black);
  }

  @Override
  public void processMouseEvent(MouseEvent e) {
    /*
     * if (e.isPopupTrigger()) { int	x = e.getX() - RILEFT_MARGIN2; int	y = e.getY()
     * - TOP_MARGIN -2;
     *
     * int	jj = ((x + (step_x)/2) / step_x) + 1; int	hh = ((y * 30)/12)+540; int
     * mm = hh % 60; hh /=	60; Date d = new Date(annee-1900,mois-1,jj,hh,mm);
     * popup.setLabel(d.toString());
     * popup.show(e.getComponent(),e.getX(),e.getY()); }
     */
    super.processMouseEvent(e);
  }

  private int getColId() {
    int x = clickX - LEFT_MARGIN - 2;
    int y = clickY - TOP_MARGIN - 2;

    int col = x / step_x;
    col += colOffset;
//		int	col = ((x + (step_x)/2) / step_x) + 1;
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

    clickX = e.getX();
    clickY = e.getY();
    int x = clickX - LEFT_MARGIN - 2;
    int y = clickY - TOP_MARGIN - 2;

    int col = x / step_x; // le numéro de la colonne
//		int	jj = ((x + (step_x)/2) / step_x) + 1;
    int hh = ((y * 30) / step_y) + H_START; //heure
    int mm = hh % 60; // minute
    hh /= 60;

    Calendar c = (Calendar) cal.clone();
    c.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), hh, mm);

    if (col + colOffset >= cols.size()) {
      return;
    }

    DayPlan pj = cols.elementAt(col + colOffset);
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
      if (cc != null) {
        if (cc.isCollective()) {
          // les plages affichées sont restreintes aux limites des plannings
          if (pg.getScheduleId() == clickSchedule.getId()) {
            clickRange.add(pg);
          }
        } else {
          // les plages de plusieurs plannings peuvent être ajoutées si elles font
          // référence au même prof
          if (pg.getIdAction() == clickSchedule.getIdAction()
                  && pg.getTeacher() != null && pg.getTeacher().getId() == clickSchedule.getIdPerson()) {
            clickRange.add(pg);
          }
        }
      } else if (pg.getScheduleId() == clickSchedule.getId()) {
        clickRange.add(pg);
      }
    }
    if (listener != null) {
      listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Click"));
    }
  }

  public void setColOffset(int t) {
    colOffset = t;
    img = null;
  }

  public Rectangle computeScroll() {
    int c = cols.size();
    int newExtent = (visibleCols == 0 ? 1 : visibleCols);
    int newMin = 0;
    int newMax = c < 0 ? 0 : c + 1;

    return new Rectangle(colOffset,newExtent,newMin,newMax);
  }

  public List<DayPlan> getCurrentPlanning() {
    return cols;
  }
}
