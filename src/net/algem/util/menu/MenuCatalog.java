/*
 * @(#)MenuCatalog.java	2.9.4.10 20/07/15
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import net.algem.Algem;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.course.CourseSearchCtrl;
import net.algem.course.ModuleSearchCtrl;
import net.algem.enrolment.EnrolmentListCtrl;
import net.algem.enrolment.EnrolmentService;
import net.algem.enrolment.ExtendeModuleOrderListCtrl;
import net.algem.enrolment.ExtendedModuleOrder;
import net.algem.enrolment.ExtendedModuleOrderTableModel;
import net.algem.planning.fact.ui.PlanningFactCRUDController;
import net.algem.script.ui.ScriptingFormController;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;

/**
 * Catalog menu.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.10
 * @since 1.0a 07/07/1999
 */
public class MenuCatalog
        extends GemMenu
{

  private static final String ENROLMENT_BROWSER_KEY="Enrolment.browser";
  private static final String MODULE_BROWSER_KEY="Module.browser";
  private static final String COURSE_BROWSER_KEY="Course.browser";
  private JMenuItem miModule;
  private JMenuItem miModuleOrder;
  private JMenuItem miCoursBrowse;
  private JMenuItem miEnrolment;
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
      final ProgressMonitor monitor = new ProgressMonitor(orderListCtrl, "Patientez...", "1", 1, 100);
      monitor.setProgress(0);
      monitor.setMillisToDecideToPopup(10);
      final List<ExtendedModuleOrder> modules;
      try {
        modules = service.getExtendedModuleList(dataCache.getStartOfYear().getDate(), dataCache.getEndOfYear().getDate());

        ProgressMonitorManager progressManager = new ProgressMonitorManager(monitor);
        SwingWorker<Void, Void> task = new SwingWorker<Void, Void>()
        {
          @Override
          protected Void doInBackground() throws Exception {
            int i = 0;
            int size = modules.size();
            for (ExtendedModuleOrder em : modules) {
              em.setCompleted(service.getCompletedTime(em.getIdper(), em.getId(), dataCache.getStartOfYear().getDate(), dataCache.getEndOfYear().getDate()));
              setProgress(++i * 100 / size);
            }
            orderListCtrl.load(modules);
            return null;
          }
        };
        task.addPropertyChangeListener(progressManager);
        task.execute();
      } catch (SQLException ex) {
        Logger.getLogger(MenuCatalog.class.getName()).log(Level.SEVERE, null, ex);
      }
      
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
        desktop.addPanel("Scripts", new ScriptingFormController(desktop).getPanel(), new Dimension(905,600));
    } else if (src == factsItem) {
        desktop.addPanel("Absences & remplacement", new PlanningFactCRUDController(desktop).getPanel(), GemModule.XXL_SIZE);
    }
    desktop.setDefaultCursor();
  }
  class ProgressMonitorManager
    implements PropertyChangeListener
  {
    private ProgressMonitor monitor;

    public ProgressMonitorManager(ProgressMonitor monitor) {
      this.monitor = monitor;
    }
    // executes in event dispatch thread
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        // if the operation is finished or has been canceled by
        // the user, take appropriate action
        if (monitor.isCanceled()) {
//            monitor.cancel(true);
        } else if (event.getPropertyName().equals("progress")) {            
            // get the % complete from the progress event
            // and set it on the progress monitor
            int progress = ((Integer)event.getNewValue()).intValue();
            monitor.setProgress(progress);            
        }        
    }
    
  }
}
