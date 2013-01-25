/*
 * @(#)ModifPlanCourseDlg.java 2.6.a 21/09/12
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

import javax.swing.JDialog;
import net.algem.planning.DateFr;
import net.algem.planning.ScheduleObject;
import net.algem.util.BundleUtil;
import net.algem.util.module.GemDesktop;

/**
 * Dialog for course modification.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.4.b 30/05/12
 */
public class ModifPlanCourseDlg
extends ModifPlanDlg
{
  
  private ModifPlanCourseView pv;

  public ModifPlanCourseDlg(GemDesktop desktop, ScheduleObject plan) {
    super(desktop.getFrame());
    pv = new ModifPlanCourseView(BundleUtil.getLabel("Course.label"), desktop.getDataCache());
    pv.set(plan);
    validation = false;
    dlg = new JDialog(parent, true);
    addContent(pv, "Schedule.course.modification.title");
  }

  @Override
  public boolean isEntryValid() {
    return  pv.getCourse() > 0;
  }

  @Override
  public boolean isValidate() {
    return validation;
  }

  public DateFr getStart() {
    return pv.getDateStart();
  }

  public DateFr getEnd() {
    return pv.getDateEnd();
  }

  public int getCourse() {
    if (isEntryValid()) {
      validation = true;
    }
    return pv.getCourse();
  }

}
