/*
 * @(#)QuoteEditor 2.14.0 30/05/17
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
 * @version 2.14.0
 * @since 2.4.d 07/06/12
 */
public class QuoteEditor
        extends InvoiceEditor
{

  private GemButton btInvoice;

  public QuoteEditor(GemDesktop desktop) {
    super(desktop);
  }

  public QuoteEditor(GemDesktop desktop, BillingService service, Quote q) {
    super(desktop, service, q);
  }

  @Override
   protected void addView() {
    add(view, BorderLayout.CENTER);

    btDuplicate = new GemButton(GemCommand.DUPLICATE_CMD);
    btDuplicate.addActionListener(this);
    
    btPrint = new GemButton(GemCommand.PRINT_CMD);
    btPrint.addActionListener(this);
    
    btInvoice = new GemButton(BundleUtil.getLabel("Quotation.invoice.creation.label"));
    btInvoice.addActionListener(this);
    btInvoice.setToolTipText(BundleUtil.getLabel("Quotation.invoice.creation.tip"));

    buttons.add(btPrint,0);
    buttons.add(btDuplicate,0);
    buttons.add(btInvoice,0);
    
    add(buttons, BorderLayout.SOUTH);
  }

  @Override
  public void validation() {

    Quote edit = (Quote) view.get();

    if (edit.getNumber() == null || edit.getNumber().isEmpty()) {
      try {
        service.create(edit);
        view.setId(edit.getNumber()); // rafraîchissement du numéro
        desktop.postEvent(new QuoteCreateEvent(edit));
      } catch (SQLException e) {
        System.err.println(e.getMessage());
      } catch (BillingException fe) {
        MessagePopup.warning(this, MessageUtil.getMessage("quote.create.exception")+"\n"+fe.getMessage());
      }
    } else {    
      try {
        service.update(edit);
        MessagePopup.information(view, MessageUtil.getMessage("modification.success.label"));
        desktop.postEvent(new QuoteUpdateEvent((Quote) edit));
      } catch (BillingException fe) {
        MessagePopup.warning(this, MessageUtil.getMessage("quote.update.exception")+"\n"+fe.getMessage());
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
      if (!MessagePopup.confirm(this, MessageUtil.getMessage("invoice.estimate.create.confirmation"))) {
        return;
      }
      Quote edit = (Quote) view.get();
      // le devis doit être d'abord enregistré
      if (edit.getNumber() == null || edit.getNumber().isEmpty()) {
        MessagePopup.warning(this, MessageUtil.getMessage("invoice.estimate.create.warning"));
        return;
      }
      edit.setItems(view.getItems()); // récupération des articles éventuellement modifiés dans la vue 
      try {
        Invoice v = service.createInvoiceFrom(edit);
        if (v != null) {
          MessagePopup.information(this, MessageUtil.getMessage("invoice.create.info", v.getNumber()));
          desktop.postEvent(new InvoiceCreateEvent(v));
        }
      } catch (BillingException ex) {
        MessagePopup.warning(this, ex.getMessage());
      }
      cancel();
    } 
  }

}
