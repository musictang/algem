/*
 * @(#)DDMandateEditor.java 2.8.r 08/01/14
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
package net.algem.accounting;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.8.r
 * @since 2.8.r 08/01/14
 */
public class DDMandateEditor
        extends JDialog
        implements ActionListener
{

  private DDMandateView view;
  private GemButton okBt;
  private GemButton cancelBt;
  private boolean validation;

  public DDMandateEditor(Frame owner, boolean modal, DDMandate dd, boolean multiple) {
    super(owner, modal);
    setTitle(BundleUtil.getLabel("Direct.debit.mandate.edition.label"));
    setLayout(new BorderLayout());

    GemPanel buttons = new GemPanel(new GridLayout(1, 2));
    okBt = new GemButton(GemCommand.VALIDATION_CMD);
    okBt.addActionListener(this);
    cancelBt = new GemButton(GemCommand.CANCEL_CMD);
    cancelBt.addActionListener(this);
    buttons.add(okBt);
    buttons.add(cancelBt);
    
    view = new DDMandateView();
    view.set(dd, multiple);
    
    add(view, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);

    setSize(GemModule.S_SIZE);
    pack();
    setLocationRelativeTo(owner);
    setVisible(true);
  }

  DDMandate get() {
    return view.get();
  }

  boolean isValidated() {
    return validation;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == cancelBt) {
      view.clear();
      validation = false;
    } else {
      validation = true;
    }
    setVisible(false);
  }
}
