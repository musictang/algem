/*
 * @(#)InvoiceItemEditor.java	2.8.n 19/09/13
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

package net.algem.billing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.n
 * @since 2.3.a 14/02/12
 */
public class InvoiceItemEditor
        extends JDialog
        implements ActionListener
{

  private InvoiceItemView view;
  private GemButton ok;
  private GemButton cancel;
  private boolean changed = false;

  public InvoiceItemEditor(GemDesktop desktop, final InvoiceItem it, BillingService service) {
    super(desktop.getFrame(), MessageUtil.getMessage("invoicing.item.modification.label"), true);

    view = new InvoiceItemView(service);
    view.set(it);

    ok = new GemButton(GemCommand.VALIDATION_CMD);
    ok.addActionListener(this);
    cancel = new GemButton(GemCommand.CANCEL_CMD);
    cancel.addActionListener(this);

    GemPanel boutons = new GemPanel(new GridLayout(1,2));
    boutons.add(ok);
    boutons.add(cancel);

    setLayout(new BorderLayout());
    add(view, BorderLayout.CENTER);
    add(boutons, BorderLayout.SOUTH);
    
    setSize(GemModule.DEFAULT_SIZE);
    setLocationRelativeTo(desktop.getFrame());
  }

  public InvoiceItem get() {
    return view.getInvoiceItem();
  }

  public boolean hasChanged() {
    return changed;
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    if (e.getSource() == ok) {
      changed = true;
    } else if (e.getSource() == cancel) {
      changed = false;
      view.clear();
    }
    setVisible(false);
  }

}
