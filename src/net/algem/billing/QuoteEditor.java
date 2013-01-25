/*
 * @(#)QuoteEditor 2.6.a 25/09/12
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
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.4.d 07/06/12
 */
public class QuoteEditor
        extends InvoiceEditor
{

  private GemButton btInvoice;

  public QuoteEditor(GemDesktop desktop) {
    super(desktop);
  }

  public QuoteEditor(GemDesktop desktop, BillingServiceI service, Quote f) {
    super(desktop, service, f);
  }

  @Override
   protected void addView() {
    add(view, BorderLayout.CENTER);

    btPrint = new GemButton(GemCommand.PRINT_CMD);
    btPrint.addActionListener(this);
    btInvoice = new GemButton(BundleUtil.getLabel("Quotation.invoice.creation.label"));
    btInvoice.addActionListener(this);

    buttons.add(btPrint,0);
    buttons.add(btInvoice,0);

    add(buttons, BorderLayout.SOUTH);
  }

  @Override
  public void validation() {

    Quote d = view.get();

    if (d.getNumber() == null || d.getNumber().isEmpty()) {
      try {
        service.create(d);
        view.setId(d.getNumber()); // rafraîchissement du numéro
        desktop.postEvent(new QuoteCreateEvent(d));
      } catch (SQLException e) {
        System.err.println(e.getMessage());
      } catch (BillingException fe) {
        MessagePopup.warning(this, MessageUtil.getMessage("invoicing.create.exception")+"\n"+fe.getMessage());
      }
    } else {    
      try {
        service.update(d);
        MessagePopup.information(view, MessageUtil.getMessage("modification.confirmation.label"));
        desktop.postEvent(new QuoteUpdateEvent(d));
      } catch (BillingException fe) {
        MessagePopup.warning(this, MessageUtil.getMessage("invoicing.update.exception")+"\n"+fe.getMessage());
      }
    }

    //abandon(); // COMMENTÉ POUR LAISSER OUVERTE LA FENETRE

  }
	
	@Override
  public void cancel() {
    if (listener != null) {
      listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlAbandonDevis"));
    }
    view.reset();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    super.actionPerformed(e);
    if (e.getSource() == btInvoice) {
      Quote d = view.get();
      // le devis doit être d'abord enregistré
      if (d.getNumber() == null || d.getNumber().isEmpty()) {
        MessagePopup.warning(this, MessageUtil.getMessage("invoice.estimate.create.warning"));
        return;
      }
      d.setItems(view.getItems()); // récupération des articles éventuellement modifiés dans la vue 
      try {
        Invoice f = service.createInvoiceFrom(d);
        if (f != null) {
          MessagePopup.information(this, MessageUtil.getMessage("invoice.create.info", new Object[] {f.getNumber()}));
          desktop.postEvent(new InvoiceCreateEvent(f));
        }
      } catch (BillingException ex) {
        MessagePopup.warning(this, ex.getMessage());
      }
      cancel();
    }
  }

}
