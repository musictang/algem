/*
 * @(#)MenuCatalog.java	2.9.1 09/12/14
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
package net.algem.util.menu;

import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.course.CourseSearchCtrl;
import net.algem.course.ModuleSearchCtrl;
import net.algem.enrolment.EnrolmentListCtrl;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;

/**
 * Catalog menu.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 * @since 1.0a 07/07/1999
 */
public class MenuCatalog
        extends GemMenu
{

  private JMenuItem miModule;
  private JMenuItem miCoursBrowse;
  private JMenuItem miEnrolment;

  public MenuCatalog(GemDesktop desktop) {
    super(BundleUtil.getLabel("Menu.catalog.label"), desktop);
    String course = ConfigUtil.getConf(ConfigKey.COURSE_MANAGEMENT.getKey());
    if (course != null && course.startsWith("t")) {
      miModule = new JMenuItem(BundleUtil.getLabel("Module.label"));
      add(miModule);
      miCoursBrowse = new JMenuItem(BundleUtil.getLabel("Course.label"));
      add(miCoursBrowse);
    }
    if (course != null && course.startsWith("t")) {
      add(miEnrolment = new JMenuItem(BundleUtil.getLabel("Menu.enrolment.label")));
    }
    setListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String arg = evt.getActionCommand();
    Object src = evt.getSource();

    desktop.setWaitCursor();

    if (src == miModule) {
      ModuleSearchCtrl moduleBrowser = new ModuleSearchCtrl(desktop);
      moduleBrowser.addActionListener(this);
      moduleBrowser.init();
      desktop.addPanel(GemModule.MODULE_BROWSER_KEY, moduleBrowser);
    } else if (src == miCoursBrowse) {
      CourseSearchCtrl coursCtrl = new CourseSearchCtrl(desktop);
      coursCtrl.addActionListener(this);
      coursCtrl.init();
      desktop.addPanel(GemModule.COURSE_BROWSER_KEY, coursCtrl);
    } else if (src == miEnrolment) {
      EnrolmentListCtrl enrolmentList = new EnrolmentListCtrl(desktop);
      enrolmentList.addActionListener(this);
      desktop.addPanel(GemModule.ENROLMENT_BROWSER_KEY, enrolmentList);
    } else if (arg.equals(GemCommand.CANCEL_CMD)) {
      desktop.removeCurrentModule();
    }
    desktop.setDefaultCursor();
  }
}
