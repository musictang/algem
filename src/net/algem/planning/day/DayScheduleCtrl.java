/*
 * @(#)DayScheduleCtrl.java 2.9.4.8 18/06/15
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

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import net.algem.Algem;
import net.algem.config.ColorPrefs;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.planning.*;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.planning.export.PlanningExportService;
import net.algem.room.Establishment;
import net.algem.room.RoomUpdateEvent;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.FileUtil;
import net.algem.util.GemLogger;
import net.algem.util.event.GemEvent;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.jdesktop.DesktopOpenHandler;
import net.algem.util.model.GemCloseVetoException;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.module.GemModule;

/**
 * Day schedule main controller.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.8
 * @since 1.0b 06/10/2001
 */
public class DayScheduleCtrl
        extends GemModule
{

  static final Dimension DAY_SIZE = new Dimension(920, 550);
  private JMenuItem miPrint;
  private JMenuItem miQuit;
  private JMenuItem miExport;

  private DaySchedule daySchedule;
  private boolean monthLink = false;
  private Calendar cal;
  private JCheckBoxMenuItem miAllRoom;

  public DayScheduleCtrl() {
    super("TableauJour");
    cal = Calendar.getInstance(Locale.FRANCE);
  }

  /**
   * Inits the module.
   * @see net.algem.util.module.GemDesktop#addModule(net.algem.util.module.GemModule)
   */
  @Override
  public void init() {
    daySchedule = dataCache.getDaySchedule();

    GemList<Establishment> estabs = dataCache.getList(Model.Establishment);
    view = new DayScheduleView(desktop, daySchedule, estabs);
    view.setSize(DAY_SIZE);
    view.addActionListener(this);

    desktop.addGemEventListener(this);

    JMenuBar mBar = new JMenuBar();
    JMenu mFile = createJMenu("Menu.file");
    miQuit = getMenuItem("Menu.quit");
    JMenu mOptions = new JMenu("Options");
    JCheckBoxMenuItem miLinkMonth = new JCheckBoxMenuItem(BundleUtil.getLabel("Day.schedule.link.label"), monthLink);
    miLinkMonth.setSelected(false);
    miLinkMonth.addItemListener(new ItemListener()
    {
      public void itemStateChanged(ItemEvent e) {
        monthLink = (e.getStateChange() == ItemEvent.SELECTED);
      }
    });

    miAllRoom = new JCheckBoxMenuItem(BundleUtil.getLabel("Room.show.all.label"));
    miAllRoom.setSelected(false);
    miAllRoom.addItemListener(new ItemListener()
    {
      public void itemStateChanged(ItemEvent e) {
        ((DayScheduleView) view).propertyChange(new PropertyChangeEvent(daySchedule, "@all_rooms", null, e.getStateChange() == ItemEvent.SELECTED));
      }
    });

    miPrint = getMenuItem("Menu.print");
    miExport = getMenuItem("Menu.export");
    mFile.add(miPrint);

    if (Algem.isFeatureEnabled("export_planning_xls")) {
      mFile.add(miExport);
    }
    mFile.add(miQuit);
    mOptions.add(miLinkMonth);
    mOptions.add(miAllRoom);
    mBar.add(mFile);
    mBar.add(mOptions);

    miQuit.addActionListener(this);
    miLinkMonth.addActionListener(this);
    view.setJMenuBar(mBar);
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run() {
        load(new java.util.Date());
      }
    });
  }

  @Override
  public String getSID() {
    return cal.getTime().toString();
  }

  //XXX dans DataCache + thread
  public void load(Date date) {
    cal.setTime(date);
    dataCache.setDaySchedule(date);
  }

  /**
   * Maximizes the internal view to optimize name display in ranges.
   */
  public void mayBeMaximize() {
     if (ConfigUtil.getConf(ConfigKey.SCHEDULE_RANGE_NAMES.getKey()).equals("t")) {
        view.setSize(new Dimension(960,720));
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    String cmd = evt.getActionCommand();
    if (src instanceof DateDayBar) {
      Date d = ((DateDayBar) evt.getSource()).getDate();
      desktop.setWaitCursor();
      load(d);
      desktop.setDefaultCursor();
      //((DayScheduleView) view).stateChanged(new ChangeEvent(cal));
      desktop.postEvent(new SelectDateEvent(this, d));
    } else if (src instanceof DateFrField) {
      desktop.setWaitCursor();
      Date d = ((DateFrField) evt.getSource()).getDate();
      load(d);
      desktop.setDefaultCursor();
      //((DayScheduleView) view).stateChanged(new ChangeEvent(cal));
      desktop.postEvent(new SelectDateEvent(this, d));
    } else if ("Click".equals(cmd)) {
      ScheduleView v = (ScheduleView) evt.getSource();
      Schedule p = v.getSchedule();
      ScheduleDetailEvent pde = new ScheduleDetailEvent(this, p);
      Point pos = v.getClickPosition();
      pos.translate(150, 0); // deplacement vers la droite
      pde.setPosition(pos);
      pde.setRanges(v.getScheduleRanges());
      desktop.postEvent(pde);
    } else if (BundleUtil.getLabel("Action.today.label").equals(cmd)) {
      desktop.setWaitCursor();
      Date d = new Date();
      load(d);
      desktop.setDefaultCursor();
      //((DayScheduleView) view).stateChanged(new ChangeEvent(cal));
      desktop.postEvent(new SelectDateEvent(this, d));
    } else if (src == miPrint) {
      view.print();
    } else if (src == miQuit) {
      try {
        close();
      } catch (GemCloseVetoException ex) {
        GemLogger.logException(ex);
      }
    } else if (src == miExport) {
      List<DayPlan> planning = ((DayScheduleView) view).getCurrentPlanning();
      File destFile = FileUtil.getSaveFile(view, "xls", BundleUtil.getLabel("Excel.file.label"), null);
      if (destFile != null) {
        try {
          new PlanningExportService(
                  new PlanningService(DataCache.getDataConnection()),
                  new ScheduleColorizer(new ColorPrefs(), (ActionIO) DataCache.getDao(Model.Action))
          ).exportPlanning(planning, destFile);
          new DesktopOpenHandler().open(destFile.getAbsolutePath());
        } catch (IOException e) {
          GemLogger.log(e.getMessage());
        } catch (DesktopHandlerException e) {
          GemLogger.log(e.getMessage());
        }
      }
    }
  }

  @Override
  public void postEvent(GemEvent _evt) {

    if (_evt instanceof SelectDateEvent) {
      if (!monthLink) {
        return;
      }

      final Date d = ((SelectDateEvent) _evt).getDate();
      if (d.equals(cal.getTime())) {
        return;
      }
      EventQueue.invokeLater(new Runnable()
      {
        public void run() {
          load(d);
        }
      });
    } else if (_evt instanceof ScheduleDetailEvent) {
      if (!monthLink) {
        return;
      }

      ScheduleDetailEvent e = (ScheduleDetailEvent) _evt;

      final Date d = e.getDate();
      if (d.equals(cal.getTime())) {
        return;
      }
      EventQueue.invokeLater(new Runnable()
      {
        public void run() {
          load(d);
        }
      });
    } else if (_evt instanceof ModifPlanEvent) {
      final Date d = cal.getTime();
      DateFr start = ((ModifPlanEvent) _evt).getStart();
      DateFr end = ((ModifPlanEvent) _evt).getEnd();
      DateFr currentDate = new DateFr(d);
      //if (d.getTime() >= debut.getTime() && d.getTime() <= fin.getTime())
      //Correction bug non rafraichissement du planning
      if (currentDate.afterOrEqual(start) && currentDate.beforeOrEqual(end)) {//XXX OU logique ?
        EventQueue.invokeLater(new Runnable()
        {

          public void run() {
            load(d);
          }
        });
      }
    } else if (_evt instanceof RoomUpdateEvent) {
      final Date d = ((RoomUpdateEvent) _evt).getDate();
      if (d != null) {
        EventQueue.invokeLater(new Runnable()
        {
          public void run() {
            load(d);
          }
        });
      }
    }
  }

  @Override
  public void close() throws GemCloseVetoException {
    view.close();
    view.removeActionListener(this);
    desktop.removeGemEventListener(this);
    desktop.removeModule(this);
  }

  public void setState(Object[] state) {
    if (state != null && state.length > 0) {
      if (state[0].getClass() == Boolean.class) {
        miAllRoom.setSelected((Boolean) state[0]);
      }
    }
  }

  @Override
  public Object[] getState() {
    return new Object[]{miAllRoom.isSelected()};
  }
}


