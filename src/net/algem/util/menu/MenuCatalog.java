/*
 * @(#)MenuCatalog.java	2.7.a 09/01/13
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
package net.algem.util.menu;

import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.course.*;
import net.algem.enrolment.EnrolmentListCtrl;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;

/**
 * Catalog menu.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class MenuCatalog
        extends GemMenu
{

  private JMenu mModule;
  private JMenuItem miModuleBrowse;
  private JMenuItem miModuleCreate;
  private JMenuItem miModuleDelete;
  private JMenu mCours;
  private JMenuItem miCoursBrowse;
  private JMenuItem miCoursCreate;
  private JMenuItem miCoursDelete;
  private JMenuItem miAtelierBrowse;
  private JMenuItem miEnrolment;

  public MenuCatalog(GemDesktop _desktop) {
    super(BundleUtil.getLabel("Menu.catalog.label"), _desktop);
    DataConnection dc = _desktop.getDataCache().getDataConnection();

    String course = ConfigUtil.getConf(ConfigKey.COURSE_MANAGEMENT.getKey(), dc);
    //String workshop = ConfigUtil.getConf(ConfigKey.WORKSHOP_MANAGEMENT.getKey(), dc);
    if (course != null && course.startsWith("t")) {
      mModule = new JMenu(BundleUtil.getLabel("Module.label"));
      mModule.add(miModuleBrowse = new JMenuItem(BundleUtil.getLabel("View.modify.label")));
      mModule.add(miModuleCreate = new JMenuItem(BundleUtil.getLabel("New.module.label")));
      mModule.add(miModuleDelete = new JMenuItem(GemCommand.DELETE_CMD));
      add(mModule);

      mCours = new JMenu(BundleUtil.getLabel("Course.label"));
      mCours.add(miCoursBrowse = new JMenuItem(BundleUtil.getLabel("View.modify.label")));
      mCours.add(miCoursCreate = new JMenuItem(BundleUtil.getLabel("Course.creation.label")));
      mCours.add(miCoursDelete = getItem(new JMenuItem(GemCommand.DELETE_CMD), "Course.suppression.auth"));
      add(mCours);
    }
    //if (workshop != null && workshop.startsWith("t")) {
//      miAtelierBrowse = add(dataCache.getMenu2("Workshop.reading", true));
    miAtelierBrowse = add(dataCache.getMenu2("Single.workshop", false));
    //}
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
    String t = " " + BundleUtil.getLabel("Module.label");
    if (src == miModuleBrowse) {
      ModuleSearchCtrl moduleBrowser = new ModuleSearchCtrl(desktop);
      moduleBrowser.addActionListener(this);
      moduleBrowser.init();
      desktop.addPanel(GemModule.MODULE_BROWSER_KEY, moduleBrowser);
    } else if (src == miModuleCreate) {
      ModuleCreateCtrl mCreateCtrl = new ModuleCreateCtrl(desktop);
      mCreateCtrl.addActionListener(this);
      desktop.addPanel(GemModule.MODULE_CREATE_KEY, mCreateCtrl);
    } else if (src == miModuleDelete) {
      ModuleDeleteCtrl mDeleteCtrl = new ModuleDeleteCtrl(desktop);
      mDeleteCtrl.addActionListener(this);
      mDeleteCtrl.init();
      desktop.addPanel(GemModule.MODULE_DELETE_KEY, mDeleteCtrl);
    } else if (src == miCoursBrowse) {
      CourseSearchCtrl coursCtrl = new CourseSearchCtrl(desktop);
      coursCtrl.addActionListener(this);
      coursCtrl.init();
      desktop.addPanel(GemModule.COURSE_BROWSER_KEY, coursCtrl);
    } else if (src == miCoursCreate) {
      CourseCreateCtrl crsCreateCtrl = new CourseCreateCtrl(desktop);
      crsCreateCtrl.addActionListener(this);
      crsCreateCtrl.init();
      desktop.addPanel(GemModule.COURSE_CREATE_KEY, crsCreateCtrl);
    } else if (src == miCoursDelete) {
      CourseSearchDeleteCtrl crsDeleteCtrl = new CourseSearchDeleteCtrl(desktop);
      crsDeleteCtrl.addActionListener(this);
      crsDeleteCtrl.init();
      desktop.addPanel(GemModule.COURSE_DELETE_KEY, crsDeleteCtrl);
    } else if (src == miAtelierBrowse) {
      WorkshopSearchCtrl atelier = new WorkshopSearchCtrl(desktop);
      atelier.addActionListener(this);
      atelier.init();
      desktop.addPanel(GemModule.WORKSHOP_BROWSER_KEY, atelier);
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
