/*
 * @(#)StopCourseDlg.java	2.6.w 04/09/14
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
package net.algem.planning.editing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.JPanel;
import net.algem.course.Course;
import net.algem.enrolment.CourseOrder;
import net.algem.enrolment.EnrolmentException;
import net.algem.enrolment.EnrolmentService;
import net.algem.enrolment.EnrolmentUpdateEvent;
import net.algem.enrolment.StopCourseAbstractDlg;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.MessagePopup;

/**
 * Dialog used to stop course.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 1.0a 27/09/2001
 */
public class StopCourseDlg
        extends StopCourseAbstractDlg
{

  private GemDesktop desktop;
  private Course course;
  private int member;
  private CourseOrder courseOrder;
  private EnrolmentService service;


  public StopCourseDlg(GemDesktop desktop, int member, CourseOrder courseOrder, Course c) throws SQLException {
    super(desktop.getFrame(), BundleUtil.getLabel("Course.stop.label"), true);//modal
    this.desktop = desktop;
    service = new EnrolmentService(desktop.getDataCache());
    this.courseOrder = courseOrder;
    course = c;
    this.member = member;
    
    view = new StopCourseView(course.getTitle());

    btOk = new GemButton(GemCommand.VALIDATION_CMD);
    btOk.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    JPanel buttons = new JPanel();
    buttons.setLayout(new GridLayout(1,1));
    buttons.add(btOk);
    buttons.add(btCancel);

    setLayout(new BorderLayout());
    add(view, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setSize(GemModule.XXS_SIZE);
    setLocationRelativeTo(desktop.getFrame());
  }

  
  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btCancel) {
    } else if (evt.getSource() == btOk) {
      stopCourse();
    }
    close();
  }

  /**
   * Used when right click in the list of enrolment.
   * The course is stopped from the beginning of next week (excepted for the course
   * of type ATP, on one day only, by definition).
   *
   */
  private void stopCourse() {

    DateFr start = checkDate(view.getDateStart());

    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    try {
      service.stopCourse(member, courseOrder, course, start);
      desktop.postEvent(new ModifPlanEvent(this, start, courseOrder.getDateEnd()));
      desktop.postEvent(new EnrolmentUpdateEvent(this, member));
    } catch (EnrolmentException ex) {
      MessagePopup.warning(view, ex.getMessage());
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }

  }

}
