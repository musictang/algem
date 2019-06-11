/*
 * @(#)AbstractMonthScheduleCtrl.java	2.8.o 10/10/13
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

package net.algem.planning.month;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import net.algem.planning.*;
import net.algem.planning.editing.PlanModifCtrl;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;

/**
 * Abstract controller for individual schedule.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.o
 * @since 2.8.o 10/10/13
 */
public abstract class AbstractMonthScheduleCtrl
        extends FileTab
        implements ActionListener
{

  protected MonthSchedule monthSchedule;
  protected PlanModifCtrl modifCtrl;
  protected ScheduleDetailCtrl detailCtrl;
  protected Calendar cal;
  protected DateFr start;
  protected DateFr end;
  protected boolean loaded;

  public AbstractMonthScheduleCtrl(GemDesktop desktop) {
    super(desktop);
    cal = Calendar.getInstance(Locale.FRANCE);

    cal.set(Calendar.YEAR, 1900);
    start = new DateFr(cal.getTime());
    end = new DateFr(cal.getTime());
    modifCtrl = new PlanModifCtrl(desktop);
    monthSchedule = new MonthSchedule();

  }

  @Override
  public abstract void load();

  /**
   * Loads a specific date.
   * @param d the date to load
   */
  protected abstract void load(Date d);

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("date")) {
      Date d = ((DateBar) e.getSource()).getDate();
      load(d);
    } else if (e.getActionCommand().equals("Click")) {
      ScheduleView v = (ScheduleView) e.getSource();
      Schedule p = v.getSchedule();
      ScheduleDetailEvent pde = new ScheduleDetailEvent(this, p);
      pde.setPosition(v.getClickPosition());
      pde.setRanges(v.getScheduleRanges());
      desktop.setWaitCursor();
      detailCtrl.loadSchedule(pde);
      desktop.setDefaultCursor();
    }
    /*else if (e.getActionCommand().bufferEquals("ClickDate")) { // click hors plage
      ScheduleView v = (ScheduleView) e.getSource();
      Schedule p = v.getSchedule(); //System.out.println("PlanningMoisCtrl.ClickDate:"+p);

      ScheduleDetailEvent pde = new ScheduleDetailEvent(this, p);
      pde.setPosition(v.getClickPosition());
      //desktop.postEvent(pde);
      //modifCtrl.loadSchedule(p);
    }*/
  }

  /**
   * Delimits the start and end of month of the date {@code d}.
   * @param d date
   */
  protected void setMonthRange(Date d) {
    cal.setTime(d);
    // recherche des dates de début et de fin de mois
    cal.set(Calendar.DAY_OF_MONTH, 1);
    start = new DateFr(cal.getTime());
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    end = new DateFr(cal.getTime());
  }
}
