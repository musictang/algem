/*
 * @(#)DirectDebitRequest.java	2.15.0 14/09/17
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
 * 
 */
package net.algem.edition;

import java.awt.*;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import net.algem.accounting.DDMandate;
import net.algem.accounting.DirectDebitCreditor;
import net.algem.accounting.DirectDebitService;
import net.algem.accounting.OrderLineIO;
import net.algem.bank.BankBranch;
import net.algem.bank.BankBranchIO;
import net.algem.bank.Rib;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.Address;
import net.algem.contact.PersonFile;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 */
public class DirectDebitRequest
        extends Canvas
{

  private FontMetrics fm;
  private Font normalFont;
  private Font mediumFont;
  private Font smallFont;
  private Font boldFont;
  private Frame parent;
  private Toolkit tk;
  private Properties props = new Properties();
  private String street;
  private boolean detailed;
  private DDMandate mandate;

  /**
   *
   * @param c
   * @param ddMandate
   * @param withOrderLines prints also the amount of order lines
   */
  public DirectDebitRequest(Component c, DDMandate ddMandate, boolean withOrderLines) {

    detailed = withOrderLines;
    this.mandate = ddMandate;
    while (c.getParent() != null) {
      c = c.getParent();
    }
    if (c instanceof Frame) {
      parent = (Frame) c;
    }

    tk = Toolkit.getDefaultToolkit();

    props.put("awt.print.paperSize", "a4");

    normalFont = new Font("Helvetica", Font.PLAIN, 10);
    mediumFont = new Font("Helvetica", Font.PLAIN, 7);
    smallFont = new Font("Helvetica", Font.PLAIN, 6);
    boldFont = new Font("Helvetica", Font.BOLD, 9);

  }

  public void edit(final PersonFile dossier, final BankBranch b, String title, DataCache cache) {

    if (dossier == null) {
      return;
    }
    int marginLeft = 25;
    int mgm = 270;
    Address a = null;
    Rib rib = null;
    BankBranch branch = b;

    DataConnection dc = DataCache.getDataConnection();
    DirectDebitService ddService = DirectDebitService.getInstance(dc);
    DirectDebitCreditor ddCreditor = ddService.getCreditorInfo();

    Address branchAddress = null;
    String adr1 = ConfigUtil.getConf(ConfigKey.ORGANIZATION_ADDRESS1.getKey());
    String adr2 = ConfigUtil.getConf(ConfigKey.ORGANIZATION_ADDRESS2.getKey());
    street = adr1 + " " + adr2;
    String cp = ConfigUtil.getConf(ConfigKey.ORGANIZATION_ZIPCODE.getKey());
    String city = ConfigUtil.getConf(ConfigKey.ORGANIZATION_CITY.getKey());
    city = cp + " " + city;

    PrintJob job = tk.getPrintJob(parent, title, props);
    if (job == null) {
      return;
    }

    Graphics g = job.getGraphics();
    g.setColor(Color.black);

    g.setFont(boldFont);
    g.drawString(MessageUtil.getMessage("standing.order.title"), marginLeft, 40);
    g.drawString(mandate.getRum(), 200, 40);

    g.setFont(smallFont);
    g.drawString(MessageUtil.getMessage("standing.order.authorization.info1"), 360, 40);
    g.drawString(MessageUtil.getMessage("standing.order.info1", new Object[]{cache.getStartOfYear(), cache.getEndOfYear()}), marginLeft, 60);
    g.drawString(MessageUtil.getMessage("standing.order.info2") + " " + ddCreditor.getFirmName(), marginLeft, 70);
    g.drawString(MessageUtil.getMessage("standing.order.debtor.info.title"), marginLeft, 91);//30

    g.drawRect(marginLeft, 100, 260, 65);

    g.setFont(normalFont);
    String org = dossier.getContact().getCompanyName();
    g.drawString(org == null ? dossier.getContact().getFirstnameName() : org, 35, 115);
    g.setFont(smallFont);
    g.drawString(String.valueOf(dossier.getId()), 255, 160);
    a = dossier.getContact().getAddress();

    if (a != null) {
      g.setFont(normalFont);
      g.drawString(a.getAdr1(), 35, 130);
      g.drawString(a.getAdr2(), 35, 145);
      g.drawString(a.getCdp() + " " + a.getCity(), 35, 160);
    }

    g.setFont(smallFont);
    g.drawString(MessageUtil.getMessage("standing.order.firm.info.title"), 310, 91); // 305
    g.drawRect(310, 100, 260, 65);

    g.setFont(normalFont);
    branchAddress = null;
    rib = dossier.getRib();
    if (branch == null) {
      if (rib != null) {
        branch = new BankBranchIO(dc).findId(rib.getBranchId());
      }
    }
    if (branch != null) {
      g.drawString(branch.getBank().getName(), 320, 115);
      branchAddress = branch.getAddress();
    }

    if (branchAddress != null) {
      g.drawString(branchAddress.getAdr1(), 320, 130);
      g.drawString(branchAddress.getAdr2(), 320, 145);
      g.drawString(branchAddress.getCdp() + " " + branchAddress.getCity(), 320, 160);
    }
    g.setFont(boldFont);
    g.drawString(MessageUtil.getMessage("standing.order.debtor.account.title"), marginLeft, 190);

    g.setFont(smallFont);
    g.drawString(MessageUtil.getMessage("standing.order.creditor.info.title"), 320, 190);

    g.setFont(boldFont);
//    g.drawString(firmName, 320, 235);
    g.drawString(ddCreditor.getFirmName(), 320, 205);
    g.drawString(street, 320, 220);
    g.drawString(city, 320, 235);

    /*if (dossier == null) {
      drawRib(g, 25, 200, null, null);
    } else {
      drawRib(g, 25, 200, rib, branch);
    }*/
    drawRib(g, marginLeft, 200, rib, branch);

    g.setFont(smallFont);
    g.drawString("Date :", marginLeft, 235);
    g.drawString("Signature :", 100, 235);

    /* ajout montant et dates echeances */
    String where = "WHERE echeance >= '" + cache.getStartOfYear()
            + "' AND echeance <='" + cache.getEndOfYear()
            + "' AND payeur = " + dossier.getId()
            + " AND reglement = 'PRL'"
            + " AND transfert = 'f'"
            + " GROUP BY echeance ORDER BY echeance;";
    TreeMap<DateFr, String> prelevements = OrderLineIO.findPrl(where, dc);
    Set<Map.Entry<DateFr, String>> prlSet = prelevements.entrySet();
    g.setFont(smallFont);
    int i = 0;
    int x = marginLeft;
    int y = 0;
    String amountTitle = MessageUtil.getMessage("standing.order.amount.title");
    for (Map.Entry<DateFr, String> entry : prlSet) {

      if (i % 3 == 0) {
        y += 10;
        x = marginLeft;
      }
      int virgule_idx = entry.getValue().length() - 2;
      g.drawString("LE " + entry.getKey(), x, mgm + y);

      if (detailed) {
        g.drawString(amountTitle + " " + entry.getValue().substring(0, virgule_idx) + "," + entry.getValue().substring(virgule_idx), x + 50, mgm + y);
      } else {
        g.drawString(amountTitle + "                 ", x + 50, mgm + y);
      }
      x += 150;
      i++;
    }
    mgm = 320;
    // Direct debit recurrence type
    g.setFont(boldFont);
    g.drawString(BundleUtil.getLabel("Direct.debit.type.of.payment.label"), marginLeft, mgm);
//		Rectangle recurRect = new Rectangle(25, mgm + 8, 8, 8);
    g.setFont(normalFont);
    g.drawRect(marginLeft, mgm + 8, 8, 8);
    g.drawString(BundleUtil.getLabel("Recurrent.label"), marginLeft + 12, mgm + 16);
    g.drawRect(150, mgm + 8, 8, 8);
    g.drawString(BundleUtil.getLabel("Direct.debit.OOFF.label"), 150 + 12, mgm + 16);
    if (mandate.isRecurrent()) {
//			g.fillRect(25, mgm + 8, 8, 8);
      g.drawLine(marginLeft, mgm + 8, marginLeft + 8, mgm + 16);
      g.drawLine(marginLeft + 8, mgm + 8, marginLeft, mgm + 16);
    } else {
//			g.fillRect(150, mgm + 8, 8, 8);
      g.drawLine(150, mgm + 8, 150 + 8, mgm + 16);
      g.drawLine(150 + 8, mgm + 8, 150, mgm + 16);
    }

    g.setFont(smallFont);
    g.drawString(MessageUtil.getMessage("standing.order.info3"), marginLeft, mgm + 30);
    g.drawString(MessageUtil.getMessage("standing.order.info4"), marginLeft, mgm + 40);
    g.drawString(MessageUtil.getMessage("standing.order.info5"), marginLeft, mgm + 50);

    mgm += 190;
    // ligne de séparation
    g.drawLine(marginLeft, mgm - 5, 570, mgm - 5);

    g.setFont(boldFont);
    g.drawString(MessageUtil.getMessage("standing.order.authorization.title"), marginLeft, mgm + 10);

    g.drawString(mandate.getRum(), 200, mgm + 10);

    g.setFont(smallFont);
//    g.drawString(MessageUtil.getMessage("standing.order.authorization.info1"), 250, mgm + 10);
    g.drawString(MessageUtil.getMessage("standing.order.authorization.info1"), 360, mgm + 10);
    g.drawString(MessageUtil.getMessage("standing.order.authorization.info2", ddCreditor.getFirmName()), marginLeft, mgm + 20);

    g.setFont(smallFont);
    i = 0;
    x = marginLeft;
    y = 20;
    for (Map.Entry<DateFr, String> entry : prlSet) {
      if (i % 3 == 0) {
        y += 10;
        x = marginLeft;
      }
      int virgule_idx = entry.getValue().length() - 2;
      g.drawString("LE " + entry.getKey(), x, mgm + y);
      if (detailed) {
        g.drawString(amountTitle + " " + entry.getValue().substring(0, virgule_idx) + "," + entry.getValue().substring(virgule_idx), x + 50, mgm + y);
      } else {
        g.drawString(amountTitle + "                 ", x + 50, mgm + y);
      }
      x += 150;
      i++;
    }

    g.setFont(smallFont);
    int topY = mgm + 80;
    g.drawRect(marginLeft, topY, 260, 65);
    g.drawString(MessageUtil.getMessage("standing.order.debtor.info.title"), marginLeft, topY - 5);
    
    g.setFont(boldFont);
    g.drawString(org == null ? dossier.getContact().getFirstnameName() : org, 35, topY + 15);
    g.setFont(smallFont);
    g.drawString(String.valueOf(dossier.getId()), 245, topY + 60);
    if (a != null) {
      g.setFont(normalFont);
      g.drawString(a.getAdr1(), 35, topY + 30);
      g.drawString(a.getAdr2(), 35, topY + 45);
      g.drawString(a.getCdp() + " " + a.getCity(), 35, topY + 60);
    }

    g.setFont(smallFont);
    g.drawString(MessageUtil.getMessage("standing.order.creditor.info.title"), 300, topY - 5);
    g.drawRect(300, topY, 260, 65);

    g.setFont(boldFont);
    g.drawString(ddCreditor.getFirmName(), 310, topY + 15);
    g.drawString(street, 310, topY + 30);
    g.drawString(city, 310, topY + 60);

    topY += 75;
    g.setFont(smallFont);
    g.drawString(MessageUtil.getMessage("standing.order.info6"), marginLeft, topY);
    g.drawString(MessageUtil.getMessage("standing.order.info7", ddCreditor.getFirmName()), marginLeft, topY + 10);

    g.setFont(smallFont);
    g.drawString(MessageUtil.getMessage("standing.order.account.title"), marginLeft, topY + 20);

    g.setFont(smallFont);
    g.drawString(MessageUtil.getMessage("standing.order.address.info.title1"), 310, topY + 20);
    g.drawString(MessageUtil.getMessage("standing.order.address.info.title2"), 310, topY + 30);

    g.setFont(normalFont);
    if (branch != null) {
      g.drawString(branch.getBank().getName(), 310, topY + 45);
    }
    if (branchAddress != null) {
      g.drawString(branchAddress.getAdr1(), 310, topY + 60);
      g.drawString(branchAddress.getAdr2(), 310, topY + 75);
      g.drawString(branchAddress.getCdp() + " " + branchAddress.getCity(), 310, topY + 90);
    }

    /*if (dossier == null) {
      drawRib(g, 25, mgm + 170, null, null);
    } else {
      drawRib(g, 25, mgm + 170, dossier.getRib(), branch);
    }*/
    drawRib(g, marginLeft, topY +30, dossier.getRib(), branch);
    g.setFont(smallFont);
    g.drawString("Date :", marginLeft, topY + 65);
    g.drawString("Signature :", 100, topY + 65);

    topY += 105;
    g.setFont(boldFont);
    g.drawRect(marginLeft, topY, 190, 35);
//    g.drawString(MessageUtil.getMessage("standing.order.issuer.number"), 35, mgm + 265);
    g.drawString(BundleUtil.getLabel("ConfEditor.debiting.ics.tip"), 35, topY +15);
    g.drawString(ddCreditor.getIcs(), 35, topY + 30);

    g.setFont(smallFont);
    g.drawString(MessageUtil.getMessage("standing.order.info8"), 300, topY + 30);
    //g.drawString("en y joignant obligatoirement un relevé d'identité bancaire (R.I.B.) postal (RIP)",300,mgm+280);
    //g.drawString("ou caisse d'Epargne (R.I.C.E.).",300,mgm+290);

    //g.drawLine(25,mgm+295,570,mgm+295);
    g.dispose();
    job.end();
  }

  private void drawRib(Graphics g, int x, int y, Rib rib, BankBranch bb) {
    g.setFont(smallFont);
    g.setFont(smallFont.deriveFont(Font.BOLD));
    g.drawString(BundleUtil.getLabel("Iban.label") + " : ", x, y);
    g.drawString(BundleUtil.getLabel("Bic.code.label") + " : ", x + 170, y);

    g.setFont(smallFont.deriveFont(Font.PLAIN));
    int mTop = 5;
    g.drawString("Etablis", x, y + 8 + mTop);
    g.drawLine(x, y + 20 + mTop, x + 50, y + 20 + mTop);
    g.drawLine(x, y + 15 + mTop, x, y + 20 + mTop);
    g.drawLine(x + 10, y + 17 + mTop, x + 10, y + 20 + mTop);
    g.drawLine(x + 20, y + 17 + mTop, x + 20, y + 20 + mTop);
    g.drawLine(x + 30, y + 17 + mTop, x + 30, y + 20 + mTop);
    g.drawLine(x + 40, y + 17 + mTop, x + 40, y + 20 + mTop);
    g.drawLine(x + 50, y + 15 + mTop, x + 50, y + 20 + mTop);

    g.drawString("Guichet", x + 60, y + 8 + mTop);
    g.drawLine(x + 60, y + 20 + mTop, x + 110, y + 20 + mTop);
    g.drawLine(x + 60, y + 15 + mTop, x + 60, y + 20 + mTop);
    g.drawLine(x + 70, y + 17 + mTop, x + 70, y + 20 + mTop);
    g.drawLine(x + 80, y + 17 + mTop, x + 80, y + 20 + mTop);
    g.drawLine(x + 90, y + 17 + mTop, x + 90, y + 20 + mTop);
    g.drawLine(x + 100, y + 17 + mTop, x + 100, y + 20 + mTop);
    g.drawLine(x + 110, y + 15 + mTop, x + 110, y + 20 + mTop);

    g.drawString("N° de compte", x + 120, y + 8 + mTop);
    g.drawLine(x + 120, y + 20 + mTop, x + 230, y + 20 + mTop);
    g.drawLine(x + 120, y + 15 + mTop, x + 120, y + 20 + mTop);
    g.drawLine(x + 130, y + 17 + mTop, x + 130, y + 20 + mTop);
    g.drawLine(x + 140, y + 17 + mTop, x + 140, y + 20 + mTop);
    g.drawLine(x + 150, y + 17 + mTop, x + 150, y + 20 + mTop);
    g.drawLine(x + 160, y + 17 + mTop, x + 160, y + 20 + mTop);
    g.drawLine(x + 170, y + 17 + mTop, x + 170, y + 20 + mTop);
    g.drawLine(x + 180, y + 17 + mTop, x + 180, y + 20 + mTop);
    g.drawLine(x + 190, y + 17 + mTop, x + 190, y + 20 + mTop);
    g.drawLine(x + 200, y + 17 + mTop, x + 200, y + 20 + mTop);
    g.drawLine(x + 210, y + 17 + mTop, x + 210, y + 20 + mTop);
    g.drawLine(x + 220, y + 17 + mTop, x + 220, y + 20 + mTop);
    g.drawLine(x + 230, y + 15 + mTop, x + 230, y + 20 + mTop);

    g.drawString("Clé", x + 240, y + 8 + mTop);
    g.drawLine(x + 240, y + 20 + mTop, x + 260, y + 20 + mTop);
    g.drawLine(x + 240, y + 15 + mTop, x + 240, y + 20 + mTop);
    g.drawLine(x + 250, y + 17 + mTop, x + 250, y + 20 + mTop);
    g.drawLine(x + 260, y + 15 + mTop, x + 260, y + 20 + mTop);

    if (rib != null) {
      // iban
      g.setFont(mediumFont);
      g.drawString(rib.getIban() == null ? "" : formatIban(rib.getIban()), x + 23, y);
      // bic
      if (bb != null) {
        g.drawString(bb.getBicCode() == null ? "" : bb.getBicCode(), x + 211, y);
      }
      g.setFont(smallFont);
      char[] rEtab = rib.getEstablishment().toCharArray();
      g.drawChars(rEtab, 0, 1, x + 2, y + 17 + mTop);
      g.drawChars(rEtab, 1, 1, x + 12, y + 17 + mTop);
      g.drawChars(rEtab, 2, 1, x + 22, y + 17 + mTop);
      g.drawChars(rEtab, 3, 1, x + 32, y + 17 + mTop);
      g.drawChars(rEtab, 4, 1, x + 42, y + 17 + mTop);

      char[] rGuichet = rib.getBranch().toCharArray();
      g.drawChars(rGuichet, 0, 1, x + 62, y + 17 + mTop);
      g.drawChars(rGuichet, 1, 1, x + 72, y + 17 + mTop);
      g.drawChars(rGuichet, 2, 1, x + 82, y + 17 + mTop);
      g.drawChars(rGuichet, 3, 1, x + 92, y + 17 + mTop);
      g.drawChars(rGuichet, 4, 1, x + 102, y + 17 + mTop);

      char[] rCompte = rib.getAccount().toCharArray();
      g.drawChars(rCompte, 0, 1, x + 122, y + 17 + mTop);
      g.drawChars(rCompte, 1, 1, x + 132, y + 17 + mTop);
      g.drawChars(rCompte, 2, 1, x + 142, y + 17 + mTop);
      g.drawChars(rCompte, 3, 1, x + 152, y + 17 + mTop);
      g.drawChars(rCompte, 4, 1, x + 162, y + 17 + mTop);
      g.drawChars(rCompte, 5, 1, x + 172, y + 17 + mTop);
      g.drawChars(rCompte, 6, 1, x + 182, y + 17 + mTop);
      g.drawChars(rCompte, 7, 1, x + 192, y + 17 + mTop);
      g.drawChars(rCompte, 8, 1, x + 202, y + 17 + mTop);
      g.drawChars(rCompte, 9, 1, x + 212, y + 17 + mTop);
      g.drawChars(rCompte, 10, 1, x + 222, y + 17 + mTop);

      char[] cle = rib.getRibKey().toCharArray();
      g.drawChars(cle, 0, 1, x + 242, y + 17 + mTop);
      g.drawChars(cle, 1, 1, x + 252, y + 17 + mTop);

    }
  }

  private String formatIban(String iban) {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (; i < iban.length() - 3; i += 4) {
      sb.append(iban.substring(i, i + 4)).append(' ');
    }
    sb.append(iban.substring(i));
    return sb.toString();
  }
}
