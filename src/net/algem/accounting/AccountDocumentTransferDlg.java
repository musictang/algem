/*
 * @(#)AccountDocumentTransferDlg.java	2.15.9 07/06/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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
import java.util.List;
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
 * Transfer from a document number.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.9
 * @since 1.0a 27/09/2000
 */
public class AccountDocumentTransferDlg
        extends AccountTransferDlg
{

  private AccountDocumentTransferView view;

  public AccountDocumentTransferDlg(Frame parent, DataCache dataCache, AccountExportService exportService) {
    super(parent, dataCache, exportService);
    setDisplay();
  }

  private void setDisplay() {

    view = new AccountDocumentTransferView(dataCache);

    setLayout(new BorderLayout());
    setTitle(BundleUtil.getLabel("Menu.document.transfer.label"));

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
    p.add(view);

    add(p, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setSize(460,300);
    setLocation(200, 100);
    //pack();
  }

  @Override
  void transfer() {

    String payment = view.getModeOfPayment();

    List<OrderLine> orderLines = getOrderLines(
      payment,
      view.getDateStart(),
      view.getDateEnd(),
      view.getDocument(),
      view.withUnpaid()
    );
    if (orderLines.size() <= 0) {
      MessagePopup.information(this, MessageUtil.getMessage("payment.transfer.empty.collection"));
      return;
    }
    int errors = 0;
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    try {
      String codeJournal = "";
      Account documentAccount = exportService.getDocumentAccount(payment);
      if (documentAccount != null) {
        codeJournal = exportService.getCodeJournal(documentAccount.getId());
      }
      String path = filePath.getText();

      if (view.withCSV()) {
        path = path.replace(".txt", ".csv");
        exportService.exportCSV(path, orderLines);
        List<String> errorsCSV = exportService.exportCSV(path, orderLines);
        if (errorsCSV.size() > 0) {
          writeErrorLog(errorsCSV, path + ".log");
          MessagePopup.warning(this, MessageUtil.getMessage("payment.transfer.error.log.warning", new Object[] {errorsCSV.size(), path + ".log"}));
        }
      } else {
        if (ModeOfPayment.FAC.toString().equalsIgnoreCase(payment)) {
          errors = exportService.tiersExport(path, orderLines);
        } else {
          // if transfer is native, filter payment orderlines
          orderLines = filter(orderLines);
          exportService.export(path, orderLines, codeJournal, documentAccount);
        }
        updateTransfer(orderLines);
      }
      int transfered = orderLines.size() - errors;
      String msgKey = transfered > 1 ? "payment.transfer.info" : "payment.single.transfer.info";
      MessagePopup.information(this, MessageUtil.getMessage(msgKey, new Object[]{transfered, path}));
    } catch (IOException | SQLException ex) {
      GemLogger.logException(MessageUtil.getMessage("payment.transfer.exception"), ex, this);
    }
    setCursor(Cursor.getDefaultCursor());
  }

  private List<OrderLine> getOrderLines(String modeOfPayment, DateFr start, DateFr end, String document, boolean unpaid) {

    String query = "WHERE echeance >= '" + start + "' AND echeance <= '" + end
      + "' AND piece = '" + document
      + "' AND reglement = '" + modeOfPayment + "'";
    if (!unpaid) {
      query += " AND paye = 't'";
    }
    query += " AND transfert = 'f'";
    // DO NOT export if no invoice is present
    if (ModeOfPayment.FAC.name().equals(modeOfPayment)) {
      query += " AND facture IS NOT NULL AND facture != ''";
    }
    return OrderLineIO.find(query, dc);
  }
}
