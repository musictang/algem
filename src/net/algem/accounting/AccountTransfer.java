/*
 * @(#)AccountTransfer.java	2.8.a 01/04/13
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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import net.algem.config.ConfigUtil;
import net.algem.config.Preference;
import net.algem.edition.ExportDlg;
import net.algem.util.*;
import net.algem.util.model.ModelException;
import net.algem.util.model.ModelNotFoundException;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * Abstract class for accounting transfers.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 2.3.c 21/03/12
 */
public abstract class AccountTransfer
  extends JDialog
  implements ActionListener
{

  static final int LEADING = 0;
  static final int TRAILING = 1;
  protected DataCache dataCache;
  protected DataConnection dbx;
  protected JournalAccountService service;
  protected NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);
  protected GemButton btValidation;
  protected GemButton btCancel;
  protected JButton chooser;
  protected GemPanel buttons;
  protected GemField filePath;
  protected File file;
  protected static char cd = 'C';// credit
  protected static char dc = 'D';//debit
  protected DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");


  public AccountTransfer(Frame _parent, DataCache dataCache) {
    super(_parent, "Transfert Compta");
    init(dataCache);
  }

  public AccountTransfer(Dialog _parent, DataCache dataCache) {
    super(_parent, "Transfert Compta");
    init(dataCache);
  }

  protected void init(DataCache dataCache) {
    this.dataCache = dataCache;
    dbx = dataCache.getDataConnection();
    service = new JournalAccountService(dbx);
    nf.setGroupingUsed(false);
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(2);

    btValidation = new GemButton(GemCommand.VALIDATION_CMD);
    btValidation.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    buttons = new GemPanel(new GridLayout(1, 1));
    buttons.add(btValidation);
    buttons.add(btCancel);

    filePath = new GemField(ConfigUtil.getExportPath(dbx) + FileUtil.FILE_SEPARATOR + "export.txt", 25);
    chooser = new JButton(GemCommand.BROWSE_CMD);
    chooser.addActionListener(this);

  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btCancel) {
      close();
    } else if (evt.getSource() == btValidation) {
      transfer();
      close();
    } else if (evt.getSource() == chooser) {
      //JFileChooser fileChooser = new JFileChooser((File) null);
      JFileChooser fileChooser = ExportDlg.getFileChooser(filePath.getText());
      int ret = fileChooser.showDialog(this, BundleUtil.getLabel("FileChooser.selection"));
      if (ret == JFileChooser.APPROVE_OPTION) {
        file = fileChooser.getSelectedFile();
        filePath.setText(file.getPath());
      }
    }
  }

  /**
   * Update the column transfer for the orderlines {@code list}.
   * @param list list of orderlines
   * @throws SQLException
   */
  public void updateTransfer(Vector<OrderLine> list) throws SQLException {
    if (MessagePopup.confirm(this,
        MessageUtil.getMessage("payment.transfer.confirm"),
        MessageUtil.getMessage("payment.transfer.confirm.title"))) {
      GemLogger.log(Level.INFO, "ComptaTransfertDlg");
      int n = list.size();
      for (int i = 0; i < n; i++) {
        OrderLine e = list.elementAt(i);
        e.setTransfered(true);
        OrderLineIO.transfer(e, dbx);
      }
    }
  }

  public void close() {
    setVisible(false);
    dispose();
  }

  abstract void transfer();

   /**
   * Retrieves the default account for the category {@code key}.
   * 
   * @param key the category
   * @return an account
   * @throws SQLException
   */
  protected Account getAccount(String key) throws SQLException {
    Preference p = AccountPrefIO.find(String.valueOf(key), dbx);
    return AccountIO.find((Integer) p.getValues()[0], dbx);
//    return c == null ? null : c.getId();
  }


  /**
   * Retrieves the journal associated with the account {@code account}.
   * 
   * @param account
   * @return journal code
   * @throws ModelException if journal don't exist
   */
  protected String getCodeJournal(int account) throws ModelException {

    String def = "NA";
    try {
      JournalAccount j = service.find(account);
      return j == null ? def : j.getValue();
    } catch (ModelNotFoundException e) {
      MessagePopup.warning(this, e.getMessage());
      return def;
    }
  }

/**
 * Retrieves the default account for the mode of payment {@code mp}.
 * 
 * @param mp mode of payment
 * @return an account
 * @throws SQLException
 */
  protected Account getDocumentAccount(String mp) throws SQLException {

    Account c = null;
    if (ModeOfPayment.ESP.toString().equalsIgnoreCase(mp)) {
      // journal de caisse
      c = getAccount(AccountPrefIO.CASH_ACCOUNT);
    } else if (ModeOfPayment.FAC.toString().equalsIgnoreCase(mp)) {
      // journal de ventes
      c = getAccount(AccountPrefIO.PERSONAL_ACCOUNT);
    } else {
      //journal de banque
      c = getAccount(AccountPrefIO.BANK_ACCOUNT);
    }
    return c;
  }

   /**
   * Retrieves an account number.
   * Customer accounts (it is a Client account and some invoice was generated from this orderline)
   * are represented by the payer id prefixed by the character 'C'.
   * @param e the orderline
   * @return an account number
   */
  protected String getAccount(OrderLine e) {

    String c = e.getAccount().getNumber();

    if (e.getInvoice() != null && !e.getInvoice().isEmpty()
            //&& !AccountUtil.INVOICE_PAYMENT.equals(e.getModeOfPayment())
            && AccountUtil.isCustomerAccount(e.getAccount())
        ) {
      c = "C"+e.getPayer();
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
  protected String getInvoiceNumber(OrderLine e) {
     return (AccountUtil.isPersonalAccount(e.getAccount()) && e.getInvoice() != null) ? e.getInvoice() : "";
  }
  
//=====================================================
//  METHODES UTILITAIRES POUR LE FORMATAGE DES CHAMPS
//=====================================================
  /**
   * Tronque la chaine de caractères au nombre de caractères
   * fourni par <CODE>taille</CODE>. Si la chaîne a une taille
   * supérieure à l'argument <CODE>taille</CODE> alors renvoie
   * la chaîne d'origine sans altération
   */
  static String truncate(String chaine, int taille) {

    if (chaine.length() > taille) {
      return chaine.substring(0, taille);
    }

    return chaine;
  }

  /**
   * Ajoute le caractère <CODE>c</CODE> au début ou à la fin de la chaine
   * de telle manière que la chaine soit de <CODE>size</CODE> caractères
   */
  static String pad(String chaine, int size, char c, int where) {

    if (chaine == null) {
      chaine = "";
    }

    String resultat = chaine;
    int numSpaces = size - chaine.length();
    if (numSpaces > 0) {
      for (int i = 0; i < numSpaces; i++) {
        if (where == TRAILING) {
          resultat += c;
        } else {
          resultat = c + resultat;
        }
      }
    }
    return resultat;
  }

  /**
   * Remplit d'espaces une chaîne de caractères selon la taille fixée par <code>size</code>.
   */
  static String padWithTrailingSpaces(String chaine, int size) {
    return pad(chaine, size, ' ', TRAILING);
  }

  /**
   * Remplit de zéros une chaîne de caractères selon la taille fixée par size.
   * @param chaine
   * @param size
   * @return une chaîne
   */
  static String padWithTrailingZeros(String chaine, int size) {
    return pad(chaine, size, '0', TRAILING);
  }

  static String padWithLeadingZeros(String chaine, int size) {
    return pad(chaine, size, '0', LEADING);
  }

  static String padWithLeadingZeros(int chiffre, int size) {
    return pad(String.valueOf(chiffre), size, '0', LEADING);
  }

   /**
   * Programme de test des fonctions pad
   */
  public static void main(String[] args) {

    //System.out.println(System.getProperty("line.separator"));
    String t = "123456";
    int c = 121344;
    System.out.println(AccountTransferDlg.padWithLeadingZeros(c, 10) + ";");
  }
}
