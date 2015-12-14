/*
 * @(#)MenuCatalog.java	2.9.4.14 13/12/2015
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
package net.algem.util.menu;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;

import net.algem.Algem;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.PhotoHandler;
import net.algem.contact.SimplePhotoHandler;
import net.algem.course.CourseSearchCtrl;
import net.algem.course.ModuleSearchCtrl;
import net.algem.enrolment.EnrolmentListCtrl;
import net.algem.enrolment.EnrolmentService;
import net.algem.enrolment.ExtendeModuleOrderListCtrl;
import net.algem.enrolment.ExtendedModuleOrderTableModel;
import net.algem.planning.fact.ui.PlanningFactCRUDController;
import net.algem.script.ui.ScriptingFormController;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.MessagePopup;

/**
 * Catalog menu.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.10
 * @since 1.0a 07/07/1999
 */
public class MenuCatalog
  extends GemMenu {

  private static final String ENROLMENT_BROWSER_KEY = "Enrolment.browser";
  private static final String MODULE_BROWSER_KEY = "Module.browser";
  private static final String COURSE_BROWSER_KEY = "Course.browser";
  private JMenuItem miModule;
  private JMenuItem miModuleOrder;
  private JMenuItem miCoursBrowse;
  private JMenuItem miEnrolment;
  private JMenuItem miImportPhotos;
  private JMenuItem miExportPhotos;
  private final JMenuItem scriptItem;
  private final JMenuItem factsItem;

  public MenuCatalog(GemDesktop desktop) {
    super(BundleUtil.getLabel("Menu.catalog.label"), desktop);
    String course = ConfigUtil.getConf(ConfigKey.COURSE_MANAGEMENT.getKey());
    if (course != null && course.startsWith("t")) {
      miModule = new JMenuItem(BundleUtil.getLabel("Module.label"));
      add(miModule);
      miCoursBrowse = new JMenuItem(BundleUtil.getLabel("Course.label"));
      add(miCoursBrowse);
      addSeparator();
      add(miEnrolment = new JMenuItem(BundleUtil.getLabel("Menu.enrolment.label")));
      miModuleOrder = new JMenuItem(BundleUtil.getLabel("Modules.ordered.label"));
      add(miModuleOrder);

    }

    scriptItem = new JMenuItem(BundleUtil.getLabel("Scripts.label"));
    if (Algem.isFeatureEnabled("scripting")) {
      add(scriptItem);
    }

    factsItem = new JMenuItem(BundleUtil.getLabel("PlanningFact.label"));
    if (Algem.isFeatureEnabled("planning_fact")) {
      add(factsItem);
    }

    addSeparator();
    miImportPhotos = new JMenuItem(BundleUtil.getLabel("Photos.import.auth"));
    add(miImportPhotos);
    if (!dataCache.authorize("Photos.import.auth")) {
      miImportPhotos.setEnabled(false);
    }
    miExportPhotos = new JMenuItem(BundleUtil.getLabel("Photos.export.auth"));
    add(miExportPhotos);
    if (!dataCache.authorize("Photos.export.auth")) {
      miExportPhotos.setEnabled(false);
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
      desktop.addPanel(MODULE_BROWSER_KEY, moduleBrowser);
    } else if (src == miModuleOrder) {
      final EnrolmentService service = new EnrolmentService(dataCache);
      final ExtendeModuleOrderListCtrl orderListCtrl = new ExtendeModuleOrderListCtrl(desktop, service, new ExtendedModuleOrderTableModel());
      orderListCtrl.load(dataCache.getStartOfYear().getDate(), dataCache.getEndOfYear().getDate());
      desktop.addPanel("Modules.ordered", orderListCtrl, GemModule.XXL_SIZE);
    } else if (src == miCoursBrowse) {
      CourseSearchCtrl coursCtrl = new CourseSearchCtrl(desktop);
      coursCtrl.addActionListener(this);
      coursCtrl.init();
      desktop.addPanel(COURSE_BROWSER_KEY, coursCtrl);
    } else if (src == miEnrolment) {
      EnrolmentListCtrl enrolmentList = new EnrolmentListCtrl(desktop);
      enrolmentList.addActionListener(this);
      desktop.addPanel(ENROLMENT_BROWSER_KEY, enrolmentList);
    } else if (arg.equals(GemCommand.CANCEL_CMD)) {
      desktop.removeCurrentModule();
    } else if (src == scriptItem) {
      desktop.addPanel("Scripts", new ScriptingFormController(desktop).getPanel(), new Dimension(905, 600));
    } else if (src == factsItem) {
      desktop.addPanel("Absences & remplacement", new PlanningFactCRUDController(desktop).getPanel(), GemModule.XXL_SIZE);
    } else if (src == miImportPhotos) {
      importPhotos();
    } else if (src == miExportPhotos) {
      exportPhotos();
    }
    desktop.setDefaultCursor();
  }

  private void importPhotos() {
    String path = ConfigUtil.getConf(ConfigKey.PHOTOS_PATH.getKey());
    JFileChooser chooser = new JFileChooser(path);
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setDialogTitle(MessageUtil.getMessage("select.directory"));
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File selectedDir = chooser.getSelectedFile();
      if (selectedDir.isDirectory() && selectedDir.canRead()) {
        PhotoHandler handler = new SimplePhotoHandler(desktop.getFrame(), DataCache.getDataConnection());
        handler.importFilesFromDir(selectedDir);
      } else {
        MessagePopup.warning(this, MessageUtil.getMessage("directory.read.access.warning"));
      }
    }
  }

  private void exportPhotos() {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chooser.setDialogTitle(MessageUtil.getMessage("select.directory"));
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File selectedDir = chooser.getSelectedFile();
      if (selectedDir.isDirectory() && selectedDir.canWrite()) {
        PhotoHandler handler = new SimplePhotoHandler(desktop.getFrame(), DataCache.getDataConnection());
        handler.exportFilesToDir(selectedDir);
      } else {
        MessagePopup.warning(this, MessageUtil.getMessage("directory.write.access.warning"));
      }
    }
  }

}
