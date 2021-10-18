/*
 * @(#)ExportDlg.java 2.15.8 22/03/18
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
package net.algem.edition;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.algem.config.ConfigUtil;
import net.algem.contact.*;
import net.algem.util.*;
import net.algem.util.jdesktop.DesktopHandler;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.jdesktop.DesktopOpenHandler;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.MessagePopup;

/**
 * Basic exportation dialog.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.8
 * @since 1.0a 14/12/1999
 */
public abstract class ExportDlg
  extends JDialog
  implements ActionListener {

  public static final String TEXT_FILTER_LABEL = MessageUtil.getMessage("filechooser.text.filter.label");
  protected static int MAX_TELS = 3;

  protected DataConnection dc;
  protected GemDesktop desktop;
  protected GemField fileName;
  protected GemButton btValidation;
  protected GemButton btCancel;
  protected JPanel buttons;
  protected JButton chooser;
  protected File file;
  protected JProgressBar progress;
  protected java.util.List<String> selectedOptions = new ArrayList<>();
  protected JCheckBox checkId, checkOrganization, checkCivility, checkName, checkFirstName, checKNickname, checkAddress, checkTels, checkEmail1, checkEmail2;

  public ExportDlg(GemDesktop desktop, String _title) {
    super(desktop.getFrame(), _title);
    this.desktop = desktop;
    init(DataCache.getDataConnection());
  }

  public ExportDlg(Dialog parent, String title) {
    super(parent, title);
    init(DataCache.getDataConnection());
  }

  public final void init(DataConnection dc) {
    this.dc = dc;

    btValidation = new GemButton(GemCommand.VALIDATION_CMD);
    btValidation.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemPanel footer = new GemPanel(new BorderLayout());
    progress = new JProgressBar();

    buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 1));
    buttons.add(btValidation);
    buttons.add(btCancel);

    footer.add(progress, BorderLayout.NORTH);
    footer.add(buttons, BorderLayout.SOUTH);

    fileName = new GemField(ConfigUtil.getExportPath() + FileUtil.FILE_SEPARATOR + getFileName() + ".csv", 30);
    GemPanel filePanel = new GemPanel(new FlowLayout(FlowLayout.LEFT));
    filePanel.setBorder(BorderFactory.createTitledBorder("Fichier"));
    filePanel.setPreferredSize(new Dimension(480, 80));
    filePanel.setMinimumSize(new Dimension(480, 60));
    filePanel.add(fileName);
    chooser = new JButton(GemCommand.BROWSE_CMD);
    chooser.setPreferredSize(new Dimension(chooser.getPreferredSize().width, fileName.getPreferredSize().height));
    chooser.addActionListener(this);
    filePanel.add(chooser);

    ItemListener optionsListener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          selectedOptions.add(((JCheckBox) e.getItem()).getActionCommand());
        } else {
          selectedOptions.remove(((JCheckBox) e.getItem()).getActionCommand());
        }
      }
    };
    JPanel checkPanel = new JPanel(new GridBagLayout());
    GridBagHelper gb0 = new GridBagHelper(checkPanel);

    checkPanel.setBorder(BorderFactory.createTitledBorder("Sélection"));
    checkPanel.setPreferredSize(new Dimension(480, 100));
    checkPanel.setMinimumSize(new Dimension(480, 80));
    checkId = new JCheckBox(BundleUtil.getLabel("Id.label"));
    checkId.setActionCommand("01-id");
    checkId.addItemListener(optionsListener);
    checkOrganization = new JCheckBox(BundleUtil.getLabel("Organization.label"));
    checkOrganization.setActionCommand("02-organization");
    checkOrganization.addItemListener(optionsListener);
    checkCivility = new JCheckBox(BundleUtil.getLabel("Person.civility.label"));
    checkCivility.setActionCommand("03-civility");
    checkCivility.addItemListener(optionsListener);
    checkName = new JCheckBox(BundleUtil.getLabel("Name.label"));
    checkName.setActionCommand("04-name");
    checkName.addItemListener(optionsListener);
    checkFirstName = new JCheckBox(BundleUtil.getLabel("First.name.label"));
    checkFirstName.setActionCommand("05-firstname");
    checkFirstName.addItemListener(optionsListener);
    checKNickname = new JCheckBox(BundleUtil.getLabel("Nickname.label"));
    checKNickname.setActionCommand("06-nickname");
    checKNickname.addItemListener(optionsListener);
    checkAddress = new JCheckBox(BundleUtil.getLabel("Address.label"));
    checkAddress.setActionCommand("07-address");
    checkAddress.addItemListener(optionsListener);
    checkTels = new JCheckBox(BundleUtil.getLabel("Telephones.label"));
    checkTels.setActionCommand("08-tels");
    checkTels.addItemListener(optionsListener);
    checkEmail1 = new JCheckBox(BundleUtil.getLabel("Email.label") + " 1");
    checkEmail1.setActionCommand("09-email1");
    checkEmail1.addItemListener(optionsListener);
    checkEmail2 = new JCheckBox(BundleUtil.getLabel("Email.label") + " 2");
    checkEmail2.setActionCommand("10-email2");
    checkEmail2.addItemListener(optionsListener);

    checkId.setSelected(true);
    checkName.setSelected(true);
    checkFirstName.setSelected(true);
    checkEmail1.setSelected(true);

    gb0.add(checkId, 0, 0, 1, 1, GridBagHelper.WEST);
    gb0.add(checkOrganization, 1, 0, 1, 1, GridBagHelper.WEST);
    gb0.add(checkCivility, 2, 0, 1, 1, GridBagHelper.WEST);
    gb0.add(checkName, 3, 0, 1, 1, GridBagHelper.WEST);
    gb0.add(checkFirstName, 4, 0, 1, 1, GridBagHelper.WEST);

    gb0.add(checKNickname, 0, 1, 1, 1, GridBagHelper.WEST);
    gb0.add(checkAddress, 1, 1, 1, 1, GridBagHelper.WEST);
    gb0.add(checkTels, 2, 1, 1, 1, GridBagHelper.WEST);
    gb0.add(checkEmail1, 3, 1, 1, 1, GridBagHelper.WEST);
    gb0.add(checkEmail2, 4, 1, 1, 1, GridBagHelper.WEST);

    JPanel customPanel = new JPanel(new GridBagLayout());
    customPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    GridBagHelper gb = new GridBagHelper(customPanel);
    gb.add(checkPanel, 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(filePanel, 0, 1, 1, 1, GridBagHelper.WEST);
    JPanel criterionPanel = getCriterion();

    criterionPanel.setBorder(BorderFactory.createTitledBorder("Options"));
    criterionPanel.setPreferredSize(new Dimension(480, criterionPanel.getPreferredSize().height));
    gb.add(criterionPanel, 0, 2, 1, 1, GridBagHelper.WEST);

    setLayout(new BorderLayout());
    add(customPanel, BorderLayout.CENTER);
    add(footer, BorderLayout.SOUTH);
    setLocation(100, 100);
//    setPreferredSize(new Dimension(520,380));
    pack();
  }

  public abstract GemPanel getCriterion();

  public abstract String getRequest();

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btCancel) {
      close();
    } else if (evt.getSource() == btValidation) {
//      file = new File(fileName.getText());
//      if (!writeFile()) {
//        return;
//      }
      btValidation.setEnabled(false);
      btCancel.setEnabled(false);
      new SwingWorker<Object, Object>() {

        @Override
        protected Void doInBackground() throws Exception {
          progress.setIndeterminate(true);
          validation();
          return null;
        }

        @Override
        public void done() {
          btValidation.setEnabled(true);
          btCancel.setEnabled(true);
          progress.setIndeterminate(false);
          //close();
        }

      }.execute();
    } else if (evt.getSource() == chooser) {
      JFileChooser fileChooser = getFileChooser(fileName.getText());
      int ret = fileChooser.showDialog(this, BundleUtil.getLabel("FileChooser.selection"));
      if (ret == JFileChooser.APPROVE_OPTION) {
        file = fileChooser.getSelectedFile();
        fileName.setText(file.getPath());
      }
    }

  }

  protected boolean writeFile() {
    if (file == null) {
      return true;
    }
    return FileUtil.confirmOverWrite(this, file);
  }

  public static JFileChooser getFileChooser(String file) {
    JFileChooser fileChooser = new JFileChooser((File) null);
    // File name extension comparisons are case insensitive
    FileNameExtensionFilter filter = new FileNameExtensionFilter(TEXT_FILTER_LABEL, "csv", "txt", "pnm", "htm", "html");
    fileChooser.setFileFilter(filter);
    fileChooser.setSelectedFile(new File(file));
    return fileChooser;
  }

  public static JFileChooser getFileChooser(String file, FileNameExtensionFilter filter) {
    JFileChooser fileChooser = new JFileChooser((File) null);
    fileChooser.setFileFilter(filter);
    fileChooser.setSelectedFile(new File(file));
    return fileChooser;
  }

  /**
   * Exports to csv file.
   */
  protected void validation() {
    String path = null;

    file = new File(fileName.getText());
    if (!writeFile()) {
      return;
    }
    if (file == null) {
      path = fileName.getText();
    } else {
      path = file.getPath();
    }

    String query = getRequest();

    List<Contact> contacts = ContactIO.find(query, true, dc);
    Collections.sort(selectedOptions);
    try (PrintWriter out = new PrintWriter(new File(path), "UTF-16LE")) {
      printHeader(out, selectedOptions);
      printLines(out, contacts, selectedOptions);

      MessagePopup.information(this, MessageUtil.getMessage("export.success.info", new Object[]{contacts.size(), path}));
      DesktopHandler handler = new DesktopOpenHandler();
      ((DesktopOpenHandler) handler).open(path);
    } catch (IOException e) {
      GemLogger.logException(query, e, this);
    } catch (DesktopHandlerException ex) {
      GemLogger.log(ex.getMessage());
      MessagePopup.warning(this, ex.getMessage());
    }

  }

  public static void printHeader(PrintWriter out, java.util.List<String> selectedOptions) {
    boolean single = selectedOptions.size() == 1;
    StringBuilder sb = new StringBuilder();

    for (String key : selectedOptions) {
      switch (key) {
        case "01-id": {
          sb.append(BundleUtil.getLabel("Id.label")).append(';');
          break;
        }
        case "02-organization": {
          sb.append(BundleUtil.getLabel("Organization.label")).append(';');
          break;
        }
        case "03-civility": {
          sb.append(BundleUtil.getLabel("Person.civility.label")).append(';');
          break;
        }
        case "04-name": {
          sb.append(BundleUtil.getLabel("Name.label")).append(';');
          break;
        }
        case "05-firstname": {
          sb.append(BundleUtil.getLabel("First.name.label")).append(';');
          break;
        }
        case "06-nickname": {
          sb.append(BundleUtil.getLabel("Nickname.label")).append(';');
          break;
        }
        case "07-address": {
          sb.append(BundleUtil.getLabel("Address1.label")).append(';');
          sb.append(BundleUtil.getLabel("Address2.label")).append(';');
          sb.append(BundleUtil.getLabel("Address.zip.code.label")).append(';');
          sb.append(BundleUtil.getLabel("City.label")).append(';');
          break;
        }
        case "08-tels": {
          for (int i = 0; i < MAX_TELS; i++) {
            sb.append(BundleUtil.getLabel("Telephone.label")).append(i + 1).append(';');
          }
          break;
        }

        case "09-email1": {
          sb.append(BundleUtil.getLabel("Email.label")).append(1).append(';');
          break;
        }
        case "10-email2": {
          sb.append(BundleUtil.getLabel("Email.label")).append(2).append(';');
          break;
        }
      }
    }
    if (single) {
      sb.deleteCharAt(sb.length() - 1);
    }
    out.println(sb.toString());
  }

  private void printLines(PrintWriter out, java.util.List<Contact> v, java.util.List<String> selectedOptions) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < v.size(); i++) {
      Contact c = v.get(i);
      List<Telephone> t = c.getTele();
      List<Email> emails = c.getEmail();

      boolean single = selectedOptions.size() == 1;
      for (String key : selectedOptions) {
        switch (key) {
          case "01-id": {
            sb.append(c.getId()).append(';');
            break;
          }
          case "02-organization": {
            if (c.getOrganization() == null || c.getOrganization().getName() == null) {
              sb.append(';');
            } else {
              sb.append(c.getOrganization()).append(';');
            }
            break;
          }
          case "03-civility": {
            sb.append(c.getGender() == null ? "" : c.getGender()).append(';');
            break;
          }
          case "04-name": {
            sb.append(c.getName()).append(';');
            break;
          }
          case "05-firstname": {
            sb.append(c.getFirstName()).append(';');
            break;
          }
          case "06-nickname": {
            sb.append(c.getNickName() == null ? "" : c.getNickName()).append(';');
            break;
          }
          case "07-address": {
            Address a = c.getAddress();
            if (a != null && !a.isArchive()) {//on tient compte de l'attribut archive
              sb.append(a.getAdr1() == null || "null".equalsIgnoreCase(a.getAdr1()) ? "" : a.getAdr1()).append(';');
              sb.append(a.getAdr2() == null || "null".equalsIgnoreCase(a.getAdr2()) ? "" : a.getAdr2()).append(';');
              sb.append(a.getCdp() == null || "null".equalsIgnoreCase(a.getCdp()) ? "" : a.getCdp()).append(';');
              sb.append(a.getCity() == null || "null".equalsIgnoreCase(a.getCity()) ? "" : a.getCity()).append(';');
            } else {
              sb.append(";;;;");
            }
            break;
          }
          case "08-tels": {
            int j = 0;
            if (t != null && t.size() > 0) {
              for (; j < t.size() && j < MAX_TELS; j++) {
                sb.append(t.get(j).getNumber()).append(';');
              }
            }
            while (j++ < MAX_TELS) {
              sb.append(";");
            }
            break;

          }
          case "09-email1": {
            if (emails != null && emails.size() > 0) {
              Email e = emails.get(0);
              sb.append(e.isArchive() ? "" : e.getEmail()).append(';');
            } else {
              sb.append(';');
            }
            break;
          }
          case "10-email2": {
            if (emails != null && emails.size() > 1) {
              Email e = emails.get(1);
              sb.append(e.isArchive() ? "" : e.getEmail()).append(';');
            } else {
              sb.append(';');
            }
            break;
          }
        }

      }
      if (single) {
        sb.deleteCharAt(sb.length() - 1);
      }
      out.println(sb.toString());
      sb.delete(0, sb.length());
    }
  }

  protected void close() {
    setVisible(false);
    dispose();
  }

  protected abstract String getFileName();

}
