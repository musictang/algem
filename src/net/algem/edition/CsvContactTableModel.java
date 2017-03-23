/*
 * @(#) CsvContactTableModel.java Algem 2.13.0 22/03/2017
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

import java.util.List;
import net.algem.contact.Address;
import net.algem.contact.Contact;
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
  extends JTableModel<Contact>
{

  public CsvContactTableModel() {
    header = ImportCsvCtrl.IMPORT_FIELDS;
  }

  @Override
  public int getIdFromIndex(int i) {
    return tuples.get(i).getId();
  }

  @Override
  public Object getValueAt(int line, int col) {
    Contact c = tuples.elementAt(line);
    Address a = c.getAddress();
    List<Telephone> tels = c.getTele();
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
        return a == null ? "" : a.getAdr1();
      case 5:
        return a == null ? "" : a.getAdr2();
      case 6:
        return a == null ? "" : a.getCdp();
      case 7:
        return a == null ? "" : a.getCity();
      case 8:
        return tels == null ? "" : tels.isEmpty() ? "" : tels.get(0).getNumber();
      case 9:
        return tels == null ? "" : tels.size() < 2 ? "" : tels.get(1).getNumber();
        case 10:
        return emails == null ? "" : emails.isEmpty() ? "" : emails.get(0).getEmail();
      case 11:
        return emails == null ? "" : emails.size() < 2 ? "" : emails.get(1).getEmail();

    }
    return null;
  }

   @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
        return Integer.class;
      default:
        return String.class;
    }
  }

  @Override
  public void setValueAt(Object value, int line, int column) {
    
  }

}
