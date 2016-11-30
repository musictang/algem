/*
 * @(#)CommunAccountExportService.java	2.9.4.12 23/09/15
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
package net.algem.accounting;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.algem.bank.BankUtil;
import net.algem.bank.Rib;
import net.algem.bank.RibIO;
import net.algem.config.Preference;
import net.algem.contact.Contact;
import net.algem.contact.ContactIO;
import net.algem.contact.Person;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.TextUtil;
import net.algem.util.model.ModelNotFoundException;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.12
 * @since 2.8.r 13/12/13
 */
public abstract class CommunAccountExportService
        implements AccountExportService
{

  protected DataConnection dbx;
  protected JournalAccountService journalService;
  protected NumberFormat defaultNumberFormat = NumberFormat.getInstance(Locale.FRENCH);
  {
    defaultNumberFormat.setGroupingUsed(false);
    defaultNumberFormat.setMinimumFractionDigits(2);
    defaultNumberFormat.setMaximumFractionDigits(2);
  }

  protected DateFormat defaultDateFormat = new SimpleDateFormat("dd-MM-yyyy");

  /**
   * Retrieves the default account for the category {@literal key}.
   *
   * @param key the category
   * @return an account
   * @throws SQLException
   */
  @Override
  public Account getAccount(String key) throws SQLException {
    Preference p = AccountPrefIO.find(String.valueOf(key), dbx);
    return AccountIO.find((Integer) p.getValues()[0], dbx);
//    return c == null ? null : c.getId();
  }

  @Override
  public Account getAccount(int id) throws SQLException {
    return AccountIO.find(id, dbx);
  }

  @Override
  public int getPersonalAccountId(int id) throws SQLException {
    return PersonalRevenueAccountIO.find(id, dbx);
  }

  /**
   * Retrieves the journal associated with the account {@literal account}.
   *
   * @param account account id
   * @return journal code
   * 
   */
  @Override
  public String getCodeJournal(int account) {

    String def = "NA";
    try {
      JournalAccount j = journalService.find(account);
      return j == null ? def : j.getValue();
    } catch (ModelNotFoundException e) {
      return def;
    }
  }

  /**
   * Retrieves the default account associated with the mode of payment {@literal mp}.
   *
   * @param mp mode of payment
   * @return an account
   * @throws SQLException
   */
  @Override
  public Account getDocumentAccount(String mp) throws SQLException {

    Account c = null;
    if (ModeOfPayment.ESP.toString().equalsIgnoreCase(mp)) {
      // journal de caisse
      c = getAccount(AccountPrefIO.CASH);
    } else if (ModeOfPayment.FAC.toString().equalsIgnoreCase(mp)) {
      // journal de ventes
      c = getAccount(AccountPrefIO.PERSONAL);
    } else {
      //journal de banque
      c = getAccount(AccountPrefIO.BANK);
    }
    return c;
  }

  /**
   * Retrieves an account number.
   * Customer accounts (it is a Client account and some invoice was generated from this orderline)
   * are represented by the payer id prefixed by the character 'C'.
   *
   * @param e the orderline
   * @return an account number
   */
  @Override
  public String getAccount(OrderLine e) {

    String c = e.getAccount().getNumber();

    if (e.getInvoice() != null && !e.getInvoice().isEmpty()
            //&& !AccountUtil.INVOICE_PAYMENT.bufferEquals(e.getModeOfPayment())
            && AccountUtil.isCustomerAccount(e.getAccount())) {
      c = "C" + e.getPayer();
    }
    return c;
  }

  /**
   * Retrieves the document number.
   *
   * Invoice number is substituted to document number when an orderline references an account of class 4.
   *
   * @param e the orderline
   * @return a string representing invoice or document number
   */
  @Override
  public String getInvoiceNumber(OrderLine e) {
    return (AccountUtil.isPersonalAccount(e.getAccount()) && e.getInvoice() != null) ? e.getInvoice() : "";
  }

  /**
   * CSV export with payers names.
   *
   * @param path location
   * @param orderLines list of order lines
   * @throws IOException
   */
  @Override
  public List<String> exportCSV(String path, Vector<OrderLine> orderLines) throws IOException {
    DirectDebitService ddService = DirectDebitService.getInstance(dbx);
    List<String> errors = new ArrayList<>();
    int total = 0;
    OrderLine e = null;
    // "UTF-16LE" is the best option : no BOM and excel-compatible
    try (PrintWriter out = new PrintWriter(new File(path), "UTF-16LE")) {
      out.print("id payeur;payeur;id adherent;adherent;date;reglement;piece;libelle;montant;n°compte;libelle compte;analytique;libelle analytique" + TextUtil.LINE_SEPARATOR);
      for (int i = 0, n = orderLines.size(); i < n; i++) {
        e = orderLines.elementAt(i);
        String modeOfPayment = e.getModeOfPayment();
        if (ModeOfPayment.PRL.name().equals(modeOfPayment)) {
          List<String> err = checkDirectDebit(e, ddService);
          if (err.size() > 0) {
            errors.addAll(err);
          }
        }
        Contact c = ContactIO.findId(e.getPayer(), dbx);
        Person m = ContactIO.findId(e.getMember(), dbx);
        String payerName = c == null ? "" : (c.getOrganization() != null && c.getOrganization().length() > 0 ? c.getOrganization() : c.getNameFirstname());
        total += e.getAmount();
        out.print(String.valueOf(e.getPayer())
                + ";" + payerName
                + ";" + e.getMember()
                + ";" + (m == null ? "" : (m.getNameFirstname() == null ? "" : m.getNameFirstname())) // important : between brackets !
                + ";" + defaultDateFormat.format(e.getDate().getDate())
                + ";" + e.getModeOfPayment()
                + ";" + e.getDocument()
                + ";" + e.getLabel()
                + ";" + defaultNumberFormat.format(e.getAmount() / 100.0)
                + ";" + e.getAccount().getNumber()
                + ";" + e.getAccount().getLabel()
                + ";" + e.getCostAccount().getNumber()
                + ";" + e.getCostAccount().getLabel()
                + TextUtil.LINE_SEPARATOR);
      }
      if (total > 0) {
        out.print(";;;;" + defaultDateFormat.format(e.getDate().getDate()) + ";;;TOTAL;" + defaultNumberFormat.format(total / 100.0) + ";;;;" + (char) 13);
      }
      return errors;
    }
  }
  
  private List<String> checkDirectDebit(OrderLine e, DirectDebitService ddService) {
    List<String> errors = new ArrayList<>();
    int payer = e.getPayer();

    Rib rib = RibIO.findId(payer, dbx);
    if (rib == null) {
      errors.add("Payeur " + payer + " : pas de RIB");
    } else {
      if (rib.getIban() == null || !rib.getIban().matches(SepaXmlBuilder.IBAN_REGEX) || !BankUtil.isIbanOk(rib.getIban())) {
        errors.add("Payeur " + payer + ": erreur IBAN");
      }
//      if (rib.getB)
      DDMandate mandate;
      try {
        //mandate = ddService.getMandateWithBic(payer);
        if (mandate.getBic() == null) {
          errors.add("Payeur " + payer + ": pas de MANDAT SEPA");
        }
      } catch (DDMandateException ex) {
        GemLogger.logException(ex);
        errors.add("Payeur " + payer + ": erreur MANDAT");
      }

    }
    return errors;
  }

}
