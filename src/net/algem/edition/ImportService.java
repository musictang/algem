/*
 * @(#) ImportService.java Algem 2.13.0 22/03/2017
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
 */

package net.algem.edition;

import java.sql.SQLException;
import java.util.List;
import net.algem.contact.Contact;
import net.algem.contact.ContactIO;
import net.algem.contact.ContactImport;
import net.algem.util.DataConnection;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 * @since 2.13.0 22/03/2017
 */
public class ImportService {

  private DataConnection dc;

  public ImportService(DataConnection dc) {
    this.dc = dc;
  }

  public int importContacts(final List<ContactImport> contacts) throws Exception {
    final ContactIO contactIO = new ContactIO(dc);

    return dc.withTransaction(new DataConnection.SQLRunnable<Integer>() {
      @Override
      public Integer run(DataConnection conn) throws Exception {
        int n = 0;
        for (ContactImport c : contacts) {
          updateParent(contactIO, c.getParent());
          if (c.getId() == 0) {
            contactIO.insert(c);
            n++;
          } else {
              Contact o = ContactIO.findId(c.getId(), dc);
              update(contactIO, o, c);
          }
        }
        return n;
      }

    });
  }
  
  private void update(ContactIO contactIO, Contact o, Contact c) throws SQLException {
//    Contact o = ContactIO.findId(c.getId(), dc);
    if (o != null) {
      if (c.getTele() != null) {//
        // preserve old other tels
        if (o.getTele() != null && o.getTele().size() > c.getTele().size()) {//3 > 1
          for (int i = c.getTele().size(); i < o.getTele().size(); i++) {
            c.getTele().add(o.getTele().elementAt(i));
          }
        }
      }
      if (c.getEmail() != null) {//
        // preserve old other emails
        if (o.getEmail() != null && o.getEmail().size() > c.getEmail().size()) {//3 > 1
          for (int i = c.getEmail().size(); i < o.getEmail().size(); i++) {
            c.getEmail().add(o.getEmail().elementAt(i));
          }
        }
      }
      contactIO.update(o, c);// old,new
    }
  }
  
  private void updateParent(ContactIO contactIO, Contact p) throws SQLException {
    if (p != null && p.getName() != null && p.getFirstName() != null) {
      List<Contact> op = ContactIO.find("WHERE lower(nom) = '" + p.getName().toLowerCase() + "' AND lower(prenom) = '" + p.getFirstName().toLowerCase() + "'", true, dc);
      if (op != null && op.size() > 0) {
        update(contactIO, op.get(0), p);
      } else {
        contactIO.insert(p);
      }
    }
  }

}
