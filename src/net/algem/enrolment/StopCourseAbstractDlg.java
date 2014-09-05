/*
 * @(#)StopCourseAbstractDlg.java	2.6.w 03/09/14
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
package net.algem.enrolment;

import java.awt.Frame;
import java.awt.event.ActionListener;
import java.util.Calendar;
import javax.swing.JDialog;
import net.algem.planning.DateFr;
import net.algem.planning.editing.StopCourseView;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.MessagePopup;

/**
 * Abstract dialog used to stop courses or module.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.w
 * @since 2.6.w 03/09/14
 */
public abstract class StopCourseAbstractDlg 
  extends JDialog
  implements ActionListener
{
  
  protected StopCourseView view;
  protected GemButton btOk;
  protected GemButton btCancel;

  public StopCourseAbstractDlg(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }
  
  protected void close() {
    setVisible(false);
    dispose();
  }
  
  /**
   * Checks the start date when stopping a course.
   * If the selected day is not a Sunday and if the modification is confirmed,
   * the date is automatically modified to the next Sunday.
   * 
   * @param start date
   * @return a date
   */
  protected DateFr checkDate(DateFr start) {
    DateFr d  = start;
    Calendar cal = Calendar.getInstance();
    cal.setTime(d.getDate());
    if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
      if (MessagePopup.confirm(null,
                                MessageUtil.getMessage("stopping.course.date.confirmation"),
                                BundleUtil.getLabel("Confirmation.title"))
                                ) {
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
          cal.add(Calendar.DAY_OF_WEEK, 1);
        }
        d = new DateFr(cal.getTime());
      }
    }
    return d;
  }
  
  
  @Override
  public String toString() {
    return getClass().getName();
  }

}
