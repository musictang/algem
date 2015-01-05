/*
 * @(#)MemberService.java	2.9.2 02/01/15
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.contact.member;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import net.algem.accounting.AccountPrefIO;
import net.algem.accounting.AccountUtil;
import net.algem.accounting.OrderLine;
import net.algem.accounting.OrderLineIO;
import net.algem.config.Preference;
import net.algem.contact.EmailIO;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonFileEvent;
import net.algem.contact.PersonFileIO;
import net.algem.enrolment.Enrolment;
import net.algem.enrolment.EnrolmentIO;
import net.algem.planning.*;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 * @since 2.4.a 14/05/12
 */
public class MemberService
{

  private DataConnection dc;
  private ConflictService conflictService;
  private PersonSubscriptionCardIO cardIO;

  public MemberService(DataConnection dc) {
    this.dc = dc;
    conflictService = new ConflictService(dc);
    cardIO = new PersonSubscriptionCardIO(dc);
  }

  public Member findMember(int id) throws SQLException {
    return (Member) DataCache.findId(id, Model.Member);
  }

  public String getEmail(int id) throws SQLException {
    return EmailIO.findId(id, dc);
  }

  List<PersonalCardSession> getSessions(int cardId) throws SQLException {
    return cardIO.findSessions(cardId);
  }

  /**
   * Method called when changing time or length of some rehearsal.
   * @param plan schedule
   * @param start start time
   * @param end end time
   * @throws SQLException
   */
  public void updatePersonalSession(ScheduleObject plan, Hour start, Hour end) throws SQLException {
    PersonSubscriptionCard nc = null;
    PersonSubscriptionCard card = findSubscriptionCard(plan.getIdPerson());
    if (card != null) {
      int oldDuration = plan.getStart().getLength(plan.getEnd());
      int newDuration = start.getLength(end);
      if (newDuration >= oldDuration) {
        card.dec(newDuration - oldDuration);
        if (card.getRest() < 0) {

          nc = createSubscriptionCard(card, plan);
          sendPersonFileEvent(nc);
        } else {
          updateSubscriptionCard(card);
        }
      } else { // on récupère des heures sinon
        // TODO traiter chevauchement
        card.inc(oldDuration - newDuration);
        updateSubscriptionCard(card);
      }
      // TODO refresh
    }
  }

  /**
   * Updates the subscription card when a single rehearsal is cancelled.
   *
   * @param dc dataCache
   * @param plan schedule
   * @throws java.sql.SQLException
   */
  public void cancelPersonalSession(DataCache dc, ScheduleObject plan) throws SQLException {
    PersonSubscriptionCard card = findSubscriptionCard(plan.getIdPerson());
    if (card == null) {
      return;
    }
    int duree = plan.getStart().getLength(plan.getEnd());

    card.inc(duree);
    // TODO si chevauchement entre 2 cartes
    // si card.getRest() > max ??
    // supprimer card ?? ou card.setRest(0) ??
    // supprimer session(idplanning)
    // ancienne carte inc(max-card.getRest) ??
    // supprimer session(idplanning)
    Iterator<PersonalCardSession> iterator = card.getSessions().iterator();
    while(iterator.hasNext()) {
      PersonalCardSession s = iterator.next();
      if (s.getScheduleId() == plan.getId()) {
        iterator.remove();
        break;
      }

    }
    updateSubscriptionCard(card);
  }

  PersonSubscriptionCard findSubscriptionCard(int idper) {
    try {
      return cardIO.find(idper, null, true);
    } catch (SQLException ex) {
      GemLogger.logException(getClass().getName()+"#findSubscriptionCard", ex);
      return null;
    }
  }

  /**
   * Creates a new subscription card if there is not enough time remaining on card after
   * the extension of a rehearsal's duration.
   *
   * @param card
   * @param date
   */
  private PersonSubscriptionCard createSubscriptionCard(PersonSubscriptionCard card, ScheduleObject plan) throws SQLException {
    if (card == null) {
      return null;
    }

    int offset = Math.abs(card.getRest());//abo.getRest() devrait être négatif
    card.setRest(0);
    // création automatique d'une nouvelle carte d'abonnement
    RehearsalCard pass = RehearsalCardIO.find(card.getPassId(), dc);
    PersonSubscriptionCard nc = new PersonSubscriptionCard(card.getIdper(), card.getPassId(), new DateFr(new Date()), pass.getTotalLength() - offset);
    Hour endOffset = new Hour(plan.getStart());
    Hour end = new Hour(plan.getEnd());
    endOffset.incMinute(offset);
    plan.setEnd(endOffset);
    card.addSession(plan);
    cardIO.update(card);
    plan.setStart(endOffset);
    plan.setEnd(end);
    nc.addSession(plan);
    cardIO.insert(nc);

    PersonFile pf = ((PersonFileIO)DataCache.getDao(Model.PersonFile)).findMember(card.getIdper(), false);
    OrderLine e = AccountUtil.setRehearsalOrderLine(pf, plan.getDate(), getPrefAccount(AccountPrefIO.REHEARSAL_KEY_PREF), pass.getAmount());

    AccountUtil.createEntry(e, dc);

    return nc;
  }

  public void create(PersonSubscriptionCard card, PersonFile pFile) throws SQLException {

    cardIO.insert(card);
    RehearsalCard abo = RehearsalCardIO.find(card.getPassId(), dc);
    Preference p = AccountPrefIO.find(AccountPrefIO.REHEARSAL_KEY_PREF, dc);
    OrderLine e = AccountUtil.setRehearsalOrderLine(pFile, new DateFr(new Date()), p, abo.getAmount());
    AccountUtil.createEntry(e, dc);
  }

  /**
   * Synchronisation of the member's subscription cards.
   * If remaining time on current card exceeds the total available duration of this card,
   * a former card is searched and its remaining time is incremented by the duration
   * available after sessions' cancelling. Then, the current card is destroyed.
   *
   * @param card actual card
   */
  private void updateSubscriptionCard(PersonSubscriptionCard card) throws SQLException {
    assert card != null : "Card should not be null here.";
    RehearsalCard pass = RehearsalCardIO.find(card.getPassId(), dc);
    // recherche d'une carte précédente
    PersonSubscriptionCard lastCard = cardIO.find(card.getIdper(), " id < " + card.getId(), true);
    int rest = card.getRest();
    int max = pass.getTotalLength();
    // si la durée restante sur la carte actuelle excède la durée totale possible
    if (rest > max) {// TODO traiter chevauchement
//      SELECT count(id) from carteabopersessions where idplanning = plan.getId();
      if (lastCard != null) {
        lastCard.inc(card.getRest() - pass.getTotalLength());
        cardIO.delete(card.getId());
        cardIO.update(lastCard);
        //Suppression ligne échéancier
        deleteOrderLine(card.getPurchaseDate(), card.getIdper());
        sendPersonFileEvent(lastCard);
      } else {
        card.setRest(pass.getTotalLength());// persistence is useless here
        sendPersonFileEvent(card);
      }
    } else if (card.getRest() < max) {
      cardIO.update(card);
      sendPersonFileEvent(card);
    } else { // suppression d'une carte quand la durée restante est égale au max disponible
      deleteSubscriptionCard(card);
      if (lastCard != null) {
        sendPersonFileEvent(lastCard);
      }
    }
  }

  private void deleteSubscriptionCard(PersonSubscriptionCard abo) throws SQLException {
    cardIO.delete(abo.getId());
    deleteOrderLine(abo.getPurchaseDate(), abo.getIdper());
  }


  public void create(PersonSubscriptionCard card) throws SQLException {
    cardIO.insert(card);
  }

  public void update(PersonSubscriptionCard card) throws SQLException {
    cardIO.update(card);
  }

  public void deleteOrderLine(DateFr date, int member) throws SQLException {
    String where = "WHERE echeance = '" + date + "' AND adherent = " + member + " AND paye = 'f' AND transfert = 'f'";
    Vector<OrderLine> ve = OrderLineIO.find(where, dc);
    if (ve.size() > 0) {
      OrderLineIO.delete(ve.elementAt(0), dc); // suppression de la première échéance trouvée seulement
    }
  }

  /**
   * Searches the member's enrolments between 2 dates.
   *
   * @param memberId member id
   * @param start start date
   * @param end end date
   * @return a list of enrolments
   * @throws java.sql.SQLException
   */
  public Vector<Enrolment> getEnrolments(int memberId, DateFr start, DateFr end) throws SQLException {
    String where = "WHERE adh = " + memberId + " AND creation >='" + start + "' AND creation <='" + end + "' ORDER BY id";
    return EnrolmentIO.find(where, dc);
  }

  public Vector<Enrolment> getEnrolments(int memberId, String start) throws SQLException {
    String where = "WHERE adh = " + memberId + " AND creation >='" + start + "'";
    return EnrolmentIO.find(where, dc);
  }

  public PersonFile find(int id) {
    return ((PersonFileIO)DataCache.getDao(Model.PersonFile)).findId(id);
  }

  /**
   * Searches collective or individual follow-up's notes for the member {@literal memberId}
   * and the {@literal courseId}.
   *
   * @param memberId member's id
   * @param courseId course's id
   * @param collective collective or individual if false
   * @return a list of schedule ranges
   * @throws SQLException
   */
  public Vector<ScheduleRangeObject> findFollowUp(int memberId, int courseId, boolean collective) throws SQLException {
    String where = " AND (pg.note >= 0 OR p.note > 0)"
            + " AND pg.note = s1.id"
            + " AND p.note = s2.id"
            + " AND pg.adherent = " + memberId + " AND p.action = a.id AND a.cours = " + courseId + " ORDER BY p.jour, pg.debut";
    return ScheduleRangeIO.findFollowUp(where, true, dc);
  }

  /**
   * Gets the member's schedule ranges containing a follow up's note.
   *
   * @param memberId
   * @param dates
   * @return a list of schedule ranges
   * @throws SQLException
   */
  public Vector<ScheduleRangeObject> findFollowUp(int memberId, DateRange dates) throws SQLException {
    String where = " AND p.jour BETWEEN '" + dates.getStart() + "' AND '" + dates.getEnd()
            + "' AND (pg.note >= 0 OR p.note > 0)"
//            + " AND (pg.note = s.id OR p.note = s.id)"
           // + " AND (pg.note = s.id OR (p.note = s.id AND p.note > 0 AND s.texte IS NOT NULL AND trim(s.texte) != ''))"
            + " AND pg.note = s1.id"
            + " AND p.note = s2.id"
            + " AND pg.adherent = " + memberId
            + " ORDER BY p.jour, pg.debut";
    return ScheduleRangeIO.findFollowUp(where, false, dc);
  }

  public void saveRehearsal(ScheduleObject p) throws MemberException {

    ActionIO actionIO = new ActionIO(dc);
    Action a = new Action();
    a.setCourse(0);
    try {
      dc.setAutoCommit(false);
      actionIO.insert(a);
      p.setIdAction(a.getId());
      ScheduleIO.insert(p, dc);
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new MemberException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void savePassRehearsal(Vector<DateFr> dates, Hour start, Hour end, int idper, int room) throws MemberException {
    try {
      dc.setAutoCommit(false);
      ActionIO actionIO = new ActionIO(dc);
      Action a = new Action();
      actionIO.insert(a);
      ScheduleObject dto = new MemberRehearsalSchedule();
      dto.setType(Schedule.MEMBER);
      dto.setIdPerson(idper);
      dto.setIdRoom(room);
      dto.setNote(0);
      dto.setStart(start);
      dto.setEnd(end);
      dto.setIdAction(a.getId());

      for (int i = 0; i < dates.size(); i++) {
        DateFr d = dates.elementAt(i);
        dto.setDate(d);
        ScheduleIO.insert(dto, dc);
      }
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new MemberException(MessageUtil.getMessage("rehearsal.create.exception") + "\n" + sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  /**
   * Saves an order line for a single rehearsal.
   *
   * @param pFile person file
   * @param date date of reservation
   * @param amount
   * @throws SQLException
   */
  public void saveRehearsalOrderLine(PersonFile pFile, DateFr date, double amount) throws SQLException {

    Preference p = AccountPrefIO.find(AccountPrefIO.REHEARSAL_KEY_PREF, dc);
    OrderLine e = AccountUtil.setRehearsalOrderLine(pFile, date, p, amount);
    AccountUtil.createEntry(e, dc);
  }

  public Vector<ScheduleTestConflict> testRoom(DateFr debut, Hour hd, Hour hf, int salle) throws SQLException {
    return conflictService.testRoomConflict(debut, hd, hf, salle);
  }

  public Vector<ScheduleTestConflict> testMemberSchedule(ScheduleObject plan, DateFr debut, Hour hd, Hour hf) throws SQLException {
    return conflictService.testMemberConflict(plan, debut, hd, hf);
  }

  public Vector<ScheduleTestConflict> testRangeSchedule(ScheduleObject plan, DateFr debut, Hour hd, Hour hf) throws SQLException {
    return conflictService.testMemberScheduleConflict(plan, debut, hd, hf);
  }

  public Vector<DateFr> generationDate(int day, DateFr start, DateFr end) {

    Vector<DateFr> v = new Vector<DateFr>();

    while(!start.after(end)) {
      if (start.getDayOfWeek() == day + 1) {
        v.addElement(new DateFr(start));
      }
      start.incDay(1);
    }
    return v;
  }

  public Vector<RehearsalCard> getPassList() throws SQLException {
    return RehearsalCardIO.findAll("", dc);
  }

  /**
   * Gets the preferred account for {@literal key}.
   *
   * @param key preference key
   * @return a Preference
   * @throws SQLException
   */
  private Preference getPrefAccount(String key) throws SQLException {
    return AccountPrefIO.find(key, dc);
  }

  private void sendPersonFileEvent(PersonSubscriptionCard carte) {
    PersonFileEvent event = new PersonFileEvent(carte, PersonFileEvent.SUBSCRIPTION_CARD_CHANGED);
    //XXX is event should be managed ?
//    EditeurDossierPersonne editeur = ((GemDesktopCtrl) desktop).findDossierPersonneModule(carte.getIdper());
//    if (editeur != null) {
//      editeur.getVuePersonne().contentsChanged(event);
//    }
  }
}
