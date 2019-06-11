/*
 * @(#)AtelierInstrumentsDialog.java 2.15.2 27/09/17
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
package net.algem.planning.editing.instruments;

import net.algem.config.Instrument;
import net.algem.contact.Person;
import net.algem.planning.editing.instruments.AtelierInstrumentsService.PersonInstrumentRow;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:alexandre.delattre.biz@gmail.com">Alexd</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.2
 * @since 2.9.2
 */
public class AtelierInstrumentsDialog
        extends JDialog
{

  {
    setupUI();
  }

  private void setupUI() {
    contentPane = new GemPanel(new BorderLayout());
    table1 = new JTable();
    JScrollPane scroll = new JScrollPane(table1);
    contentPane.add(scroll, BorderLayout.CENTER);
    contentPane.setPreferredSize(new Dimension(300, 200));
    GemPanel buttonPanel = new GemPanel(new GridLayout(1, 2));
    buttonOK = new JButton(GemCommand.VALIDATE_CMD);
    buttonPanel.add(buttonOK);
    buttonCancel = new JButton(GemCommand.CANCEL_CMD);
    buttonPanel.add(buttonCancel);
    contentPane.add(buttonPanel, BorderLayout.SOUTH);
  }

  public JComponent getRootComponent() {
    return contentPane;
  }

  interface Callback
  {

    void onOkSelected(List<PersonInstrumentRow> rows);
  }

  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JTable table1;

  private TableModel tableModel;
  private AtelierInstrumentsService instrumentsService;
  private Callback callback;

  static class TableModel
          extends AbstractTableModel
  {

    final List<PersonInstrumentRow> rows;
    String header[] = {BundleUtil.getLabel("Name.label"), BundleUtil.getLabel("Instrument.label")};

    public List<PersonInstrumentRow> getRows() {
      return rows;
    }

    TableModel(List<PersonInstrumentRow> rows) {
      this.rows = rows;
    }

    @Override
    public int getRowCount() {
      return rows.size();
    }

    @Override
    public int getColumnCount() {
      return 2;
    }

    @Override
    public String getColumnName(int col) {
      return header[col];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return columnIndex == 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      PersonInstrumentRow row = rows.get(rowIndex);
      switch (columnIndex) {
        case 0:
          return row.person;
        case 1:
          return row.instrument;
      }
      return row;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      if (columnIndex == 1) {
        PersonInstrumentRow row = rows.get(rowIndex);
        row.instrument = (Instrument) aValue;
      }
    }
  }

  class InstrumentCellEditor
          extends DefaultCellEditor
  {

    public InstrumentCellEditor() {
      super(new JComboBox<Instrument>());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      JComboBox<Instrument> component = (JComboBox<Instrument>) super.getTableCellEditorComponent(table, value, isSelected, row, column);
      Person person = (Person) table.getModel().getValueAt(row, 0);
      List<Instrument> availableInstruments = null;
      try {
        availableInstruments = instrumentsService.getAvailableInstruments(person);
        DefaultComboBoxModel<Instrument> aModel = new DefaultComboBoxModel<>();

        for (Instrument availableInstrument : availableInstruments) {
          aModel.addElement(availableInstrument);
        }
        component.setModel(aModel);
        component.setSelectedItem(table.getModel().getValueAt(row, 1));
      } catch (Exception e) {
        GemLogger.logException(e);
      }
      return component;
    }
  }

    public AtelierInstrumentsDialog(AtelierInstrumentsService instrumentsService, Callback callback) {
    this.instrumentsService = instrumentsService;
    this.callback = callback;
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    buttonOK.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        onOK();
      }
    });

    buttonCancel.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        onCancel();
      }
    });

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });

    contentPane.registerKeyboardAction(new ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        onCancel();
      }
    }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    setTitle(BundleUtil.getLabel("Course.instrument.allocation.label"));
  }

  void setData(List<PersonInstrumentRow> data) {
    this.tableModel = new TableModel(data);

    table1.setModel(tableModel);
    table1.getColumnModel().getColumn(1).setCellEditor(new InstrumentCellEditor());
  }

  private void onOK() {
    callback.onOkSelected(tableModel.getRows());
    dispose();
  }

  private void onCancel() {
    dispose();
  }

}
