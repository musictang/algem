/*
 * @(#)PersonFileIO.java  2.7.k 04/03/13
 *
 * Copyright (c) 2009 Musiques Tangentes All Rights Reserved.
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
import net.algem.bank.Rib;
import net.algem.bank.RibIO;
import net.algem.contact.member.Member;
import net.algem.contact.member.MemberIO;
import net.algem.contact.member.PersonSubscriptionCardIO;
import net.algem.contact.teacher.Teacher;
import net.algem.contact.teacher.TeacherIO;
import net.algem.group.GroupIO;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Cacheable;
import net.algem.util.model.Model;
import net.algem.util.model.TableIO;

/**
 * IO methods for class {@link net.algem.contact.PersonFile}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.k
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

  private DataConnection dc;
  private TeacherIO teacherIO;
  private MemberIO memberIO;

  public PersonFileIO(DataConnection dc) {
    this.dc = dc;
    teacherIO = (TeacherIO) DataCache.getDao(Model.Teacher);
    memberIO = (MemberIO) DataCache.getDao(Model.Member);
  }

  public Vector<String> update(PersonFile dossier) throws SQLException {
    Vector<String> logEvents = new Vector<String>();
    ContactIO contactIO = new ContactIO(dc);

    if (dossier.getId() == 0) { // nouveau dossier
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
      if (dossier.getTeacher() != null) {
        if (dossier.getOldTeacher() == null) {
          dossier.getTeacher().setId(dossier.getId());
          teacherIO.insert(dossier.getTeacher());
          logEvents.addElement(TEACHER_CREATE_EVENT);
        } else {
          if (!dossier.getTeacher().equals(dossier.getOldTeacher())) {
            teacherIO.update(dossier.getTeacher());
            logEvents.addElement(TEACHER_UPDATE_EVENT);
          }
        }
      }

      if (dossier.getRib() != null) {
        Rib fb = dossier.getRib();
        if (dossier.getOldRib() == null) {
          //dossier.getRib().setId(dossier.getId());
          if (0 != fb.getBranchId()) {
            RibIO.insert(dossier.getRib(), dc);
            logEvents.addElement("bic.create.event " + dossier.getId());
          }
        } else {
          //Suppression du rib si la chaîne code compte est vide
          //if (fb.getAccount().isEmpty()) {
          //Suppression du rib si aucun des champs de la vue n'est rempli
          if (fb.isEmpty()) {
            RibIO.delete(dossier.getOldRib(), dc);
            logEvents.addElement("bic.delete.event");
          } else if (!fb.equals(dossier.getOldRib())) {
            RibIO.update(dossier.getRib(), dc);
            logEvents.addElement("bic.update.event");
          }
        }
      }
    }
    return logEvents;
  }

  public void delete(PersonFile dossier) throws Exception {
    //FicheEleveIO.delete(a, dc);
    //RibIO.delete(a.getRib(), dc);
  }

  public PersonFile findId(int n, boolean complete) {

    String query = "WHERE id = " + n;
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
    String query = 
            "SELECT " + PersonIO.COLUMNS + "," + MemberIO.COLUMNS
            + " FROM " + PersonIO.TABLE + ", " + MemberIO.TABLE + " ";
    if (where != null) {
      query += where + " AND personne.id = eleve.idper";
    } else {
      query += " WHERE personne.id = eleve.idper";
    }

    query += " ORDER BY personne.prenom,personne.nom";
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        Person p = PersonIO.getFromRS(rs);
        Member m = memberIO.getFromRS(rs, PersonIO.PERSON_COLUMNS_OFFSET);
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
    Vector<PersonFile> v = new Vector();
    String query = "SELECT id,ptype,nom,prenom,civilite,note FROM personne ";
    if (where != null) {
      query += where + " AND";
    } else {
      query += " WHERE";
    }

    query += " id IN (SELECT payeur FROM eleve)";
    query += " ORDER BY nom,prenom";
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
        Member m = memberIO.getFromRS(rs, PersonIO.PERSON_COLUMNS_OFFSET);
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
    pf.addTeacher((Teacher) DataCache.findId(pf.getId(), Model.Teacher));
    /*pf.setMember(memberIO.findId(pf.getId()));
    pf.addTeacher(teacherIO.findId(pf.getId()));*/
    pf.addRib(RibIO.findId(pf.getId(), dc));
    pf.setSubscriptionCard(new PersonSubscriptionCardIO(dc).find(pf.getId(), null));
    pf.setGroups(((GroupIO) DataCache.getDao(Model.Group)).find(pf.getId()));
  }

  @Override
  public List<PersonFile> load() throws SQLException {
    return null;
  }

}
