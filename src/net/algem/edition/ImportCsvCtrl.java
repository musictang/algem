/*
 * @(#) ImportCsvCtrl.java  3.0.0 04/10/2021
 *                          2.15.4 18/10/17
 *
 * Copyright (c) 2021 eric@productionlibre.fr. All Rights Reserved.
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
package net.algem.edition;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import net.algem.contact.ContactImport;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;
import net.algem.util.ui.ProgressMonitorHandler;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.Truncate;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvListReader;

/**
 *
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.4
 * @since 2.13.0 15/03/2017
 */
public class ImportCsvCtrl
  extends CardCtrl {

  private static final short COLS = 20;
  static final String[] IMPORT_FIELDS = {
    BundleUtil.getLabel("Number.abbrev.label"),
    BundleUtil.getLabel("Person.civility.label"),
    BundleUtil.getLabel("Name.label") + "*",
    BundleUtil.getLabel("First.name.label"),
    BundleUtil.getLabel("Birth.date.label"),
    BundleUtil.getLabel("Instrument.label"),
    BundleUtil.getLabel("Parent.number.label"),
    BundleUtil.getLabel("Parent.gender.label"),
    BundleUtil.getLabel("Parent.name.label"),
    BundleUtil.getLabel("Parent.first.name.label"),
    BundleUtil.getLabel("Address1.label"),
    BundleUtil.getLabel("Address2.label"),
    BundleUtil.getLabel("Zipcode.label"),
    BundleUtil.getLabel("City.label"),
    BundleUtil.getLabel("Home.phone.label"),
    BundleUtil.getLabel("Mobile.phone.label"),
    BundleUtil.getLabel("Email.label"),
    BundleUtil.getLabel("Parent.email.label"),
    BundleUtil.getLabel("Office.phone.label"),
    BundleUtil.getLabel("Note.label")
  };

  private Map<String, Integer> importMap;
  private ImportCsvHandler importCsvHandler;
  private List<String> csvHeader = new ArrayList<>();
  private GemButton btBrowse;
  private JTextField fileName;
  private ImportCsvPreview preview;
  private ImportCsvTablePreview tablePreview;
  private List<ContactImport> contacts;
  private ImportServiceImpl service;
  private JEditorPane help;
  private JComboBox<Charset> charsetBox;

  public ImportCsvCtrl(ImportCsvHandler handler) {
    this.importCsvHandler = handler;
    this.service = new ImportServiceImpl(DataCache.getDataConnection());
    importMap = new HashMap<>();
    for (String f : IMPORT_FIELDS) {
      importMap.put(f, -1);
    }
  }

  public ImportCsvCtrl(String[] header) {
    this.csvHeader.add(0, "[" + BundleUtil.getLabel("Import.header.no.match.label") + "]");
    this.csvHeader.addAll(Arrays.asList(header));
  }

  public void createUI() {
    GemPanel mp = new GemPanel();

    mp.setLayout(new BorderLayout());
    GemPanel filePanel = new GemPanel();
    JLabel fileLabel = new JLabel(BundleUtil.getLabel("File.label"));
    fileName = new JTextField(20);
    btBrowse = new GemButton(GemCommand.BROWSE_CMD);
    btBrowse.addActionListener(this);
    filePanel.add(fileLabel);
    filePanel.add(fileName);
    filePanel.add(btBrowse);

    charsetBox = new JComboBox(getCommonCharsets());
    charsetBox.addActionListener(this);
    JLabel encodingLabel = new JLabel(BundleUtil.getLabel("Encoding.label"));
    encodingLabel.setToolTipText(BundleUtil.getLabel("Import.encoding.tip"));
    filePanel.add(encodingLabel);
    filePanel.add(charsetBox);

    mp.add(filePanel, BorderLayout.NORTH);

    GemPanel helpPanel = new GemPanel();
    helpPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    try {
      URL url = getClass().getResource(FileUtil.DEFAULT_HELP_DIR + "/detail/import-csv.html");
      if (url != null) {
        help = new JEditorPane(url);
        help.setEditable(false);
        help.setPreferredSize(new Dimension(800, 505));
        helpPanel.add(help);
      }
    } catch (IOException ex) {
      GemLogger.logException(ex);
    }
    JScrollPane jsp = new JScrollPane(helpPanel);
    mp.add(jsp, BorderLayout.CENTER);

    preview = new ImportCsvPreview(COLS);
    preview.createUI();

    tablePreview = new ImportCsvTablePreview(new CsvContactTableModel());
    tablePreview.createUI();

    addCard(BundleUtil.getLabel("Import.csv.file.selection.label"), mp);
    addCard(BundleUtil.getLabel("Import.csv.matching.selection.label"), preview);
    addCard(BundleUtil.getLabel("Import.csv.preview.label"), tablePreview);
    select(0);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    super.actionPerformed(e);
    Object src = e.getSource();
    try {
      if (src == btBrowse) {
        File file = FileUtil.getFile(
                this,
                BundleUtil.getLabel("FileChooser.selection"),
                null,
                MessageUtil.getMessage("filechooser.csv.filter.label"),
                "csv", "CSV");
        if (file != null) { // if cancelled
          fileName.setText(file.getPath());
          importCsvHandler.setFile(file);
          Charset detectedCharset = FileUtil.getCharset(file);
          if (detectedCharset != null) {
            charsetBox.setSelectedItem(detectedCharset);
          } else {
            loadPreview();
          }
        }
      } else if (src == charsetBox) {
        importCsvHandler.setCharset((Charset) charsetBox.getSelectedItem());
        loadPreview();
      }
    } catch (IOException ex) {
      GemLogger.logException(ex);
    }

  }

  @Override
  public boolean next() {
    if (fileName.getText().isEmpty()) {
      MessagePopup.warning(this, MessageUtil.getMessage("file.none.selected"));
      select(0);
      return false;
    }
    select(step + 1);
    if (step == 2) {
      contacts = getContactsFromCsv();
      if (contacts != null) {
        tablePreview.load(contacts, importCsvHandler.getErrors());
      } else {
        select(1);
      }
    }
    return true;
  }

  @Override
  public boolean prev() {
    select(step - 1);
    return true;
  }

  @Override
  public boolean cancel() {
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
      return true;
    }
    return false;
  }

  @Override
  public boolean validation() {
    if (save()) {
      return cancel();
    }
    return false;
  }

  @Override
  public boolean loadId(int id) {
    return true;
  }

  @Override
  public boolean loadCard(Object p) {
    return true;
  }

  private void loadPreview() throws IOException {
    csvHeader.clear();
    ICsvListReader listReader = importCsvHandler.getReader();
    csvHeader.add(0, "[" + BundleUtil.getLabel("Import.header.no.match.label") + "]");
    csvHeader.addAll(Arrays.asList(listReader.getHeader(true)));

    List<String> model = importCsvHandler.getPreview(listReader);
    preview.reload(csvHeader, model);
  }

  private List<ContactImport> getContactsFromCsv() {
    try {
      preview.setMatchings(importMap);
      boolean hasMatching = false;
      for(Integer n : importMap.values()) {
        if (n > -1) {
          hasMatching = true;
          break;
        }
      }
      if (!hasMatching) {
        MessagePopup.error(this, MessageUtil.getMessage("import.matching.warning"));
        return null;
      }
      return importCsvHandler.create(buildProcessors(csvHeader), importMap);
    } catch (IOException ex) {
      GemLogger.logException(ex);
      return null;
    }
  }

  private CellProcessor[] buildProcessors(List<String> header) {
    final CellProcessor[] processors = new CellProcessor[header.size() - 1];
    for (int i = 0; i < IMPORT_FIELDS.length; i++) {
      int idx = importMap.get(IMPORT_FIELDS[i]);
      if (idx > -1) {
        switch (i) {
          case 0:
            processors[idx] = new ParseInt(new Trim());
            break; // id
          case 1:
            processors[idx] = new ConvertNullTo("\"\"", new Trim(new Truncate(4)));
            break; // title
          case 2:
            processors[idx] = new Trim(new Truncate(32)); //XXX manage null values
            break; // lastName *
          case 3:
            processors[idx] = new ConvertNullTo("\"\"", new Trim(new Truncate(32)));
            break; // firstName
          case 4:
            processors[idx] = new Optional(new ParseDate("dd/MM/yyyy"));
            break; // date of birth
          case 5:
            processors[idx] = new Optional(new Trim());
            break; // instrument
          case 6:
            processors[idx] = new ParseInt(new Trim());
            break; // parent id
          case 7:
            processors[idx] = new Optional(new ConvertNullTo("\"\"", new Trim(new Truncate(4))));
            break; // parent title
          case 8:
            processors[idx] = new Optional(new Trim(new Truncate(32)));
            break; // parent lastName
          case 9:
            processors[idx] = new Optional(new Trim(new Truncate(32)));
            break; // parent firstName
          case 10:
            processors[idx] = new Optional(new Trim(new Truncate(50)));// adr1
            break;
          case 11:
            processors[idx] = new Optional(new Trim(new Truncate(50)));// adr2
            break;
          case 12:
            processors[idx] = new Optional(new Trim(new StrMinMax(0, 5)));// cdp
            break;
          case 13:
            processors[idx] = new Optional(new Trim(new Truncate(50)));// ville
            break;
          case 14:
          case 15:
            processors[idx] = new Optional(new Trim(new Truncate(24)));// tel
            break;
          case 16:
          case 17:
            processors[idx] = new Optional(new Trim(new Truncate(64)));// email
            break;
          case 18:
            processors[idx] = new Optional(new Trim(new Truncate(24)));// tel
            break;
          case 19:
            processors[idx] = new Optional(new Trim(new Truncate(128)));// note
            break;
          default: processors[idx] = new Optional();
        }
      }
    }
    for (CellProcessor cp : processors) {
      if (cp == null) {
        cp = new Optional();
      }
    }
    return processors;

  }

  private boolean save() {
    if (contacts != null && contacts.size() > 0) {
      SwingWorker<Integer, Void> task = null;
      try {
        ProgressMonitor monitor = new ProgressMonitor(this, BundleUtil.getLabel("Importing.label"), "", 1, 100);
        monitor.setMillisToDecideToPopup(10);
        task = service.new ImportCsvTask(contacts);
        task.addPropertyChangeListener(new ProgressMonitorHandler(monitor, task));
        task.execute();
      } catch (Exception ex) {
        if (task != null) {task.cancel(true);}
        GemLogger.logException(ex);
        MessagePopup.error(this, ex.getMessage());
      }
      return true;
    }
    return false;
  }

  private Object[] getCommonCharsets() {
    try {
      return new Charset[]{
        Charset.forName("UTF-8"),
        Charset.forName("UTF-16LE"),
        Charset.forName("UTF-16BE"),
        Charset.forName("ISO-8859-1"),
        Charset.forName("ISO-8859-15"),
        Charset.forName("windows-1252")
      };
    } catch (IllegalCharsetNameException | UnsupportedCharsetException ex) {
      GemLogger.log(ex.getMessage());
      return Charset.availableCharsets().values().toArray();
    }

  }

}
