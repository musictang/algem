/*
 * @(#)ExportDlg.java 2.15.0 26/07/2017
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
package net.algem.edition;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
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
import net.algem.util.ui.MessagePopup;

/**
 * Basic exportation dialog.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 1.0a 14/12/1999
 */
public abstract class ExportDlg
        extends JDialog
        implements ActionListener
{

  public static final String TEXT_FILTER_LABEL = MessageUtil.getMessage("filechooser.text.filter.label");

  protected DataConnection dc;
  protected GemDesktop desktop;
  protected GemField fileName;
  protected GemButton btValidation;
  protected GemButton btCancel;
  protected JPanel buttons;
  protected JButton chooser;
  protected File file;
  protected JProgressBar progress;

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
    GemPanel pFile = new GemPanel();
    pFile.add(new Label(BundleUtil.getLabel("Menu.file.label")));
    pFile.add(fileName);
    chooser = new JButton(GemCommand.BROWSE_CMD);
    chooser.setPreferredSize(new Dimension(chooser.getPreferredSize().width, fileName.getPreferredSize().height));
    chooser.addActionListener(this);
    pFile.add(chooser);

    GemPanel body = new GemPanel(new BorderLayout());
    body.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    body.add(pFile, BorderLayout.NORTH);
    body.add(getCriterion(), BorderLayout.SOUTH);

    setLayout(new BorderLayout());
    add(body, BorderLayout.CENTER);
    add(footer, BorderLayout.SOUTH);
    setLocation(100,100);
    //setPreferredSize(new Dimension(520,260));
    pack();
  }

  public abstract GemPanel getCriterion();

  public abstract String getRequest();

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btCancel) {
      close();
    } else if (evt.getSource() == btValidation) {
      file = new File(fileName.getText());
      if (!writeFile()) {
        return;
      }
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
    int cpt = 0;
    int maxTels = 3;
    int maxEmails = 2;
    String path = null;
    if (file == null) {
      path = fileName.getText();
    } else {
      path = file.getPath();
    }
    String query = getRequest();

    PrintWriter out;
    try {
      /*out = new PrintWriter(new File(path), StandardCharsets.UTF_8.name());
      // force Byte Order Mark (BOM) : windows compatibility
      out.print("\ufeff");*/
      out = new PrintWriter(new File(path), "UTF-16LE");
      out.println(MessageUtil.getMessage("export.headers"));

      Vector<Contact> v = ContactIO.find(query, true, dc);
      for (int i = 0; i < v.size(); i++) {
        Contact c = v.elementAt(i);
        cpt++;
        out.print(c.getId() + ";");
        out.print(c.getOrgName() == null ? ";" : c.getOrgName() + ";");
        out.print(c.getGender() + ";");
        out.print(c.getName() + ";");
        out.print(c.getFirstName() + ";");
        out.print(c.getNickName() == null ? ";" : c.getNickName() + ";");
        Address a = c.getAddress();
        if (a != null && !a.isArchive()) {//on tient compte de l'attribut archive
          out.print(a.getAdr1() + ";");
          out.print(a.getAdr2() + ";");
          out.print(a.getCdp() + ";");
          out.print(a.getCity() + ";");
        } else {
          out.print(";;;;");
        }
        //Telephones
        Vector<Telephone> t = c.getTele();
        int j = 0;
        if (t != null) {
          for (j = 0; j < t.size() && j < maxTels; j++) {
            Telephone tel = t.elementAt(j);
            //out.print(tel.getTypeTel() + ":" + tel.getNumber() + ";");
            out.print(tel.getNumber() + ";");
          }
        }
        while (j++ < maxTels) {
          out.print(";");
        }
        // emails
        int k = 0;
        Vector<Email> emails = c.getEmail();
        if (emails != null) {
          for (; k < emails.size() && k < maxEmails; k++) {
            Email e = emails.elementAt(k);
            if (e.isArchive()) {
              out.print(";");
            } else {
              out.print(e.getEmail() + ";");
            }
          }
        }
        while (k++ < maxEmails - 1) {
          out.print(";");
        }

        out.println();
      }
      out.close();
      MessagePopup.information(this, MessageUtil.getMessage("export.success.info", new Object[]{cpt, path}));
      DesktopHandler handler = new DesktopOpenHandler();
      ((DesktopOpenHandler) handler).open(path);
    } catch (IOException e) {
      GemLogger.logException(query, e, this);
    } catch (DesktopHandlerException ex) {
      GemLogger.log(ex.getMessage());
      MessagePopup.warning(this, ex.getMessage());
    }

  }

  protected void close() {
    setVisible(false);
    dispose();
  }

  protected abstract String getFileName();

}
