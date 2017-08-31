/*
 * @(#) TrainingContractHistory.java Algem 2.15.0 30/08/2017
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
package net.algem.enrolment;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.course.Module;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import static net.algem.util.model.Model.Module;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.JTableModel;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 30/08/2017
 */
public class TrainingContractHistory
  extends FileTab
  implements ActionListener {

  private JTable table;
  private int idper;
  private JTableModel<TrainingContract> tableModel;
  private TrainingContractIO contractIO;
  private GemButton btDelete, btCreate, btEdit;
  private Enrolment lastEnrolment;

  public TrainingContractHistory(GemDesktop desktop, int idper, TrainingContractIO contractIO) {
    super(desktop);

    this.idper = idper;
    this.contractIO = contractIO;
  }

  public void createUI() {
    tableModel = new TrainingContractTableModel();
    table = new JTable(tableModel);
    table.setAutoCreateRowSorter(true);
    TableColumnModel cm = table.getColumnModel();
    cm.getColumn(0).setPreferredWidth(20);
    cm.getColumn(1).setPreferredWidth(150);
    cm.getColumn(2).setPreferredWidth(150);

    JScrollPane sp = new JScrollPane(table);
    JPanel mainPanel = new GemPanel(new BorderLayout());
    mainPanel.add(sp, BorderLayout.CENTER);

    setLayout(new BorderLayout());
    add(mainPanel, BorderLayout.CENTER);
    JPanel buttons = new JPanel(new GridLayout(1, 3));

    btDelete = new GemButton(GemCommand.DELETE_CMD);
    btDelete.addActionListener(this);
    btCreate = new GemButton(GemCommand.CREATE_CMD);
    btCreate.addActionListener(this);
    btEdit = new GemButton(GemCommand.VIEW_EDIT_CMD);
    btEdit.addActionListener(this);

    buttons.add(btDelete);
    buttons.add(btCreate);
    buttons.add(btEdit);

    add(buttons, BorderLayout.SOUTH);
  }

//  @Override
//  public void validation() {
//
//  }
//
//  @Override
//  public void cancel() {
//
//  }
  @Override
  public void load() {
    tableModel.clear();
    try {
      List<TrainingContract> contracts = contractIO.findAll(idper);
      for (TrainingContract t : contracts) {
        tableModel.addItem(t);
      }

    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  @Override
  public boolean isLoaded() {
    return tableModel.getRowCount() > 0;
  }

  public void setLastEnrolment(Enrolment lastEnrolment) {
    this.lastEnrolment = lastEnrolment;
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    Object src = e.getSource();
    if (btCreate == src) {
      TrainingContract c = new TrainingContract();
      if (lastEnrolment != null) {
        try {
          Module m = contractIO.getModuleInfo(lastEnrolment.getId());
          c.setTotal(m.getBasePrice());
          c.setLabel(m.getTitle());

          c.setInternalVolume(contractIO.getVolume(lastEnrolment.getId()));
        } catch (SQLException ex) {
          GemLogger.logException(ex);
        }
      }
      openContract(c);
    } else {
      int row = table.getSelectedRow();
      if (row < 0) {
        MessagePopup.warning(this, MessageUtil.getMessage("no.line.selected"));
        return;
      }
      if (btEdit == src) {
        TrainingContract c = tableModel.getItem(table.convertRowIndexToModel(row));
        openContract(c);
      } else if (btDelete == src) {

      }
    }
  }

  private void openContract(TrainingContract c) {
    TrainingContractEditor editor = new TrainingContractEditor(desktop, BundleUtil.getLabel("Training.contract.label"), false);
editor.createUI();
    editor.setContract(c);

  }

}
