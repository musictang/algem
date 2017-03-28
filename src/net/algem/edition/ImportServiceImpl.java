/*
 * @(#) ImportServiceImpl.java Algem 2.13.0 28/03/2017
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
import javax.swing.SwingWorker;
import net.algem.contact.Contact;
import net.algem.contact.ContactIO;
import net.algem.contact.ContactImport;
import net.algem.contact.member.Member;
import net.algem.contact.member.MemberIO;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import static net.algem.util.ui.SearchCtrl.TRANSLATE_FROM;
import static net.algem.util.ui.SearchCtrl.TRANSLATE_TO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 * @since 2.13.0 22/03/2017
 */
public class ImportServiceImpl implements ImportService {

  private DataConnection dc;

  public ImportServiceImpl(DataConnection dc) {
    this.dc = dc;
  }

  /**
   * Imports a list of contacts.
   * A student is created if the contact has a birth date.
   * @param contacts list of contacts to import
   * @return the number of new contacts
   * @throws Exception if errors occurred
   */
  @Override
  public int importContacts(final List<ContactImport> contacts) throws Exception {
    final ContactIO contactIO = new ContactIO(dc);
    final MemberIO memberIO = new MemberIO(dc);

    return dc.withTransaction(new DataConnection.SQLRunnable<Integer>() {
      @Override
      public Integer run(DataConnection conn) throws Exception {
        int n = 0;
        for (ContactImport c : contacts) {
          boolean np = updateParent(contactIO, c.getParent());
          if (c.getId() == 0) {
            String query = "WHERE translate(lower(nom),'" + TRANSLATE_FROM + "', '" + TRANSLATE_TO + "') ~* '" + c.getName().toLowerCase()
              + "' AND translate(lower(prenom),'" + TRANSLATE_FROM + "', '" + TRANSLATE_TO + "') ~* '" + c.getFirstName().toLowerCase() + "'";
            List<Contact> oc = ContactIO.find(query, true, dc);
            if (oc != null && oc.size() > 0) {
              continue;// do not import duplicates
            }
            contactIO.insert(c);
            if (c.getBirthDate() != null || np) {
              Member m = new Member(c.getId());
              if (np) {
                m.setPayer(c.getParent().getId());
              }
              m.setBirth(new DateFr(c.getBirthDate()));
              memberIO.insert(m);
            }
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
  
  private void importContact(ContactImport c, ContactIO contactIO, MemberIO memberIO) throws SQLException {
    boolean np = updateParent(contactIO, c.getParent());
    if (c.getId() == 0) {
      String query = "WHERE translate(lower(nom),'" + TRANSLATE_FROM + "', '" + TRANSLATE_TO + "') ~* '" + c.getName().toLowerCase()
              + "' AND translate(lower(prenom),'" + TRANSLATE_FROM + "', '" + TRANSLATE_TO + "') ~* '" + c.getFirstName().toLowerCase() + "'";
      List<Contact> oc = ContactIO.find(query, true, dc);
      if (oc != null && oc.size() > 0) {
        return;// do not import duplicates
      }
      contactIO.insert(c);
      if (c.getBirthDate() != null || np) {
        Member m = new Member(c.getId());
        if (np) {
          m.setPayer(c.getParent().getId());
        }
        m.setBirth(new DateFr(c.getBirthDate()));
        memberIO.insert(m);
      }
//      return 1;
//            n++;
    } else {
      Contact o = ContactIO.findId(c.getId(), dc);
      update(contactIO, o, c);
//      return 0;
    }
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
  
  /**
   * 
   * @param contactIO dao
   * @param p the parent if any
   * @return true if a new parent was created
   * @throws SQLException 
   */
  private boolean updateParent(ContactIO contactIO, Contact p) throws SQLException {
    if (p == null) {return false;}
    if (p.getId() > 0) {
      Contact op = ContactIO.findId(p.getId(), dc);
      if (op != null) {
        update(contactIO, op, p);
      }
    } else if (p.getName() != null && p.getFirstName() != null) {
      String query = "WHERE translate(lower(nom),'" + TRANSLATE_FROM + "', '" + TRANSLATE_TO + "') ~* '" + p.getName().toLowerCase()
              + "' AND translate(lower(prenom),'" + TRANSLATE_FROM + "', '" + TRANSLATE_TO + "') ~* '" + p.getFirstName().toLowerCase() + "'";
      List<Contact> op = ContactIO.find(query, true, dc);
      if (op != null && op.size() > 0) {
        //update(contactIO, op.get(0), p);
        return false;
      } else {
        contactIO.insert(p);
        return true;
      }
    }
    return false;
  }


  public class ImportCsvTask
          extends SwingWorker<Void, Void>
  {

    private List<ContactImport> contacts;

    public ImportCsvTask(List<ContactImport> contacts) {
      this.contacts = contacts;
    }

    @Override
    protected Void doInBackground() throws Exception {
      final ContactIO contactIO = new ContactIO(dc);
      final MemberIO memberIO = new MemberIO(dc);
      dc.withTransaction(new DataConnection.SQLRunnable<Void>()
      {
        @Override
        public Void run(DataConnection conn) throws Exception {
          int size = contacts.size();
          int k = 0;
          for (ContactImport c : contacts) {
            importContact(c, contactIO, memberIO);
            int p = ++k * 100 / size;
            setProgress(p);
          }
          return null;
        }

      });
      return null;
//      return importContacts(contacts);
    }
    
  }
}
