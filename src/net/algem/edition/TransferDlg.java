/*
 * @(#)TransferDlg.java	2.8.k 19/07/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import net.algem.config.ConfigUtil;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;

/**
 * Abstract class for transfer operations dialog.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.k
 * @since 2.4.a 10/05/12
 */
public abstract class TransferDlg
        extends JDialog
        implements ActionListener
{

  protected GemButton btValidation;
  protected GemButton btCancel;
  protected JButton chooser;
  protected GemPanel buttons;
  protected GemField filepath;
  protected File file;
  protected DataConnection dc;

  public TransferDlg(Frame _parent, String title, String file, DataConnection dc) {
    super(_parent, title);
    this.dc = dc;
  }

  protected void init(String file, DataConnection dc) {

    btValidation = new GemButton(GemCommand.VALIDATION_CMD);
    btValidation.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    buttons = new GemPanel(new GridLayout(1, 1));
    buttons.add(btValidation);
    buttons.add(btCancel);

    chooser = new JButton(GemCommand.BROWSE_CMD);
    chooser.addActionListener(this);

    filepath = new GemField(ConfigUtil.getExportPath(dc) + FileUtil.FILE_SEPARATOR + file, 25);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btCancel) {
      close();
    } else if (evt.getSource() == btValidation) {
      file = new File(filepath.getText());
      if (!FileUtil.confirmOverWrite(this, file)) {
        return;
      }
      transfer();
      close();
    } else if (evt.getSource() == chooser) {
      JFileChooser fileChooser = ExportDlg.getFileChooser(filepath.getText());
      int ret = fileChooser.showDialog(this, BundleUtil.getLabel("FileChooser.selection"));
      if (ret == JFileChooser.APPROVE_OPTION) {
        file = fileChooser.getSelectedFile();
        if (FileUtil.confirmOverWrite(this, file)) {
          filepath.setText(file.getPath());
        }
      }
    }
  }

  protected void close() {
    setVisible(false);
    dispose();
  }

  abstract protected void transfer();
}
