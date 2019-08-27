/*
 * @(#)MenuCatalog.java	2.11.2 14/10/16
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
package net.algem.util.menu;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

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
import net.algem.rental.RentSearchCtrl;
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
 * @version 2.11.2
 * @since 1.0a 07/07/1999
 */
public class MenuCatalog
        extends GemMenu
{

  private static final String ENROLMENT_BROWSER_KEY = "Enrolment.browser";
  private static final String MODULE_BROWSER_KEY = "Module.browser";
  private static final String COURSE_BROWSER_KEY = "Course.browser";
  private static final String RENTAL_BROWSER_KEY = "Rental.browser";
  private JMenuItem miModule;
  private JMenuItem miModuleOrder;
  private JMenuItem miCoursBrowse;
  private JMenuItem miRentalBrowse;
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
      miModule.setToolTipText(BundleUtil.getLabel("Module.catalog.tip"));
      add(miModule);
      miCoursBrowse = new JMenuItem(BundleUtil.getLabel("Course.label"));
      miCoursBrowse.setToolTipText(BundleUtil.getLabel("Course.catalog.tip"));
      add(miCoursBrowse);
      if (Algem.isFeatureEnabled("location")) {
        miRentalBrowse = new JMenuItem(BundleUtil.getLabel("Rental.label"));
        miRentalBrowse.setToolTipText(BundleUtil.getLabel("Rental.catalog.tip"));
        add(miRentalBrowse);
      }
      addSeparator();
      
      add(miEnrolment = new JMenuItem(BundleUtil.getLabel("Menu.enrolment.label")));
      miEnrolment.setToolTipText(BundleUtil.getLabel("Enrolment.catalog.tip"));
      miModuleOrder = new JMenuItem(BundleUtil.getLabel("Modules.ordered.label"));
      miModuleOrder.setToolTipText(BundleUtil.getLabel("Modules.ordered.tip"));
      add(miModuleOrder);
    }

    scriptItem = new JMenuItem(BundleUtil.getLabel("Scripts.label"));
    scriptItem.setToolTipText(BundleUtil.getLabel("Scripts.catalog.tip"));
    if (Algem.isFeatureEnabled("scripting")) {
      add(scriptItem);
    }

    factsItem = new JMenuItem(BundleUtil.getLabel("PlanningFact.label"));
    if (Algem.isFeatureEnabled("planning_fact")) {
      add(factsItem);
    }

    addSeparator();
    miImportPhotos = new JMenuItem(BundleUtil.getLabel("Photos.import.auth"));
    miImportPhotos.setToolTipText(BundleUtil.getLabel("Photos.import.auth.tip"));
    add(miImportPhotos);
    if (!dataCache.authorize("Photos.import.auth")) {
      miImportPhotos.setEnabled(false);
    }
    miExportPhotos = new JMenuItem(BundleUtil.getLabel("Photos.export.auth"));
    miExportPhotos.setToolTipText(BundleUtil.getLabel("Photos.export.auth.tip"));
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
    } else if (src == miRentalBrowse) {
      RentSearchCtrl rentCtrl = new RentSearchCtrl(desktop);
      rentCtrl.addActionListener(this);
      rentCtrl.init();
      desktop.addPanel(RENTAL_BROWSER_KEY, rentCtrl);
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
    chooser.setSelectedFile(new File(path));
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    chooser.setFileHidingEnabled(true);
    chooser.setFileFilter(new FileNameExtensionFilter(MessageUtil.getMessage("filechooser.image.filter.label"), "jpg", "png"));
    chooser.setDialogTitle(MessageUtil.getMessage("select.directory"));
    chooser.setApproveButtonToolTipText(MessageUtil.getMessage("open.selected.dir.tip"));
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File selectedDir = chooser.getSelectedFile();
      if (selectedDir.isDirectory()) {
        if (selectedDir.canRead()) {
          PhotoHandler handler = new SimplePhotoHandler(desktop.getFrame(), DataCache.getDataConnection());
          handler.importFilesFromDir(selectedDir);
        } else {
          MessagePopup.warning(this, MessageUtil.getMessage("directory.read.access.warning"));
        }
      } else {
        MessagePopup.warning(this, MessageUtil.getMessage("selected.file.is.not.directory"));
      }
    }
  }

  private void exportPhotos() {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    chooser.setFileHidingEnabled(true);
    chooser.setDialogTitle(MessageUtil.getMessage("select.directory"));
    chooser.setApproveButtonToolTipText(MessageUtil.getMessage("open.selected.dir.tip"));
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File selectedDir = chooser.getSelectedFile();
      if (selectedDir.isDirectory()) {
        if (selectedDir.canWrite()) {
          PhotoHandler handler = new SimplePhotoHandler(desktop.getFrame(), DataCache.getDataConnection());
          handler.exportFilesToDir(selectedDir);
        } else {
          MessagePopup.warning(this, MessageUtil.getMessage("directory.write.access.warning"));
        }
      } else {
        MessagePopup.warning(this, MessageUtil.getMessage("selected.file.is.not.directory"));
      }
    }
  }

}
