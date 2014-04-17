/*
 * @(#)MemberEnrolmentDlg.java	2.8.t 16/04/14
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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import net.algem.accounting.NullAccountException;
import net.algem.contact.PersonFile;
import net.algem.course.*;
import net.algem.edition.MemberCardEditor;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.ErrorDlg;
import net.algem.util.ui.FileTabDialog;
import net.algem.util.ui.MessagePopup;

/**
 * Enrolment dialog.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 1.0a 07/07/1999
 * @see net.algem.contact.PersonFileEditor
 *
 */
public class MemberEnrolmentDlg
        extends FileTabDialog
{

  static final String MODULE_ADD = BundleUtil.getLabel("Module.add.label");
  static final String MODULE_MODIFY = BundleUtil.getLabel("Module.modify.label");
  static final String MODULE_REMOVE = BundleUtil.getLabel("Module.remove.label");
  static final String COURSE_MODIFY = BundleUtil.getLabel("Course.modification.label");

  private final static int SESSIONS_MAX = 66;
  private EnrolmentView view;

  /** Module order list. */
  private Vector<ModuleOrder> module_orders;

  private ModuleDlg moduleDlg;
  private CourseEnrolmentDlg courseEnrolmentDlg;
  private double totalBase = 0.0;
  private int currentModule = 0;
  private int sessionsMax = 0; // (le commandes_cours dans la liste comportant le plus grand nombre de séances)
  private StringBuffer msg = new StringBuffer(MessageUtil.getMessage("enrolment.confirmation"));
  private PersonFile dossier;
  private ActionListener listener;
  private EnrolmentService service;

  public MemberEnrolmentDlg(GemDesktop _desktop, ActionListener _listener, PersonFile _dossier) {
    super(_desktop);
    dossier = _dossier;
    listener = _listener;
    service = new EnrolmentService(desktop.getDataCache());

    view = new EnrolmentView();
    view.setMember(dossier.getContact());
    view.addActionListener(this);

    module_orders = new Vector<ModuleOrder>();

    setLayout(new BorderLayout());
    add(view, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
  }

  @Override
  public void cancel() {
    listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "MemberEnrolmentCancel"));
  }

  @Override
  public void validation() {

    if (module_orders.isEmpty()) {
      new ErrorDlg(this, MessageUtil.getMessage("enrolment.empty.list"));
      return;
    }
    try {
      // Vérification des modules
      for (int i = 0; i < module_orders.size(); i++) {
        ModuleOrder m = module_orders.elementAt(i);
        Module mod = service.getModule(m.getModule());
        if (m.getModule() == 0 || mod == null) {// si module inexistant
          new ErrorDlg(this, MessageUtil.getMessage("invalid.module.choice"));
          return;
        }
      }

      Date d = new Date();
      Order order = new Order();
      order.setMember(dossier.getId());
      order.setPayer(dossier.getMember().getPayer());
      order.setInvoice(null);
      order.setCreation(new DateFr(d));
      //insertion dans la table commande
      dc.setAutoCommit(false);
      service.create(order);

      //Détermination de l'école pour l'enregistrement des échéances
      int school = getSchool(module_orders.elementAt(0));

      ModuleOrder m = null;

      //premier parcours de boucle pour déterminer le prix total.
      for (int i = 0; i < module_orders.size(); i++) {
        m = module_orders.elementAt(i);
        totalBase += m.getPrice();// prix calculé en fonction de la périodicité
      }

      // enregistrement des modules
      for (int i = 0; i < module_orders.size(); i++) {
        m = module_orders.elementAt(i);
        m.setIdOrder(order.getId());//récupération du numéro de commande
        saveModule(m);
        currentModule++;
      }
      //XXX if sessionsMax == 0 no order line is generated (this behavior is not necessarily the best)
      if (module_orders != null && module_orders.size() > 0 && sessionsMax > 0) {
        try {
          EnrolmentOrderUtil orderUtil = new EnrolmentOrderUtil(dossier, dc);
          orderUtil.setTotalBase(totalBase);
          int n = orderUtil.saveOrderLines(module_orders.elementAt(0), school);
          for(ModuleOrder mo : module_orders) {
            orderUtil.updateModuleOrder(n, mo);
          }
        } catch (NullAccountException ne) {
          MessagePopup.warning(view, ne.getMessage());
        }
      }
      if (!MessagePopup.confirm(view, msg.toString(), BundleUtil.getLabel("Confirmation.title"))) {
        throw new SQLException("abandon");
      }
      dc.commit();
      desktop.postEvent(new ModifPlanEvent(this, m.getStart(), m.getEnd()));
      desktop.postEvent(new EnrolmentCreateEvent(this, dossier.getId()));
      clear();
    } catch (SQLException e1) {
      dc.rollback();
      resetIdModule();
      GemLogger.logException("Insertion inscription", e1);
      MessagePopup.information(view, MessageUtil.getMessage("enrolment.cancel.info"));
      return;
    } finally {
      dc.setAutoCommit(true);
      totalBase = 0.0;//
      currentModule = 0;
      sessionsMax = 0;
    }
    // print member's card
    listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "MemberEnrolmentValidation"));
    MemberCardEditor ca = new MemberCardEditor(desktop, dossier);
    ca.edit();

  }

  private void resetIdModule() {
    for(int i = 0, size = module_orders.size(); i < size; i++) {
      ModuleOrder mo = module_orders.elementAt(i);
      mo.setId(i);
    }
  }

  private int getSchool(ModuleOrder mo) throws SQLException {
    if (mo.getCourseOrders().isEmpty()) {
      return 0;
    }
    Course c = planningService.getCourseFromAction(mo.getCourseOrders().get(0).getAction());
    return c == null ? 0 : c.getSchool();
  }

  /**
   * Saves a module order.
   *
   * @param module
   * @throws java.lang.SQLException
   */
  private void saveModule(ModuleOrder mo) throws SQLException {

    int sessions = 0; // le nombre de plages insérées (nombre de séances)
    //insertion dans la table commande_module
    service.create(mo);

    for (CourseOrder co : mo.getCourseOrders()) {
      co.setIdOrder(mo.getIdOrder());// mise à jour id de la commande
      co.setModuleOrder(mo.getId());// mise à jour id de la commande module
      // dates de début et de fin  spécifiques pour les ateliers ?
      //if (!cc.getCode().bufferEquals("ATP")) {
      co.setDateStart(mo.getStart());
      co.setDateEnd(mo.getEnd());

      //insertion dans la table commande_cours
      service.create(co);
      int updated = service.updateRange(mo, co, dossier.getId());
      if (updated > sessions) {
        sessions = updated;
      }
    }

    if (sessions > sessionsMax) {
      sessionsMax = sessions;
    }
    if (sessionsMax > SESSIONS_MAX) {
      sessionsMax = SESSIONS_MAX;
    }
  }

  /**
   * Reset.
   */
  public void clear() {
    view.clear();
    module_orders = new Vector<ModuleOrder>();
  }

  public void load(PersonFile d) {
    clear();
    dossier = d;
    view.setMember(d.getContact());
  }

  @Override
  public void load() {
    load(dossier);
  }

  @Override
  public boolean isLoaded() {
    return dossier != null;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals(MODULE_ADD)) {
      addModule();
    } else if (e.getActionCommand().equals(MODULE_MODIFY)) {
      modifyModule();
    } else if (e.getActionCommand().equals(MODULE_REMOVE)) {
      removeModule();
    } else if (e.getActionCommand().equals(COURSE_MODIFY)) {
      modifyCourse();
    }  else {
      super.actionPerformed(e);
    }
  }

  /**
   * Adds a module to the list of module orders.
   */
  void addModule() {
    try {
      if (moduleDlg == null) {
        moduleDlg = new ModuleDlg(this, dossier, service, dataCache);
      }
      moduleDlg.show();
      if (!moduleDlg.isValidation()) {
        return;
      }
      int idModule = Integer.parseInt(moduleDlg.getField(0));
      // Un même module peut être sélectionné plusieurs fois à partir de la version 2.8
      ModuleOrder mo = new ModuleOrder();

      Module m = ((ModuleIO) DataCache.getDao(Model.Module)).findId(idModule);
      addModule(mo, m);

      for (CourseModuleInfo info : m.getCourses()) {
        addCourse(mo, info);
      }
    } catch (SQLException ex) {
      MessagePopup.warning(view, "#addModule " + ex.getMessage());
    }
  }

  /**
   * Adds a module.
   *
   * @param mo module order
   * @param m module
   */
  private void addModule(ModuleOrder mo, Module m) {

    mo.setTitle(m.getTitle());
    mo.setPayer(dossier.getMember().getPayer());
    mo.setModule(m.getId());
    mo.setSelectedModule(Integer.parseInt(moduleDlg.getField(7)));
    mo.setStart(new DateFr(moduleDlg.getField(2)));
    mo.setEnd(new DateFr(moduleDlg.getField(3)));
    mo.setPrice(Double.parseDouble(moduleDlg.getField(4)));
    mo.setModeOfPayment(moduleDlg.getField(5));
    mo.setPayment(moduleDlg.getField(6));
    mo.setNOrderLines(1);
    mo.setId(module_orders.size());// id temporaire
    view.addModule(mo);

    module_orders.addElement(mo);
  }

  private void modifyModule() {
    int n = view.getSelectedModule();
    if (n < 0) {
      return;
    }

    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    ModuleOrder mo = (ModuleOrder) module_orders.elementAt(n);
    int oldModule = mo.getModule();
    if (moduleDlg == null) {
      try {
        moduleDlg = new ModuleDlg(this, dossier, service, dataCache);
      } catch (SQLException ex) {
        GemLogger.log(getClass().getName(), "modifyModule", ex);
        return;
      }
    }

    moduleDlg.setField(0, String.valueOf(mo.getModule()));
    moduleDlg.setField(2, mo.getStart().toString());
    moduleDlg.setField(3, mo.getEnd().toString());
    moduleDlg.setField(4, String.valueOf(mo.getPrice()));
    moduleDlg.setField(5, mo.getModeOfPayment());
    moduleDlg.setField(6, mo.getPayment());
    moduleDlg.setField(7, String.valueOf(mo.getSelectedModule()));

    setCursor(Cursor.getDefaultCursor());

    moduleDlg.show();
    if (moduleDlg.isValidation()) {
      mo.setModule(Integer.parseInt(moduleDlg.getField(0)));
      mo.setSelectedModule(Integer.parseInt(moduleDlg.getField(7)));
      mo.setTitle(moduleDlg.getField(1));
      mo.setStart(new DateFr(moduleDlg.getField(2)));
      mo.setEnd(new DateFr(moduleDlg.getField(3)));
      mo.setPrice(Double.parseDouble(moduleDlg.getField(4)));
      mo.setModeOfPayment(moduleDlg.getField(5));
      mo.setPayment(moduleDlg.getField(6));
      view.changeModule(n, mo);

      if (mo.getModule() != oldModule) {
        for(CourseOrder co : mo.getCourseOrders()) {
          view.remove(co);
        }
        mo.getCourseOrders().clear();
        try {
          Module m = ((ModuleIO) DataCache.getDao(Model.Module)).findId(mo.getModule());
          if (m != null) {
            for (CourseModuleInfo info : m.getCourses()) {
              addCourse(mo, info);
            }
          }
        } catch (SQLException ex) {
          GemLogger.logException(ex);
        }
      } else {
        for(CourseOrder co : mo.getCourseOrders()) {
          co.setDateStart(mo.getStart());
          co.setDateEnd(mo.getEnd());
        }
      }
    }
  }

  private void removeModule() {
    int n = view.getSelectedModule();
    if (n < 0) {
      return;
    }

    ModuleOrder mo = module_orders.elementAt(n);
    module_orders.removeElementAt(n);
    view.removeModule(n);
    for(CourseOrder co : mo.getCourseOrders()) {
      view.remove(co);
    }
    mo.getCourseOrders().clear();
  }

  /**
   * Checks if the module {@code id} has been already added.
   *
   * @param id module id
   * @return true if module exists
   * @deprecated from 2.8.a
   */
  private boolean alreadySelectedModule(int id) {
    for (ModuleOrder cmd : module_orders) {
      if (cmd.getModule() == id) {
        return true;
      }
    }
    return false;
  }

  private void addCourse(ModuleOrder mo, CourseModuleInfo moduleInfo) {
    CourseOrder co = new CourseOrder();
    co.setTitle("["+mo.getTitle()+"]"+ moduleInfo.getCode().getLabel() + " " + BundleUtil.getLabel("To.define.label"));
    co.setDay(0);//dimanche
    co.setModuleOrder(mo.getId()); // id module temp
    co.setStart(new Hour());
    co.setEnd(new Hour(moduleInfo.getTimeLength()));
    co.setCourseModuleInfo(moduleInfo);
    co.setDateStart(mo.getStart());
    co.setDateEnd(mo.getEnd());

    view.addCourse(co);
    mo.addCourseOrder(co);
  }

  /**
   * Opens the dialog for course order modification.
   */
  private void modifyCourse() {
    int n = view.getSelectedCourse();// le commandes_cours selectionné
    if (n < 0) {
      return;
    }

    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    CourseOrder co = view.getCourseOrder(n);
    if (courseEnrolmentDlg == null) {
      courseEnrolmentDlg = new CourseEnrolmentDlg(desktop, service, dossier.getId());
    }
    courseEnrolmentDlg.clear();
    courseEnrolmentDlg.setCourseInfo(co.getCourseModuleInfo());
    try {
      courseEnrolmentDlg.loadEnrolment(co);
    } catch (EnrolmentException ex) {
      MessagePopup.warning(view, ex.getMessage());
      setCursor(Cursor.getDefaultCursor());
      return;
    }
    setCursor(Cursor.getDefaultCursor());
    courseEnrolmentDlg.entry();
    if (courseEnrolmentDlg.isValidation()) {
      co.setModule(Integer.parseInt(courseEnrolmentDlg.getField(1)));//XXX module number not valid here
      co.setAction(Integer.parseInt(courseEnrolmentDlg.getField(2)));
      co.setTitle(getModuleTitle(co) + courseEnrolmentDlg.getField(3));
      co.setDay(Integer.parseInt(courseEnrolmentDlg.getField(4)));

      if (CourseCodeType.ATP.getId() == courseEnrolmentDlg.getCourse().getCode()) {
        DateFr dfr = new DateFr(courseEnrolmentDlg.getField(7));
        co.setDateStart(dfr);
        co.setDateEnd(dfr);
      }

      Hour start = new Hour(courseEnrolmentDlg.getField(5));
      Hour length = new Hour(courseEnrolmentDlg.getField(6));
      co.setStart(start);
      co.setEnd(start.end(length.toMinutes()));
      co.setEstab(courseEnrolmentDlg.getEstab());

      view.changeCourse(n, co);
    }
  }

  private String getModuleTitle(CourseOrder co) {
    String t = co.getTitle();
    return t.substring(0, t.indexOf(']')+1);
  }


  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + dossier.getId();
  }

}
