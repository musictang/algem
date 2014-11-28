/*
 * @(#)AccountDocumentTransferDlg.java	2.9.1 27/11/14
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
package net.algem.accounting;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagLayout;
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
 * Transfer from a document number.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
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
    p.add(view);

    add(p, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setLocation(200, 100);
    pack();
  }

  @Override
  void transfer() {

    String payment = view.getModeOfPayment();

    Vector<OrderLine> orderLines = getOrderLines(payment);
    if (orderLines.size() <= 0) {
      MessagePopup.information(this, MessageUtil.getMessage("payment.transfer.empty.collection"));
      return;
    }
    int errors = 0;
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    try {

      /* String codeJournal = "";
       * String documentAccount = "";
       *
       * Compte c = getDocumentAccount(reglement);
       * if (c != null) {
       * codeJournal = getCodeJournal(c.getId());
       * documentAccount = c.getNumber();
       * } */

      /* if ("ESP".equalsIgnoreCase(reglement)) {
       * codeJournal = "CA";
       * documentAccount = "5300000000";
       * } else {
       * codeJournal = "CC";
       * documentAccount = "5120300000";
       * } */
      String codeJournal = "";
      Account documentAccount = exportService.getDocumentAccount(payment);
      if (documentAccount != null) {
        codeJournal = exportService.getCodeJournal(documentAccount.getId());
      }
      String path = filePath.getText();

      if (view.withCSV()) {
        path = path.replace(".txt", ".csv");
        exportService.exportCSV(path, orderLines);
      } else {
        if (ModeOfPayment.FAC.toString().equalsIgnoreCase(payment)) {
          errors = exportService.tiersExport(path, orderLines);
        } else {
          exportService.export(path, orderLines, codeJournal, documentAccount);
        }
        updateTransfer(orderLines);
      }
      MessagePopup.information(this, MessageUtil.getMessage("payment.transfer.info", new Object[]{orderLines.size() - errors, path}));
    } catch (Exception ex) {
      GemLogger.logException(MessageUtil.getMessage("payment.transfer.exception"), ex, this);
    }
    setCursor(Cursor.getDefaultCursor());
  }

  private Vector<OrderLine> getOrderLines(String payment) {

    DateFr start = view.getDateStart();
    DateFr end = view.getDateEnd();
    String document = view.getDocument();

    String query = "WHERE echeance >= '" + start + "' AND echeance <= '" + end
            + "' AND piece = '" + document
            + "' AND reglement = '" + payment
            + "' AND paye = 't' AND transfert = 'f'";
    return OrderLineIO.find(query, dc);
  }
}
