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
import java.util.ArrayList;
import java.util.List;
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
        implements Cacheable {

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

    public List<String> update(PersonFile dossier) throws SQLException, DDMandateException {
        List<String> logEvents = new ArrayList<>();
        ContactIO contactIO = new ContactIO(dc);

        if (dossier.getId() <= 0) { // nouveau dossier
            contactIO.insert(dossier.getContact());
            logEvents.add(CONTACT_CREATE_EVENT);
        } else {
            if (!dossier.getContact().equals(dossier.getOldContact())) {
                contactIO.update(dossier.getOldContact(), dossier.getContact());
                logEvents.add(CONTACT_UPDATE_EVENT);
            }
            if (dossier.getMember() != null) {
                if (dossier.getOldMember() == null) {
                    dossier.getMember().setId(dossier.getId());
                    memberIO.insert(dossier.getMember());
                    logEvents.add(MEMBER_CREATE_EVENT);
                } else {
                    if (!dossier.getMember().equals(dossier.getOldMember())) {
                        memberIO.update(dossier.getMember());
                        logEvents.add(MEMBER_UPDATE_EVENT);
                    }
                }
            }
            updateTeacher(dossier, logEvents);
            updateRib(dossier, logEvents);
        }
        return logEvents;
    }

    private void updateTeacher(PersonFile dossier, List<String> logEvents) throws SQLException {
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
                    logEvents.add(TEACHER_CREATE_EVENT);
                }
            } else {
                MessagePopup.information(null, MessageUtil.getMessage("teacher.create.authorization.warning"));
            }
        } else {
            if (!t.equals(dossier.getOldTeacher())) {
                teacherIO.update(t);
                dossier.loadTeacher(t);
                logEvents.add(TEACHER_UPDATE_EVENT);
            }
        }

    }

    private void updateRib(PersonFile dossier, List<String> logEvents) throws SQLException, DDMandateException {
        if (dossier.getRib() == null) {
            return;
        }
        Rib rib = dossier.getRib();
        if (dossier.getOldRib() == null) {
            //dossier.getRib().setId(dossier.getId());
            if (0 != rib.getBranchId()) {
                RibIO.insert(rib, dc);
                logEvents.add("bic.create.event " + dossier.getId());
            }
        } else {
            //Suppression du rib si la chaîne code compte est vide
            //if (fb.getAccount().isEmpty()) {
            //Suppression du rib si aucun des champs de la vue n'est rempli
            if (rib.isEmpty()) {
                RibIO.delete(dossier.getOldRib(), dc);
                logEvents.add("bic.delete.event");
            } else if (!rib.equals(dossier.getOldRib())) {
                RibIO.update(rib, dc);
                logEvents.add("bic.update.event");
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
        List<PersonFile> v = find(query, complete);
        if (v.size() > 0) {
            return v.get(0);
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
     * @param complete with contact infos (email, tel, address) and dossier
     * infos(member,teacher, bic,...)
     * @return a list of PersonFile
     */
    public List<PersonFile> find(String where, boolean complete) {

        List<PersonFile> vpf = new ArrayList<>();
        List<Contact> vc = ContactIO.find(where, complete, dc);
        for (int i = 0; i < vc.size(); i++) {
            Contact c = vc.get(i);
            PersonFile dossier = new PersonFile(c);
            if (complete) {
                try {
                    complete(dossier);
                } catch (SQLException ex) {
                    GemLogger.logException("Complete dossier", ex);
                }
            }
            vpf.add(dossier);
        }
        return vpf;
    }

    public PersonFile findMember(int _id, boolean _complete) {
        List<PersonFile> v = findMembers("WHERE id = " + _id);

        if (v.isEmpty()) {
            return null;
        }

        Contact c = v.get(0).getContact();
        if (_complete) {
            ContactIO.complete(c, dc);
        }

        return v.get(0);
    }

    public List<PersonFile> findMembers(String where) {
        List<PersonFile> v = new ArrayList<>();
        String query = "SELECT " + PersonIO.COLUMNS + "," + MemberIO.COLUMNS
                + " FROM " + PersonIO.VIEW + " p JOIN " + MemberIO.TABLE + " m ON p.id = m.idper";
        query += (where == null ? "" : " " + where) + " ORDER BY p.prenom,p.nom";
        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                Person p = PersonIO.getFromRS(rs);
                Member m = memberIO.getFromRS(rs, PersonIO.COLUMNS_OFFSET);
//        m.setInstruments(InstrumentIO.find(m.getId(), InstrumentIO.MEMBER, dc));
                PersonFile dossier = new PersonFile(new Contact(p));
                dossier.setMember(m);

                v.add(dossier);
            }
        } catch (SQLException e) {
            GemLogger.logException(query, e);
        }
        return v;
    }

    public PersonFile findPayer(int _id) {
        List<PersonFile> v = findPayers("WHERE id=" + _id);
        if (v.isEmpty()) {
            return null;
        }

        Contact c = v.get(0).getContact();
        ContactIO.complete(c, dc);

        return v.get(0);
    }

    public List<PersonFile> findPayers(String where) {
        List<PersonFile> v = new ArrayList<>();
//    String query = "SELECT " + PersonIO.COLUMNS + " FROM " + PersonIO.TABLE + " p";
        String query = PersonIO.PRE_QUERY;
        query += (where != null) ? (" " + where + " AND") : " WHERE";
        query += " p.id IN (SELECT payeur FROM " + MemberIO.TABLE + ")";
        query += " ORDER BY p.nom,p.prenom";
        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                Person p = PersonIO.getFromRS(rs);

                PersonFile d = new PersonFile(new Contact(p));
                Rib r = RibIO.findId(p.getId(), dc);
                if (r != null) {
                    d.setRib(r);
                }

                v.add(d);
            }
        } catch (SQLException e) {
            GemLogger.logException(query, e);
        }
        return v;
    }

    public List<PersonFile> findRegistered(String query) {
        List<PersonFile> v = new ArrayList<>();
        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                Person p = PersonIO.getFromRS(rs);
                Member m = memberIO.getFromRS(rs, PersonIO.COLUMNS_OFFSET);
                PersonFile d = new PersonFile(new Contact(p));
                d.setMember(m);

                v.add(d);
            }
        } catch (SQLException e) {
            GemLogger.logException(query, e);
        }
        return v;
    }

    public void complete(PersonFile pf) throws SQLException {
        if (pf.getMember() == null) {
            pf.setMember((Member) DataCache.findId(pf.getId(), Model.Member));
        }
        if (pf.getTeacher() == null) {
            pf.loadTeacher((Teacher) DataCache.findId(pf.getId(), Model.Teacher));
        }
        if (pf.getRib() == null) {
            pf.addRib(RibIO.findId(pf.getId(), dc));
        }
//        if (pf.getSubscriptionCard() == null) {
//            pf.setSubscriptionCard(new PersonSubscriptionCardIO(dc).find(pf.getId(), null, false, 1));//XXX TODO lasy loading
//        }
        if (pf.getGroups() == null) {
            pf.setGroups(((GroupIO) DataCache.getDao(Model.Group)).find(pf.getId()));
        }
    }

    @Override
    public List<PersonFile> load() throws SQLException {
        return null;
    }

}
