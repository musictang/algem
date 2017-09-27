/*
 * @(#)PersonFileIO.java  2.15.2 27/09/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes All Rights Reserved.
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
package net.algem.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import net.algem.accounting.DDMandate;
import net.algem.accounting.DDMandateException;
import net.algem.accounting.DirectDebitService;
import net.algem.bank.Rib;
import net.algem.bank.RibIO;
import net.algem.contact.member.Member;
import net.algem.contact.member.MemberIO;
import net.algem.contact.teacher.Teacher;
import net.algem.contact.teacher.TeacherIO;
import net.algem.group.GroupIO;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Cacheable;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;
import net.algem.util.ui.MessagePopup;

/**
 * IO methods for class {@link net.algem.contact.PersonFile}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.2
 */
public class PersonFileIO
        extends TableIO
        implements Cacheable
{

  static final String CONTACT_CREATE_EVENT = "contact.create.event";
  static final String CONTACT_UPDATE_EVENT = "contact.update.event";
  static final String TEACHER_CREATE_EVENT = "teacher.create.event";
  static final String TEACHER_UPDATE_EVENT = "teacher.update.event";
  static final String MEMBER_CREATE_EVENT = "member.create.event";
  static final String MEMBER_UPDATE_EVENT = "member.update.event";

  private final DataConnection dc;
  private final DataCache dataCache;
  private final TeacherIO teacherIO;
  private final MemberIO memberIO;

  public PersonFileIO(DataCache dataCache) {
    this.dataCache = dataCache;
    this.dc = DataCache.getDataConnection();
    teacherIO = (TeacherIO) DataCache.getDao(Model.Teacher);
    memberIO = (MemberIO) DataCache.getDao(Model.Member);
  }

  public Vector<String> update(PersonFile dossier) throws SQLException, DDMandateException {
    Vector<String> logEvents = new Vector<String>();
    ContactIO contactIO = new ContactIO(dc);

    if (dossier.getId() <= 0) { // nouveau dossier
      contactIO.insert(dossier.getContact());
      logEvents.addElement(CONTACT_CREATE_EVENT);
    } else {
      if (!dossier.getContact().equals(dossier.getOldContact())) {
        contactIO.update(dossier.getOldContact(), dossier.getContact());
        logEvents.addElement(CONTACT_UPDATE_EVENT);
      }
      if (dossier.getMember() != null) {
        if (dossier.getOldMember() == null) {
          dossier.getMember().setId(dossier.getId());
          memberIO.insert(dossier.getMember());
          logEvents.addElement(MEMBER_CREATE_EVENT);
        } else {
          if (!dossier.getMember().equals(dossier.getOldMember())) {
            memberIO.update(dossier.getMember());
            logEvents.addElement(MEMBER_UPDATE_EVENT);
          }
        }
      }
      updateTeacher(dossier, logEvents);
      updateRib(dossier, logEvents);
    }
    return logEvents;
  }

  private void updateTeacher(PersonFile dossier, Vector<String> logEvents) throws SQLException {
    if (dossier.getTeacher() == null) {
      return;
    }
    Teacher t = dossier.getTeacher();
    if (dossier.getOldTeacher() == null) {
      if (dataCache.authorize("Teacher.creation.auth")) {
        if (t.isEmpty() && !MessagePopup.confirm(null, MessageUtil.getMessage("teacher.create.confirmation"))) {
          GemLogger.log(Level.INFO, "Teacher creation cancelled");
        } else {
          t.setId(dossier.getId());
          teacherIO.insert(t);
          dossier.loadTeacher(t);
          logEvents.addElement(TEACHER_CREATE_EVENT);
        }
      } else {
        MessagePopup.information(null, MessageUtil.getMessage("teacher.create.authorization.warning"));
      }
    } else {
      if (!t.equals(dossier.getOldTeacher())) {
        teacherIO.update(t);
        dossier.loadTeacher(t);
        logEvents.addElement(TEACHER_UPDATE_EVENT);
      }
    }

  }

  private void updateRib(PersonFile dossier, Vector<String> logEvents) throws SQLException, DDMandateException {
    if (dossier.getRib() == null) {
      return;
    }
    Rib rib = dossier.getRib();
    if (dossier.getOldRib() == null) {
      //dossier.getRib().setId(dossier.getId());
      if (0 != rib.getBranchId()) {
        RibIO.insert(rib, dc);
        logEvents.addElement("bic.create.event " + dossier.getId());
      }
    } else {
      //Suppression du rib si la chaîne code compte est vide
      //if (fb.getAccount().isEmpty()) {
      //Suppression du rib si aucun des champs de la vue n'est rempli
      if (rib.isEmpty()) {
        RibIO.delete(dossier.getOldRib(), dc);
        logEvents.addElement("bic.delete.event");
      } else if (!rib.equals(dossier.getOldRib())) {
        RibIO.update(rib, dc);
        logEvents.addElement("bic.update.event");
      }
      int payer = dossier.getMember() == null ? 0 : dossier.getMember().getPayer();
      if (payer > 0) {
        DirectDebitService ddService = DirectDebitService.getInstance(dc);
        DDMandate mandate = ddService.getMandateIfValid(payer);
        if (mandate != null) {
          throw new DDMandateException(MessageUtil.getMessage("rib.update.mandate.exception"));
        }
      }
    }

  }

  public void delete(PersonFile dossier) throws Exception {
    //FicheEleveIO.delete(a, dc);
    //RibIO.delete(a.getRib(), dc);
  }

  public PersonFile findId(int n, boolean complete) {

    String query = "WHERE p.id = " + n;
    Vector<PersonFile> v = find(query, complete);
    if (v.size() > 0) {
      return v.elementAt(0);
    }
    return null;
  }

  public PersonFile findId(int n) {
    return findId(n, true);
  }

  public int count(String where) {
    return ContactIO.count(where, dc);
  }

  /**
   *
   * @param where
   * @param complete with contact infos (email, tel, address) and dossier infos(member,teacher, bic,...)
   * @return a list of PersonFile
   */
  public Vector<PersonFile> find(String where, boolean complete) {

    Vector<PersonFile> vpf = new Vector<PersonFile>();
    Vector<Contact> vc = ContactIO.find(where, complete, dc);
    for (int i = 0; i < vc.size(); i++) {
      Contact c = vc.elementAt(i);
      PersonFile dossier = new PersonFile(c);
      if (complete) {
        try {
          complete(dossier);
        } catch (SQLException ex) {
          GemLogger.logException("Complete dossier", ex);
        }
      }
      vpf.addElement(dossier);
    }
    return vpf;
  }

  public PersonFile findMember(int _id, boolean _complete) {
    Vector<PersonFile> v = findMembers("WHERE id = " + _id);

    if (v.isEmpty()) {
      return null;
    }

    Contact c = v.elementAt(0).getContact();
    if (_complete) {
      ContactIO.complete(c, dc);
    }

    return v.elementAt(0);
  }

  public Vector<PersonFile> findMembers(String where) {
    Vector<PersonFile> v = new Vector<PersonFile>();
    String query = "SELECT " + PersonIO.COLUMNS + "," + MemberIO.COLUMNS
      + " FROM " + PersonIO.VIEW + " p JOIN " +  MemberIO.TABLE + " m ON p.id = m.idper";
    query += (where == null ? "" : " " + where) + " ORDER BY p.prenom,p.nom";
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Person p = PersonIO.getFromRS(rs);
        Member m = memberIO.getFromRS(rs, PersonIO.COLUMNS_OFFSET);
//        m.setInstruments(InstrumentIO.find(m.getId(), InstrumentIO.MEMBER, dc));
        PersonFile dossier = new PersonFile(new Contact(p));
        dossier.setMember(m);

        v.addElement(dossier);
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
    return v;
  }

  public PersonFile findPayer(int _id) {
    Vector<PersonFile> v = findPayers("WHERE id=" + _id);
    if (v.isEmpty()) {
      return null;
    }

    Contact c = v.elementAt(0).getContact();
    ContactIO.complete(c, dc);

    return v.elementAt(0);
  }

  public Vector<PersonFile> findPayers(String where) {
    Vector<PersonFile> v = new Vector<>();
//    String query = "SELECT " + PersonIO.COLUMNS + " FROM " + PersonIO.TABLE + " p";
String query = PersonIO.PRE_QUERY;
    query += (where != null) ? (" " + where + " AND") :  " WHERE";
    query += " p.id IN (SELECT payeur FROM " + MemberIO.TABLE + ")";
    query += " ORDER BY p.nom,p.prenom";
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Person p = PersonIO.getFromRS(rs);

        PersonFile d = new PersonFile(new Contact(p));
        Rib r = RibIO.findId(p.getId(), dc);
        if (r != null) {
          d.setRib(r);
        }

        v.addElement(d);
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
    return v;
  }

  public Vector<PersonFile> findRegistered(String query) {
    Vector<PersonFile> v = new Vector<PersonFile>();
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Person p = PersonIO.getFromRS(rs);
        Member m = memberIO.getFromRS(rs, PersonIO.COLUMNS_OFFSET);
        PersonFile d = new PersonFile(new Contact(p));
        d.setMember(m);

        v.addElement(d);
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
    return v;
  }

  public void complete(PersonFile pf) throws SQLException {
    pf.setMember((Member) DataCache.findId(pf.getId(), Model.Member));
    pf.loadTeacher((Teacher) DataCache.findId(pf.getId(), Model.Teacher));
    pf.addRib(RibIO.findId(pf.getId(), dc));
//    pf.setSubscriptionCard(new PersonSubscriptionCardIO(dc).find(pf.getId(), null, false, 1));//XXX TODO lasy loading
    pf.setGroups(((GroupIO) DataCache.getDao(Model.Group)).find(pf.getId()));
  }

  @Override
  public List<PersonFile> load() throws SQLException {
    return null;
  }

}
