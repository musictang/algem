/*
 * @(#)CommunAccountTransferDlg.java	2.11.3 16/11/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.MessagePopup;

/**
 * Commun transfer.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.3
 * @since 2.8.r 13/12/13
 */
public class CommunAccountTransferDlg
        extends AccountTransferDlg
{
  private AccountTransferView transferView;

  public CommunAccountTransferDlg(Frame parent, DataCache dataCache, AccountExportService exportService) {
    super(parent, dataCache, exportService);
    setDisplay();
  }

  private void setDisplay() {

    transferView = new AccountTransferView(dataCache);

    setLayout(new BorderLayout());

    GemPanel p = new GemPanel();
    p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

    GemPanel header = new GemPanel();
    header.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(header);

    gb.add(new JLabel(BundleUtil.getLabel("Menu.file.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(filePath, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(chooser, 2, 0, 1, 1, GridBagHelper.WEST);

    p.add(header);
    p.add(transferView);

    add(p, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setLocation(200, 100);
    pack();
  }

  @Override
  void transfer() {
    // mode of payment selected in dialog
    String modeOfPayment = transferView.getModeOfPayment();

    Vector<OrderLine> orderLines = getOrderLines(modeOfPayment);
    if (orderLines.size() <= 0) {
      MessagePopup.information(this, MessageUtil.getMessage("payment.transfer.empty.collection"));
      return;
    }

    int errors = 0;
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    try {
      String codeJournal = "";
      Account documentAccount = exportService.getDocumentAccount(modeOfPayment);
      if (documentAccount != null) {
        codeJournal = exportService.getCodeJournal(documentAccount.getId());
      }
      String path = filePath.getText();

      if (transferView.withCSV()) {
        path = path.replace(".txt", ".csv");
        exportService.exportCSV(path, orderLines);
      } else {
        if (ModeOfPayment.FAC.toString().equalsIgnoreCase(modeOfPayment)) {
          errors = exportService.tiersExport(path, orderLines);
        } else {
          exportService.export(path, orderLines, codeJournal, documentAccount);
        }
        // maj transfer echeances
        updateTransfer(orderLines);
      }
      MessagePopup.information(this, MessageUtil.getMessage("payment.transfer.info", new Object[]{orderLines.size() - errors, path}));
    } catch (IOException ioe) {
      System.err.println(ioe.getMessage());
    } catch (SQLException sqe) {
      GemLogger.logException(MessageUtil.getMessage("payment.transfer.exception"), sqe, this);
    } finally {
      setCursor(Cursor.getDefaultCursor());
    }
  }

  protected Vector<OrderLine> getOrderLines(String modeOfPayment) {

    DateFr start = transferView.getDateStart();
    DateFr end = transferView.getDateEnd();
    int school = transferView.getSchool();

    String query = "WHERE echeance >= '" + start + "' AND echeance <= '" + end
            + "' AND ecole = '" + school
            + "' AND paye = 't' AND transfert = 'f' AND reglement = '" + modeOfPayment + "'";
    // les échéances de type prélèvement impliquent que le payeur ait un rib et qu'il existe en tant que contact.
    if (ModeOfPayment.PRL.name().equals(modeOfPayment)) {
      // TODO vérification iban/bic ??
      query += " AND payeur IN (SELECT idper FROM rib) AND payeur IN (SELECT id FROM personne)";
    }

    return OrderLineIO.find(query, dc);
  }
}
