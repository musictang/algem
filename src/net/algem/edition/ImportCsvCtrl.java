/*
 * @(#) ImportCsvCtrl.java Algem 2.12.1 15/03/2017
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
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import net.algem.util.BundleUtil;
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.SimpleCharsetDecoder;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.Truncate;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvListReader;

/**
 * String encodedWithISO88591 = "Ã¼zÃ¼m baÄlarÄ±";
 * String decodedToUTF8 = new String(encodedWithISO88591.getBytes("ISO-8859-1"), "UTF-8");
 * //Result, decodedToUTF8 --> "üzüm bağları"
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.1
 * @since 2.12.1 15/03/2017
 */
public class ImportCsvCtrl
        extends JDialog
        implements ActionListener
{

  private static final short COLS = 12;
  static final String [] IMPORT_FIELDS = {
    BundleUtil.getLabel("Number.label"),
    BundleUtil.getLabel("Person.civility.label"),
    BundleUtil.getLabel("Name.label"),
    BundleUtil.getLabel("First.name.label"),
    BundleUtil.getLabel("Address1.label"),
    BundleUtil.getLabel("Address2.label"),
    BundleUtil.getLabel("Zipcode.label"),
    BundleUtil.getLabel("City.label"),
    BundleUtil.getLabel("Home.phone.label"),
    BundleUtil.getLabel("Mobile.phone.label"),
    BundleUtil.getLabel("Email.label") + " 1",
    BundleUtil.getLabel("Email.label") + " 2"
  };

  private JLabel idLabel;
  private JLabel titleLabel;
  private JLabel nameLabel;
  private JLabel firstNameLabel;
  private JLabel streetLabel;
  private JLabel additionalAddressLabel;
  private JLabel zipCodeLabel;
  private JLabel cityLabel;
  private JLabel homePhoneLabel;
  private JLabel mobilePhoneLabel;
  private JLabel email1Label;
  private JLabel email2Label;
  private GridBagHelper gb;
  
  private Map<String, Integer> importMap;

  private List<String> csvHeader = new ArrayList<>();
  private JComboBox[] matchingBoxes;
  private JLabel[] preview;
  private GemButton btOk;
  private GemButton btCancel;
  private GemButton btBrowse;
  private JTextField fileName;
  private List<String> model;
  private GemPanel configPanel;
  private ImportCsvHandler importHandler;
  private ActionListener cbListener;

  public ImportCsvCtrl(Frame owner, boolean modal, ImportCsvHandler handler) {
    this.importHandler = handler;
    cbListener = new PreviewCsvFieldListener();
    importMap = new HashMap<>();
    for (String f : IMPORT_FIELDS) {
      importMap.put(f, -1);
    }
  }

  public ImportCsvCtrl(Frame owner, boolean modal, String[] header, List<String> model) {
    super(owner, modal);
    this.csvHeader.add(0, "["+BundleUtil.getLabel("Import.header.no.match.label")+"]");
    this.csvHeader.addAll(Arrays.asList(header));
    this.model = model;
    System.out.println(model);
  }

  public void createUI() {
    setLayout(new BorderLayout());
    GemPanel filePanel = new GemPanel();
    JLabel fileLabel = new JLabel(BundleUtil.getLabel("File.label"));
    fileName = new JTextField(20);
    btBrowse = new GemButton(GemCommand.BROWSE_CMD);
    btBrowse.addActionListener(this);
    filePanel.add(fileLabel);
    filePanel.add(fileName);
    filePanel.add(btBrowse);

    matchingBoxes = new JComboBox[COLS];
    preview = new JLabel[COLS];
    GemPanel buttons = new GemPanel(new GridLayout(1, 2));
    btOk = new GemButton(GemCommand.OK_CMD);
    btOk.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    buttons.add(btOk);
    buttons.add(btCancel);
    configPanel = new GemPanel(new GridBagLayout());
    configPanel.setBorder(BorderFactory.createTitledBorder("Configurer"));
    gb = new GridBagHelper(configPanel);
  
    add(filePanel, BorderLayout.NORTH);

    GemPanel mp = new GemPanel();
    mp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    mp.add(configPanel);

    add(mp, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);

    setSize(905, 540);
    setLocation(100, 100);
    setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == btCancel) {
      setVisible(false);
      dispose();
    } else if (src == btBrowse) {
      File file = FileUtil.getFile(
              this,
              BundleUtil.getLabel("FileChooser.selection"),
              null,
              MessageUtil.getMessage("filechooser.csv.filter.label"),
              "csv", "CSV");
      if (file != null) { // if cancelled
        fileName.setText(file.getPath());
        Charset c = getCharset(file);
        try {
          importHandler.setOptions(file.getPath(), c);
          //importHandler.setReader(file.getPath(), c);
          loadPreview();
        } catch (IOException ex) {
          GemLogger.logException(ex);
        }
      }
    } else if (src == btOk) {
      try {
        importCsv();
      } catch (IOException ex) {
        GemLogger.logException(ex);
      }
    }

  }

  private void loadPreview() throws IOException {
    csvHeader.clear();
    ICsvListReader listReader = importHandler.getReader();
    csvHeader.add(0, "["+BundleUtil.getLabel("Import.header.no.match.label")+"]");
    csvHeader.addAll(Arrays.asList(listReader.getHeader(true)));
    
    this.model = importHandler.getPreview(listReader);

    clearConfigPanel();
    idLabel = new JLabel(IMPORT_FIELDS[0]);
    titleLabel = new JLabel(IMPORT_FIELDS[1]);
    nameLabel = new JLabel(IMPORT_FIELDS[2]);
    firstNameLabel = new JLabel(IMPORT_FIELDS[3]);
    streetLabel = new JLabel(IMPORT_FIELDS[4]);
    additionalAddressLabel = new JLabel(IMPORT_FIELDS[5]);
    zipCodeLabel = new JLabel(IMPORT_FIELDS[6]);
    cityLabel = new JLabel(IMPORT_FIELDS[7]);
    homePhoneLabel = new JLabel(IMPORT_FIELDS[8]);
    mobilePhoneLabel = new JLabel(IMPORT_FIELDS[9]);
    email1Label = new JLabel(IMPORT_FIELDS[10]);
    email2Label = new JLabel(IMPORT_FIELDS[11]);

    gb.add(new JLabel("<html><b>Algem</b></html>"), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel("<html><b>"+BundleUtil.getLabel("Matching.label")+"<html><b>"), 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel("<html><b>"+BundleUtil.getLabel("Preview.label")+"<html><b>"), 2, 0, 1, 1, GridBagHelper.WEST);
    gb.add(idLabel, 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(titleLabel, 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(nameLabel, 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(firstNameLabel, 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(streetLabel, 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(additionalAddressLabel, 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(zipCodeLabel, 0, 7, 1, 1, GridBagHelper.WEST);
    gb.add(cityLabel, 0, 8, 1, 1, GridBagHelper.WEST);
    gb.add(homePhoneLabel, 0, 9, 1, 1, GridBagHelper.WEST);
    gb.add(mobilePhoneLabel, 0, 10, 1, 1, GridBagHelper.WEST);
    gb.add(email1Label, 0, 11, 1, 1, GridBagHelper.WEST);
    gb.add(email2Label, 0, 12, 1, 1, GridBagHelper.WEST);

    for (int i = 0; i < COLS; i++) {
      matchingBoxes[i] = new JComboBox(csvHeader.toArray());
      matchingBoxes[i].addActionListener(cbListener);
      gb.add(matchingBoxes[i], 1, i + 1, 1, 1);
    }
    int def_preview_width = 200;
    for (int i = 0; i < COLS; i++) {
      preview[i] = new JLabel();
      preview[i].setPreferredSize(new Dimension(def_preview_width, 20));
      gb.add(preview[i], 2, i + 1, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    }
    revalidate();
  }
  
  private void importCsv() throws IOException {
    for (int i = 0 ; i < IMPORT_FIELDS.length ; i++) {
      int index = matchingBoxes[i].getSelectedIndex();
      importMap.put(IMPORT_FIELDS[i], index  == 0 ? -1 : index -1);
    }
    System.out.println(importMap);
    importHandler.create(buildProcessors(csvHeader), importMap);   
  }
  
  private CellProcessor[] buildProcessors(List<String> header) {
    final CellProcessor[] processors = new CellProcessor[header.size()-1];
    for (int i=0 ; i < IMPORT_FIELDS.length; i++) {
      int idx = importMap.get(IMPORT_FIELDS[i]);
      if (idx > -1) {
        switch(i) {
          case 0: processors[idx] = new ParseInt(); break; // id
          case 1: processors[idx] = new ConvertNullTo("\"\"", new Trim(new Truncate(4)));break; // title
          case 2: processors[idx] = new NotNull(new Trim(new Truncate(32)));break; // lastName
          case 3: processors[idx] = new ConvertNullTo("\"\"",new Trim(new Truncate(32)));break; // firstName
          case 4: processors[idx] = new Optional(new Trim(new Truncate(50)));// adr1
          case 5: processors[idx] = new Optional(new Trim(new Truncate(50)));// adr2
          case 6: processors[idx] = new Optional(new Trim(new StrMinMax(0, 5)));// cdp
          case 7: processors[idx] = new Optional(new Trim(new Truncate(50)));// ville
//          case 8: processors[idx] = new ParseInt(); break; // id
        }
        System.out.println(processors);
      }
    }
    for (CellProcessor cp : processors) {
      if (cp == null) {
        cp = new Optional();
      }
    }
    return processors;
//    for (int i = 0 ; i < csvHeader.size(); i++) {
//      switch(i) {
//        case 
//    processors[importMap.get(IMPORT_FIELDS[0])] = new ParseInt();// id
//    processors[importMap.get(IMPORT_FIELDS[1])] = new ConvertNullTo("\"\"", new Trim(new Truncate(4))); // title
//    processors[importMap.get(IMPORT_FIELDS[2])] = new NotNull(new Trim(new Truncate(32))); // lastName
//    processors[importMap.get(IMPORT_FIELDS[3])] = new ConvertNullTo("\"\"",new Trim(new Truncate(32))); // firstName
//    processors[importMap.get(IMPORT_FIELDS[4])] = new Optional(new Trim(new Truncate(50)));// adr1
//    processors[importMap.get(IMPORT_FIELDS[5])] = new Optional(new Trim(new Truncate(50)));// adr2
//    processors[importMap.get(IMPORT_FIELDS[7])] = new Optional(new Trim(new StrMinMax(0, 5)));// cdp
//    processors[importMap.get(IMPORT_FIELDS[8])] = new Optional(new Trim(new Truncate(50)));// ville
//    processors[importMap.get(IMPORT_FIELDS[8])] = new Optional(new Trim(new Truncate(50)));// ville
//      }
//    }
    
//    processors[importMap.get(IMPORT_FIELDS[0])] = new ParseInt();// id
//    for(Map.Entry<String, Integer> entry : importMap.entrySet()) {
//      System.out.printf("%s -> %d\n", entry.getKey(), entry.getValue());
//    }
//    final CellProcessor[] processors = new CellProcessor[]{
//      new ParseInt(), // customerNo (must be unique) // id
//      new ConvertNullTo("\"\""), // title
//      new NotNull(), // lastName
//      new ConvertNullTo("\"\""), // firsName
//      org.supercsv.cellprocessor.
//      org.supercsv.cellprocessor.constraint.
//      BundleUtil.getLabel("Address1.label"),
//    BundleUtil.getLabel("Address2.label"),
//    BundleUtil.getLabel("Zipcode.label"),
//    BundleUtil.getLabel("City.label"),
//    BundleUtil.getLabel("Home.phone.label"),
//    BundleUtil.getLabel("Mobile.phone.label"),
//    BundleUtil.getLabel("Email.label") + " 1",
//    BundleUtil.getLabel("Email.label") + " 2"
//      new ParseDate("dd/MM/yyyy")}; // birthDate
    
  }
  
  private Charset getCharset(File f) {
    try {
      String[] charsetsToBeTested = {"UTF-8", "windows-1252", "ISO-8859-1", "ISO-8859-15", "x-MacRoman"};
      SimpleCharsetDecoder cd = new SimpleCharsetDecoder();
      return cd.detectCharset(f, charsetsToBeTested);
    } catch (IOException ex) {
      GemLogger.logException(ex);
      return null;
    }
  }

  private void clearConfigPanel() {
    configPanel.removeAll();
    if (matchingBoxes != null) {
      for (int i = 0; i < matchingBoxes.length; i++) {
        if (matchingBoxes[i] != null) {
          matchingBoxes[i].removeActionListener(cbListener);
          matchingBoxes[i] = null;
        }
      }
    }
    if (preview != null) {
      for (int i = 0; i < preview.length; i++) {
        if (preview[i] != null) {
          preview[i] = null;
        }
      }
    }
  }

  class PreviewCsvFieldListener
          implements ActionListener
  {

    @Override
    public void actionPerformed(ActionEvent e) {
      JComboBox cb = (JComboBox) e.getSource();
      //System.out.println(cb.getSelectedIndex());
      assert (matchingBoxes.length == preview.length);
      int index = 0;
      for (int i = 0; i < matchingBoxes.length; i++) {
        JComboBox c = matchingBoxes[i];
        if (c == cb) {
          index = cb.getSelectedIndex();
          if (index > 0) {
            preview[i].setText(model.get(index - 1));
          } else {
            preview[i].setText(null);
          }
          break;
        }
      }

    }
  }

}
