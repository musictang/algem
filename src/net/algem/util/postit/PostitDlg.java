/*
 * @(#)PostitDlg.java	2.6.a 06/08/2012
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
package net.algem.util.postit;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.PopupDlg;

/**
 * Dialog for postit editing.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class PostitDlg
        implements ActionListener
{

  private JDialog dlg;
  private PostitView pv;
  private Postit post;
  private boolean suppression;
  private boolean modification;
  private GemButton btValid;
  private GemButton btDelete;
  private GemButton btCancel;

  public PostitDlg(Component c) {
    dlg = new JDialog(PopupDlg.getTopFrame(c), true);

    GemLabel title = new GemLabel(BundleUtil.getLabel("Postit.label"));

    pv = new PostitView();

    btDelete = new GemButton(GemCommand.DELETE_CMD);
    btDelete.addActionListener(this);
    
    btValid = new GemButton(GemCommand.MODIFY_CMD);
    btValid.addActionListener(this);
    
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemPanel buttons = new GemPanel();

    buttons.setLayout(new GridLayout(1, 3));
    buttons.add(btDelete);
    buttons.add(btValid);
    buttons.add(btCancel);

    dlg.getContentPane().add("North", title);
    dlg.getContentPane().add("Center", pv);
    dlg.getContentPane().add("South", buttons);
    dlg.pack();
    dlg.setLocation(100, 100);
  }

  public void setPost(Postit p) {
    post = p;
    pv.set(p);
  }

  public void clear() {
    pv.clear();
  }

  public void entry() {
    dlg.setVisible(true);
  }

  public boolean isEntryValid() {
    return true;
  }

  public boolean isSuppression() {
    return suppression;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    
    if (GemCommand.DELETE_CMD.equals(evt.getActionCommand())) {
      suppression = true;
      exit();
    }
    else if (GemCommand.MODIFY_CMD.equals(evt.getActionCommand())){
      modification = true;
      exit();
    }
    else {
      exit();
    }
  }

  public String getText() {
    return pv.get();
  }
  
  public void exit() {
    dlg.setVisible(false);
    dlg.dispose();
  }

  public boolean isModif() {
    return modification;
  }
}
