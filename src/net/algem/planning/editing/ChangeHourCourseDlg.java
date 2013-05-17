/*
 * @(#)ChangeHourCourseDlg.java	2.8.a 15/04/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JPanel;
import net.algem.course.Course;
import net.algem.enrolment.CourseOrder;
import net.algem.enrolment.EnrolmentService;
import net.algem.enrolment.EnrolmentUpdateEvent;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.ScheduleRange;
import net.algem.util.*;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.MessagePopup;

/**
 * Modification of course time in member enrolment course order.
 * TODO
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 1.0a 27/09/2001
 */
public class ChangeHourCourseDlg
        extends JDialog
        implements ActionListener
{

  private GemDesktop desktop;
  private DataCache dataCache;
  private Course course;
  private int member;
  private CourseOrder courseOrder;
  private int action;
  private ChangeHourCourseView view;
  private GemButton btValidation;
  private GemButton btCancel;
  private JPanel buttons;
  private boolean validation = false;
  private EnrolmentService service;

  public ChangeHourCourseDlg(GemDesktop _desktop, EnrolmentService service, CourseOrder co, int member) throws SQLException {
    super(_desktop.getFrame(), "Changement heure de cours", true);//modal
    this.desktop = _desktop;
    dataCache = desktop.getDataCache();
    this.service = service;
    courseOrder = co;
    this.member = member;
//    this.order = order;
  }

  public void init() throws SQLException {
    action = courseOrder.getAction();
    course = service.getCourse(action);
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
    Hour length = new Hour(courseOrder.getStart().getLength(courseOrder.getEnd()));
    Hour hStart = view.getHour();
    Hour hEnd = new Hour(hStart);
    hEnd.incMinute(length.toMinutes());
    if (hStart.equals(new Hour(Hour.NULL_HOUR))
            || length.equals(new Hour(Hour.NULL_HOUR))) {
      MessagePopup.warning(this, MessageUtil.getMessage("invalid.time.slot"));
      return;
    }
    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    DataConnection dc = dataCache.getDataConnection();
    try {
      dc.setAutoCommit(false);
      // Vérification des conflits
      List<ScheduleRange> vr = service.getRangeOverlap(start, member, hStart, hEnd, action);
      if (!vr.isEmpty()) {
        MessagePopup.warning(this, MessageUtil.getMessage("time.slot.conflict.detail", vr.size()));
        return;
      }
      service.deleteRange(start, member, action);
      // si la demande d'arret est postérieure à la date d'inscription à ce cours
      if (start.after(courseOrder.getDateStart())) {
        courseOrder.setDateEnd(start);
        service.update(courseOrder);
        courseOrder.setStart(hStart);
        courseOrder.setEnd(hEnd);
        courseOrder.setDateStart(start);
        courseOrder.setDateEnd(dataCache.getEndOfYear());
        service.create(courseOrder);
      } else {// si la demande d'arret est antérieure ou égale à la date d'inscription à ce cours
        courseOrder.setStart(hStart);
        courseOrder.setEnd(hEnd);
        service.update(courseOrder);
      }
      service.updateRange(courseOrder, member);
      dc.commit();
      desktop.postEvent(new ModifPlanEvent(this, courseOrder.getDateStart(), courseOrder.getDateEnd()));
      desktop.postEvent(new EnrolmentUpdateEvent(this, member));
    } catch (SQLException ex) {
      dc.rollback();
      desktop.postEvent(new EnrolmentUpdateEvent(this, member));
      GemLogger.logException(ex);
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }

  }

  @Override
  public String toString() {
    return getClass().getSimpleName()+" "+member;
  }
}
