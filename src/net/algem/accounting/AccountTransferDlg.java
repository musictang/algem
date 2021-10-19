/*
 * @(#)AccountTransferDlg.java	2.17.0 01/07/2019
 *                              2.15.9 07/06/18
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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import net.algem.Algem;
import net.algem.config.ConfigUtil;
import net.algem.edition.ExportDlg;
import net.algem.util.*;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * Dialog for transfering orderlines to a file readable by accounting software.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.0
 * @since 2.8.r 13/12/13
 */
public abstract class AccountTransferDlg
        extends JDialog
        implements ActionListener
{

  protected DataCache dataCache;
  protected DataConnection dc;
  protected AccountExportService exportService;
  protected GemButton btValidation;
  protected GemButton btCancel;
  protected JButton chooser;
  protected GemPanel buttons;
  protected GemField filePath;
  protected File file;

  /**
   * Empty constructor used for testing.
   */
  public AccountTransferDlg() {
  }

  public AccountTransferDlg(Frame parent, DataCache dataCache, AccountExportService exportService) {
    super(parent);
    this.dataCache = dataCache;
    dc = DataCache.getDataConnection();
    this.exportService = exportService;

    btValidation = new GemButton(GemCommand.VALIDATION_CMD);
    btValidation.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    buttons = new GemPanel(new GridLayout(1, 1));
    buttons.add(btValidation);
    buttons.add(btCancel);

    filePath = new GemField(ConfigUtil.getExportPath() + FileUtil.FILE_SEPARATOR + (Algem.isFeatureEnabled("cc-mdl") ? "WTAMC001.txt" : "export.txt"));
    filePath.setMinimumSize(new Dimension(200, filePath.getPreferredSize().height));
    chooser = new JButton(GemCommand.BROWSE_CMD);
    chooser.addActionListener(this);
  }

  void close() {
    setVisible(false);
    dispose();
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btCancel) {
      close();
    } else if (evt.getSource() == btValidation) {
      file = new File(filePath.getText());
      if (!FileUtil.confirmOverWrite(this, file)) {
        return;
      }
      transfer();
      close();
    } else if (evt.getSource() == chooser) {
      JFileChooser fileChooser = ExportDlg.getFileChooser(filePath.getText());
      int ret = fileChooser.showDialog(this, BundleUtil.getLabel("FileChooser.selection"));
      if (ret == JFileChooser.APPROVE_OPTION) {
        file = fileChooser.getSelectedFile();
        if (FileUtil.confirmOverWrite(this, file)) {
          filePath.setText(file.getPath());
        }
      }
    }
  }

  protected void updateTransfer(List<OrderLine> list) throws SQLException {
    if (MessagePopup.confirm(this,
            MessageUtil.getMessage("payment.transfer.confirm"),
            MessageUtil.getMessage("payment.transfer.confirm.title"))) {
      GemLogger.log(Level.INFO, "ComptaTransfertDlg");
      int n = list.size();
      for (int i = 0; i < n; i++) {
        OrderLine e = list.get(i);
        e.setTransfered(true);
        OrderLineIO.transfer(e, dc);
      }
    }
  }

  protected void writeErrorLog(List<String> errors, String path) throws IOException {
    if (errors.size() > 0) {
      try (PrintWriter logFile = new PrintWriter(new FileWriter(path))) {
        for (String e : errors) {
          logFile.println(e);
        }
      }
    }
  }

   /**
   * Filter lines with a personal account and no invoice association.
   * @param orderLines the lines to filter
   * @return a list of (possibly) filtered orderLines
   */
  protected List<OrderLine> filter(List<OrderLine> orderLines) {
    List<OrderLine> filtered = new ArrayList<>();
    for(OrderLine ol : orderLines) {
      if (!ModeOfPayment.FAC.name().equals(ol.getModeOfPayment())
        && AccountUtil.isPersonalAccount(ol.getAccount())
        && (ol.getInvoice() == null || ol.getInvoice().isEmpty())) {
        continue;
      }
      filtered.add(ol);
    }
    return filtered;
  }


  /**
   * Transfer order lines.
   */
  abstract void transfer();

}
