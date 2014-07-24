/*
 * @(#)DayScheduleView.java	2.8.w 08/07/14
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
package net.algem.planning.day;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.room.Establishment;
import net.algem.room.Room;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.GemCloseVetoException;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemView;
import net.algem.util.ui.TabPanel;

/**
 * Day schedule view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @version 1.0b 06/10/2001
 */
public class DayScheduleView
        extends GemView
        implements PropertyChangeListener
{

  private static final int PREF_WIDTH = 700;
  private static final int PREF_HEIGHT = 540;
  
  private DaySchedule daySchedule;
  private DayPlanTableView teacherView;

  /** Room view array with default size. Actual size is calculated in constructor. */
  private DayPlanTableView roomView[] = new DayPlanTableView[3];

  private TabPanel tabPanel;

  public DayScheduleView(GemDesktop desktop, DaySchedule daySchedule, GemList<Establishment> estabList) {
    super(desktop, "Menu.day.schedule");

    this.daySchedule = daySchedule;
    this.daySchedule.addPropertyChangeListener(this);
    
    tabPanel = new TabPanel();
    add(tabPanel, BorderLayout.CENTER);

    setSize(PREF_WIDTH, PREF_HEIGHT);
    // @add par Baptiste
    int b = 1;
    String s = null;
    if ((s = ConfigUtil.getConf(ConfigKey.TEACHER_MANAGEMENT.getKey())) != null && s.startsWith("t")) {
      teacherView = new DayPlanTeacherView(dataCache.getList(Model.Teacher));
      tabPanel.addItem(teacherView, BundleUtil.getLabel("Day.schedule.teacher.tab"));
    }
    
    // récupération de la liste des salles
    GemList<Room> vs = dataCache.getList(Model.Room);
    roomView = new DayPlanTableView[estabList.getSize()];
    // ajout des onglets pour les différents établissements
    for (int i = 0; i < estabList.getSize() && i < roomView.length; i++) {
      Establishment e = (Establishment) estabList.getElementAt(i);
      roomView[i] = new DayPlanRoomView(vs, e.getId());
      tabPanel.addItem(roomView[i], BundleUtil.getLabel("Rooms.label") + " " + e.getName());
    }
    int e = 0;
    Establishment estab = null;
    try {
      e = Integer.parseInt(ConfigUtil.getConf(ConfigKey.DEFAULT_ESTABLISHMENT.getKey()));
      estab =  (Establishment) DataCache.findId(e, Model.Establishment);
    } catch (NumberFormatException nfe) {
      GemLogger.log(getClass().getName() + "#init " + nfe.getMessage());
    } catch (SQLException sqe) {
      GemLogger.log(sqe.getMessage());
    }
    String teacherManaged = ConfigUtil.getConf(ConfigKey.TEACHER_MANAGEMENT.getKey());
    int offset = (teacherManaged.equals("t")) ? 1 : 0;
    tabPanel.setSelectedIndex(estabList.indexOf(estab) + offset);//+1 quand la gestion prof est activée car le premier onglet correspond aux profs

  }
  
   @Override
  public void print() {
    DayPlanTableView v = (DayPlanTableView) tabPanel.getSelectedComponent();
    
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

    if (job.printDialog(atts))  {
      try {
        job.print(atts);
      } catch (PrinterException ex) {
          GemLogger.logException(ex);
      }
    }

  }

  @Override
  public void setSelectedTab(int tabIndex) {
    tabPanel.setSelectedIndex(tabIndex);
  }

  @Override
  public void addActionListener(ActionListener l) {
    if (teacherView != null) {
      teacherView.addActionListener(l);
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

    for (int i = 0; i < roomView.length; i++) {
      roomView[i].removeActionListener(l);
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (teacherView != null) {
      teacherView.propertyChange(evt);
    }

    for (int i = 0; i < roomView.length; i++) {
      roomView[i].propertyChange(evt);
    }
  }

  @Override
  public void close() throws GemCloseVetoException {
    daySchedule.removePropertyChangeListener(this);
    setVisible(false);
    dispose();
  }
}

