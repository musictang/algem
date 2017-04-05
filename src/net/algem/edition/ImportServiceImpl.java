/*
 * @(#) ImportServiceImpl.java Algem 2.13.0 04/04/17
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import net.algem.config.Instrument;
import net.algem.config.InstrumentIO;
import net.algem.contact.Contact;
import net.algem.contact.ContactIO;
import net.algem.contact.ContactImport;
import net.algem.contact.member.Member;
import net.algem.contact.member.MemberIO;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.TextUtil;
import net.algem.util.ui.MessagePopup;
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
          boolean newParent = updateParent(contactIO, c.getParent());
          if (c.getId() == 0) {
            if (hasDuplicates(c)) {
              continue;// do not import duplicates
            }
            contactIO.insert(c);
            Member m = getMemberFromContact(c, newParent, dc);
            if (m != null) {
              memberIO.insert(m);
            }
            n++;
          } else {
              //Contact o = ContactIO.findId(c.getId(), dc);
              //update(contactIO, o, c);
          }
        }
        return n;
      }

    });
  }

  /**
   * Imports a contact.
   * Existing contacts are not imported.
   *
   * @param c the contact to import
   * @param contactIO
   * @param memberIO
   * @return the number of new contacts (inserted)
   * @throws Exception
   */
  private int importContact(ContactImport c, ContactIO contactIO, MemberIO memberIO) throws Exception {
    boolean np = updateParent(contactIO, c.getParent());
    if (c.getId() == 0) {
      if (hasDuplicates(c)) {
        return 0;// do not import duplicates
      }
      contactIO.insert(c);
      Member m = getMemberFromContact(c, np, dc);
      if (m != null) {
        memberIO.insert(m);
      }
      return 1;
    } else {
      /*Contact o = ContactIO.findId(c.getId(), dc);
      update(contactIO, o, c);*/
      return 0;
    }
  }

  private boolean hasDuplicates(Contact c) {
    String query = "WHERE translate(lower(nom),'" + TRANSLATE_FROM + "', '" + TRANSLATE_TO + "') = '" + TextUtil.stripDiacritics(c.getName().toLowerCase()) + "'";
    if (c.getFirstName() != null) {
      query += " AND translate(lower(prenom),'" + TRANSLATE_FROM + "', '" + TRANSLATE_TO + "') = '" + TextUtil.stripDiacritics(c.getFirstName().toLowerCase()) + "'";
    }

    List<Contact> oc = ContactIO.find(query, true, dc);
    if (oc != null && oc.size() > 0) {
      return true;
    }
    return false;
  }

  private Member getMemberFromContact(ContactImport c, boolean np, DataConnection dc) throws SQLException {
    if (c.getBirthDate() != null || c.getInstrument() != null || np) {
      String instrument = c.getInstrument();
      Date birth = c.getBirthDate();
      Member m = new Member(c.getId());
      if (np) {
        m.setPayer(c.getParent().getId());
      }
      m.setBirth(birth == null ? null : new DateFr(birth));
      if (instrument != null && instrument.trim().length() > 0) {
        List<Instrument> ii = InstrumentIO.find("WHERE lower(nom) = E'" + instrument.toLowerCase() + "'", dc);
        if (ii != null && ii.size() > 0) {
          List<Integer> myii = new ArrayList<>();
          myii.add(ii.get(0).getId());
          m.setInstruments(myii);
        }
      }
      return m;
    }
    return null;
  }

  private void update(ContactIO contactIO, Contact o, Contact c) throws SQLException {
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
    if (p == null) {
      return false;
    }
    if (p.getId() > 0) {
      Contact op = ContactIO.findId(p.getId(), dc);
      if (op != null) {
        return false;
        //update(contactIO, op, p);
      } else {
        p.setId(0);
        contactIO.insert(p);
        return true;
      }
    } else if (p.getName() != null) {
      if (hasDuplicates(p)) {
        //update(contactIO, op.get(0), p);
        return false;
      } else {
        contactIO.insert(p);
        return true;
      }
    }
    return false;
  }

  private static class ImportException extends Exception {

    public ImportException(String err) {
      super(err);
    }
  }


  public class ImportCsvTask
          extends SwingWorker<Integer, Void>
  {

    private final List<ContactImport> contacts;
    private final ContactIO contactIO;
    private final MemberIO memberIO;

    public ImportCsvTask(List<ContactImport> contacts) {
      this.contacts = contacts;
      contactIO = new ContactIO(dc);
      memberIO = new MemberIO(dc);
    }

    @Override
    protected Integer doInBackground() throws Exception {
      return dc.withTransaction(new DataConnection.SQLRunnable<Integer>() {
        @Override
        public Integer run(DataConnection conn) throws Exception {
          int size = contacts.size();
          int k = 0;
          int nc = 0;
          for (ContactImport c : contacts) {
            try {
              nc += importContact(c, contactIO, memberIO);
              int p = ++k * 100 / size;
              setProgress(p);
            } catch (Exception e) {
              String err = "CONTACT " + c.toString() + "\n";
              err += e.getMessage();
              GemLogger.log(e.getMessage() + " IMPORT CONTACT : " + c.toString());
              setProgress(100);
              throw new ImportException(err);
            }
          }
          return nc;
        }

      });
    }

    @Override
    public void done() {
      try {
        int ci = get();
        MessagePopup.information(null, MessageUtil.getMessage(ci <= 1 ? "contact.imported" : "contacts.imported", ci));
      } catch (InterruptedException | ExecutionException ex) {
        GemLogger.logException(ex);
        MessagePopup.error(null, MessageUtil.getMessage("import.exception") + ":\n" + ex.getMessage());
      }
    }

  }
}
