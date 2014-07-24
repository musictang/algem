/*
 * @(#)MonthScheduleView.java	2.8.w 09/07/14
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
package net.algem.planning.month;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.teacher.TeacherChoice;
import net.algem.course.CourseChoice;
import net.algem.course.CourseChoiceTypeActiveModel;
import net.algem.course.CourseChoiceTypeModel;
import net.algem.planning.ScheduleDetailEvent;
import net.algem.room.Establishment;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.event.GemEvent;
import net.algem.util.model.GemCloseVetoException;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemView;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.TabPanel;

/**
 * Month schedule view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 */
public class MonthScheduleView
        extends GemView
        implements PropertyChangeListener
{

  private final static int PREF_WIDTH = 700;
  private final static int PREF_HEIGHT = 500;
  private GemList<Establishment> estabList;
  private MonthSchedule monthSchedule;
  private MonthPlanDetailView teacherView;
  private MonthPlanDetailView roomView[];
  private MonthPlanDetailView collectiveView;
  private MonthPlanDetailView privateView;
  private TabPanel tabPanel;
  private GemChoice teacherChoice;
  private GemChoice roomChoice[];
  private GemChoice collectiveChoice;
  private GemChoice privateChoice;

  public MonthScheduleView(GemDesktop desktop, MonthSchedule schedule, GemList<Establishment> list) {
    super(desktop, "Menu.month.schedule");

    monthSchedule = schedule;
    estabList = list;

    monthSchedule.addPropertyChangeListener(this);

    tabPanel = new TabPanel();

    getContentPane().add(tabPanel, BorderLayout.CENTER);

    //String s = dataCache.getLabel("Month.schedule.width");
    //int width = PREF_WIDTH;
    /*try {
      width = Integer.parseInt(s);
    } catch (Exception e) {
    }*/
    //s = dataCache.getLabel("Month.schedule.height");
    //int height = PREF_HEIGHT;
    /*try {
      height = Integer.parseInt(s);
    } catch (Exception e) {
    }*/

    setSize(PREF_WIDTH, PREF_HEIGHT);
    String s = "";
    if ((s = ConfigUtil.getConf(ConfigKey.TEACHER_MANAGEMENT.getKey())) != null && s.startsWith("t")) {
      teacherChoice = new TeacherChoice(dataCache.getList(Model.Teacher));
      teacherView = new MonthPlanTeacherView(teacherChoice);
      tabPanel.addItem(teacherView, BundleUtil.getLabel("Month.schedule.teacher.tab"));
    }

    roomChoice = new RoomChoice[estabList.getSize()];
    roomView = new MonthPlanDetailView[estabList.getSize()];

    for (int i = 0; i < estabList.getSize(); i++) {
      Establishment e = (Establishment) estabList.getElementAt(i);
      roomChoice[i] = new RoomChoice(dataCache.getList(Model.Room), e.getId());
      roomView[i] = new MonthPlanRoomView(roomChoice[i]);
      tabPanel.addItem(roomView[i], BundleUtil.getLabel("Rooms.label") + " " + e.getName());
    }

    if ((s = ConfigUtil.getConf(ConfigKey.COURSE_MANAGEMENT.getKey())) != null && s.startsWith("t")) {
      collectiveChoice = new CourseChoice(new CourseChoiceTypeModel(dataCache.getList(Model.Course), true));
      collectiveView = new MonthPlanCourseView(collectiveChoice, true);
      tabPanel.addItem(collectiveView, BundleUtil.getLabel("Month.schedule.collective.course.tab"));
      privateChoice = new CourseChoice(new CourseChoiceTypeActiveModel(dataCache.getList(Model.Course), false, true));//modif 1.1d : filtre sur les cours actifs
      privateView = new MonthPlanCourseView(privateChoice, false);
      tabPanel.addItem(privateView, BundleUtil.getLabel("Month.schedule.course.tab"));
    }

    tabPanel.setSelectedIndex(0);
  }

  @Override
  public void addActionListener(ActionListener l) {
    if (teacherView != null) {
      teacherView.addActionListener(l);
    }
    if (collectiveView != null) {
      collectiveView.addActionListener(l);
    }
    if (privateView != null) {
      privateView.addActionListener(l);
    }

    for (int i = 0; i < roomView.length; i++) {
      roomView[i].addActionListener(l);
    }
  }

  @Override
  public void removeActionListener(ActionListener l) {
    if (teacherView != null) {
      teacherView.removeActionListener(l);
    }
    if (collectiveView != null) {
      collectiveView.removeActionListener(l);
    }
    if (privateView != null) {
      privateView.removeActionListener(l);
    }

    for (int i = 0; i < roomView.length; i++) {
      roomView[i].removeActionListener(l);
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (teacherView != null) {
      teacherView.propertyChange(evt);
    }
    if (collectiveView != null) {
      collectiveView.propertyChange(evt);
    }
    if (privateView != null) {
      privateView.propertyChange(evt);
    }

    for (int i = 0; i < roomView.length; i++) {
      roomView[i].propertyChange(evt);
    }
  }

  @Override
  public void postEvent(GemEvent evt) {
    if (evt instanceof ScheduleDetailEvent) {
      ScheduleDetailEvent e = (ScheduleDetailEvent) evt;
      if (teacherView != null) {
        teacherView.detailChange(e);
      }
      if (collectiveView != null) {
        collectiveView.detailChange(e);
      }
      if (privateView != null) {
        privateView.detailChange(e);
      }

      for (int i = 0; i < roomView.length; i++) {
        roomView[i].detailChange(e);
      }
    } else if (teacherView != null && teacherView instanceof MonthPlanTeacherView) {        
        ((MonthPlanTeacherView) teacherView).reload(dataCache.getList(Model.Teacher));
    }
  }

  @Override
  public void print() {
  
    MonthPlanDetailView v = (MonthPlanDetailView) tabPanel.getSelectedComponent();

    PrinterJob job = PrinterJob.getPrinterJob();
    // A4 paper size in inches : 8.3 in × 11.7
    // A4 paper size in mm : 210 in × 297
    PrintRequestAttributeSet atts = new HashPrintRequestAttributeSet();
    // margins must be large enough to fit any printer (0.5 in = 12.7 mm)
    MediaPrintableArea printableArea = new MediaPrintableArea(12.7f, 12.7f, 184.60f, 271.60f, MediaSize.MM);

    atts.add(MediaSizeName.ISO_A4);
    atts.add(OrientationRequested.LANDSCAPE);
    atts.add(printableArea);
    atts.add(DialogTypeSelection.NATIVE);
    job.setPrintable(v.getCanvas());

    if (job.printDialog(atts)) {
      try {
        job.print(atts);
      } catch (PrinterException ex) {
        GemLogger.logException(ex);
      }
    }
  }

  @Override
  public void close() throws GemCloseVetoException {
    monthSchedule.removePropertyChangeListener(this);
    setVisible(false);
    dispose();
  }

}

