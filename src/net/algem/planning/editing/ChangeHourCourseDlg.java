/*
 * @(#)ChangeHourCourseDlg.java	2.6.a 21/09/12
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
package net.algem.planning.editing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JPanel;
import net.algem.course.Course;
import net.algem.enrolment.*;
import net.algem.planning.*;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.ErrorDlg;
import net.algem.util.ui.GemButton;

/**
 * Modification of course time in member enrolment course order.
 * TODO
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 27/09/2001
 */
public class ChangeHourCourseDlg
        extends JDialog
        implements ActionListener
{

  private GemDesktop desktop;
  private DataCache dataCache;
  private Course course;
  private Order order;
  private CourseOrder courseOrder;
  private ChangeHourCourseView view;
  private GemButton btValidation;
  private GemButton btCancel;
  private JPanel buttons;
  private boolean validation = false;
  private EnrolmentService service;

  public ChangeHourCourseDlg(GemDesktop _desktop, EnrolmentService service, Order _commande, CourseOrder _ccours) throws SQLException {
    super(_desktop.getFrame(), "Changement heure de cours", true);//modal
    this.service = service;
    init(_desktop, _commande, _ccours);
  }

  public void init(GemDesktop _desktop, Order _commande, CourseOrder _ccours) throws SQLException {
    desktop = _desktop;
    dataCache = desktop.getDataCache();

    courseOrder = _ccours;
    order = _commande;

    course = service.getCourse(courseOrder.getAction());
    view = new ChangeHourCourseView(course.getTitle(), courseOrder.getStart(), courseOrder.getEnd());

    btValidation = new GemButton(GemCommand.VALIDATION_CMD);
    btValidation.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    buttons = new JPanel();
    buttons.setLayout(new GridLayout(1,1));
    buttons.add(btValidation);
    buttons.add(btCancel);

    setLayout(new BorderLayout());
    getContentPane().add(view, BorderLayout.NORTH);
    getContentPane().add(buttons, BorderLayout.SOUTH);
    pack();
    setLocationRelativeTo(desktop.getFrame());
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btCancel) {
    } else if (evt.getSource() == btValidation) {
      validation = true;
      changeHour();
    }
    setVisible(false);
    dispose();
  }

  public CourseOrder getCourseOrder() {
    if (validation) {
      return courseOrder;
    } else {
      return null;
    }
  }

  /**
   * Modification of time slot.
   */
  void changeHour() {
    DateFr start = view.getDateStart();
    Hour duration = new Hour(courseOrder.getStart().getDuration(courseOrder.getEnd()));
    Hour hStart = view.getHour();
    Hour hEnd = new Hour(hStart);
    hEnd.incMinute(duration.toMinutes());

    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    
    //auparavant la recherche se faisait à partir du début de période
    // recherche du planning par défaut pour ce cours
		Schedule pp = service.get1PlanCours(courseOrder.getAction(), dataCache.getStartOfYear());
    Hour hnull = new Hour("00:00:00");
    Vector<HourRange> vpc = null;
    try {
      vpc = service.getPlageCours(pp);
    } catch (EnrolmentException ex) {
      GemLogger.logException(ex);
    }
    if (hStart.equals(hnull)
            || duration.equals(hnull)
            || vpc == null
            || vpc.size() < 1) {
      new ErrorDlg(this, MessageUtil.getMessage("invalid.time.slot"));
      return;
    }

    // vérifier que la plage horaire est bien comprise dans le planning
    boolean inRange = false;
    for (int i = 0; i < vpc.size(); i++) {
      HourRange ph = vpc.elementAt(i);
      if (hStart.ge(ph.getStart()) && hEnd.le(ph.getEnd())) {
        inRange = true;
        break;
      }
    }
    if (!inRange) {
      new ErrorDlg(this, MessageUtil.getMessage("time.slot.out.of.range"));
      return;
    }

    // Vérification des conflits
    Vector<ScheduleRange> pl = new Vector<ScheduleRange>();
//TODO EG 18/08/09 conflits à verifier
//		String query = "select distinct debut,end from plage where cours="+ccours.getAction()+" and jour>= '"+debut+"' and jour<='"+ccours.getDateEnd()+"' and adherent<>"+commande.getMember()+" order by debut";
    String query = "SELECT DISTINCT debut,fin FROM plage WHERE cours="+courseOrder.getAction()+" AND jour>= '"+start+"' AND jour<='"+courseOrder.getDateEnd()+"' AND salle='"+pp.getPlace()+"' AND adherent <> "+order.getMember()+" ORDER BY DEBUT";
    try {
      java.sql.ResultSet rs = dataCache.getDataConnection().executeQuery(query);
      while (rs.next()) {
        ScheduleRange p = new ScheduleRange();
        p.setCourseId(courseOrder.getAction()); //?
        p.setStart(new Hour(rs.getString(1)));
        p.setEnd(new Hour(rs.getString(2)));
        pl.addElement(p);
      }
      rs.close();
    } catch (Exception e) {
      GemLogger.logException(e);
    }
    if (pl != null && pl.size() > 0) {
      Enumeration enu = pl.elements();
      while (enu.hasMoreElements()) {
        ScheduleRange p = (ScheduleRange) enu.nextElement();
        if ((hStart.ge(p.getStart()) && hStart.lt(p.getEnd())) || (hEnd.gt(p.getStart()) && hEnd.le(p.getEnd()))) {
          new ErrorDlg(view, MessageUtil.getMessage("time.slot.conflict.detail", p));
          return;
        }
      }
    }
    try {
      ModifPlanEvent evt = new ModifPlanEvent(this, start, courseOrder.getDateEnd());
      String where = "SET debut='"+hStart+"',fin='"+hEnd+"' WHERE cours="+courseOrder.getAction()+" AND adherent="+order.getMember()+" AND debut='"+courseOrder.getStart()+"' AND jour >= '"+start+"' AND jour <= '"+courseOrder.getDateEnd()+"'";
			ScheduleRangeIO.update(where, dataCache.getDataConnection());
      if (start.after(courseOrder.getDateStart())) {
        DateFr dateEnd = courseOrder.getDateEnd();
        courseOrder.setDateEnd(start);
        //update de l'ancienne planification de commande cours
        CourseOrderIO.update(courseOrder, dataCache.getDataConnection());
        // création d'une nouvelle planification de commande cours
        courseOrder.setDateStart(start);
        courseOrder.setDateEnd(dateEnd);
        courseOrder.setStart(hStart);
        courseOrder.setEnd(hStart.end(duration.toMinutes()));
        CourseOrderIO.insert(courseOrder, dataCache.getDataConnection());
      } else {
        courseOrder.setStart(hStart);
        courseOrder.setEnd(hStart.end(duration.toMinutes()));
        CourseOrderIO.update(courseOrder, dataCache.getDataConnection());
      }
      desktop.postEvent(evt);
      desktop.postEvent(new EnrolmentUpdateEvent(this, order.getMember()));
    } catch (Exception ex) {
      GemLogger.logException(ex);
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }
    
  }

  @Override
  public String toString() {
    int adh = order == null ? 0 : order.getMember();
    return getClass().getSimpleName()+" "+adh;
  }
}
