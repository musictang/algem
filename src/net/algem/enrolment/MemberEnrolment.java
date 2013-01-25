/*
 * @(#)MemberEnrolment.java	2.7.a 26/11/12
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
package net.algem.enrolment;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import net.algem.accounting.*;
import net.algem.config.*;
import net.algem.contact.PersonFile;
import net.algem.course.Course;
import net.algem.course.Module;
import net.algem.course.ModuleDlg;
import net.algem.course.ModuleIO;
import net.algem.edition.MemberCardEditor;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.PlanningService;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.room.Room;
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
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 * @see net.algem.contact.PersonFileEditor
 *
 */
public class MemberEnrolment
        extends FileTabDialog
{

  private final static int SESSIONS_MAX = 66;
  /** Last month of debiting. */
  private static int LAST_MONTH_PRL = 6;
  private final static String NONE = MessageUtil.getMessage("none.info");
  private EnrolmentView vue;
  /** Module order list. */
  private Vector<ModuleOrder> modules;
  /** Course order list. */
  private Vector<CourseOrder> commandes_cours;
  private ModuleDlg moduleDlg;
  private CourseEnrolmentDlg coursDlg;
  private double totalBase = 0.0;
  private int moduleCourant = 0;
  private int maxCours = 0; // (le commandes_cours dans la liste comportant le plus grand nombre de séances)
  private StringBuffer msg = new StringBuffer(MessageUtil.getMessage("enrolment.confirmation"));
  private PersonFile dossier;
  private ActionListener listener;
  private EnrolmentService service;

  public MemberEnrolment(GemDesktop _desktop, ActionListener _listener, PersonFile _dossier) {
    super(_desktop);
    dossier = _dossier;
    listener = _listener;
    service = new EnrolmentService(desktop.getDataCache());

    vue = new EnrolmentView();
    vue.setMember(dossier.getContact());
    vue.addActionListener(this);

    modules = new Vector<ModuleOrder>();
    commandes_cours = new Vector<CourseOrder>();

    setLayout(new BorderLayout());
    add(vue, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
  }

  @Override
  public void cancel() {
    listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "AdherentInscription.Abandon"));
  }

  @Override
  public void validation() {

    if (modules.isEmpty()) {
      new ErrorDlg(this, "Inscription vide");
      return;
    }
    try {
      // Vérification des modules
      for (int i = 0; i < modules.size(); i++) {
        ModuleOrder m = modules.elementAt(i);
        Module mod = service.getModule(m.getModule());
        if (m.getModule() == 0 || mod == null) {// si module inexistant
          new ErrorDlg(this, "Choix module incorrect");
          return;
        }
      }
      PlanningService pService = new PlanningService(dc);

      Date d = new Date();
      Order cmd = new Order();
      cmd.setMember(dossier.getId());
      cmd.setPayer(dossier.getMember().getPayer());
      cmd.setInvoice(null);
      cmd.setCreation(new DateFr(d));
      //insertion dans la table commande
      dc.setAutoCommit(false);
      OrderIO.insert(cmd, dc);

      //Détermination de l'établissement et de l'école pour l'enregistrement des échéances
      CourseOrder cc = commandes_cours.elementAt(0);
      Room s = service.getRoom(cc.getRoom());//1.1da
      int etablissement = s.getEstab();
      Course c = pService.getCourseFromAction(cc.getAction());
      int ecole = c.getSchool();

      ModuleOrder m = null;

      //premier parcours de boucle pour détermine le prix total.
      for (int i = 0; i < modules.size(); i++) {
        m = modules.elementAt(i);
        totalBase += m.getPrice();
      }
      /*
       * enregistrement des modules
       */
      for (int i = 0; i < modules.size(); i++) {
        m = modules.elementAt(i);
        m.setId(cmd.getId());//récupération du numéro de commande
        enregistreModule(m);
        moduleCourant++;
      }

      if (modules != null && modules.size() > 0 && maxCours > 0) {// ajout maxCours > 0 2.0pq
        try {
          saveOrderLines(modules.elementAt(0), ecole, etablissement);
        } catch (NullAccountException ne) {
          MessagePopup.warning(vue, ne.getMessage());
        }
      }
      if (!MessagePopup.confirm(vue, msg.toString(), BundleUtil.getLabel("Confirmation.title"))) {
        throw new SQLException("abandon");
      }
      dc.commit();
      desktop.postEvent(new ModifPlanEvent(this, m.getStart(), m.getEnd()));
      desktop.postEvent(new EnrolmentCreateEvent(this, dossier.getId()));
      clear();
    } catch (SQLException e1) {
      dc.rollback();
      GemLogger.logException("Insertion inscription", e1);
      MessagePopup.information(vue, MessageUtil.getMessage("enrolment.cancel.info"));
      return;
    } finally {
      dc.setAutoCommit(true);
      totalBase = 0.0;//
      moduleCourant = 0;
      maxCours = 0;
    }

    listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "AdherentInscription.Validation"));
    MemberCardEditor ca = new MemberCardEditor(desktop, dossier);
    ca.edit();

  }

  /**
   * Saves a module order.
   *
   * @param module
   * @throws java.lang.SQLException
   */
  public void enregistreModule(ModuleOrder module) throws SQLException {

    int sessions = 0;

    //insertion dans la table commande_module
    ModuleOrderIO.insert(module, dc);

    //parcours des commandes_cours
    for (int i = 0; i < commandes_cours.size(); i++) {

      CourseOrder cc = commandes_cours.elementAt(i);
      if (module.getModule() != cc.getModule()) {
        continue; // on ne sélectionne que les commandes_cours appartenant au même module
      }
      cc.setIdOrder(module.getId());//id de la commande
      // dates de début et de end  spécifiques pour les ateliers ?
      //if (!cc.getCode().equals("ATP")) {
      cc.setDateStart(module.getStart());
      cc.setDateEnd(module.getEnd());

      //insertion dans la table commande_cours
      CourseOrderIO.insert(cc, dc);

      /*
       * 2.0j : ajout conditionnel d'un atelier ponctuel supplementaire.
       */
      if (Course.ATP_CODE.equals(cc.getCode()) && !"00:00".equals(cc.getStart().toString())) {
        CourseOrder cd = new CourseOrder();
        cd.setIdOrder(cc.getIdOrder());
        cd.setModule(cc.getModule());
        cd.setCode(Course.ATP_CODE);
        cd.setAction(service.getIdAction(Course.ATP_CODE));
        cd.setStart(new Hour("00:00:00"));
        cd.setEnd(new Hour("02:00:00"));
        cd.setDateStart(module.getStart());
        cd.setDateEnd(module.getEnd());
        CourseOrderIO.insert(cd, dc);
      }
      // le nombre de plages insérées (nombre de séances)
      sessions = service.updateRange(module, cc, dossier.getId());
      if (sessions > maxCours) {
        maxCours = sessions;
      }
      if (maxCours > SESSIONS_MAX) {
        maxCours = SESSIONS_MAX;
      }
    }// end parcours des commandes_cours

  }//fin enregistreModule

  /**
   * Saves the order lines. 
   * This depends on organization configuration. 
   *
   * @param moduleOrder
   * @param schoolId
   * @param estabId estab id
   * @throws Exception
   */
  public void saveOrderLines(ModuleOrder moduleOrder, int schoolId, int estabId) throws SQLException, NullAccountException {

    String label = "p" + dossier.getMember().getPayer() + " a" + dossier.getId();
    ArrayList<OrderLine> orderLines = new ArrayList<OrderLine>();

    Module f = ((ModuleIO) DataCache.getDao(Model.Module)).findId(moduleOrder.getModule());
    // le payeur, le type de reglement et le numero d'inscription sont déjà récupérés par le constructeur
    OrderLine e = new OrderLine(moduleOrder);
    //numero adherent
    e.setMember(dossier.getId());
    //libelle
    e.setLabel(label); // 1.2c
    //compte et analytique
    String analytics = "";

    int key = 0;
    if (f.getCode().charAt(0) == 'L') {
      Preference p = AccountPrefIO.find(AccountPrefIO.LEISURE_KEY_PREF, dc);
      key = (Integer) p.getValues()[0];
      analytics = (String) p.getValues()[1];
    } else {
      Preference p = AccountPrefIO.find(AccountPrefIO.PRO_KEY_PREF, dc);
      key = (Integer) p.getValues()[0];
      analytics = (String) p.getValues()[1];
    }
    Account p = AccountIO.find(key, dc);
    e.setAccount(p);
    Param a = ParamTableIO.findByKey(CostAccountCtrl.tableName, CostAccountCtrl.columnKey, analytics, dc);
    if (a != null) {
      e.setCostAccount(new Account(a));
    } else {
      throw new NullAccountException(MessageUtil.getMessage("no.default.cost.account"));
    }
    e.setCostAccount(new Account(a));

    Param school = ParamTableIO.findByKey(SchoolCtrl.TABLE, SchoolCtrl.COLUMN_KEY, String.valueOf(schoolId), dc);
    e.setSchool(school.getValue());
    // ajout des lignes d'échéances
    if (!ModeOfPayment.NUL.toString().equals(moduleOrder.getModeOfPayment())) {
      if ("TRIM".equals(moduleOrder.getPayment())) { // echeance trimestrielle
        orderLines = setQuarterOrderLines(moduleOrder, e);
      } else if ("MOIS".equals(moduleOrder.getPayment())) {// echeance mensuelle
        orderLines = setMonthOrderLines(moduleOrder, e);
      } else { // (ANNUEL)
        e.setAmount(AccountUtil.getIntValue(totalBase));
        DateFr de = moduleOrder.getStart();
        de.incMonth(1);
        de.setDay(15);
        e.setDate(de);
        //insertion echeances
        orderLines.add(e);
      }
    }

    for (OrderLine ol : orderLines) {
      AccountUtil.createEntry(ol, dc);
    }
    //mise à jour nombre d'échéances dans commande_module
    if (orderLines.size() > 0) {
      Vector<ModuleOrder> cmv = ModuleOrderIO.findId(moduleOrder.getId(), dc);
      if (!cmv.isEmpty()) {
        ModuleOrder cm = cmv.elementAt(0);
        cm.setNOrderLines(orderLines.size());
        ModuleOrderIO.update(cm, dc);
      }
    }

  } //fin saveOrderLines

  /**
   *
   * @param module
   * @param e
   * @return a list of order lines
   */
  public ArrayList<OrderLine> setQuarterOrderLines(ModuleOrder module, OrderLine e) {
    ArrayList<OrderLine> orderLines = new ArrayList<OrderLine>();
    Vector<DateFr> dates = getOrderQuarterDates(module.getStart(), module.getEnd());
    int firstDocumentNumber = 0;
    try {
      firstDocumentNumber = Integer.parseInt(ConfigUtil.getConf(ConfigKey.ACCOUNTING_DOCUMENT_NUMBER.getKey(), dc));
    } catch (NumberFormatException nfe) {
      System.err.println(getClass().getName() + "#setEcheancesTrim " + nfe.getMessage());
    }

    int documentNumber = firstDocumentNumber;
    /* int nombreEcheances = 0; if (echeances != null) { nombreEcheances =
     * echeances.size();
		} */


    // DESACTIVATION CALCUL PRORATA
    // double montantPremiereEcheance = calcFirstOrderLineAmount(totalBase, maxCours, nombreEcheances, "TRIM");
    // e.setAmount(AccountUtil.getIntValue(montantPremiereEcheance));
    e.setAmount(AccountUtil.getIntValue(totalBase));
    e.setDate((DateFr) dates.elementAt(0));
    if (module.getModeOfPayment().equals("PRL")) {
      documentNumber = calcDocumentNumber(firstDocumentNumber, ((DateFr) dates.elementAt(0)).getMonth());
      e.setDocument("PRL" + String.valueOf(documentNumber));
    } else {
      e.setDocument(module.getModeOfPayment() + 1);// pas nécessaire
    }
    orderLines.add(new OrderLine(e));
    for (int i = 1; i < dates.size(); i++) {
      //libelle numero piece
      if (module.getModeOfPayment().equals("PRL")) {
        documentNumber = calcDocumentNumber(firstDocumentNumber, ((DateFr) dates.elementAt(i)).getMonth());
        e.setDocument("PRL" + String.valueOf(documentNumber));
      } else {
        e.setDocument(module.getModeOfPayment() + (i + 1));
      }
      e.setAmount(AccountUtil.getIntValue(totalBase));
      e.setDate((DateFr) dates.elementAt(i));
      orderLines.add(new OrderLine(e));
    }
    return orderLines;
  }

  /**
   *
   * @param module
   * @param e
   * @return a list of order lines
   */
  public ArrayList<OrderLine> setMonthOrderLines(ModuleOrder module, OrderLine e) {
    ArrayList<OrderLine> orderLines = new ArrayList<OrderLine>();
    Vector<DateFr> orderDates = getOrderMonthDates(module.getStart(), module.getEnd());
    int firstDocumentNumber = 0;
    try {
      firstDocumentNumber = Integer.parseInt(ConfigUtil.getConf(ConfigKey.ACCOUNTING_DOCUMENT_NUMBER.getKey(), dc));
    } catch (NumberFormatException ne) {
      System.err.println("Format Numero.piece " + ne.getMessage());
    }
    int documentNumber = firstDocumentNumber;
    int orderLinesNumber = 0;
    if (orderDates != null) {
      orderLinesNumber = orderDates.size();
    }
    // DESACTIVATION CALCUL PRORATA
//		double montantPremiereEcheance = calcFirstOrderLineAmount(totalBase, maxCours, nombreEcheances, "MOIS");
//		e.setAmount(AccountUtil.getIntValue(montantPremiereEcheance));

    e.setAmount(AccountUtil.getIntValue(totalBase));
    e.setDate(orderDates.elementAt(0));
    documentNumber = calcDocumentNumber(firstDocumentNumber, ((DateFr) orderDates.elementAt(0)).getMonth());
    if ("PRL".equals(module.getModeOfPayment())) {
      e.setDocument("PRL" + String.valueOf(documentNumber));
    } else {
      e.setDocument(module.getModeOfPayment() + 1);
    }
    orderLines.add(new OrderLine(e));

    for (int i = 1; i < orderLinesNumber; i++) {
      e.setAmount(AccountUtil.getIntValue(totalBase));
      e.setDate((DateFr) orderDates.elementAt(i));
      if ("PRL".equals(module.getModeOfPayment())) {
        documentNumber = calcDocumentNumber(firstDocumentNumber, ((DateFr) orderDates.elementAt(i)).getMonth());
        e.setDocument("PRL" + String.valueOf(documentNumber));
      } else {
        e.setDocument(module.getModeOfPayment() + (i + 1));
      }
      orderLines.add(new OrderLine(e));
    }
    return orderLines;
  }

  /**
   * Reset.
   */
  public void clear() {
    vue.clear();
    modules = new Vector<ModuleOrder>();
    commandes_cours = new Vector<CourseOrder>();
    //if (moduleDlg != null)
    //	moduleDlg.clear();
  }

  public void load(PersonFile d) {
    clear();
    dossier = d;
    vue.setMember(d.getContact());
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
    if (e.getActionCommand().equals("Ajouter Module")) {
      addModule();
    } else if (e.getActionCommand().equals("Enlever Module")) {
      removeModule();
    } else if (e.getActionCommand().equals("Modification Cours")) {
      modifyCourse();
    } else if (e.getActionCommand().equals("Modifier Module")) {
      modifyModule();
    } else {
      super.actionPerformed(e);
    }
  }

  public void removeModule() {
    int n = vue.getSelectedModule();
    if (n < 0) {
      return;
    }

    ModuleOrder cm = modules.elementAt(n);
    modules.removeElementAt(n);
    vue.removeModule(n);

    for (int i = commandes_cours.size() - 1; i >= 0; i--) {
      CourseOrder cc = commandes_cours.elementAt(i);
      if (cc.getModule() == cm.getModule()) {
        vue.removeCourse(i);
        commandes_cours.removeElementAt(i);
      }
    }
  }

  /**
   * Adds a module.
   */
  public void addModule() {
    try {
      if (moduleDlg == null) {
        moduleDlg = new ModuleDlg(this, dossier, service, dataCache);
      }
      moduleDlg.show();
      if (!moduleDlg.isValidation()) {
        return;
      }
      int idModule = Integer.parseInt(moduleDlg.getField(0));
      if (alreadySelectedModule(idModule)) {
        MessagePopup.warning(null, MessageUtil.getMessage("module.already.selected", new Object[]{idModule}));
        return;
      }
      ModuleOrder cm = new ModuleOrder();

      Module m = ((ModuleIO) DataCache.getDao(Model.Module)).findId(idModule);
      addModule(cm, m);

      int duree = 0;

      // ajout des commandes_cours d'instrument
      if ((duree = m.getInstrumentDuration()) > 0) {
        addCourseInstrument(cm, duree);
      }

      // ajout des commandes_cours collectifs
      if ((duree = m.getWorkshopDuration()) > 0) {
        addCourseCo(cm, duree);
      }

      // ajout des ateliers découverte
      if (m.withSelectiveWorkshop()) {
        addWorkshop(cm);
      }

      // ajout des commandes_cours collectifs de formation musicale
      if (m.withMusicalFormation()) {
        addFM(cm);
      }
    } catch (SQLException ex) {
      MessagePopup.warning(vue, "#moduleAjouter " + ex.getMessage());
    }
  }// end addModule

  public void modifyModule() {
    int n = vue.getSelectedModule();
    if (n < 0) {
      return;
    }

    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    ModuleOrder cm = (ModuleOrder) modules.elementAt(n);

    if (moduleDlg == null) {
      try {
        moduleDlg = new ModuleDlg(this, dossier, service, dataCache);
      } catch (SQLException ex) {
        GemLogger.log(getClass().getName(), "moduleModif", ex);
        return;
      }
    }

    moduleDlg.setField(0, String.valueOf(cm.getModule()));
    moduleDlg.setField(2, cm.getStart().toString());
    moduleDlg.setField(3, cm.getEnd().toString());
    moduleDlg.setField(4, String.valueOf(cm.getPrice()));
    moduleDlg.setField(5, cm.getModeOfPayment());
    moduleDlg.setField(6, cm.getPayment());
    moduleDlg.setField(7, String.valueOf(cm.getSelectedModule()));

    setCursor(Cursor.getDefaultCursor());

    moduleDlg.show();
    if (moduleDlg.isValidation()) {
      cm.setModule(Integer.parseInt(moduleDlg.getField(0)));
      cm.setSelectedModule(Integer.parseInt(moduleDlg.getField(7)));
      cm.setTitle(moduleDlg.getField(1));
      cm.setStart(new DateFr(moduleDlg.getField(2)));
      cm.setEnd(new DateFr(moduleDlg.getField(3)));
      cm.setPrice(Double.parseDouble(moduleDlg.getField(4)));
      cm.setModeOfPayment(moduleDlg.getField(5));
      cm.setPayment(moduleDlg.getField(6));
      vue.changeModule(n, cm);
    }
  }

  /**
   * Opens the dialog for course order modification.
   */
  public void modifyCourse() {
    int n = vue.getSelectedCourse();// le commandes_cours selectionné
    if (n < 0) {
      return;
    }
    Hour start = null;

    setCursor(new Cursor(Cursor.WAIT_CURSOR));

    CourseOrder cc = commandes_cours.elementAt(n);

    if (coursDlg == null) {
      coursDlg = new CourseEnrolmentDlg(desktop, service, dossier.getId());
    }
    coursDlg.clear();
    coursDlg.setCode(cc.getCode());
    try {
      coursDlg.loadEnrolment(cc);
    } catch (EnrolmentException ex) {
      MessagePopup.warning(vue, ex.getMessage());
      setCursor(Cursor.getDefaultCursor());
      return;
    }
    setCursor(Cursor.getDefaultCursor());
    coursDlg.entry();
    if (coursDlg.isValid()) {
      cc.setModule(Integer.parseInt(coursDlg.getField(1)));
      cc.setAction(Integer.parseInt(coursDlg.getField(2)));
      cc.setTitle(coursDlg.getField(3));
      cc.setDay(Integer.parseInt(coursDlg.getField(4)));

      if (Course.ATP_CODE.equalsIgnoreCase(coursDlg.getCourse().getCode())) {
        DateFr dfr = new DateFr(coursDlg.getField(7));
        cc.setDateStart(dfr);
        cc.setDateEnd(dfr);
      }

      start = new Hour(coursDlg.getField(5));
      Hour du = new Hour(coursDlg.getField(6));
      cc.setStart(start);
      cc.setEnd(start.end(du.toMinutes()));

      cc.setRoom(coursDlg.getRoomId()); // modification de la salle
      vue.changeCourse(n, cc);
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + dossier.getId();
  }

  /**
   * Gets a list of dates for quarterly payment.
   *
   * @param orderDateStart
   * @param orderDateEnd
   * @return a list of dates
   */
  public static Vector<DateFr> getOrderQuarterDates(DateFr orderDateStart, DateFr orderDateEnd) {
    Vector<DateFr> dates = new Vector<DateFr>();
    boolean inc = false;
    int nbMonths = 0;
    int nbOrderLines = 0;

    DateFr firstOrderLine = new DateFr(orderDateStart);
    firstOrderLine.setDay(15);

    int orderStartMonth = orderDateStart.getMonth();
    //System.out.println("1 date 1ere echeance " + firstOrderLine);
    // on incrémente d'un mois, passé le 10 du mois ou si mois de septembre
    if (orderDateStart.getDay() > 10 || orderStartMonth == 9) {
      firstOrderLine.incMonth(1);
      inc = true;
    }
    dates.add(new DateFr(firstOrderLine));
    if (inc && orderStartMonth != 9 && orderStartMonth != 12 && orderStartMonth != 3) {
      //nbMois = calcNumberOfMonths(moisDebutCommande, dateFinCommande.getMonth());
      nbMonths = calcNumberOfMonths(orderDateStart, orderDateEnd);
    } else {
      //nbMois = calcNumberOfMonths(premiereEcheance.getMonth(), dateFinCommande.getMonth());
      nbMonths = calcNumberOfMonths(firstOrderLine, orderDateEnd);// premiere echeance
    }
    if (nbMonths <= 3) {
      nbOrderLines = 1;
    } else if (nbMonths <= 6) {
      nbOrderLines = 2;
    } else {
      nbOrderLines = 3;
    }
    //System.out.println("nombre echeances trimestre: " + nbOrderLines);
    // ajout des échéances
    for (int i = 1; i < nbOrderLines; i++) {
      switch (firstOrderLine.getMonth()) {
        case 10:
        case 1:
        case 4:
          firstOrderLine.incMonth(3);
          break;
        case 11:
        case 2:
          firstOrderLine.incMonth(2);
          break;
        case 12:
        case 3:
          firstOrderLine.incMonth(1);
          break;
      }
      dates.add(new DateFr(firstOrderLine));
    }
    return dates;
  }

  public static Vector<DateFr> getOrderMonthDates(DateFr startOrderDate, DateFr endOrderDate) {
    Vector<DateFr> dates = new Vector<DateFr>();

    int n = 0;
    DateFr firstOrderLine = new DateFr(startOrderDate);
    firstOrderLine.setDay(15);
    /*
     * int jourDebutCommande = dateDebutCommande.getDay(); int moisDebutCommande
     * = dateDebutCommande.getMonth();
     */

    if (startOrderDate.getDay() > 10 || startOrderDate.getMonth() == 9) {
      firstOrderLine.incMonth(1);
    }
    dates.add(new DateFr(firstOrderLine));

    n = calcNumberOfMonths(firstOrderLine, endOrderDate);
    for (int i = 1; i < n; i++) {
      firstOrderLine.incMonth(1);
      dates.add(new DateFr(firstOrderLine));
    }

    return dates;
  }

  /**
   *
   * @param total montant période
   * @param maxSessions nombre de séances
   * @param nbOrderLines nombre d'échéances
   * @param type périodicité des échéances
   * @return un double
   */
  public static double calcFirstOrderLineAmount(double total, int maxSessions, int nbOrderLines, String type) {

    double fistAmount = total;
    double sessionPrice = 0.0;

    if (type.equals("TRIM")) {
      sessionPrice = total / 11; // 11 séances par trimestre
    } else {
      sessionPrice = ((total * 3) / 11);
    }

    if (nbOrderLines > 0) {
      if (maxSessions > 0) {
        // au prorata du nombre de commandes_cours effectifs
        // (prixSeance * maxCours) supposé supérieur à (total * (necheances - 1)
        fistAmount = (sessionPrice * maxSessions) - (total * (nbOrderLines - 1));
      }
    } else {
      fistAmount = 0.0;
    }

    return fistAmount;
  }

  /**
   * Calculates an intervall between two months.
   *
   * @param monthStart
   * @param monthEnd
   * @return an integer
   */
  public static int calcNumberOfMonths(int monthStart, int monthEnd) {
    if (monthEnd > MemberEnrolment.LAST_MONTH_PRL && monthEnd < 9) {
      monthEnd = MemberEnrolment.LAST_MONTH_PRL;
    }
    if (monthStart > monthEnd) {
      return monthEnd + (13 - monthStart);
    } else {
      return Math.abs((monthEnd - monthStart)) + 1;
    }
  }

  public static int calcNumberOfMonths(DateFr deb, DateFr fin) {
    DateFr d = new DateFr(deb);
    DateFr f = new DateFr(fin);
    if (d.getMonth() == f.getMonth()) {
      return 1;
    }
    int m = 0;
    while (d.compareTo(f) <= 0) {
      m++;
      d.incMonth(1);
    }
    return m;
  }

  /**
   * Calculates the document number for debiting.
   *
   * @param month first month
   * @return
   */
  private static int calcDocumentNumber(int num, int month) {
    int number = num;
    if (month >= 9 && month <= 12) {
      return number + (month - 10);
    } else {
      return number + 2 + month;
    }
  }

  /**
   * Checks if the module {@code id} has been already added.
   *
   * @param id module id
   * @return true if module exists
   */
  private boolean alreadySelectedModule(int id) {
    for (ModuleOrder cmd : modules) {
      if (cmd.getModule() == id) {
        return true;
      }
    }
    return false;
  }

  /**
   * Adds a module.
   *
   * @param cm module order
   * @param f module
   */
  private void addModule(ModuleOrder cm, Module f) {

    cm.setTitle(f.getTitle());
    cm.setPayer(dossier.getMember().getPayer());
    cm.setModule(f.getId());
    cm.setSelectedModule(Integer.parseInt(moduleDlg.getField(7)));
    //System.out.println("module selected :" + cm.getSelectedModule());
    cm.setStart(new DateFr(moduleDlg.getField(2)));
    cm.setEnd(new DateFr(moduleDlg.getField(3)));
    cm.setPrice(Double.parseDouble(moduleDlg.getField(4)));
    cm.setModeOfPayment(moduleDlg.getField(5));
    cm.setPayment(moduleDlg.getField(6));
    cm.setNOrderLines(1);

    vue.addModule(cm);
    modules.addElement(cm);
  }

  private void addCourseInstrument(ModuleOrder cm, int duree) throws SQLException {
    CourseOrder cc = new CourseOrder();
    cc.setAction(service.getIdAction("Inst"));
    cc.setTitle(NONE);
    cc.setDay(0);//dimanche
    cc.setModule(cm.getModule());
    cc.setStart(new Hour("00:00"));
    cc.setEnd(new Hour(duree));
    cc.setCode("Inst");
    cc.setDateStart(cm.getStart());
    cc.setDateEnd(cm.getEnd());
    cc.setRoom(0);// salle à définir
    vue.addCourse(cc);
    commandes_cours.addElement(cc);
  }

  private void addCourseCo(ModuleOrder cm, int duree) throws SQLException {
    CourseOrder cc = new CourseOrder();
    cc.setTitle(NONE);
    cc.setDay(0);
    cc.setModule(cm.getModule());
    cc.setStart(new Hour("00:00"));
    cc.setEnd(new Hour(duree));

    String code = String.valueOf(duree);
    if (duree < 100) {
      code = "0" + code;
    }
    code = "AT" + code;
    cc.setCode(code);
    cc.setAction(service.getIdAction(code));
    cc.setDateStart(cm.getStart());
    cc.setDateEnd(cm.getEnd());
    vue.addCourse(cc);
    commandes_cours.addElement(cc);
  }

  private void addFM(ModuleOrder cm) throws SQLException {
    String fmCode = "F.M.";
    CourseOrder cc = new CourseOrder();
    cc.setAction(service.getIdAction(fmCode));
    cc.setTitle(NONE);
    cc.setDay(0);
    cc.setModule(cm.getModule());
    cc.setCode(fmCode);
    cc.setStart(new Hour("00:00"));
    cc.setEnd(new Hour("01:00"));
    cc.setDateStart(cm.getStart());
    cc.setDateEnd(cm.getEnd());
    vue.addCourse(cc);
    commandes_cours.addElement(cc);
  }

  private void addWorkshop(ModuleOrder cm) throws SQLException {
    CourseOrder cc = new CourseOrder();
    cc.setAction(service.getIdAction(Course.ATP_CODE));
    cc.setTitle(NONE);
    cc.setDay(0);
    cc.setModule(cm.getModule());
    cc.setStart(new Hour("00:00"));
    cc.setEnd(new Hour("00:00"));
    cc.setCode(Course.ATP_CODE);
    cc.setDateStart(cm.getStart());
    cc.setDateEnd(cm.getEnd());
    vue.addCourse(cc);
    commandes_cours.addElement(cc);
  }
}
