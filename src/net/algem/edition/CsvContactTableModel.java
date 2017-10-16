/*
 * @(#) CsvContactTableModel.java Algem 2.13.0 31/03/2017
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import net.algem.contact.Address;
import net.algem.contact.Contact;
import net.algem.contact.ContactImport;
import net.algem.contact.Email;
import net.algem.contact.Telephone;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 * @since 2.13.0 22/03/2017
 */
public class CsvContactTableModel
  extends JTableModel<ContactImport>
{

  private final DateFormat df;

  public CsvContactTableModel() {
    header = ImportCsvCtrl.IMPORT_FIELDS;
    df = new SimpleDateFormat("dd/MM/yyyy");
  }

  @Override
  public int getIdFromIndex(int i) {
    return tuples.get(i).getId();
  }

  @Override
  public Object getValueAt(int line, int col) {
    ContactImport c = tuples.elementAt(line);
    Contact p = c.getParent();
    Address a = c.getAddress();

    if (a == null) {
      if (p != null) {
        a = p.getAddress();
      }
    }
    List<Telephone> tels = c.getTele();
    if (tels == null) {
      if (p != null) {
        tels = p.getTele();
      }
    }
    List<Email> emails = c.getEmail();
    switch (col) {
      case 0:
        return c.getId();
      case 1:
        return c.getGender();
      case 2:
        return c.getName();
      case 3:
        return c.getFirstName();
      case 4:
        return c.getBirthDate() != null ? df.format(c.getBirthDate()) : null;
      case 5:
        return c.getInstrument();
      case 6:
        return p != null ? p.getId() : 0;
      case 7:
        return p != null ? p.getGender() : null;
      case 8:
        return p != null ? p.getName(): null;
      case 9:
        return p != null ? p.getFirstName(): null;
      case 10:
        return a == null ? "" : a.getAdr1();
      case 11:
        return a == null ? "" : a.getAdr2();
      case 12:
        return a == null ? "" : a.getCdp();
      case 13:
        return a == null ? "" : a.getCity();
      case 14:
        if (tels == null || tels.isEmpty()) {return "";}
        for (Telephone t : tels) {
          if (t.getTypeTel() == 1) {
            return t.getNumber();
          }
          return "";
        }
      case 15:
        if (tels == null || tels.isEmpty()) {return "";}
        for (Telephone t : tels) {
          if (t.getTypeTel() == 8) {
            return t.getNumber();
          }
          return "";
        }
      case 16:
        return emails == null ? "" : emails.isEmpty() ? "" : emails.get(0).getEmail();
      case 17:
        if (p == null) return null;
        List<Email> pMails = p.getEmail();
        return pMails == null ? "" : (pMails.isEmpty()  ? "" : pMails.get(0).getEmail());

    }
    return null;
  }

   @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
      case 6:
        return Integer.class;
      default:
        return String.class;
    }
  }

  @Override
  public void setValueAt(Object value, int line, int column) {

  }

}
