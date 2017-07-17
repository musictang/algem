/*
 * @(#) ExportEBP.java Algem 2.15.0 14/07/2017
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
 */

package net.algem.accounting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import net.algem.billing.VatIO;
import net.algem.contact.Contact;
import net.algem.contact.ContactIO;
import net.algem.contact.Telephone;
import net.algem.util.DataConnection;
import net.algem.util.FileUtil;
import net.algem.util.MessageUtil;
import net.algem.util.TextUtil;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 10/07/2017
 */
public class ExportEBP
extends CommonAccountExportService
{

  private DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
  private NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
  private static char cd = 'C';// credit
  private static char dc = 'D';//debit

  public ExportEBP(DataConnection dc) {
    dbx = dc;
    journalService = new JournalAccountService(dc);
    nf.setGroupingUsed(false);
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
  }
  @Override
  public String getFileExtension() {
    return ".txt";
  }

  @Override
  public void export(String path, Vector<OrderLine> lines, String codeJournal, Account documentAccount) throws IOException {
    int totalDebit = 0;
    int totalCredit = 0;
    String number = (documentAccount == null) ? "" : documentAccount.getNumber();
    //String label = (documentAccount == null) ? "" : TextUtil.stripDiacritics(documentAccount.getLabel());
    OrderLine e = null;
    Map<String,Contact> accountInfo = new HashMap<>();//comptes clients
    try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.ISO_8859_1), true)) {
      int lineNumber = 0;
      StringBuilder sb = new StringBuilder();

      for (int i = 0, n = lines.size(); i < n; i++) {
        e = lines.elementAt(i);
        if (e.getAmount() > 0) {
          totalDebit += e.getAmount();
        } else {
          totalCredit += Math.abs(e.getAmount());
        }
        String account = getAccount(e);
        if (account.startsWith("411") && !"4110000000".equals(account) && accountInfo.get(account) == null) {
          accountInfo.put(account, getContactFromCustomer(e.getPayer()));
        }
        sb.append(++lineNumber).append(','); // line number (ignored)
        sb.append(dateFormat.format(e.getDate().getDate())).append(',');//date
        sb.append(codeJournal).append(',');//journal
        sb.append(account).append(',');//numéro compte
        sb.append(',');// libellé auto
        sb.append('"').append(TextUtil.stripDiacritics(e.getLabel())).append('"').append(',');// libelle
        sb.append('"').append(e.getDocument()).append('"').append(',');// numéro de pièce
        sb.append(nf.format(e.getAmount() / 100.0)).append(',');//montant
        sb.append(e.getAmount() > 0 ? cd : dc).append(',');
        sb.append(dateFormat.format(new Date())).append(',');//date d'échéance
        sb.append("\r\n");
        out.print(sb.toString());
        sb.delete(0, sb.length());
      }
      Date now = new Date();
      if (totalDebit > 0) {
        assert(e != null);
        sb.append(++lineNumber).append(','); // line number (ignored)
        sb.append(dateFormat.format(e.getDate().getDate())).append(',');//date
        sb.append(codeJournal).append(',');//journal
        sb.append(number).append(',');//numéro compte
        sb.append(',');// libellé auto
        sb.append('"').append("CENTRALISE").append('"').append(',');// libelle
        sb.append('"').append("IMPORT ").append(dateFormat.format(now)).append('"').append(',');// numéro de pièce
        sb.append(nf.format(totalDebit / 100.0)).append(',');//montant
        sb.append(dc).append(',');
        sb.append(dateFormat.format(now)).append(',');//date d'échéance
        sb.append("\r\n");
        out.print(sb.toString());
        sb.delete(0, sb.length());
      }
      if (totalCredit > 0) {
        assert(e != null);
        sb.append(++lineNumber).append(','); // line number (ignored)
        sb.append(dateFormat.format(e.getDate().getDate())).append(',');//date
        sb.append(codeJournal).append(',');//journal
        sb.append(number).append(',');//numéro compte
        sb.append(',');// libellé auto
        sb.append('"').append("CENTRALISE C").append('"').append(',');// libelle
        sb.append('"').append("IMPORT ").append(dateFormat.format(now)).append('"').append(',');// numéro de pièce
        sb.append(nf.format(totalCredit / 100.0)).append(',');//montant
        sb.append(dc).append(',');
        sb.append(dateFormat.format(new Date())).append(',');//date d'échéance
        sb.append("\r\n");
        out.print(sb.toString());
        sb.delete(0, sb.length());
      }
    }
    // write COMPTES.TXT
    writeAccountFile(path, accountInfo);
  }

  @Override
  public int tiersExport(String path, Vector<OrderLine> lines) throws IOException, SQLException {
    VatIO vatIO = new VatIO(dbx);
    OrderLine e = null;
    int errors = 0;
    boolean m1 = false;
    boolean m2 = false;
    String message = "";
    StringBuilder logMessage = new StringBuilder();
    String m1prefix = MessageUtil.getMessage("account.error");
    String m2prefix = MessageUtil.getMessage("matching.account.error");
    String logpath = path + ".log";
    Map<String,Contact> accountInfo = new HashMap<>();//comptes clients
    try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.ISO_8859_1), true)) {
      int lineNumber = 0;
      StringBuilder sb = new StringBuilder();

      for (int i = 0, n = lines.size(); i < n; i++) {
        e = lines.elementAt(i);
        if (!AccountUtil.isPersonalAccount(e.getAccount())) {
          errors++;
          logMessage.append(m1prefix).append(" -> ").append(e).append(" [").append(e.getAccount()).append("]").append(TextUtil.LINE_SEPARATOR);
          m1 = true;
          continue;
        }

        int p = getPersonalAccountId(e.getAccount().getId());
        if (p == 0) {
          errors++;
          logMessage.append(m2prefix).append(" -> ").append(e.getAccount()).append(TextUtil.LINE_SEPARATOR);
          m2 = true;
          continue;
        }

        Account taxAccount = null;
        double exclTax = 0;//HT
        double vat = 0;
        if (e.getTax() > 0.0) {
          taxAccount = getTaxAccount(e.getTax(), vatIO);
          double coeff = 100 / (100 + e.getTax());
          exclTax = AccountUtil.round((Math.abs(e.getAmount()) / 100d) * coeff);
          vat = AccountUtil.round((Math.abs(e.getAmount()) / 100d) - exclTax);
        }
        // COMPTE DE PRODUIT (7xx)
        Account c = getAccount(p);
        String m = nf.format(Math.abs(e.getAmount()) / 100.0); // le montant doit être positif
        String codeJournal = getCodeJournal(e.getAccount().getId());

        sb.append(++lineNumber).append(','); // line number (ignored)
        sb.append(dateFormat.format(e.getDate().getDate())).append(',');//date
        sb.append(codeJournal).append(',');//journal
        sb.append(c.getNumber()).append(',');//numéro compte
        sb.append(',');// libellé auto
        sb.append('"').append(TextUtil.stripDiacritics(e.getLabel())).append('"').append(',');// libelle
        sb.append('"').append(e.getDocument()).append('"').append(',');// numéro de pièce
        sb.append(exclTax > 0 ? nf.format(exclTax) : m).append(',');//montant
        sb.append(e.getAmount() < 0 ? cd : dc).append(',');
        sb.append(dateFormat.format(new Date())).append(',');//date d'échéance
        sb.append("\r\n");
        out.print(sb.toString());
        sb.delete(0, sb.length());
        // ANALYTIQUE
        String ca = e.getCostAccount().getNumber();
        if (ca != null && ca.length() > 0) {
          sb.append('>').append(e.getCostAccount().getNumber()).append(',').append("100.00").append(',').append(exclTax > 0 ? nf.format(exclTax) : m).append("\r\n");
        }
        out.print(sb.toString());
        sb.delete(0, sb.length());
        // TVA
        if (vat > 0.0) {
          assert (taxAccount != null);
          sb.append(++lineNumber).append(','); // line number (ignored)
          sb.append(dateFormat.format(e.getDate().getDate())).append(',');//date
          sb.append(codeJournal).append(',');//journal
          sb.append(taxAccount.getNumber()).append(',');//numéro compte
          sb.append(',');// libellé auto
          sb.append('"').append(TextUtil.stripDiacritics(taxAccount.getLabel())).append('"').append(',');// libelle
          sb.append('"').append(e.getDocument()).append('"').append(',');// numéro de pièce
          sb.append(nf.format(vat)).append(',');
          sb.append(e.getAmount() < 0 ? cd : dc).append(',');
          sb.append(dateFormat.format(new Date())).append(',');//date d'échéance
          sb.append("\r\n");
          out.print(sb.toString());
          sb.delete(0, sb.length());
        }
        // COMPTE D'ATTENTE (411)
        String client = getAccount(e);
        if (!"4110000000".equals(client) && accountInfo.get(client) == null) {
          accountInfo.put(client, getContactFromCustomer(e.getPayer()));
        }
        sb.append(++lineNumber).append(','); // line number (ignored)
        sb.append(dateFormat.format(e.getDate().getDate())).append(',');//date
        sb.append(codeJournal).append(',');//journal
        sb.append(client).append(',');//numéro compte
        sb.append(',');// libellé auto
        sb.append('"').append(TextUtil.stripDiacritics(e.getLabel())).append('"').append(',');// libelle
        sb.append('"').append(e.getDocument()).append('"').append(',');// numéro de pièce
        sb.append(m).append(',');// montant
        sb.append(e.getAmount() < 0 ? dc : cd).append(','); // debit
        sb.append(dateFormat.format(new Date())).append(',');//date d'échéance
        sb.append("\r\n");
        out.print(sb.toString());
        sb.delete(0, sb.length());

      }
    }// out.close()

    if (logMessage.length() > 0) {
      try (PrintWriter log = new PrintWriter(new FileWriter(logpath))) {
        log.println(logMessage.toString());
      }
    }

    if (errors > 0) {
      if (m1) {
        message += MessageUtil.getMessage("personal.account.export.warning");
      }
      if (m2) {
        message += MessageUtil.getMessage("no.revenue.matching.warning");
      }
      String err = MessageUtil.getMessage("error.count.warning", errors);
      String l = MessageUtil.getMessage("see.log.file", path);
      MessagePopup.warning(null, err + message + l);
    } else {
      // write COMPTES.TXT
      writeAccountFile(path, accountInfo);
    }
    return errors;
  }

  /**
   * Generates a file named "COMPTES.TXT".
   * This file is read by EBP before importing order lines and located at the same place as the initial file to be imported.
   * Le fichier des comptes se nomme obligatoirement COMPTES.TXT. Il doit être placé dans le même
   * répertoire que celui contenant le fichier des écritures.
   * Au moment de l'importation, vous sélectionnez le fichier des écritures. Le programme détectera
   * automatiquement la présence du fichier COMPTES.TXT, et l'importera avant les écritures. En
   * l'absence de ce fichier des comptes, le libellé des comptes créés sera "Compte créé pendant
   * transfert".
   * @param path import file path
   * @param accountInfo map association between customer's account and contact
   * @throws IOException
   */
  private void writeAccountFile(String path, Map<String, Contact> accountInfo) throws IOException {
    String parentDir = new File(path).getParent();
    if (parentDir != null) {
      String accountFilePath = parentDir + FileUtil.FILE_SEPARATOR + "COMPTES.TXT";
      try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(accountFilePath), StandardCharsets.ISO_8859_1), true)) {
        out.print(getAccountFileContent(accountInfo));
      }
    }
  }

  /**
   * Builds customers' accounts content info.
   * La fin de chaque enregistrement est un retour chariot.
   * Le séparateur de champs est la virgule.
   * Numéro de compte : alphanumérique sur 15 caractères maximum.
   * Intitulé : alphanumérique sur 60 caractères maximum (ce champ correspond au libellé du compte).
   * Raison sociale : alphanumérique sur 30 caractères maximum (ce champ correspond à la raison
   * sociale de l'onglet Adresse).
   * Adresse : alphanumérique sur 100 caractères maximum.
   * Code postal : numérique sur 5 caractères maximum.
   * Ville : alphanumérique sur 30 caractères maximum.
   * Pays : alphanumérique sur 35 caractères maximum.
   * Interlocuteur : alphanumérique sur 35 caractères maximum.
   * Téléphone : alphanumérique sur 20 caractères maximum.
   * Fax : alphanumérique sur 20 caractères maximum.
   *
   * @param map map association between customer's account and contact
   * @return a set of lines formatted as string
   */
  private String getAccountFileContent(Map<String, Contact> map) {
    StringBuilder sb = new StringBuilder();
    char c = ',';
    for (Map.Entry<String, Contact> entry : map.entrySet()) {
      sb.append(entry.getKey()).append(c);//account number
      Contact v = entry.getValue();
      String org = v.getOrganization();
      String vName = org == null || org.isEmpty() ? v.getNameFirstname() : v.getOrganization();
      sb.append(vName).append(c);//account label
      String companyName = (org == null || org.isEmpty() ? "" : org);
      sb.append(companyName).append(c);//company name
      if (v.getAddress() != null) {
        sb.append(TextUtil.truncate(v.getAddress().getAdr1(), 100)).append(c);//address
        sb.append(TextUtil.truncate(v.getAddress().getCdp(), 5)).append(c);//postal code
        sb.append(TextUtil.truncate(v.getAddress().getCity(), 30)).append(c);//city
      } else {
        sb.append(c).append(c).append(c);
      }
      sb.append(TextUtil.truncate(v.getNameFirstname().trim(), 35)).append(c);//referent
      List<Telephone> tels = v.getTele();
      String tel = tels != null ? tels.get(0).getNumber() : "";
      sb.append(TextUtil.truncate(tel, 20)).append(c); // tel
      sb.append("\r\n");
    }
    return sb.toString();
  }

  /**
   *
   * @param customerAccount
   * @return
   */
  private Contact getContactFromCustomer(int payer) {
//    int payer = Integer.parseInt(customerAccount.substring(3));
    return ContactIO.findId(payer, dbx);
  }

}
