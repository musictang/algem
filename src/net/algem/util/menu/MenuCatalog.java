/*
 * @(#)MenuCatalog.java	2.9.4.13 06/11/15
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ProgressMonitor;

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
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.FilePanel;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
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
      addSeparator();
      miImportPhotos = new JMenuItem(BundleUtil.getLabel("Importer photos"));
      add(miImportPhotos);
    }

    scriptItem = new JMenuItem(BundleUtil.getLabel("Scripts.label"));
    if (Algem.isFeatureEnabled("scripting")) {
      add(scriptItem);
    }

    factsItem = new JMenuItem(BundleUtil.getLabel("PlanningFact.label"));
    if (Algem.isFeatureEnabled("planning_fact")) {
      add(factsItem);
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
        ImportDlg  dlg = new ImportDlg(desktop.getFrame());
        dlg.createUI();
//        if (dlg.isValidation()) {
//          
//          ProgressMonitor monitor = new ProgressMonitor(this, BundleUtil.getLabel("Loading.label"), "", 1, 100);
//        monitor.setProgress(1);
//        monitor.setMillisToDecideToPopup(0);
//          PhotoHandler handler = new SimplePhotoHandler(DataCache.getDataConnection(), monitor);
//          File dir = new File(dlg.getDir());
//          System.out.println(dir.getName());
//          System.out.println(dir.getPath());
//          int saved = handler.importFilesFromDir(dir);
//          
//          if (saved >= 0) {
//            MessagePopup.information(this, saved + " nouvelle(s) photo(s) enregistrée(s)");
//          } else {
//            MessagePopup.error(this, "Erreur enregistrement");
//          }
//        } 
        
//      String dir = FileUtil.getDir(this, arg, arg)
    }
    desktop.setDefaultCursor();
  }
  
  class ImportDlg extends JDialog implements ActionListener, PropertyChangeListener {

    private FilePanel filePanel;
    private GemButton btOk;
    private GemButton btCancel;
    private boolean validation;
    private JProgressBar progressBar;
//    private PhotoHandler handler;
    public ImportDlg(Frame owner) {
      super(owner);
//      this.handler = handler;
      String path = ConfigUtil.getConf(ConfigKey.PHOTOS_PATH.getKey());  
      filePanel = new FilePanel(ConfigKey.PHOTOS_PATH.getLabel(), path);
    }
    
    void createUI() {
      setLayout(new BorderLayout());
      setTitle("Choisissez un répertoire");
      GemPanel content = new GemPanel(new BorderLayout());
      content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//Where the GUI is constructed:
progressBar = new JProgressBar(0, 100);
progressBar.setValue(0);
//progressBar.setStringPainted(true);
      
      GemPanel buttons = new GemPanel(new GridLayout(1,2));
      btOk = new GemButton(GemCommand.OK_CMD);
      btCancel = new GemButton(GemCommand.CANCEL_CMD);
      btOk.addActionListener(this);
      btCancel.addActionListener(this);
      buttons.add(btOk);
      buttons.add(btCancel);
      content.add(filePanel, BorderLayout.NORTH);
      content.add(progressBar, BorderLayout.SOUTH);
      add(content, BorderLayout.CENTER);
      add(buttons, BorderLayout.SOUTH);
      setSize(new Dimension(550,150));
      setLocationRelativeTo(desktop.getFrame());
//      pack();
      setVisible(true);
    }
    
    boolean isValidation() {
      return validation;
    }
    
    String getDir() {
      return filePanel.getText();
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
//            taskOutput.append(String.format(
//                    "Completed %d%% of task.\n", task.getProgress()));
        } 
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == btOk) {
//        ProgressMonitor monitor = new ProgressMonitor(this, BundleUtil.getLabel("Loading.label"), "", 1, 100);
//        monitor.setProgress(1);
//        monitor.setMillisToDecideToPopup(10);
          PhotoHandler handler = new SimplePhotoHandler(DataCache.getDataConnection(), this);
        handler.importFilesFromDir(new File(getDir()));
//        validation = true;
      } else {
        dispose();
//        validation = false;
      }
//      dispose();
    }

  }

}
