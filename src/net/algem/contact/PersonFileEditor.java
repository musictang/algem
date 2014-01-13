/*
 * @(#)PersonFileEditor 2.8.r 10/01/14
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
package net.algem.contact;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.*;
import net.algem.accounting.*;
import net.algem.bank.*;
import net.algem.billing.*;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.member.*;
import net.algem.contact.teacher.TeacherEditor;
import net.algem.contact.teacher.TeacherEvent;
import net.algem.contact.teacher.TeacherIO;
import net.algem.edition.*;
import net.algem.enrolment.MemberEnrolmentDlg;
import net.algem.group.PersonFileGroupView;
import net.algem.planning.TeacherBreakDlg;
import net.algem.planning.month.MonthScheduleTab;
import net.algem.security.User;
import net.algem.security.UserCreateDlg;
import net.algem.security.UserException;
import net.algem.security.UserService;
import net.algem.util.*;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.jdesktop.DesktopOpenHandler;
import net.algem.util.menu.PersonFileMenuItem;
import net.algem.util.model.GemCloseVetoException;
import net.algem.util.model.Model;
import net.algem.util.module.FileEditor;
import net.algem.util.module.FileView;
import net.algem.util.module.GemDesktopCtrl;
import net.algem.util.ui.*;

/**
 * Person file editor.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.r
 */
public class PersonFileEditor
        extends FileEditor
        implements PersonFileListener
{

  private JMenuBar mBar;
  private JMenu mFile;
  private JMenu mOptions;
  private JMenu mHelp;
  private JMenuItem miAbout;
  private JMenuItem miDoc;
  private JMenuItem miDelete;
  private JMenuItem miLogin;
  private JMenuItem miMember, miTeacher, miBank, miEmployee;
  private JMenuItem miPassRehearsal, miRehearsal;
  private JMenuItem miHistoRehearsal;
  private JMenuItem miCard;
  private JMenuItem miHistoInvoice;
  private JMenuItem miHistoQuote;
  private JMenuItem miMonthPlanning;
  private JMenuItem miGroups;
  private PersonFile dossier, parent;
  private PersonFileTabView personFileView;
  private PersonFileListCtrl memberList;
  private HistoInvoice histoInvoice;
  private OrderLineEditor orderLineEditor;
  private DataConnection dc;
  private static BankBranchIO BANK_BRANCH_IO;

  public PersonFileEditor() {
    super("Fiche: ");
    dossier = new PersonFile();
  }

  public PersonFileEditor(PersonFile dossier) {
    super("Fiche:" + dossier.getContact().toString());
    this.dossier = dossier;
  }

  public PersonFileEditor(PersonFile dossier, PersonFile parent) {
    this(dossier);
    this.parent = parent;
  }

  /**
   *
   * @param dossier
   * @deprecated
   */
  public void setDossierPersonne(PersonFile dossier) {
    this.dossier = dossier;
    personFileView.contentsChanged(new PersonFileEvent(dossier, PersonFileEvent.CONTENTS_CHANGED));
  }

  /**
   *
   * @return person id as string
   */
  @Override
  public String getSID() {
    return String.valueOf(dossier.getId());
  }

  public int getDossierID() {
    return dossier.getId();
  }

  public PersonFileTabView getPersonView() {
    return personFileView;
  }

  /**
   * Called by {@link net.algem.util.module.GemDesktop#addModule(net.algem.util.module.GemModule) }.
   */
  @Override
  public void init() {

    super.init();

    dc = dataCache.getDataConnection();
    BANK_BRANCH_IO = new BankBranchIO(dc);

    desktop.addGemEventListener(this);
    view = personFileView = new PersonFileTabView(desktop, dossier, this);
    if (parent != null) {//???
      personFileView.setParent(parent);
    }
    personFileView.init();

    initMenus();

    // search for linked members
    checkAttachedMembers();

    // search for payer
    checkPayer();

    // search for groups
    if (personFileView.addGroupsTab(false)) {
      miGroups.setEnabled(false);
    }

    // search of number of memberships
    checkMembershipNumber();
  }

  /**
   * @see PersonFileListener
   *
   */
  @Override
  public void contentsChanged(PersonFileEvent _evt) {
    System.out.println("PersonFileEditor.contentChanged:" + _evt);
    if (PersonFileEvent.MEMBER_ADDED == _evt.getType()) {
      PersonFile d = ((PersonFile) _evt.getSource());
      addMenuDossier("Adhérent", d);
    } else if (PersonFileEvent.SUBSCRIPTION_CARD_CHANGED == _evt.getType()) {
      personFileView.contentsChanged(_evt);
    }

  }

  /**
   * @see GemEventListener
   */
  @Override
  public void postEvent(GemEvent evt) {
    //System.out.println("PersonFileEditor.postEvent:" + evt);
    if (evt instanceof InvoiceEvent) {
      Invoice f = ((InvoiceEvent) evt).getInvoice();
      if (dossier.getId() == f.getPayer() || dossier.getId() == f.getMember()) {
        if (orderLineEditor != null) {
          orderLineEditor.postEvent(evt);
        }
      }
    }

  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String arg = evt.getActionCommand();

    Object src = evt.getSource();
    // On save au préalable l'éventuel nouveau contact avant d'executer les actions des menus.
    if (dossier.getId() == 0
            && !arg.equals(GemCommand.SAVE_CMD)
            && !arg.equals(GemCommand.CLOSE_CMD)) {

      updatePersonFile();
      String msg = dossier.hasErrors();
      if (msg != null) {
        new ErrorDlg(personFileView, msg);
        return;
      }
      msg = checkContact(MessageUtil.getMessage("contact.save.warning"));
      if (MessagePopup.confirm(personFileView, msg, BundleUtil.getLabel("Contact.save.label"))) {
        dossier.setOldValues();// important
        save();
        personFileView.setID(dossier.getId());

      } else {
        return;
      }

    }
    if ("Member.reading".equals(arg)) {
      personFileView.addMemberTab();
      miMember.setEnabled(false);
    } else if ("Rehearsal.card.editing".equals(arg)) {
      personFileView.addSubscriptionCardTab(dossier);
      miCard.setEnabled(false);
    } else if ("Teacher".equals(arg)) {
      personFileView.addTeacherTab(this);
      miTeacher.setEnabled(false);
    } else if ("TeacherDelete".equals(arg)) {
      if (!dataCache.authorize("Teacher.suppression.auth")) {
        MessagePopup.warning(view, MessageUtil.getMessage("teacher.delete.authorization.warning"));
        return;
      }
      TeacherIO dao = (TeacherIO) DataCache.getDao(Model.Teacher);
      try {
        int c = dao.hasSchedules(dossier.getId());
        if (c > 0) {
          MessagePopup.warning(view, MessageUtil.getMessage("teacher.delete.warning", c));
        } else {
          dao.delete(dossier.getId());
          dataCache.remove(dossier.getTeacher());
          desktop.postEvent(new TeacherEvent(this, GemEvent.SUPPRESSION, dossier.getTeacher()));
          dossier.removeTeacher();
          personFileView.removeTeacher();
          miTeacher.setEnabled(true);
        }
      } catch (SQLException ex) {
        GemLogger.logException(ex);
      }
    } else if ("Person.group.tab".equals(arg) || "Groups".equals(arg)) {
      if (personFileView.addGroupsTab(true)) {
        miGroups.setEnabled(false);
      }
    } else if ("Person.bank.editing".equals(arg)) {
      personFileView.addBankTab();
      miBank.setEnabled(false);
      // export dossier
    } else if ("Member.file".equals(arg)) {
      showPersonFile();
    } else if ("Member.card".equals(arg)) {
      MemberCardEditor cardEditor = new MemberCardEditor(desktop, dossier);
      cardEditor.edit();
    } else if (GemCommand.NOTE_CMD.equals(arg)) {
      NoteDlg nd = new NoteDlg(desktop);
      nd.loadNote(personFileView.getNote(), dossier.getContact());
      nd.show();
      personFileView.setNote(nd.getNote());
    } else if ("Employee".equals(arg)) {
      personFileView.addEmployeeTab();
      miEmployee.setEnabled(false);
    } else if ("EmployeeDelete".equals(arg)) {
      personFileView.deleteEmployee();
      miEmployee.setEnabled(dataCache.authorize("Employee.editing.auth"));
    } /*
     * else if (evt.getActionCommand().equals("Payeur")) { view.setCursor(new
     * Cursor(Cursor.WAIT_CURSOR)); PersonFile payeur =
     * PersonFileIO.findPayer(dataCache, dossier.getMember().getPayer());
     *
     * PersonFileIO.complete(dataCache, dossier); PersonFileEditor editeur = new
     * PersonFileEditor(dossier); desktop.addModule(editeur);
     *
     * view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); }
     */ else if ("Member.schedule.payment".equals(arg)) {
      // jm interdire l'ouverture multiple de l'échéancier
      ((GemButton) evt.getSource()).setEnabled(false);
      dlgSchedulePayment();
    } else if ("Payer.debiting".equals(arg)) {
      if (dossier == null || dossier.getRib() == null) {
        MessagePopup.error(personFileView, MessageUtil.getMessage("payer.invalid.warning"));
        return;
      }
      dossier.setRib(personFileView.getRibFile());// get rib from view
      if (dossier.hasChanged()) {// non enregistrement éventuel du rib
        MessagePopup.warning(personFileView, MessageUtil.getMessage("rib.error.printing"));
        return;
      }
			//TODO  detection existence mandat sepa
			DirectDebitService ddService = DirectDebitService.getInstance(dc);
			try {
				int payer = dossier.getMember() == null ? dossier.getId() : dossier.getMember().getPayer();
				DDMandate dd = ddService.getMandate(payer);
				if (dd == null) {
					if (payer > 0 && MessagePopup.confirm(view, "Aucun mandat actif : Voulez-vous créer un nouveau mandate de prélèvement pour le payeur ?")) {
						dd = ddService.createMandate(payer);
					} 
				}
				personFileView.addMandates(ddService, payer);
				

					/*boolean printOrderLines = true;
					if (!MessagePopup.confirm(personFileView, MessageUtil.getMessage("standing.order.print.warning"), "Confirmation")) {
						printOrderLines = false;
					}
					DirectDebitRequest prl = new DirectDebitRequest(personFileView, printOrderLines);
		//      dossier.setRib(personFileView.getRibFile());// get rib from view
					prl.edit(dossier, personFileView.getBranchBank(), BundleUtil.getLabel("Menu.debiting.label"), dataCache);*/
			} catch (DDMandateException ex) {
				MessagePopup.error(view, ex.getMessage());
			}
    } else if ("Login.creation".equals(arg)) {
      dlgLogin();
    } else if ("Lié".equals(arg)) {
      Person p = new Person(dossier.getContact().getName());
      PersonFile d = new PersonFile(new Contact(p));
      PersonFileEditor ed = new PersonFileEditor(d, dossier);
      d.addPersonFileListener(this);
      desktop.addModule(ed);
    } else if ("Teacher.break".equals(arg)) {
      TeacherBreakDlg dlg = new TeacherBreakDlg(desktop, dossier.getId());
      dlg.entry();
      if (dlg.isValidation()) {
        dlg.save();
      } else if (dlg.getError() != null) {
        MessagePopup.information(desktop.getFrame(), dlg.getError());
      }
    } else if ("Teacher.presence".equals(arg)) {
      AttendanceSheetDlg dlg = new AttendanceSheetDlg(personFileView, dataCache, dossier.getTeacher());
    } else if ("Teacher.hour".equals(arg)) {
      String file = "heures_" + dossier.getContact().getFirstName() + "_" + dossier.getContact().getName() + ".txt";
      HourTeacherDlg heureProf = new HourTeacherDlg(desktop.getFrame(), file, dossier.getId(), dataCache);
      heureProf.setVisible(true);
    } // clic sur le bouton/icone Fermer la fiche
    else if (GemCommand.CLOSE_CMD.equals(arg)) { // GemCommand.
      try {
        close();
      } catch (GemCloseVetoException i) {
        System.err.println("GemCloseVetoException");
      }
    } // clic sur le bouton Enregistrer
    else if (GemCommand.SAVE_CMD.equals(arg)) {
      savePersonFile();
    } //SUPPRESSION CONTACT ajout 2.0c
    else if ("Contact.suppression".equals(arg)) {
      suppressPerson();
    } else if ("Person.pass.scheduling".equals(arg)) {
      MemberRehearsalPassCtrl dlg = new MemberRehearsalPassCtrl(desktop, this, dossier);
      personFileView.addTab(dlg, BundleUtil.getLabel("Rehearsal.pass.label"));
      miPassRehearsal.setEnabled(false);
    } else if ("Person.rehearsal.scheduling".equals(arg)) {
      MemberRehearsalCtrl dlg = new MemberRehearsalCtrl(desktop, this, dossier);
      personFileView.addTab(dlg, BundleUtil.getLabel("Rehearsal.label"));
      miRehearsal.setEnabled(false);
      personFileView.activate(false, "Person.rehearsal.scheduling");
    } else if ("Rehearsal.history".equals(arg)) {
      personFileView.addRehearsalHistoryTab();
      miHistoRehearsal.setEnabled(false);
    } else if ("Menu.month.schedule".equals(arg)) {
      MonthScheduleTab dlg = new MonthScheduleTab(desktop, this, dossier);
      personFileView.addTab(dlg, BundleUtil.getLabel("Menu.month.schedule.label"));
      miMonthPlanning.setEnabled(false);
    } else if ("AdherentRepetitionPonctuelle.Abandon".equals(arg) || "AdherentRepetitionPonctuelle.Validation".equals(arg)) {
      personFileView.removeTab((MemberRehearsalCtrl) src);
      personFileView.activate(true, "Person.rehearsal.scheduling");
      miRehearsal.setEnabled(true);
    } else if ("AdherentForfaitRepetition.Abandon".equals(arg) || "AdherentForfaitRepetition.Validation".equals(arg)) {
      personFileView.removeTab((MemberRehearsalPassCtrl) src);
      miPassRehearsal.setEnabled(true);
    } else if ("MemberEnrolmentCreate".equals(arg)) {
      MemberEnrolmentDlg dlg = new MemberEnrolmentDlg(desktop, this, dossier);
      personFileView.addTab(dlg, BundleUtil.getLabel("Course.registration.label"));
      personFileView.activateEnrolment(false);// désactivation du bouton nouvelle inscription
    } else if ("MemberEnrolmentCancel".equals(arg) || "MemberEnrolmentValidation".equals(arg)) {
      personFileView.removeTab((MemberEnrolmentDlg) src);
      personFileView.activateEnrolment(true);//réactivation du bouton de nouvelle inscription
    } else if ("EditionCarteAbo.Abandon".equals(arg) || "EditionCarteAbo.Validation".equals(arg)) {
      personFileView.removeTab((SubscriptionCardEditor) src);
      miCard.setEnabled(true);
    } else if ("HistoFacture.Abandon".equals(arg)) {
      personFileView.removeTab((HistoInvoice) src);
      miHistoInvoice.setEnabled(true);
    } else if ("HistoDevis.Abandon".equals(arg)) {
      personFileView.removeTab((HistoQuote) src);
      miHistoQuote.setEnabled(true);
    } else if ("CtrlAbandonFacture".equals(arg)) {
      personFileView.removeTab((InvoiceEditor) src);
    } else if ("CtrlAbandonDevis".equals(arg)) {
      personFileView.removeTab((QuoteEditor) src);
    } else if ("HistoRepet.Abandon".equals(arg)) {
      miHistoRehearsal.setEnabled(true);
      personFileView.removeTab((HistoRehearsalView) src);
    } else if ("Invoice.history".equals(arg)) {
      int payer = getPayer();
      if (payer > 0) {
        histoInvoice = addHistoInvoice(dossier.getId());
        histoInvoice.addActionListener(this);
        personFileView.addTab(histoInvoice, FileView.HISTO_INVOICE_TAB_TITLE);
        miHistoInvoice.setEnabled(false);
      }
    } else if (FileView.INVOICE_TAB_TITLE.equals(arg)) {
      addInvoice(src);
    } else if (FileView.ESTIMATE_TAB_TITLE.equals(arg)) {
      addQuotation(src);
    } else if ("Quotation.history".equals(arg)) {
      int payer = getPayer();
      if (payer > 0) {
        HistoQuote histoQuote = getHistoQuotation(dossier.getId());
        histoQuote.addActionListener(this);
        personFileView.addTab(histoQuote, FileView.HISTO_ESTIMATE_TAB_TITLE);
        miHistoQuote.setEnabled(false);
      }
    } else if (CloseableTab.CLOSE_CMD.equals(arg)) {
      closeTab(src);
    } else {
      super.actionPerformed(evt);
    }

  }

  /**
   * Gets the payer id.
   *
   * @return
   */
  private int getPayer() {
    int payer = 0;
    if (dossier.getMember() != null) {
      payer = dossier.getMember().getPayer();
    }
    return payer == 0 ? dossier.getId() : payer;
  }

  /**
   * Gets data view.
   */
  private void updatePersonFile() {

    dossier.setContact(personFileView.getContact());

    if (dossier.getMember() == null) {// nouvel onglet
      dossier.addMember(personFileView.getMemberFile());
    } else {
      dossier.setMember(personFileView.getMemberFile());
    }

    dossier.setTeacher(personFileView.getTeacher());

//    if (dossier.getRib() == null) {
//      dossier.addRib(personFileView.getRibFile());
//    } else {
    dossier.setRib(personFileView.getRibFile());
//    }

    BankBranch a = personFileView.getBranchBank();
    if (a != null && a.getBicCode() != null && !a.getBicCode().isEmpty()) {
      try {
        BankBranch b = BANK_BRANCH_IO.findId(a.getId());
        if (b != null && !a.getBicCode().equals(b.getBicCode())) {
          BANK_BRANCH_IO.update(a.getId(), a.getBicCode());
        }
      } catch (SQLException ex) {
        GemLogger.log(Level.WARNING, ex.getMessage());
      }
    }

  }

  /**
   * Inserts or updates dossier.
   *
   * @return true if no errors
   */
  boolean save() {

    DataConnection dc = dataCache.getDataConnection();

    try {
      dc.setAutoCommit(false);
      if (personFileView.isNewBank()) {
        Bank b = personFileView.getBank();
        if (b.isValid()) {
          if (BankIO.findCode(b.getCode(), dc) == null) {
            BankIO.insert(b, dc);
          }
        }
      }

      if (personFileView.isNewBranchOfBank()) {
        BankBranch a = personFileView.getBranchBank();
        if (a != null && a.isValid()) {
          BANK_BRANCH_IO.insert(a);
          personFileView.setBranchBank(a); // maj id guichet
        }
      }

      personFileView.updateEmployee();

      Vector<String> logs = ((PersonFileIO) DataCache.getDao(Model.PersonFile)).update(dossier);
      if (logs.contains(PersonFileIO.CONTACT_CREATE_EVENT)) {
        dataCache.add(dossier.getContact());
        desktop.postEvent(new GemEvent(this, GemEvent.CREATION, GemEvent.CONTACT, dossier.getContact()));
      }
      if (logs.contains(PersonFileIO.CONTACT_UPDATE_EVENT)) {
        dataCache.update(dossier.getContact());
        desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.CONTACT, dossier.getContact()));
      }
      if (logs.contains(PersonFileIO.TEACHER_CREATE_EVENT)) {
        dataCache.add(dossier.getTeacher());
        desktop.postEvent(new TeacherEvent(this, GemEvent.CREATION, dossier.getTeacher()));
      }
      if (logs.contains(PersonFileIO.TEACHER_UPDATE_EVENT) && dossier.getTeacher() != null) {
        dataCache.update(dossier.getTeacher()); // mise à jour liste professeurs
        desktop.postEvent(new TeacherEvent(this, GemEvent.MODIFICATION, dossier.getTeacher()));
      }
      if (logs.contains(PersonFileIO.MEMBER_CREATE_EVENT)) {
        dataCache.add(dossier.getMember());
        desktop.postEvent(new GemEvent(this, GemEvent.CREATION, GemEvent.MEMBER, dossier.getMember()));
      }
      if (logs.contains(PersonFileIO.MEMBER_UPDATE_EVENT)) {
        dataCache.update(dossier.getMember());
        desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.MEMBER, dossier.getMember()));
      }
      dc.commit();
    } catch (SQLException e1) {
      dc.rollback();
      GemLogger.logException(e1.getMessage(), e1);
      JOptionPane.showMessageDialog(personFileView,
              "identifiant = " + dossier.getId() + "<br>" + e1,
              "Erreur mise à jour dossier :",
              JOptionPane.ERROR_MESSAGE);
      return false;
    } finally {
      dc.setAutoCommit(true);
    }

    return true;
  }

  /**
   * Closes the module.
   * Click on closing icon.
   *
   * @throws net.algem.event.GemCloseVetoException
   */
  @Override
  public void close() throws GemCloseVetoException {
    updatePersonFile();
    String msg = dossier.hasErrors();
    if (msg != null) {
      MessagePopup.error(personFileView, msg);
    } else {
      if (hasChanged()) {
        msg = checkContact(MessageUtil.getMessage("update.warning"));
        if (MessagePopup.confirm(personFileView, msg, MessageUtil.getMessage("closing.record.info", dossier.getId()))) {
          save();
        }
      } else {
        System.out.println(MessageUtil.getMessage("no.update.info"));
      }
    }
    personFileView.clear();
    closeModule();

  }

  private boolean hasChanged() {
    return dossier.hasChanged() || personFileView.hasEmployeeChanged();
  }

  void dlgLogin() {

//    DataConnection dc = dataCache.getDataConnection();
    UserService service = dataCache.getUserService();
//    UserIO dao = (UserIO) DataCache.getDao(Model.User);
    personFileView.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    UserCreateDlg dlg = new UserCreateDlg(personFileView, "login", dossier.getContact());
    User u = service.findId(dossier.getId());
    if (u != null) {
      dlg.setUser(u);
    }
    personFileView.setCursor(Cursor.getDefaultCursor());
    dlg.display();
    if (!dlg.isValidation()) {
      return;
    }

    try {
      if (u == null) {
        u = dlg.getUser();
        service.create(u);
        dataCache.add(u);
        desktop.postEvent(new GemEvent(this, GemEvent.CREATION, GemEvent.USER, u));
      } else {
        User nu = dlg.getUser();
        if (service.update(nu, u)) {
          dataCache.update(u);
          desktop.postEvent(new GemEvent(this, GemEvent.MODIFICATION, GemEvent.USER, u));
        }
      }
    } catch (SQLException e) {
      GemLogger.logException("enregistrement user", e, personFileView);
    } catch (UserException ue) {
      GemLogger.logException(ue);
    }
  }

  void dlgSchedulePayment() {
    if (dossier == null) {
      return;
    }
    DataConnection dc = dataCache.getDataConnection();
    personFileView.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    OrderLineTableModel tableEcheancier = new OrderLineTableModel();
    Person p = dossier.getContact();

    // chargement des échéances
    //un adhérent a pu avoir plusieurs payeurs  
    if (dossier.getMember() != null) {
      // si l'adhérent n'est pas son propre payeur
      if (dossier.getMember().getPayer() != dossier.getId()) {
        p = ((PersonIO) DataCache.getDao(Model.Person)).findId(dossier.getMember().getPayer());// le payeur de l'adherent
        if (p == null) {
          p = dossier.getContact();
        }
        tableEcheancier.load(OrderLineIO.findByMember(dossier.getId(), p.getId(), dc));
      } else {
        tableEcheancier.load(OrderLineIO.findByMemberOrPayer(dossier.getId(), p.getId(), dc));
      }
    } else {
      tableEcheancier.load(OrderLineIO.findByMemberOrPayer(dossier.getId(), p.getId(), dc));
    }

    addSchedulePayment(tableEcheancier, p);

    personFileView.setCursor(Cursor.getDefaultCursor());
  }

  /**
   * Ajout d'un item vers l'adhérent lié dans le menu fichier.
   *
   * @param _label le titre de l'item
   * @param _dossier
   * @deprecated
   */
  public void addMenuDossier(String _label, PersonFile _dossier) {
    PersonFileMenuItem m = new PersonFileMenuItem(_label, _dossier);
    m.addActionListener(new ActionListener()
    {

      public void actionPerformed(ActionEvent evt) {
        PersonFile d = ((PersonFileMenuItem) evt.getSource()).getPersonFile();
        PersonFileEditor m = ((GemDesktopCtrl) desktop).getPersonFileEditor(d.getId());
        if (m == null) {
          PersonFileEditor editeur = new PersonFileEditor(d);
          desktop.addModule(editeur);
        } else {
          desktop.setSelectedModule(m);
        }
      }
    });
    mFile.add(m);
  }

  private void addPayerFile(String _label, final PersonFile _dossier) {
    GemButton b = personFileView.addIcon("Member.payer");
    b.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent evt) {
        PersonFileEditor m = ((GemDesktopCtrl) desktop).getPersonFileEditor(_dossier.getId());
        if (m == null) {
          PersonFileEditor editor = new PersonFileEditor(_dossier);
          desktop.addModule(editor);
        } else {
          desktop.setSelectedModule(m);
        }
      }
    });
  }

  private void addSchedulePayment(OrderLineTableModel t, Person p) {

    orderLineEditor = new OrderLineEditor(desktop, t);
    orderLineEditor.init();
    orderLineEditor.addActionListener(this);
    if (dossier.getMember() == null) {
      orderLineEditor.setPayerId(dossier.getId());
    } else {
      orderLineEditor.setMemberId(dossier.getId());
      orderLineEditor.setPayerId(dossier.getMember().getPayer());
    }
    /*
     *
     */
    if (dossier.getMember() == null || dossier.getMember().getPayer() == dossier.getId()) {
      orderLineEditor.setLabel(dossier.getContact().getName() + " " + dossier.getContact().getFirstName());
    } else {
      orderLineEditor.setLabel(p.getName() + " " + p.getFirstName());
    }
    personFileView.addTab(orderLineEditor, BundleUtil.getLabel("Person.schedule.payment.tab.label"));
  }

  private void initMenus() {
    mBar = new JMenuBar();
    mFile = new JMenu(BundleUtil.getLabel("Menu.file.label"));
    miDelete = getMenuItem("Contact.suppression");
    mOptions = createJMenu("Menu.options");

    mHelp = new JMenu(BundleUtil.getLabel("Menu.help.label"));
    miAbout = new JMenuItem(BundleUtil.getLabel("About.label"));
    miDoc = new JMenuItem(BundleUtil.getLabel("Menu.doc.label"));

    mFile.add(miDelete);

    mOptions.add(miMember = getMenuItem("Member.reading"));
    mOptions.add(miBank = getMenuItem("Person.bank.editing"));
    mOptions.add(miTeacher = getMenuItem("Teacher"));
    mOptions.add(miEmployee = getMenuItem("Employee"));
    miEmployee.setEnabled(dataCache.authorize("Employee.editing.auth"));

    mOptions.add(miGroups = getMenuItem("Groups"));
    //Désactivation conditionnelle des menus Adherent, Prof et Bank
    if (dossier.getMember() != null) {
      miMember.setEnabled(false);
    }
    if (dossier.getTeacher() != null) {
      miTeacher.setEnabled(false);
    }
    if (dossier.getRib() != null) {
      miBank.setEnabled(false);
    }

    mOptions.addSeparator();
    mOptions.add(miRehearsal = getMenuItem("Person.rehearsal.scheduling"));
    mOptions.add(miPassRehearsal = getMenuItem("Person.pass.scheduling"));
    mOptions.addSeparator();

    mOptions.add(miHistoRehearsal = getMenuItem("Rehearsal.history"));
    mOptions.add(miMonthPlanning = getMenuItem("Menu.month.schedule"));
    mOptions.addSeparator();

    mOptions.add(miHistoInvoice = getMenuItem("Invoice.history"));
    mOptions.add(miHistoQuote = getMenuItem("Quotation.history"));
    mOptions.addSeparator();

    mOptions.add(miCard = getMenuItem("Rehearsal.card.editing"));
    mOptions.add(miLogin = getMenuItem("Login.creation"));

    mHelp.add(miAbout);
    mHelp.add(miDoc);

    mBar.add(mFile);
    mBar.add(mOptions);
    mBar.add(Box.createHorizontalGlue());
    mBar.add(mHelp);

    personFileView.setJMenuBar(mBar);
  }

  /**
   * Checks and updates if necessary the number of memberships.
   *
   */
  private void checkMembershipNumber() {
    int a = 0;
    int nba = 0;
    if (dossier.getMember() != null) {
      a = dossier.getMember().getMembershipCount();
      try {
        nba = AccountUtil.getMemberShips(dossier.getMember().getId(), dataCache.getDataConnection());
      } catch (SQLException ex) {
        nba = a;
      }
      // en cas de mise en service tardive du logiciel (des adhésions ayant été enregistrées indépendamment d'algem),
      // le nombre d'adhésions enregistré dans les échéances pourra être < au nombre réel.
      personFileView.setMemberShipNumber((nba > a) ? nba : a);//mise à jour de la view
    }
  }

  /**
   * Payer identification.
   */
  private void checkPayer() {
    if (dossier.getMember() != null && dossier.getMember().getPayer() != dossier.getId()) {
      Contact c = ContactIO.findId(dossier.getMember().getPayer(), dataCache.getDataConnection());
      if (c != null) {
        PersonFile d = new PersonFile(c);
        try {
          ((PersonFileIO) DataCache.getDao(Model.PersonFile)).complete(d);
        } catch (SQLException ex) {
          GemLogger.logException(MessageUtil.getMessage("record.completion.exception"), ex);
        }
        //addMenuDossier("payeur", d);
        addPayerFile("Payeur", d);// ajout jm 2.0ma
      } else { //Attention aux cas où le payeur auquel est lié l'adhérent n'existe pas
        MessagePopup.information(personFileView,
                MessageUtil.getMessage("not.existing.payer.link", dossier.getMember().getPayer()));
        personFileView.setParent(dossier);
      }
    }
  }

  /**
   * Opens a tab for linked members.
   */
  private void checkAttachedMembers() {
    if (setMemberList() > 0) {
      //final GemButton b = personFileView.addLinkedMembersIcon();
      final GemButton b = personFileView.addIcon("Payer.members");
      b.addActionListener(new ActionListener()
      {

        public void actionPerformed(ActionEvent evt) {
          ListCtrl list = PersonFileEditor.this.getMemberList();
          MemberListTab memberList = new MemberListTab(desktop, list);
          personFileView.addTab(memberList, BundleUtil.getLabel("Person.linked.members.tab.label"));
          b.setEnabled(false);
        }
      });
    }
  }

  /**
   * Gets the number of linked members.
   */
  private int setMemberList() {
    Vector<PersonFile> v = ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findMembers("WHERE payeur = " + dossier.getId() + " AND id != payeur");
    memberList = new PersonFileListCtrl();

    for (int i = 0; i < v.size(); i++) {
      PersonFile d = v.elementAt(i);
      ContactIO.complete(d.getContact(), dataCache.getDataConnection());
    }
    if (v != null && v.size() > 0) {
      memberList.addBlock(v);
      return v.size();
    }
    return 0;
  }

  /**
   * Gets the list of linked members.
   *
   * @return a {@link ListCtrl } instance
   */
  private ListCtrl getMemberList() {
    return memberList;
  }

  /**
   * Homonymous verification.
   */
  private boolean contactExists(String name, String firstName) {
    String where = " WHERE lower(nom) = '" + name.toLowerCase() + "' AND lower(prenom) = '" + firstName.toLowerCase() + "'";
    Contact c = ContactIO.findId(where, dataCache.getDataConnection());
    return c != null;
  }

  /**
   * Checks if a contact exists.
   * If he is new, we should check that it does not already exist.
   *
   * @return a message
   */
  private String checkContact(String message) {
    if (dossier.getContact().getId() == 0
            && contactExists(dossier.getContact().getName(), dossier.getContact().getFirstName())) {
      return MessageUtil.getMessage("contact.create.warning");
    }
    return message;
  }

  /**
   * Opens an external document with the contact's main data.
   */
  private void showPersonFile() {
    String path = null;
    ExportMemberRTF rtfExport = null;
    try {
      rtfExport = new ExportMemberRTF(desktop, System.getProperty("java.io.tmpdir"), dossier);
      rtfExport.edit();

      ExportPayeurRTF fic2 = new ExportPayeurRTF(System.getProperty("java.io.tmpdir"), dossier, dataCache.getDataConnection());
      path = fic2.getPath();
      fic2.edit();
      // jm	java Desktop rtf handler
      DesktopOpenHandler handler = new DesktopOpenHandler();
      handler.open(rtfExport.getPath(), path);
    } catch (DesktopHandlerException de) {
      System.err.println(de.getMessage());
      try {
        Runtime.getRuntime().exec("oowriter " + rtfExport.getPath() + " " + path); // TODO paramétrer lecteur par défaut
      } catch (IOException ioe) {
        System.err.println(ioe.getMessage());
      }
    } catch (FileNotFoundException fe) {
      System.err.println(fe.getMessage());
      JOptionPane.showMessageDialog(view, path, fe.getMessage(), JOptionPane.ERROR_MESSAGE);
    }
  }

  private void savePersonFile() {

    Object[] backup = dossier.backUpOldValues();
    updatePersonFile();
    String msg = dossier.hasErrors();
    if (msg != null) {
      MessagePopup.error(personFileView, msg);
      return;
    }
    if (hasChanged()) {
      msg = checkContact(MessageUtil.getMessage("update.warning"));
      if (MessagePopup.confirm(personFileView, msg, "Enregistrement du dossier:" + dossier.getId())) {
        save();
        dossier.setOldValues();
        personFileView.setID(dossier.getId());//mise à jour de la view dans le cas d'un nouveau contact(2.0c)
        //dossier.clearOldValues();
        // jm 2.0ma ajout automatique des onglets inscription et suivi lors de l'enregistrement d'un nouvel adhérent
        if (dossier.getMember() != null && personFileView.getMemberEnrolment() == null) {
          personFileView.completeMember();
        }
      } else {
        dossier.restoreOldValues(backup);
        personFileView.reload(dossier);
      }
      //vuePersonne.setSaveState(true);
    } else {
      dossier.restoreOldValues(backup);
      JOptionPane.showMessageDialog(personFileView,
              MessageUtil.getMessage("no.update.info"),
              BundleUtil.getLabel("Warning.label"),
              JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * Contact suppression.
   * Suppression is checked {@link ContactIO#checkDelete(net.algem.ctrl.DataCache, net.algem.modele.contact.Contact) }
   * }
   */
  private void suppressPerson() {
    // interdire la suppression aux personnes non autorisées
    if (!dataCache.authorize("Contact.suppression.auth")) {
      MessagePopup.information(personFileView, "Droits insuffisants");
      return;
    }
    DataConnection dc = dataCache.getDataConnection();
    if (MessagePopup.confirm(personFileView, MessageUtil.getMessage("contact.delete.confirmation", dossier.getId()))) {
      try {
        dc.setAutoCommit(false);
        new ContactIO(dc).delete(dossier.getContact());
        dc.commit();
        try {
          view.close();
          view.removeActionListener(this);
          desktop.removeGemEventListener(this);
          desktop.removeModule(this);
        } catch (GemCloseVetoException e) {
          GemLogger.logException(e);
        }
      } catch (ContactDeleteException ex) {
        MessagePopup.warning(view, ex.getMessage());
      } catch (SQLException ex) {
        GemLogger.logException("delete contact", ex);
        dc.rollback();
      } finally {
        dc.setAutoCommit(true);
      }
    }
  }

  private void closeTab(Object source) {
    String classname = null;
		
    if (source instanceof String) {
      classname = (String) source;
    } else {
      return;
    }

    if (RibView.class.getSimpleName().equals(classname)) {
      savePersonFile();// on enregistre (le rib) par précaution
      personFileView.clearRib();
      miBank.setEnabled(true);
    } else if (MonthScheduleTab.class.getSimpleName().equals(classname)) {
      miMonthPlanning.setEnabled(true);
    } else if (TeacherEditor.class.getSimpleName().equals(classname)) {
      personFileView.closeTeacher();
      miTeacher.setEnabled(true);
    } else if (MemberEditor.class.getSimpleName().equals(classname)) {
      personFileView.removeTab(personFileView.getEnrolmentView());
      personFileView.removeTab(personFileView.getMemberFollowUp());
      personFileView.removeMemberIcons();
      miMember.setEnabled(true);
    } else if (OrderLineEditor.class.getSimpleName().equals(classname)) {
      personFileView.activate(true, "Member.schedule.payment");
      desktop.removeGemEventListener(orderLineEditor);
    } else if (SubscriptionCardEditor.class.getSimpleName().equals(classname)) {
      miCard.setEnabled(true);
    } else if (HistoRehearsalView.class.getSimpleName().equals(classname)) {
      miHistoRehearsal.setEnabled(true);
    } else if (PersonFileGroupView.class.getSimpleName().equals(classname)) {
      miGroups.setEnabled(true);
    } else if (MemberEnrolmentDlg.class.getSimpleName().equals(classname)) {
      personFileView.activateEnrolment(true);
    } else if (MemberRehearsalCtrl.class.getSimpleName().equals(classname)) {
      miRehearsal.setEnabled(true);
      personFileView.activate(true, "Person.rehearsal.scheduling");
    } else if (MemberRehearsalPassCtrl.class.getSimpleName().equals(classname)) {
      miPassRehearsal.setEnabled(true);
    } else if (HistoInvoice.class.getSimpleName().equals(classname)) {
      miHistoInvoice.setEnabled(true);
    } else if (HistoQuote.class.getSimpleName().equals(classname)) {
      miHistoQuote.setEnabled(true);
    } else if (MemberListTab.class.getSimpleName().equals(classname)) {
      personFileView.activate(true, "Payer.members");
    } else if (EmployeeEditor.class.getSimpleName().equals(classname)) {
      miEmployee.setEnabled(dataCache.authorize("Employee.editing.auth"));
    } else if ("net.algem.contact.PersonFileTabView$1".equals(classname)) {
			personFileView.activate(true, "Payer.debiting");
		}

  }

  private void closeModule() throws GemCloseVetoException {
    view.close();
    view.removeActionListener(this);
    desktop.removeGemEventListener(this);
    desktop.removeGemEventListener(personFileView);
    personFileView.clear();
    desktop.removeModule(this);
  }

  /**
   * Adds a tab for invoice edition / creation.
   *
   * @param source
   */
  private void addInvoice(Object source) {

    Invoice inv = new Invoice(dossier, dataCache.getUser());
    inv.setEstablishment(Integer.parseInt(ConfigUtil.getConf(ConfigKey.DEFAULT_ESTABLISHMENT.getKey(), dataCache.getDataConnection())));
    if (source != null && source instanceof OrderLineEditor) {
      BillingUtil.setInvoiceOrderLines(inv, ((OrderLineEditor) source).getInvoiceSelection());
    }

    InvoiceEditor editor = new InvoiceEditor(desktop, billingService, inv);
    editor.addActionListener(this);
    editor.load();
    personFileView.addTab(editor, FileView.INVOICE_TAB_TITLE);
  }

  private void addQuotation(Object source) {
    Quote q = new Quote(dossier, dataCache.getUser());
    q.setEstablishment(Integer.parseInt(ConfigUtil.getConf(ConfigKey.DEFAULT_ESTABLISHMENT.getKey(), dataCache.getDataConnection())));
    /*
     * if (source != null && source instanceof OrderLineEditor) {
     * AccountUtil.setQuoteOrderLines(d, ((OrderLineEditor)
     * source).getInvoiceSelection()); }
     */
    QuoteEditor editor = new QuoteEditor(desktop, billingService, q);
    editor.addActionListener(this);
    editor.load();
    personFileView.addTab(editor, FileView.ESTIMATE_TAB_TITLE);
  }
}
