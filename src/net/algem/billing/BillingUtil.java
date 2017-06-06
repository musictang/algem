/*
 * @(#)BillingUtil.java	2.14.0 05/06/17
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.algem.accounting.ModeOfPayment;
import net.algem.accounting.OrderLine;
import net.algem.util.BundleUtil;

/**
 * Utility for billing.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.8.n 25/09/13
 */
public class BillingUtil {


  public static List<OrderLine> getInvoiceOrderLines(List<OrderLine> olist, String n) {

    List<OrderLine> lines = new ArrayList<OrderLine>();

    for (OrderLine ol : olist) {
      if (ol.getInvoice() != null && ol.getInvoice().equals(n)) {
        lines.add(ol);
      }
    }
    return lines;
  }

  /**
   * Adds orderlines and items to invoice when created from a selection.
   *
   * @param inv invoice
   * @param orderLines
   */
  public static void setInvoiceOrderLines(Invoice inv, Collection<OrderLine> orderLines) {
    int i = 0;
    for (OrderLine ol : orderLines) {
      if (ModeOfPayment.FAC.toString().equals(ol.getModeOfPayment())) {
        inv.addItem(new InvoiceItem(ol)); // un reglement "FAC" correspond à un item de facturation
      }
      inv.addOrderLine(ol); // on garde la trace des échéances sélectionnées
      if (i++ == 0) {
        // IMPORTANT : le payeur enregistré dans l'échéance est prioritaire
        // par rapport à celui de la fiche (la première échéance est prise comme modèle).
        inv.setDescription(BundleUtil.getLabel("Invoice.label") + " " + ol.getLabel());
        inv.setPayer(ol.getPayer());
        // on utilise le numéro d'adhérent enregistré dans l'échéance
        inv.setMember(ol.getMember());
      }
    }

  }

  public static void setQuoteOrderLines(Quote d, Collection<OrderLine> orderLines) {
    if (orderLines != null) {
      int i = 0;
      for (OrderLine e : orderLines) {
        d.addItem(new InvoiceItem(e));
        if (i++ == 0) {
          d.setPayer(e.getPayer());
          d.setMember(e.getMember());
        }
      }
    }
  }

}
