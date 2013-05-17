/*
 * @(#)StandingOrderExportDlg.java	2.7.f 07/02/13
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
package net.algem.accounting;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import net.algem.config.*;
import net.algem.planning.DateFr;
import net.algem.planning.DateRangePanel;
import net.algem.util.*;
import net.algem.util.ui.*;

/**
 * Export standing order info list and bank file.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.f
 */
public class StandingOrderExportDlg
        extends JDialog
        implements ActionListener
{

  private static final String MAILING_FILE_NAME = "mailing_prlv.csv";
  private static final String EXPORT_FILE_NAME = "prlv.txt";
  private static final String LOG_FILE_NAME = ".log";
  private static String lf = FileUtil.LINE_SEPARATOR;
  /*
   * $date="151001";
   * $entete=sprintf("0308 %06d%12.12s%-24.24s%-7.7s E %-5.5s%-11.11s %05d ",$noemet,$date,$raison,"TRIM1",$guichet,$compte,$etab);
   */
  /** Numéro d'émetteur. */
  private String noIssuer;
  /** Raison sociale. */
  private String firmName;
  /** Code agence bancaire. */
  private String bankBranch;
  /** Numéro de compte. */
  private String account;
  /** Code établissement (bancaire). */
  private String bankHouse;
  private String label = "COTIS";
  private DataConnection dc;
  private PrintWriter pMailing;
//  private PrintStream pExport;
  private PrintWriter pExport;
  private PrintWriter pLog;
  private GemField fMailling;
  private GemField fExport;
  private GemField flabel;
  private DateRangePanel datePanel;
  private GemButton btValidation;
  private GemButton btCancel;
  private JPanel buttons;
  private GemChoice schoolChoice;
  private JButton browse1, browse2;
  private File file1, file2, logFile;
  

  public StandingOrderExportDlg(Frame _parent, String _titre, DataConnection dc) {
    super(_parent, _titre);
    init(dc);
  }

  public StandingOrderExportDlg(Dialog _parent, String _titre, DataConnection dc) {
    super(_parent, _titre);
    init(dc);
  }

  public void init(DataConnection dc) {
    this.dc = dc;

    noIssuer = ConfigUtil.getConf(ConfigKey.STANDING_ORDER_ISSUER.getKey(), dc);
    firmName = ConfigUtil.getConf(ConfigKey.STANDING_ORDER_FIRM_NAME.getKey(), dc);
    bankBranch = ConfigUtil.getConf(ConfigKey.STANDING_ORDER_BANK_BRANCH.getKey(), dc);
    account = ConfigUtil.getConf(ConfigKey.STANDING_ORDER_ACCOUNT.getKey(), dc);
    bankHouse = ConfigUtil.getConf(ConfigKey.STANDING_ORDER_BANKHOUSE_CODE.getKey(), dc);

    schoolChoice = new ParamChoice(ParamTableIO.find(SchoolCtrl.TABLE, SchoolCtrl.SORT_COLUMN, dc));
    schoolChoice.addItemListener(new ItemListener()
    {

      public void itemStateChanged(ItemEvent e) {
        flabel.setText(getLabel());
      }
    });

    browse1 = new JButton(GemCommand.BROWSE_CMD);
    browse1.addActionListener(this);

    browse2 = new JButton(GemCommand.BROWSE_CMD);
    browse2.addActionListener(this);

    btValidation = new GemButton(GemCommand.VALIDATION_CMD);
    btValidation.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    buttons = new GemPanel();
    buttons.add(btValidation);
    buttons.add(btCancel);

    GemPanel p = new GemPanel();
    p.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(p);
    gb.insets = GridBagHelper.SMALL_INSETS;

    Calendar c = Calendar.getInstance();
    c.set(Calendar.DAY_OF_MONTH, 15);
    datePanel = new DateRangePanel(DateRangePanel.SIMPLE_DATE, null);
    datePanel.setDate(c.getTime());
    String path = ConfigUtil.getExportPath(this.dc) + FileUtil.FILE_SEPARATOR;


    fMailling = new GemField(path + MAILING_FILE_NAME, 30);
    fMailling.setAutoscrolls(true);
    gb.add(new GemLabel(BundleUtil.getLabel("Mailing.file.label")), 0, 0, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(fMailling, 1, 0, 1, 1);
    gb.add(browse1, 2, 0, 1, 1, GridBagHelper.NONE, GridBagHelper.WEST);

    fExport = new GemField(path + EXPORT_FILE_NAME, 30);
    gb.add(new GemLabel(BundleUtil.getLabel("Export.file.label")), 0, 1, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(fExport, 1, 1, 1, 1);
    fExport.setAutoscrolls(true);
    gb.add(browse2, 2, 1, 1, 1, GridBagHelper.NONE, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("Date.label")), 1, 2, 1, 1, GridBagHelper.NONE, GridBagHelper.EAST);
    gb.add(datePanel, 2, 2, 1, 1, GridBagHelper.NONE, GridBagHelper.WEST);

    gb.add(new GemLabel(BundleUtil.getLabel("School.label")), 1, 3, 1, 1, GridBagHelper.NONE, GridBagHelper.EAST);
    gb.add(schoolChoice, 2, 3, 1, 1, GridBagHelper.NONE, GridBagHelper.WEST);

    flabel = new GemField(getLabel());
    gb.add(new GemLabel(BundleUtil.getLabel("Label.label")), 0, 4, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(flabel, 1, 4, 2, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);

    getContentPane().add(p, BorderLayout.CENTER);
    getContentPane().add(buttons, BorderLayout.SOUTH);
    setSize(500, 300);
    pack();
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    JFileChooser fileChooser = new JFileChooser((File) null);
    int ret = JFileChooser.CANCEL_OPTION;

    if (evt.getSource() == btCancel) {
      close();
    } else if (evt.getSource() == btValidation) {
      validation();
      close();
    } else if (evt.getSource() == browse1) {
      ret = fileChooser.showDialog(this, BundleUtil.getLabel("FileChooser.selection"));
      if (ret == JFileChooser.APPROVE_OPTION) {
        file1 = fileChooser.getSelectedFile();
        fMailling.setText(file1.getPath());
      }
    } else if (evt.getSource() == browse2) {
      ret = fileChooser.showDialog(this, BundleUtil.getLabel("FileChooser.selection"));
      if (ret == JFileChooser.APPROVE_OPTION) {
        file2 = fileChooser.getSelectedFile();
        fExport.setText(file2.getPath());
      }
    }
  }

  void validation() {
    boolean error = false;
    int cpt = 0; // nombre de lignes exportées
    String path1 = fMailling.getText();
    String path2 = fExport.getText();

    label = flabel.getText();

    DateFr datePrl = datePanel.get();
    //Param pe = (Param) schoolChoice.getSelectedItem();
    int school = schoolChoice.getKey();
    // TODO faire un groupement par payeur afin de calculer automatiquement le montant total.
    String query = "SELECT payeur,montant,analytique FROM echeancier2"
            + " WHERE ecole = '" + school + "' AND reglement = 'PRL' AND paye = 't' AND echeance = '" + datePrl.toString() + "'"
            //+" AND payeur IN (SELECT idper FROM rib)"
            + " ORDER BY payeur,echeance";
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    try {
      pMailing = new PrintWriter(new FileWriter(path1));
//      pExport = new PrintStream(path2);// XXX PrintWriter ?
      pExport = new PrintWriter(new FileWriter(path2));

      pMailing.println("id;civil;nom;prenom;adr1;adr2;cdp;ville;analytique;montant");
      pExport.format("0308        %-6.6s%12.12s%-24.24s%-7.7s                   E     %-5.5s%-11.11s                                               %-5.5s      ", noIssuer, datePrl.toStringShort(), firmName, "PRLV", bankBranch, account, bankHouse);

      int oldid = 0;
      int total = 0;
      int gtotal = 0;
      String costAccount = null;

      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        int payer = rs.getInt(1);
        int amount = rs.getInt(2);
        costAccount = rs.getString(3);
        if (oldid == payer) {
          total += amount;
        } else {
          if (total > 0) {
            //cpt++;
            if (!display(oldid, total, costAccount)) {
              error = true;
            } else {
              cpt++;
              gtotal += total;
            }
          }
          total = amount;
          oldid = payer;
        }
      }
      if (total > 0) {
        if (!display(oldid, total, costAccount)) {
          error = true;
        } else {
          cpt++;
          gtotal += total;
        }
      }
      pExport.format(lf + "0808        %-6.6s                                                                                    %016d                                          ", noIssuer, gtotal);

      String message = MessageUtil.getMessage("export.success.info", new Object[]{cpt, path1});
      if (error) {
        message += lf + MessageUtil.getMessage("payer.export.warning");
        message += lf + MessageUtil.getMessage("payer.log.info", logFile.getAbsolutePath());
        MessagePopup.warning(this, message);
      } else {
        MessagePopup.information(this, message);
      }
    } catch (FileNotFoundException fne) {
      MessagePopup.information(this, MessageUtil.getMessage("file.open.exception"));
    } catch (IOException e) {
      GemLogger.logException(e);
    } catch (SQLException e) {
      GemLogger.logException(query, e, this);
    } finally {
      if (pMailing != null) {
        pMailing.close();
      }
      if (pExport != null) {
        pExport.close();
      }
      if (pLog != null) {
        pLog.close();
      }
      setCursor(Cursor.getDefaultCursor());
    }

  }

  private void createLogFile(String path) throws IOException {
    logFile = new File(path + LOG_FILE_NAME);
    pLog = new PrintWriter(new FileWriter(logFile.getPath()));
  }

  /**
   * Adds a line info in export file, corresponding to the contact of the payer.
   * Address is optional.
   *
   * @param id payer id
   * @param total total amount
   * @return true if payer has a rib
   * @throws Exception
   */
  private boolean display(int id, int total, String analytique) throws SQLException {
    String query = "SELECT p.id,p.civilite,p.nom,p.prenom,a.adr1,a.adr2,a.cdp,a.ville,r.etablissement,r.guichet,r.compte,r.clerib"
            + " FROM personne p LEFT JOIN adresse a ON p.id=a.idper, rib r"
            + " WHERE p.id = " + id + " and p.id = r.idper";
    ResultSet rs2 = dc.executeQuery(query);
    String payerId = "";
    String payerName = "";

    if (rs2.next()) {
      payerId = rs2.getString(1);
      //nomPayeur = rs2.getString(3)+" "+rs2.getString(4);
      // Correction 1.1a bug sur la longueur du champ nom
      payerName = rs2.getString(3);
      payerName = FileUtil.replaceChars(payerName);
      for (int i = 1; i <= 8; i++) {
        String info = rs2.getString(i);
        pMailing.print((info == null ? "" : info.trim()) + ";");
      }
      pMailing.println(analytique + ";" + total);
    } else {
        try {
          if (pLog == null) {
            createLogFile(fExport.getText());
          }
          pLog.println(MessageUtil.getMessage("payer.export.error", id));
        } catch (IOException io) {
          GemLogger.logException(io);
        }
      rs2.close();
      return false;
    }

    String destEtab = rs2.getString(9);
    String destBankBranch = rs2.getString(10);
    String destAccount = rs2.getString(11);

    pExport.format(lf + "0608        %-6.6s%-12.12s%-24.24s%-24.24s        %-5.5s%-11.11s%016d%-31.31s%-5.5s      ", noIssuer, payerId, payerName, " ", destBankBranch, destAccount, total, label, destEtab);

    rs2.close();
    return true;
  }

  private void close() {
    setVisible(false);
    dispose();
  }

  private String getLabel() {
    String e = ((Param) schoolChoice.getSelectedItem()).getValue();
    String school = (e == null) ? "" : e;
    DateFr d = new DateFr(ConfigUtil.getConf(ConfigKey.BEGINNING_YEAR.getKey(), dc));
    DateFr f = new DateFr(ConfigUtil.getConf(ConfigKey.END_YEAR.getKey(), dc));

    return "COTIS " + school + " " + String.valueOf(d.getYear()) + "-" + String.valueOf(f.getYear());
  }

  /**
   * For test only.
   *
   * @param args
   */
  public static void main(String[] args) {
    String n = "FÂURÉÈ'N'DÏAYE";
    System.out.println(FileUtil.replaceChars(n));
  }
}
