/*
 * @(#)InvoiceFooterEditor.java 2.6.a 03/10/12
 *
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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
package net.algem.billing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import net.algem.util.*;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Reading and updating of invoice footer.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.3.a 27/02/12
 */
public class InvoiceFooterEditor
        extends GemPanel
        implements ActionListener
{

  private GemLabel label;
  private JTextArea area;
  private GemButton ok;
  private GemButton cancel;
  private GemDesktop desktop;
  
  /** Clé de fermeture du module */
  private String key;

  /** Espacement de la bordure */
  private static final int bp = 10;


  public InvoiceFooterEditor(String key, GemDesktop desktop) {
    this.desktop = desktop;
    this.key = key;
    init();
  }

  private void init() {
    
    GemPanel content = new GemBorderPanel(BorderFactory.createEmptyBorder(bp, bp , bp, bp));
    content.setLayout(new BorderLayout(0,10));

    label = new GemLabel(MessageUtil.getMessage("invoice.footer.editor"));
    area = new JTextArea();
    area.setMargin(new Insets(bp, bp, bp, bp));
    area.setText(read());
    content.add(label, BorderLayout.NORTH);
    content.add(area, BorderLayout.CENTER);

    ok = new GemButton(GemCommand.VALIDATION_CMD);
    ok.addActionListener(this);
    cancel = new GemButton(GemCommand.CANCEL_CMD);
    cancel.addActionListener(this);

    GemPanel boutons = new GemPanel(new GridLayout(1, 2));
    boutons.add(ok);
    boutons.add(cancel);

    setLayout(new BorderLayout());
    //add(label, BorderLayout.NORTH);
    add(content, BorderLayout.CENTER);
    add(boutons, BorderLayout.SOUTH);
  }

  /**
   * Retrieves the actual footer.
   * @return a list of strings
   */
  public static List<String> getFooter() {

    BufferedReader br = null;
    List<String> lines = new ArrayList<String>();
    String s = null;

    try {
      URL url = new Object().getClass().getResource(FileUtil.INVOICE_FOOTER_FILE);
      br = new BufferedReader(new FileReader(url.getPath()));
      while ((s = br.readLine()) != null) {
        lines.add(s);
      }
    } catch (Exception ex) {
      GemLogger.logException(ex);
    } finally {
      try {
        if (br != null) {
          br.close();
        }
      } catch (IOException ex) {
        GemLogger.logException(ex);
      }
    }
    return lines;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (ok == e.getSource()) {
      write();
    }
    desktop.removeModule(key);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  /**
   * Fills the text area.
   * @return
   */
  private String read() {
    StringBuilder sb = new StringBuilder();
    List<String> ls = getFooter();
    for (String s : ls) {
      sb.append(s).append(TextUtil.LINE_SEPARATOR);
    }
    return sb.toString();
  }

  /**
   * Updates footer.
   * The infos into the text area are saved in the file.
   * {@code FileUtil.INVOICE_FOOTER_FILE}.
   */
  private void write() {

    BufferedWriter bw = null;
    try {
      URL url = new Object().getClass().getResource(FileUtil.INVOICE_FOOTER_FILE);
      if (url == null) {
        throw new FileNotFoundException(MessageUtil.getMessage("file.not.found.exception", FileUtil.INVOICE_FOOTER_FILE));
      }
      File f = new File(url.getPath());
      if (f != null && f.canWrite()) {
        bw = new BufferedWriter(new FileWriter(f));
        bw.append(area.getText());
      } else {
        throw new IOException(MessageUtil.getMessage("file.writing.exception", FileUtil.INVOICE_FOOTER_FILE));
      }
    } catch(FileNotFoundException f) {
      GemLogger.logException(f);
      MessagePopup.warning(this, f.getMessage());
    } catch (IOException ex) {
      GemLogger.logException(ex);
      MessagePopup.warning(this, ex.getMessage());
    } finally {
      try {
        if (bw != null) {
          bw.close();
        }
      } catch (IOException ex) {
        GemLogger.logException(ex);
      }
    }

  }
  
}
