/*
 * @(#)FilePanel.java 2.8.r 03/01/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

package net.algem.util.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import net.algem.util.BundleUtil;
import net.algem.util.FileUtil;
import net.algem.util.GemCommand;

/**
 * Panel controller for selecting directories or opening files.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.r
 */
public class FilePanel
  extends GemPanel implements ActionListener
{

  private GemField path;
  private JButton btBrowse;

  public FilePanel(String label, String defaultPath) {
    setLayout(new GridLayout(1,3));
    GemLabel fileLabel = new GemLabel();
    if (label == null) {
      fileLabel.setText(BundleUtil.getLabel("Menu.file.label"));
    }
    else fileLabel.setText(label);
    path = new GemField(15);
    path.setText(defaultPath);
    
    add(fileLabel);
    add(path);
    btBrowse = new JButton(GemCommand.BROWSE_CMD);
		btBrowse.setPreferredSize(new Dimension(100,btBrowse.getHeight()));
    btBrowse.addActionListener(this);
    add(btBrowse);

  }

  public String getText() {
    return path.getText();
  }

  public void setText(String s) {
    path.setText(s);
  }

  @Override
  public void setToolTipText(String text) {
    super.setToolTipText(text);
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    if (e.getSource() == btBrowse) {
      String p = FileUtil.getDir(this,
                                  BundleUtil.getLabel("FileChooser.selection"),
                                  path.getText());
      
      if (p != null) {
        setText(p);
      }
    }
  }

}
