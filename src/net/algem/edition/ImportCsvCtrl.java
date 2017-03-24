/*
 * @(#) ImportCsvCtrl.java Algem 2.13.0 22/03/2017
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
package net.algem.edition;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import net.algem.contact.ContactImport;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.Truncate;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvListReader;

/**
 * String encodedWithISO88591 = "Ã¼zÃ¼m baÄlarÄ±";
 * String decodedToUTF8 = new String(encodedWithISO88591.getBytes("ISO-8859-1"), "UTF-8");
 * //Result, decodedToUTF8 --> "üzüm bağları"
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 * @since 2.13.0 15/03/2017
 */
public class ImportCsvCtrl
  extends CardCtrl {

  private static final short COLS = 15;
  static final String[] IMPORT_FIELDS = {
    BundleUtil.getLabel("Number.abbrev.label"),
    BundleUtil.getLabel("Person.civility.label"),
    BundleUtil.getLabel("Name.label"),
    BundleUtil.getLabel("First.name.label"),
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
    BundleUtil.getLabel("Parent.email.label")
  };

  private GemDesktop desktop;
  private Map<String, Integer> importMap;
  private ImportCsvHandler importCsvHandler;
  private List<String> csvHeader = new ArrayList<>();
  private GemButton btBrowse;
  private JTextField fileName;
  private ImportCsvPreview preview;
  private ImportCsvTablePreview tablePreview;
  private List<ContactImport> contacts;
  private ImportService service;
  private JEditorPane help;

  public ImportCsvCtrl(GemDesktop desktop, ImportCsvHandler handler) {
    this.desktop = desktop;
    this.importCsvHandler = handler;
    this.service = new ImportService(DataCache.getDataConnection());
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

    mp.add(filePanel, BorderLayout.NORTH);

    GemPanel helpPanel = new GemPanel();
    
    helpPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    try {
      URL url = getClass().getResource(FileUtil.DEFAULT_HELP_DIR + "/detail/import-csv.html");
      if (url != null) {
        help = new JEditorPane(url);
        help.setEditable(false);
        help.setPreferredSize(new Dimension(800, 400));
        helpPanel.add(help);
      }
    } catch (IOException ex) {
      GemLogger.logException(ex);
    }
    JScrollPane jsp = new JScrollPane(helpPanel);
    mp.add(jsp, BorderLayout.CENTER);

    preview = new ImportCsvPreview(COLS);
    preview.createUi();

    tablePreview = new ImportCsvTablePreview(new CsvContactTableModel());
    tablePreview.createUi();

    addCard(BundleUtil.getLabel("Import.csv.file.selection.label"), mp);
    addCard(BundleUtil.getLabel("Import.csv.matching.selection.label"), preview);
    addCard(BundleUtil.getLabel("Import.csv.preview.label"), tablePreview);
    select(0);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    super.actionPerformed(e);
    Object src = e.getSource();
    if (src == btBrowse) {
      File file = FileUtil.getFile(
        this,
        BundleUtil.getLabel("FileChooser.selection"),
        null,
        MessageUtil.getMessage("filechooser.csv.filter.label"),
        "csv", "CSV");
      if (file != null) { // if cancelled
        fileName.setText(file.getPath());
        try {
          importCsvHandler.setFile(file);
          loadPreview();
        } catch (IOException ex) {
          GemLogger.logException(ex);
        }
      }
    }

  }

  @Override
  public boolean next() {
    if (fileName.getText().isEmpty()) {
      MessagePopup.warning(this, "Aucun fichier sélectionné");
      select(0);
      return false;
    }
    select(step + 1);
    if (step == 2) {
      contacts = getContactsFromCsv();
      if (contacts != null && contacts.size() > 0) {
        tablePreview.load(contacts);
      } else {
        MessagePopup.warning(this, "Aucune correspondance détectée");
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
      System.out.println(importMap);
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
            processors[idx] = new ConvertNullTo("\"\"", new Trim(new Truncate(32)));
            break; // lastName
          case 3:
            processors[idx] = new ConvertNullTo("\"\"", new Trim(new Truncate(32)));
            break; // firstName
          case 4:
            processors[idx] = new ConvertNullTo("\"\"", new Trim(new Truncate(4)));
            break; // parent title
          case 5:
            processors[idx] = new ConvertNullTo("\"\"", new Trim(new Truncate(32)));
            break; // parent lastName
          case 6:
            processors[idx] = new ConvertNullTo("\"\"", new Trim(new Truncate(32)));
            break; // parent firstName  
          case 7:
            processors[idx] = new Optional(new Trim(new Truncate(50)));// adr1
          case 8:
            processors[idx] = new Optional(new Trim(new Truncate(50)));// adr2
          case 9:
            processors[idx] = new Optional(new Trim(new StrMinMax(0, 5)));// cdp
          case 10:
            processors[idx] = new Optional(new Trim(new Truncate(50)));// ville
//          case 8: processors[idx] = new ParseInt(); break; // id
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
      try {
        int n = service.importContacts(contacts);
        MessagePopup.information(this, MessageUtil.getMessage("contacts.imported", n));
        return true;
      } catch (Exception ex) {
        MessagePopup.error(this, ex.getMessage());
        GemLogger.logException(ex);
      }
    }
    return false;
  }

}
