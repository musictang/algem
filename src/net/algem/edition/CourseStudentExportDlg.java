/*
 * @(#)CourseStudentExportDlg.java 2.9.7.1 25/05/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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

package net.algem.edition;

import javax.swing.JLabel;
import net.algem.course.CourseChoice;
import net.algem.course.CourseChoiceActiveModel;
import net.algem.util.BundleUtil;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GridBagHelper;

/**
 * Export dialog for contact infos of students doing the selected course.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.7.1
 * @since 2.6.d 06/11/2012
 */
public class CourseStudentExportDlg
 extends StudentExportDlg
{
  private GemChoice course;

  public CourseStudentExportDlg(GemDesktop desktop) {
    super(desktop);
  }

  @Override
  protected void setPanel() {

    course = new CourseChoice(new CourseChoiceActiveModel(desktop.getDataCache().getList(Model.Course), true));
    course.setPreferredSize(typeContact.getPreferredSize());

    gb.add(new JLabel(BundleUtil.getLabel("Course.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(course, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Date.From.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(dateRange, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Type.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(typeContact, 1, 2, 1, 1, GridBagHelper.WEST);

    nextRow = 3;
  }

  @Override
  public String getRequest() {
    // boxing required : null value may be returned
    Boolean pro = rdPro.isSelected() ? Boolean.valueOf(true) : (rdLeisure.isSelected() ? Boolean.valueOf(false) : null);
    return service.getContactQueryByCourse(course.getKey(), dateRange.getStart(), dateRange.getEnd(), pro);
  }

}
