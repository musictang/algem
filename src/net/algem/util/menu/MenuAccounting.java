/*
 * @(#)MenuAccounting.java 2.8.a 01/04/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.HashMap;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import net.algem.accounting.*;
import net.algem.billing.*;
import net.algem.config.ModeOfPaymentCtrl;
import net.algem.edition.HourTeacherDlg;
import net.algem.room.RoomRateSearchCtrl;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.MessagePopup;

/**
 * Accounting menu.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.8.a
 * @since 1.0a 07/07/1999
 */
public class MenuAccounting
        extends GemMenu
{

  private static final HashMap<String, String> menus = new HashMap<String, String>();

  static {
    initLabels();
  }
  
  private JMenuItem miAccountTransfert;
  private JMenuItem miAccountDocument;
  private JMenuItem miAccountSchedule;
  private JMenuItem miAccountHourTeacher;
  private JMenuItem miRoomRate;
  private DataConnection dc;
  
  public MenuAccounting(GemDesktop _desktop) {
    super(menus.get("Menu.accounting.label"), _desktop);

    miAccountSchedule = add(getItem(new JMenuItem(menus.get("Menu.schedule.payment.label")), "Accounting.global.schedule.auth"));
    addSeparator();
    
    miAccountTransfert = add(getItem(new JMenuItem(menus.get("Menu.schedule.payment.transfer.label")), "Accounting.transfer.auth"));
    miAccountDocument = add(getItem(new JMenuItem(menus.get("Menu.document.transfer.label")), "Accounting.document.transfer.auth"));
    add(getItem(new JMenuItem(BundleUtil.getLabel("Menu.debiting.label")), "Standing.order.export.auth"));
    miAccountHourTeacher = add(getItem(new JMenuItem(menus.get("Menu.teacher.hour.label")), "Accounting.hours.export.auth"));
    addSeparator();
    
    add(getItem(new JMenuItem(menus.get("Menu.invoice.history.label")), "Invoice.history.auth"));
    add(getItem(new JMenuItem(menus.get("Menu.quotation.history.label")), "Quotation.history.auth"));
    JMenu mInvoice = new JMenu(menus.get("Menu.invoice.label"));
    mInvoice.add(new JMenuItem(menus.get("Menu.invoice.item.label")));
    mInvoice.add(new JMenuItem(menus.get("Menu.invoice.footer.label")));
    add(mInvoice);
    addSeparator();
    
    add(getItem(new JMenuItem(menus.get("Menu.account.label")), "Accounting.account.config.auth"));
    add(getItem(new JMenuItem(menus.get("Menu.cost.account.label")), "Accounting.cost.account.config.auth")); 
    addSeparator();
    
    add(getItem(new JMenuItem(menus.get("Menu.default.account.label")), "Account.preferences.auth"));
    add(getItem(new JMenuItem(menus.get("Menu.booking.journal.label")), "Accounting.journal.config.auth"));
    add(new JMenuItem(menus.get("Menu.account.link.label")));
    addSeparator();
    
    add(new JMenuItem(menus.get("Menu.mode.of.payment.label")));
    add(new JMenuItem(menus.get("Menu.vat.label")));
    miRoomRate = new JMenuItem(menus.get("Menu.room.rate.label"));
    add(miRoomRate);
    if (!dataCache.authorize("Accounting.management.auth")) {
      setEnabled(false);
    }
    setListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String arg = evt.getActionCommand();
    Object src = evt.getSource();
    dc = dataCache.getDataConnection();
    desktop.setWaitCursor();
    
    if (src == miAccountSchedule) {
      OrderLineTableModel tableEcheancier = new OrderLineTableModel();
      OrderLineDlg dlg = new OrderLineDlg(desktop, tableEcheancier);
      dlg.init();
      dlg.setVisible(true);
    } else if (src == miAccountTransfert) {
      AccountTransferDlg accountTransfertDlg = new AccountTransferDlg(desktop.getFrame(), dataCache);
      accountTransfertDlg.setVisible(true);
    } else if (src == miAccountDocument) {
      AccountDocumentTransferDlg documentTransfertDlg = new AccountDocumentTransferDlg(desktop.getFrame(), dataCache);
      documentTransfertDlg.setVisible(true);
    } else if (menus.get("Menu.debiting.label").equals(arg)) {
      StandingOrderExportDlg dlg = new StandingOrderExportDlg((Frame) null, menus.get("Menu.debiting.label"), dc);
      dlg.setVisible(true);
    } else if (src == miAccountHourTeacher) {
      HourTeacherDlg hourTeacherDlg = new HourTeacherDlg(desktop.getFrame(), "heureprof.txt", dataCache);
      hourTeacherDlg.setVisible(true);
    } else if (menus.get("Menu.invoice.history.label").equals(arg)) {
      BillingServiceI billService = new BillingService(dataCache);
      try {
        HistoInvoice hf = new HistoInvoice(desktop, billService.getInvoices());
        desktop.addPanel("Menu.invoice.history", hf, GemModule.XXL_SIZE);
        hf.addActionListener(this);
      } catch (SQLException ex) {
        GemLogger.logException(ex);
      } finally {
        desktop.setDefaultCursor();
      }
    } else if (menus.get("Menu.quotation.history.label").equals(arg)) {
      BillingServiceI facService = new BillingService(dataCache);
      try {
        HistoQuote hd = new HistoQuote(desktop, facService.getQuotations());
        desktop.addPanel("Menu.quotation.history", hd, GemModule.XXL_SIZE);
        hd.addActionListener(this);
      } catch (SQLException ex) {
        GemLogger.logException(ex);
      } finally {
        desktop.setDefaultCursor();
      }
    } else if (menus.get("Menu.invoice.item.label").equals(arg)) {
      ItemSearchCtrl articleBrowse = new ItemSearchCtrl(desktop);
      articleBrowse.addActionListener(this);
      articleBrowse.init();
      desktop.addPanel("Menu.invoice.item", articleBrowse);
    } else if (menus.get("Menu.invoice.footer.label").equals(arg)) {
      InvoiceFooterEditor pdpFactEditor = new InvoiceFooterEditor("Menu.invoice.footer", desktop);
      desktop.addPanel("Menu.invoice.footer", pdpFactEditor);
    } else if (menus.get("Menu.account.label").equals(arg)) {
      AccountCtrl accountCtrl = new AccountCtrl(desktop);
      desktop.addPanel("Menu.account", accountCtrl);
    } else if (menus.get("Menu.cost.account.label").equals(arg)) {
      CostAccountCtrl costAccountCtrl = new CostAccountCtrl(desktop);
      desktop.addPanel("Menu.cost.account", costAccountCtrl);
    } else if (menus.get("Menu.default.account.label").equals(arg)) {
      AccountPrefCtrl prefsComptes = new AccountPrefCtrl(desktop);
      desktop.addPanel("Menu.default.account", prefsComptes, GemModule.L_SIZE);
    } else if (menus.get("Menu.booking.journal.label").equals(arg)) {
      JournalAccountService jcs = new JournalAccountService(dc);
      JournalAccountCtrl jcc = new JournalAccountCtrl(new JournalAccountTableModel(), jcs, desktop);
      desktop.addPanel("Menu.booking.journal", jcc, GemModule.XL_SIZE);
    } else if (menus.get("Menu.account.link.label").equals(arg)) {
      AccountMatchingCfg accountMatchingCfg = new AccountMatchingCfg(desktop);
      desktop.addPanel("Menu.account.link", accountMatchingCfg, GemModule.L_SIZE);
    }  else if (menus.get("Menu.mode.of.payment.label").equals(arg)) {
      ModeOfPaymentCtrl modeOfPaymentCtrl = new ModeOfPaymentCtrl(desktop);
      desktop.addPanel("Menu.mode.of.payment", modeOfPaymentCtrl);
    } else if (menus.get("Menu.vat.label").equals(arg)) {
      VATCtrl vatCtrl = new VATCtrl(desktop);
      desktop.addPanel("Menu.vat", vatCtrl);
    } else if (src == miRoomRate) {
      RoomRateSearchCtrl roomRateBrowse = new RoomRateSearchCtrl(desktop);
      roomRateBrowse.addActionListener(this);
      roomRateBrowse.init();
      desktop.addPanel("Menu.room.rate", roomRateBrowse);
    } 

    else if (GemCommand.CANCEL_CMD.equals(arg)) {
      desktop.removeCurrentModule();
    } else if ("HistoFacture.Abandon".equals(arg)) {
      desktop.removeModule("Menu.invoice.history");
    } else if ("HistoDevis.Abandon".equals(arg)) {
      desktop.removeModule("Menu.quotation.history");
    }
    desktop.setDefaultCursor();
  }

  private static void initLabels() {
    menus.put("Menu.schedule.payment.label", BundleUtil.getLabel("Menu.schedule.payment.label"));
    menus.put("Menu.schedule.payment.transfer.label", BundleUtil.getLabel("Menu.schedule.payment.transfer.label"));
    menus.put("Menu.document.transfer.label", BundleUtil.getLabel("Menu.document.transfer.label"));
    menus.put("Menu.debiting.label", BundleUtil.getLabel("Menu.debiting.label"));
    menus.put("Menu.teacher.hour.label", BundleUtil.getLabel("Menu.teacher.hour.label"));
    menus.put("Menu.invoice.history.label", BundleUtil.getLabel("Menu.invoice.history.label"));
    menus.put("Menu.quotation.history.label", BundleUtil.getLabel("Menu.quotation.history.label"));
    menus.put("Menu.invoice.label", BundleUtil.getLabel("Menu.invoice.label"));
    menus.put("Menu.invoice.item.label", BundleUtil.getLabel("Menu.invoice.item.label"));
    menus.put("Menu.invoice.footer.label", BundleUtil.getLabel("Menu.invoice.footer.label"));
    menus.put("Menu.accounting.label", BundleUtil.getLabel("Menu.accounting.label"));
    menus.put("Menu.account.label", BundleUtil.getLabel("Menu.account.label"));
    menus.put("Menu.cost.account.label", BundleUtil.getLabel("Menu.cost.account.label"));
    menus.put("Menu.default.account.label", BundleUtil.getLabel("Menu.default.account.label"));
    menus.put("Menu.account.link.label", BundleUtil.getLabel("Menu.account.link.label"));
    menus.put("Menu.booking.journal.label", BundleUtil.getLabel("Menu.booking.journal.label"));
    menus.put("Menu.mode.of.payment.label", BundleUtil.getLabel("Menu.mode.of.payment.label"));
    menus.put("Menu.vat.label", BundleUtil.getLabel("Menu.vat.label"));
    menus.put("Menu.room.rate.label", BundleUtil.getLabel("Menu.room.rate.label"));
  }

  /**
   * @deprecated 
   */
  private void jmvLog() {
    Runtime r = Runtime.getRuntime();
    StringBuilder message = new StringBuilder("Mémoire utilisée avant réinitialisation : \n");
    message.append("Mémoire totale jvm : ").append(r.totalMemory() / 1024).append(" ko\n");
    message.append("Mémoire libre jvm : ").append(r.freeMemory() / 1024).append(" ko\n\n");
    System.out.println("Total Memory : " + r.totalMemory());
    System.out.println("Free  Memory : " + r.freeMemory());
    System.gc();
    System.runFinalization();
    System.out.println("gc+finalize");
    System.out.println("Total Memory : " + r.totalMemory());
    System.out.println("Free  Memory : " + r.freeMemory());
    message.append("Mémoire utilisée après réinitialisation :\n");
    message.append("Mémoire totale jvm : ").append(r.totalMemory() / 1024).append(" ko\n");
    message.append("Mémoire libre jvm : ").append(r.freeMemory() / 1024).append(" ko");
    System.out.println();
    MessagePopup.information(desktop.getFrame(), message.toString());
  }
}
