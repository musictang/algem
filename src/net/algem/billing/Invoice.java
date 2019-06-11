/*
 * @(#)Invoice.java 2.8.n 26/09/13
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.algem.accounting.ModeOfPayment;
import net.algem.accounting.OrderLine;
import net.algem.contact.PersonFile;
import net.algem.room.Room;
import net.algem.security.User;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.n
 * @since 2.3.a 22/12/11
 */
public class Invoice
        extends Quote {

  public Invoice() {
  }

  public Invoice(String number) {
    super(number);
  }

  public <Q extends Quote> Invoice(Q quote) {
    super(quote);
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

  boolean isFromScratch() {
    List<OrderLine> lines = new ArrayList<OrderLine>(orderLines);
    return lines != null && lines.size() > 0 && lines.get(0).getId() <= 0;
  }

}
