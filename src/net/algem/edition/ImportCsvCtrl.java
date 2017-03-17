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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

/**
 * String encodedWithISO88591 = "Ã¼zÃ¼m baÄlarÄ±";
  String decodedToUTF8 = new String(encodedWithISO88591.getBytes("ISO-8859-1"), "UTF-8");
//Result, decodedToUTF8 --> "üzüm bağları"
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.1
 * @since 2.12.1 15/03/2017
 */
public class ImportCsvCtrl
        extends JDialog
        implements ActionListener
{

  private static final short COLS = 12;

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

  private List<String> importHeader = new ArrayList<>();
  JComboBox[] combos;
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
  }

  public ImportCsvCtrl(Frame owner, boolean modal, String[] header, List<String> model) {
    super(owner, modal);
    this.importHeader.add(0, "["+BundleUtil.getLabel("Import.header.no.match.label")+"]");
    this.importHeader.addAll(Arrays.asList(header));
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

    combos = new JComboBox[COLS];
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
    add(filePanel, BorderLayout.NORTH);

    GemPanel mp = new GemPanel();
    mp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    mp.add(configPanel);

    add(mp, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);

    setSize(640, 540);
    setLocation(100, 100);
    setVisible(true);
  }

  public void setPreview() throws IOException {
    importHeader.add(0, "["+BundleUtil.getLabel("Import.header.no.match.label")+"]");
    importHeader.addAll(Arrays.asList(importHandler.getHeader()));
    this.model = importHandler.getPreview();

    clearConfigPanel();
    idLabel = new JLabel(BundleUtil.getLabel("Number.label"));
    titleLabel = new JLabel(BundleUtil.getLabel("Person.civility.label"));
    nameLabel = new JLabel(BundleUtil.getLabel("Name.label"));
    firstNameLabel = new JLabel(BundleUtil.getLabel("First.name.label"));
    streetLabel = new JLabel(BundleUtil.getLabel("Address1.label"));
    additionalAddressLabel = new JLabel(BundleUtil.getLabel("Address2.label"));
    zipCodeLabel = new JLabel(BundleUtil.getLabel("Zipcode.label"));
    cityLabel = new JLabel(BundleUtil.getLabel("City.label"));
    homePhoneLabel = new JLabel(BundleUtil.getLabel("Home.phone.label"));
    mobilePhoneLabel = new JLabel(BundleUtil.getLabel("Mobile.phone.label"));
    email1Label = new JLabel(BundleUtil.getLabel("Email.label"));
    email2Label = new JLabel(BundleUtil.getLabel("Email.label"));

    GridBagHelper gb = new GridBagHelper(configPanel);
    gb.add(new JLabel("Algem"), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel("Correspondance"), 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel("Aperçu"), 2, 0, 1, 1, GridBagHelper.WEST);
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
      combos[i] = new JComboBox(importHeader.toArray());
      combos[i].addActionListener(cbListener);
      gb.add(combos[i], 1, i + 1, 1, 1);
    }
    int def_preview_width = 200;
    for (int i = 0; i < COLS; i++) {
      preview[i] = new JLabel();
      preview[i].setPreferredSize(new Dimension(def_preview_width, 20));
      gb.add(preview[i], 2, i + 1, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    }
    revalidate();

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
      fileName.setText(file.getPath());
      Charset c = getCharset(file);
      try {
        importHandler.setReader(file.getPath(), c);

        setPreview();
      } catch (IOException ex) {
        GemLogger.logException(ex);
      }
    }

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
    if (combos != null) {
      for (int i = 0; i < combos.length; i++) {
        if (combos[i] != null) {
          combos[i].removeActionListener(cbListener);
          combos[i] = null;
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
      assert (combos.length == preview.length);
      int index = 0;
      for (int i = 0; i < combos.length; i++) {
        JComboBox c = combos[i];
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
