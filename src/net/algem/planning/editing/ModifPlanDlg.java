/*
 * @(#)ModifPlanDlg.java 2.8.y.1 08/10/14
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
package net.algem.planning.editing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.y.1
 * @since 2.2.e 20/10/11
 */
public abstract class ModifPlanDlg
        implements ActionListener
{

  protected GemButton btValid;
  protected GemButton btCancel;
  protected GemPanel buttons;
  protected boolean validation;
  protected JDialog dlg;
  protected Frame parent;

  public ModifPlanDlg(Frame f) {
    parent = f;
    buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 2));

    btValid = new GemButton(GemCommand.OK_CMD);
    btValid.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    buttons.add(btValid);
    buttons.add(btCancel);
    
  }

  protected void addContent(Component c, String key) {
    Container ct = dlg.getContentPane();
    ct.add(c, BorderLayout.CENTER);
    ct.add(buttons, BorderLayout.SOUTH);
    if (key == null) {
      dlg.setTitle(BundleUtil.getLabel("Schedule.modification.title"));
    } else {
      dlg.setTitle(BundleUtil.getLabel(key));
    }
    dlg.pack();
    dlg.setLocationRelativeTo(parent);
  }

  public abstract boolean isEntryValid();

  public abstract boolean isValidate();

  public void show() {
    dlg.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals(GemCommand.OK_CMD)) {
      if (!isEntryValid()) {
        return;
      }
      validation = true;
    } else {
      validation = false;
    }
    dlg.setVisible(false);
    dlg.dispose();
  }
}
