/*
 * @(#) TrainingAgreementHistory.java Algem 2.15.0 06/09/17
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.contact.PersonFile;
import net.algem.contact.member.Member;
import net.algem.course.Module;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 30/08/2017
 */
public class TrainingAgreementHistory
  extends FileTab
  implements ActionListener {

  private JTable table;
  private final PersonFile dossier;
  private TrainingAgreementTableModel tableModel;
  private final TrainingService trainingService;
  private GemButton btDelete, btCreate, btEdit;
  private Enrolment lastEnrolment;

  public TrainingAgreementHistory(GemDesktop desktop, PersonFile dossier, TrainingService service) {
    super(desktop);
    this.dossier = dossier;
    this.trainingService = service;
  }

  public void createUI() {
    tableModel = new TrainingAgreementTableModel();
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
    table.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          int row = table.getSelectedRow();
          if (row < 0) {
            return;
          }
          TrainingAgreement c = tableModel.getItem(table.convertRowIndexToModel(row));
          openAgreement(c);
        }
      }
    });

    buttons.add(btDelete);
    buttons.add(btCreate);
    buttons.add(btEdit);

    add(buttons, BorderLayout.SOUTH);
  }

  @Override
  public void load() {
    tableModel.clear();
    try {
      List<TrainingAgreement> contracts = trainingService.findAgreements(dossier.getId());
      for (TrainingAgreement t : contracts) {
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
      TrainingAgreement ta =  new TrainingAgreement();
      ta.setPersonId(dossier.getId());
      ta.setStart(dataCache.getStartOfYear().getDate());
      ta.setEnd(dataCache.getEndOfYear().getDate());
      ta.setSeason(dataCache.getStartOfYear().getYear() + "-" + dataCache.getEndOfYear().getYear());
      ta.setSignDate(new Date());
      if (lastEnrolment != null) {
        try {
          Module m = trainingService.getModule(lastEnrolment.getId());
          ta.setLabel(m.getTitle());
        } catch (SQLException ex) {
          GemLogger.logException(ex);
        }
      }
      Member m = dossier.getMember();
      if (m != null) {
        ta.setInsurance(m.getInsurance());
        ta.setInsuranceRef(m.getInsuranceRef());
      }
      
      openAgreement(ta);
    } else {
      int row = table.getSelectedRow();
      if (row < 0) {
        MessagePopup.warning(this, MessageUtil.getMessage("no.line.selected"));
        return;
      }
      TrainingAgreement c = tableModel.getItem(row);
      if (btEdit == src) {
        openAgreement(c);
      } else if (btDelete == src) {
        if (MessagePopup.confirm(this, MessageUtil.getMessage("action.delete.confirmation"))) {
          try {
            trainingService.deleteAgreement(c.getId());
            tableModel.deleteItem(c);
          } catch (SQLException ex) {
            GemLogger.logException(ex);
            MessagePopup.error(this, ex.getMessage());
          }
        }
      }
    }
  }

  private void openAgreement(TrainingAgreement c) {
    TrainingAgreementEditor editor = new TrainingAgreementEditor(this, trainingService, dossier, desktop);
    editor.createUI();
    editor.setAgreement(c);
  }

  void updateHistory(TrainingAgreement c, boolean creation) {
    if (creation) {
      tableModel.addItem(c);
    } else {
      int row = table.getSelectedRow();
      if (row >= 0) {
        tableModel.modItem(table.convertRowIndexToModel(row), c);
      }
    }
  }

}
