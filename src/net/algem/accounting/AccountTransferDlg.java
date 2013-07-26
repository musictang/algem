/*
 * @(#)AccountTransferDlg.java	2.8.k 25/07/13
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

package net.algem.accounting;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import net.algem.contact.Contact;
import net.algem.contact.ContactIO;
import net.algem.planning.DateFr;
import net.algem.util.*;
import net.algem.util.model.ModelException;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.MessagePopup;

/**
 * Dialog for transfering orderlines to a file readable by accounting software.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.k
 * @since 1.0a 27/09/2000
 */
public class AccountTransferDlg
        extends AccountTransfer
        implements ActionListener
{

  private AccountTransferView transferView;
  protected Account documentAccount;
  
  public AccountTransferDlg(Dialog _parent, DataCache dataCache) {
    super(_parent, dataCache);
  }

  public AccountTransferDlg(Frame _parent, DataCache dataCache) {
    super(_parent, dataCache);
  }

  @Override
  public void init(DataCache dataCache) {
    super.init(dataCache);
    setDisplay();
  }

  protected void setDisplay() {
    
    transferView = new AccountTransferView(dataCache);
    
    setLayout(new BorderLayout());

    GemPanel p = new GemPanel();
    p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
    
    GemPanel header = new GemPanel();
    header.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(header);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new JLabel(BundleUtil.getLabel("Menu.file.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(filePath, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(chooser, 2, 0, 1, 1, GridBagHelper.WEST);

    p.add(header);
    p.add(transferView);
    
    add(p, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    pack();
  }

  /**
   * Transfert échéancier.
   */
  @Override
  void transfer() {

    String codeJournal = "";
    DateFr start = transferView.getDateStart();
    DateFr end = transferView.getDateEnd();
    int school = transferView.getSchool();
    String modeOfPayment = transferView.getModeOfPayment();
    int errors = 0;
    String query = "WHERE echeance >= '"+start+"' AND echeance <= '"+end
            +"' AND ecole = '"+school
            +"' AND paye='t' AND transfert='f' AND reglement='"+modeOfPayment+"'";
    // les échéances de type prélèvement impliquent que le payeur ait un rib et qu'il existe en tant que contact.
    if ("PRL".equals(modeOfPayment)) {
      query += " AND payeur IN (SELECT idper FROM rib) AND payeur IN (SELECT id FROM personne)";
    }
    //query = "echeance >= '"+debut+"' AND echeance <= '"+fin+"' AND ecole='"+ecole+"' AND reglement='"+reglement+"' AND paye='t' AND transfer='f'";
    Vector<OrderLine> orderLines = OrderLineIO.find(query, dbx);
    if (orderLines.size() <= 0) {
      MessagePopup.information(this, MessageUtil.getMessage("payment.transfer.empty.collection"));
      return;
    }

    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    try {
      documentAccount = getDocumentAccount(modeOfPayment);
      if (documentAccount != null) {
        codeJournal = getCodeJournal(documentAccount.getId());
      }
      String path = filePath.getText();
      
      if (transferView.withCSV()) {
        path = path.replace(".txt", ".csv");
        exportCSV(path,orderLines);
      }
      else {
        if (ModeOfPayment.FAC.toString().equalsIgnoreCase(modeOfPayment)) {
          errors = tiersExport(path, orderLines);
        } else {
            export(path, orderLines, codeJournal);
        }
        // maj transfer echeances
        updateTransfer(orderLines); 
      }
      MessagePopup.information(this, MessageUtil.getMessage("payment.transfer.info", new Object[]{orderLines.size()-errors, path}));
    } catch(IOException ioe) {
      System.err.println(ioe.getMessage());
    } catch (SQLException sqe) {
      GemLogger.logException(MessageUtil.getMessage("payment.transfer.exception"), sqe, this);
    } catch (Exception ex) {
      GemLogger.logException(MessageUtil.getMessage("payment.transfer.exception"), ex, this);
    }
    finally {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  /**
   * Export filePath compta DVLOG.
   * Les échéances sont comptabilisées ligne par ligne.
   * Le total est centralisé sur un compte financier de classe 5.
   * @param path emplacement filePath
   * @param echeances vecteur d'échéances
   * @param codeJournal le code journal
   * @throws IOException
   */
  protected void export(String path, Vector<OrderLine> echeances, String codeJournal) throws IOException {
    int total = 0;
    String number = (documentAccount == null) ? "" : documentAccount.getNumber();
    OrderLine e = null;
    PrintWriter out = new PrintWriter(new FileWriter(path));

    for (int i = 0, n = echeances.size(); i < n ; i++) {
      e =  echeances.elementAt(i);
      total += e.getAmount();
      //String f = (AccountUtil.isPersonalAccount(e.getAccount()) && e.getInvoice() != null) ? e.getInvoice() : e.getInvoiceNumber();
      //out.print(padWithTrailingZeros(e.getAccount().getNumber(), 10)
      out.print(padWithTrailingZeros(getAccount(e), 10)
              + "#" + dateFormat.format(e.getDate().getDate())
              + "#" + codeJournal
              + "#" + padWithTrailingSpaces(e.getDocument(), 10)
              // La valeur 13 ne semble pas obligatoire. On peut étendre la taille du champ.
              //+ "#" + padWithTrailingSpaces(truncate(e.getLabel(), 13), 13)
              + "#" + padWithTrailingSpaces(truncate(e.getLabel() + getInvoiceNumber(e), 24), 24) // numéro de facture pour les echéances correspondant à une facture.
              + "#" + padWithLeadingZeros(nf.format(e.getAmount() / 100.0), 13)
              + "#" + cd
              + "#" + padWithTrailingSpaces(e.getCostAccount().getNumber(), 10)
              + "#" + (char) 13);
    }
    if (total > 0) {
      out.print(
              padWithTrailingZeros(number, 10)
              + "#" + dateFormat.format(e.getDate().getDate())
              + "#" + codeJournal
              + "#" + padWithTrailingSpaces("", 10)
              + "#" + padWithTrailingSpaces("CENTRALISE", 24)
              + "#" + padWithLeadingZeros(nf.format(total / 100.0), 13)
              + "#" + dc
              + "#" + padWithTrailingSpaces("", 10)
              + "#" + (char) 13);//CR (Carriage return, retour à la ligne)
    }
    out.close();
  }

  /**
   * Export d'échéances de facturation.
   * Pour chaque échéance, on identifie le compte de classe 7 correspondant.
   * Chaque échéance possède une ligne de contrepartie en classe 4.
   * Le code journal doit correspondre à un journal de ventes.
   * @param path
   * @param echeances
   * @throws IOException
   * @throws SQLException
   */
  int tiersExport(String path, Vector<OrderLine> echeances) throws IOException, SQLException, ModelException {

    OrderLine e = null;
    int errors = 0;
    boolean m1 = false;
    boolean m2 = false;
    String message = "";
    String m1prefix = MessageUtil.getMessage("account.error");
    String m2prefix = MessageUtil.getMessage("matching.account.error");
    String logpath = path+".log";
    PrintWriter out = new PrintWriter(new FileWriter(path));
    PrintWriter log = new PrintWriter(new FileWriter(logpath));

    for (int i = 0, n = echeances.size(); i < n ; i++) {
      e =  echeances.elementAt(i);
      if (!AccountUtil.isPersonalAccount(e.getAccount())) {
        errors++;
        log.println(m1prefix+" -> "+e+" ["+e.getAccount()+"]");
        m1 = true;
        continue;
      }

      int p = PersonalRevenueAccountIO.find(e.getAccount().getId(), dbx);
      if (p == 0) {
        errors++;
        log.println(m2prefix+" -> "+e.getAccount());
        m2 = true;
        continue;
      }

      Account c = AccountIO.find(p, dbx);
      String m = nf.format(Math.abs(e.getAmount()) / 100.0); // le montant doit être positif
      String codeJournal = getCodeJournal(e.getAccount().getId());
      String f = (e.getInvoice() == null) ? "" : e.getInvoice();
      out.print(padWithTrailingZeros(c.getNumber(), 10)
              + "#" + dateFormat.format(e.getDate().getDate())
              + "#" + codeJournal
              + "#" + padWithTrailingSpaces(e.getDocument(), 10)
              + "#" + padWithTrailingSpaces(truncate(e.getLabel(), 24), 24)
              + "#" + padWithLeadingZeros(m, 13)
              + "#" + cd //Crédit
              + "#" + padWithTrailingSpaces(e.getCostAccount().getNumber(), 10)
              + "#" + (char) 13);

      out.print(
              padWithTrailingZeros(getAccount(e), 10) // compte client
              + "#" + dateFormat.format(e.getDate().getDate())
              + "#" + codeJournal
              + "#" + padWithTrailingSpaces(f, 10)
              + "#" + padWithTrailingSpaces(truncate(e.getLabel(), 24), 24)
              + "#" + padWithLeadingZeros(m, 13)
              + "#" + dc //Débit
              + "#" + padWithTrailingSpaces("", 10)
              + "#" + (char) 13);//CR (Carriage return, retour à la ligne)
    }

    out.close();
    log.close();

    if (errors > 0) {
      if (m1) {
        message += MessageUtil.getMessage("personal.account.export.warning");
      }
      if (m2) {
        message += MessageUtil.getMessage("no.revenue.matching.warning");
       }
      String err = MessageUtil.getMessage("error.count.warning", new Object[] {errors});
      String l = MessageUtil.getMessage("see.log.file", new Object[] {path});
      MessagePopup.warning(transferView, err+message+l);
    }
 
    return errors;
  }

  /**
   * Export csv avec nom des payeurs.
   * @param path emplacement
   * @param echeances liste d'échéances
   * @throws IOException
   */
  protected void exportCSV(String path, Vector<OrderLine> echeances) throws IOException {
    int total = 0;
    OrderLine e = null;
    PrintWriter out = new PrintWriter(new FileWriter(path));
    out.print("id;nom;date;reglement;piece;libelle;montant;analytique"+FileUtil.LINE_SEPARATOR);
    for (int i = 0, n = echeances.size(); i < n ; i++) {
      e =  echeances.elementAt(i);
      Contact c = ContactIO.findId(e.getPayer(), dbx);
      String payerName = c == null ? "" : c.getName();
      total += e.getAmount();
      out.print(e.getPayer()
              + ";" + payerName
              + ";" + dateFormat.format(e.getDate().getDate())
              + ";" + e.getModeOfPayment()
              + ";" + e.getDocument()
              + ";" + e.getLabel()
              + ";" + nf.format(e.getAmount() / 100.0)
              + ";" + e.getCostAccount().getNumber() + " : "+e.getCostAccount().getLabel()
              + FileUtil.LINE_SEPARATOR);
    }
    if (total > 0) {
      out.print(";;"+dateFormat.format(e.getDate().getDate())+";;;TOTAL;"+nf.format(total /100.0)+";"+(char) 13);
    }
    out.close();
  }

}
