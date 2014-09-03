/*
 * @(#)StopCourseFromModuleDlg.java	2.8.a 22/04/13
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
import java.util.Calendar;
import javax.swing.JDialog;
import javax.swing.JPanel;
import net.algem.course.Course;
import net.algem.enrolment.CourseOrder;
import net.algem.enrolment.EnrolmentException;
import net.algem.enrolment.EnrolmentService;
import net.algem.enrolment.EnrolmentUpdateEvent;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.MessagePopup;

/**
 * Dialog for stopping course.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:damien.loustau@gmail.com">Damien Loustau</a>
 * @version 2.8.t
 * @since 1.0a 27/09/2001
 */
public class StopCourseFromModuleDlg
        extends JDialog
        implements ActionListener
{

  private GemDesktop desktop;
  private Course course;
  private int member;
  private CourseOrder courseOrder;
  private StopCourseView view;
  private GemButton btOk;
  private GemButton btCancel;
  private EnrolmentService service;

  public StopCourseFromModuleDlg(GemDesktop _desktop, int member, CourseOrder courseOrder, Course c) throws SQLException {
    super(_desktop.getFrame(), "Arret inscription formule", true);//modal
    init(_desktop, member, courseOrder, c);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btOk) {
      stopCourseFromModule();
    }
    close();
  }

  public DateFr getDateEnd() {
    return checkDate(view.getDateStart());
  }

  /**
   * Used when right click in the list of enrolment.
   * The course is stopped from the beginning of next week (excepted for the course
   * of type ATP, on one day only, by definition).
   *
   */
  private void stopCourseFromModule() {

    DateFr start = checkDate(view.getDateStart());

    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    try {
      service.stopCourseFromModule(member, courseOrder, course, start);
      desktop.postEvent(new ModifPlanEvent(this, start, courseOrder.getDateEnd()));
      desktop.postEvent(new EnrolmentUpdateEvent(this, member));
    } catch (EnrolmentException ex) {
      MessagePopup.warning(view, ex.getMessage());
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }

  }

  /**
   * Checks the start date when stopping a course.
   * If the selected day is not a Sunday and if the modification is confirmed,
   * the date is automatically modified to the next Sunday.
   *
   * @param start date
   * @return a date
   */
  public DateFr checkDate(DateFr start) {
    DateFr d = start;
    Calendar cal = Calendar.getInstance();
    cal.setTime(d.getDate());
    if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
      if (MessagePopup.confirm(null,
              MessageUtil.getMessage("stopping.course.date.confirmation"),
              BundleUtil.getLabel("Confirmation.title"))) {
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
          cal.add(Calendar.DAY_OF_WEEK, 1);
        }
        d = new DateFr(cal.getTime());
      }
    }
    return d;
  }

  private void init(GemDesktop _desktop, int member, CourseOrder co, Course c) throws SQLException {
    desktop = _desktop;
    service = new EnrolmentService(desktop.getDataCache());
    courseOrder = co;
    course = c;
    this.member = member;

    view = new StopCourseView(course.getTitle());

    btOk = new GemButton(GemCommand.VALIDATION_CMD);
    btOk.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    JPanel buttons = new JPanel();
    buttons.setLayout(new GridLayout(1, 1));
    buttons.add(btOk);
    buttons.add(btCancel);

    setLayout(new BorderLayout());
    add(view, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setSize(GemModule.XXS_SIZE);
    setLocationRelativeTo(desktop.getFrame());
  }

  private void close() {
    setVisible(false);
    dispose();
  }

  @Override
  public String toString() {
    return getClass().getName();
  }
}
