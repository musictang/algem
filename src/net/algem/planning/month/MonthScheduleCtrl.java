/*
 * @(#)MonthScheduleCtrl.java	2.9.4.12 25/09/15
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
package net.algem.planning.month;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import net.algem.contact.teacher.TeacherEvent;
import net.algem.planning.*;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.room.Establishment;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.event.GemEvent;
import net.algem.util.model.GemCloseVetoException;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;
import net.algem.util.module.GemModule;
import net.algem.util.ui.Toast;
import net.algem.util.ui.UIAdjustable;

/**
 * Month schedule controller.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.11a
 * @since 1.0b 06/10/2001
 */
public class MonthScheduleCtrl
        extends GemModule
        implements UIAdjustable
{

  static final Dimension MONTH_SIZE = new Dimension(920, 600);
  private JMenuBar mBar;
  private JMenu mFile;
  private JMenuItem mPrint;
  private JMenuItem mQuit;
  private JMenu mOptions;
  private JMenuItem miSaveUISettings;
  private JCheckBoxMenuItem mLinkDay;
  private MonthSchedule monthSchedule;
  private boolean linkedToDay = false;
  private Calendar cal;
  private Date start;
  private Date end;
  private boolean savePrefs;
  private final Preferences prefs = Preferences.userRoot().node("/algem/monthplan/size");

  public MonthScheduleCtrl() {
    super("PlanningMois");
    cal = Calendar.getInstance(Locale.FRANCE);
    cal.set(Calendar.YEAR, 1900);
    start = cal.getTime();
    end = cal.getTime();
  }

  /**
   * Inits the module.
   *
   * @see net.algem.util.module.GemDesktop#addModule(net.algem.util.module.GemModule)
   */
  @Override
  public void init() {
    monthSchedule = dataCache.getMonthSchedule();

    GemList<Establishment> v = dataCache.getList(Model.Establishment);

    view = new MonthScheduleView(desktop, monthSchedule, v);
    view.setSize(new Dimension(prefs.getInt("w", MONTH_SIZE.width), prefs.getInt("h", MONTH_SIZE.height)));
    view.addActionListener(this);

    desktop.addGemEventListener(this);

    mBar = new JMenuBar();
    mFile = createJMenu("Menu.file");
    mPrint = getMenuItem("Menu.print");
    mQuit = getMenuItem("Menu.quit");
    mFile.add(mPrint);
    mFile.addSeparator();
    mFile.add(mQuit);

    mOptions = new JMenu("Options");
    mLinkDay = new JCheckBoxMenuItem(BundleUtil.getLabel("Month.schedule.link.label"), linkedToDay);
    mLinkDay.setSelected(false);
    mLinkDay.addItemListener(new ItemListener()
    {
      public void itemStateChanged(ItemEvent e) {
        linkedToDay = (e.getStateChange() == ItemEvent.SELECTED);
      }
    });

    miSaveUISettings = getMenuItem(BundleUtil.getLabel("Store.ui.settings"));
    mOptions.add(mLinkDay);
    mOptions.add(miSaveUISettings);

    mBar.add(mFile);
    mBar.add(mOptions);
    view.setJMenuBar(mBar);

    load(new java.util.Date());
  }

  @Override
  public String getSID() {
    return cal.getTime().toString();
  }

  private void load(Date date) {

    cal.setTime(date);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    start = cal.getTime();
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    end = cal.getTime();

    dataCache.setMonthSchedule(start, end);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    String cmd = evt.getActionCommand();

    if (src instanceof DateBar) {
      Date d = ((DateBar) src).getDate();
      load(d);
      SelectDateEvent sde = new SelectDateEvent(this, d);
      desktop.postEvent(sde);
    } else if (cmd.equals("Click")) {
      ScheduleView v = (ScheduleView) evt.getSource();
      Schedule p = v.getSchedule();
      ScheduleDetailEvent pde = new ScheduleDetailEvent(this, p);
      pde.setPosition(v.getClickPosition());
      pde.setRanges(v.getScheduleRanges());
      desktop.postEvent(pde);
    } else if (cmd.equals("ClickDate")) {
      ScheduleView v = (ScheduleView) evt.getSource();
      Schedule p = v.getSchedule();
      ScheduleDetailEvent pde = new ScheduleDetailEvent(this, p);
      pde.setPosition(v.getClickPosition());
      desktop.postEvent(pde);
    } else if (src == mPrint) {
      view.print();
    } else if (src == mQuit) {
      savePrefs = (evt.getModifiers() & Event.SHIFT_MASK) == Event.SHIFT_MASK;
      try {
        close();
      } catch (GemCloseVetoException ex) {
        GemLogger.logException(ex);
      }
    } else if (src == miSaveUISettings) {
      storeUISettings();
      Dimension d = view.getBounds().getSize();
      Toast.showToast(desktop, BundleUtil.getLabel("New.size.label") + " : " + d.width+"x"+d.height);
    }

  }

  @Override
  public void postEvent(GemEvent evt) {
    System.out.println("MonthScheduleCtrl.postEvent:" + evt);
    if (evt instanceof SelectDateEvent) {
      if (!linkedToDay) {
        return;
      }

      final Date d = ((SelectDateEvent) evt).getDate();
      if (d.before(start) || d.after(end)) {
        EventQueue.invokeLater(new Runnable()
        {
          public void run() {
            load(d);
          }
        });
      }
    } else if (evt instanceof ScheduleDetailEvent) {
      if (!linkedToDay) {
        return;
      }

      ScheduleDetailEvent e = (ScheduleDetailEvent) evt;
      view.postEvent(e);

      final Date d = e.getDate();
      if (d.before(start) || d.after(end)) {
        EventQueue.invokeLater(new Runnable()
        {
          public void run() {
            load(d);
          }
        });
      }
    } else if (evt instanceof ModifPlanEvent) {
      final Date d = cal.getTime();
      EventQueue.invokeLater(new Runnable()
      {
        public void run() {
          load(d);
        }
      });
    } else if (evt instanceof TeacherEvent && evt.getOperation() == GemEvent.CREATION) {
      view.postEvent(evt);
    } else {
      System.out.println("MonthScheduleCtrl.postEvent: IGNORE");
    }
  }

  @Override
  public void close() throws GemCloseVetoException {
    super.close();
    if (savePrefs) {
      storeUISettings();
      Dimension d = view.getSize();
      Toast.showToast(desktop, BundleUtil.getLabel("New.size.label") + " : " + d.width+"x"+d.height);
    }
    view.removeActionListener(this);
    desktop.removeGemEventListener(this);
  }

  @Override
  public void storeUISettings() {
    Rectangle bounds = getView().getBounds();
    prefs.putInt("w", (int) bounds.getWidth());
    prefs.putInt("h", (int) bounds.getHeight());
  }

}
