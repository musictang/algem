/*
 * @(#)Invoice.java 2.5.d 25/07/12
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

import java.util.ArrayList;
import java.util.Collection;
import net.algem.accounting.ModeOfPayment;
import net.algem.accounting.OrderLine;
import net.algem.contact.PersonFile;
import net.algem.room.Room;
import net.algem.security.User;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.5.d
 * @since 2.3.a 22/12/11
 */
public class Invoice
        extends Quote {

  public Invoice() {
  }

  public Invoice(String number) {
    super(number);
  }

  public <F extends Quote> Invoice(F f) {
    super(f);
  }

  public Invoice(User u) {
    super(u);
  }

  public Invoice(Room s, User u) {
    super(s, u);
  }

  public Invoice(PersonFile dossier, User u) {
    super(dossier, u);
  }

  public Collection<OrderLine> getInvoiceOrderLines() {
    Collection<OrderLine> ef = new ArrayList<OrderLine>();
    for (OrderLine e : getOrderLines()) {
      if (ModeOfPayment.FAC.toString().equals(e.getModeOfPayment())) {
        ef.add(e);
      }
    }
    return ef.isEmpty() ? null : ef;
  }

  public void addOrderLine(OrderLine e) {
    orderLines.add(e);
  }
}
