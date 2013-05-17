/*
 * @(#)MemberService.java	2.8.a 01/04/13
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
package net.algem.contact.member;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import net.algem.accounting.AccountPrefIO;
import net.algem.accounting.AccountUtil;
import net.algem.accounting.OrderLine;
import net.algem.accounting.OrderLineIO;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
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
 * @version 2.8.a
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
//    return m != null ? m : MemberIO.findId(id, dc);
  }

  public String getEmail(int id) throws SQLException {
    return EmailIO.findId(id, dc);
  }

  public void checkSubscriptionCard(ScheduleObject plan, Hour start, Hour end) throws SQLException {
    PersonSubscriptionCard nc = null;
    PersonSubscriptionCard card = findSubscriptionCard(plan.getIdPerson());
    if (card != null) {
      int oldDuration = plan.getStart().getLength(plan.getEnd());
      int newDuration = start.getLength(end);
      if (newDuration >= oldDuration) {
        card.dec(newDuration - oldDuration);
        if (card.getRest() < 0) {
          nc = createSubscriptionCard(card, plan.getDate());
          sendPersonFileEvent(nc);
        } else {
          updateSubscriptionCard(card);
        }
      } else { // on récupère des heures sinon
        card.inc(oldDuration - newDuration);
        updateSubscriptionCard(card);
      }
    }
  }

  /**
   * Creates a new subscription card if there is not enough time remaining on card after
   * the extension of a rehearsal's duration.
   *
   * @param card
   * @param date
   */
  public PersonSubscriptionCard createSubscriptionCard(PersonSubscriptionCard card, DateFr date) throws SQLException {
    if (card == null) {
      return null;
    }

    int offset = Math.abs(card.getRest());//abo.getRest() devrait être négatif
    card.setRest(0);
    // création automatique d'une nouvelle carte d'abonnement
    RehearsalCard cr = RehearsalCardIO.find(card.getRehearsalCardId(), dc);
    PersonSubscriptionCard nc = new PersonSubscriptionCard(card.getIdper(), card.getRehearsalCardId(), date, cr.getTotalLength() - offset);
    cardIO.insert(nc);
    cardIO.update(card);
    PersonFile pf = ((PersonFileIO)DataCache.getDao(Model.PersonFile)).findMember(card.getIdper(), false);
    OrderLine e = AccountUtil.setOrderLine(pf, date, getPrefAccount(AccountPrefIO.REHEARSAL_KEY_PREF), cr.getAmount());
    String s = ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey(), dc);
    e.setSchool(Integer.parseInt(s));
    AccountUtil.createEntry(e, dc);

    return nc;
  }

  public void create(PersonSubscriptionCard card, PersonFile pFile) throws SQLException {

    cardIO.insert(card);
    RehearsalCard abo = RehearsalCardIO.find(card.getRehearsalCardId(), dc);
    Preference p = AccountPrefIO.find(AccountPrefIO.REHEARSAL_KEY_PREF, dc);
    OrderLine e = AccountUtil.setOrderLine(pFile, new DateFr(new Date()), p, abo.getAmount());
    String s = ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey(), dc);
    e.setSchool(Integer.parseInt(s));
    AccountUtil.createEntry(e, dc);
  }

  public void create(PersonSubscriptionCard card) throws SQLException {
    cardIO.insert(card);
  }

  public void update(PersonSubscriptionCard card) throws SQLException {
    cardIO.update(card);
  }

  /**
   * Updates the subscription card when a single rehearsal is cancelled.
   *
   * @param dc dataCache
   * @param plan schedule
   */
  public void editSubscriptionCard(DataCache dc, ScheduleObject plan) throws SQLException {
    PersonSubscriptionCard card = findSubscriptionCard(plan.getIdPerson());
    if (card == null) {
      return;
    }
    int duree = plan.getStart().getLength(plan.getEnd());
    card.inc(duree);
    updateSubscriptionCard(card);
  }

  /**
   * Synchronisation of the member's subscription cards.
   * If remaining time on current card exceeds the total available duration of this card, 
   * a former card is searched and its remaining time is incremented by the duration
   * available after sessions' cancelling. Then, the current card is destroyed.
   *
   * @param dc
   * @param card
   */
  private void updateSubscriptionCard(PersonSubscriptionCard card) throws SQLException {
    assert card != null : "Card should not be null here.";
    RehearsalCard rehearsalCard = RehearsalCardIO.find(card.getRehearsalCardId(), dc);
    // recherche d'une carte précédente
    PersonSubscriptionCard lastCard = cardIO.find(card.getIdper(), " id < " + card.getId());
    int rest = card.getRest();
    int totalDuration = rehearsalCard.getTotalLength();
    // si la durée restante sur la carte actuelle excède la durée totale possible
    if (rest > totalDuration) {
      if (lastCard != null) {
        lastCard.inc(card.getRest() - rehearsalCard.getTotalLength());
        cardIO.delete(card.getId());
        cardIO.update(lastCard);
        //Suppression ligne échéancier
        deleteOrderLine(card.getPurchaseDate(), card.getIdper());
        sendPersonFileEvent(lastCard);
      } else {
        card.setRest(rehearsalCard.getTotalLength());
        sendPersonFileEvent(card);
      }
    } else if (card.getRest() < rehearsalCard.getTotalLength()) {
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

  public PersonSubscriptionCard findSubscriptionCard(int idper) {
    try {
      return cardIO.find(idper, null);
    } catch (SQLException ex) {
      GemLogger.logException("find carte repet", ex);
      return null;
    }
  }

  public void deleteOrderLine(DateFr date, int member) throws SQLException {
    String where = " echeance = '" + date + "' AND adherent = " + member + " AND paye = 'f' AND transfert = 'f'";
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
   * Searches collective or individual follow-up's notes for the member {@code memberId}
   * and the {@code courseId}.
   * 
   * @param memberId member's id
   * @param courseId course's id
   * @param collective collective or individual
   * @return a list of schedule ranges
   * @throws SQLException
   */
  public Vector<ScheduleRangeObject> findFollowUp(int memberId, int courseId, boolean collective) throws SQLException {
    String where = " AND pg.note = s.id";
    if (collective) {
      where = " AND pl.note = s.id";
    }
    where += " AND pg.adherent = " + memberId + " AND pl.action = a.id AND a.cours = " + courseId + " ORDER BY jour,debut";
    return ScheduleRangeIO.findFollowUp(where, true, dc);
  }

  /**
   * Searches the schedule ranges containing a note for the member {@code memberId}.
   *
   * @param memberId
   * @return a list of schedule ranges
   * @throws SQLException
   */
  public Vector<ScheduleRangeObject> findFollowUp(int memberId) throws SQLException {
    String where = " AND pg.note = s.id AND pg.adherent = " + memberId + " ORDER BY pl.jour, pg.debut";
    return ScheduleRangeIO.findFollowUp(where, false, dc);
  }

  public void saveRehearsal(ScheduleDTO p) throws MemberException {

    ActionIO ioAction = new ActionIO(dc);
    Action a = new Action();
    a.setCourse(0);
    try {
      dc.setAutoCommit(false);
      ioAction.insert(a);
      p.setAction(a.getId());
      ScheduleIO.insert(p, dc);
      dc.commit();
    } catch (SQLException sqe) {
      dc.rollback();
      throw new MemberException(sqe.getMessage());
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void saveRehearsalPass(Vector<DateFr> dates, Hour start, Hour end, int idper, int room) throws MemberException {
    try {
      dc.setAutoCommit(false);
      ActionIO actionIO = new ActionIO(dc);
      Action a = new Action();
      actionIO.insert(a);
      ScheduleDTO dto = new ScheduleDTO();
      dto.setType(Schedule.MEMBER_SCHEDULE);
      dto.setPersonId(idper);
      dto.setPlace(room);
      dto.setNote(0);
      dto.setStart(start.toString());
      dto.setEnd(end.toString());
      dto.setAction(a.getId());

      for (int i = 0; i < dates.size(); i++) {
        DateFr d = dates.elementAt(i);
        dto.setDay(d.toString());
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
  public void saveOrderLine(PersonFile pFile, DateFr date, double amount) throws SQLException {

    Preference p = AccountPrefIO.find(AccountPrefIO.REHEARSAL_KEY_PREF, dc);
    OrderLine e = AccountUtil.setOrderLine(pFile, date, p, amount);
    String s = ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey(), dc);
    e.setSchool(Integer.parseInt(s));
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

  public Vector<DateFr> generationDate(int jour, DateFr _debut, DateFr _fin) {

    Vector<DateFr> v = new Vector<DateFr>();

    Calendar debut = Calendar.getInstance(Locale.FRANCE);
    debut.setTime(_debut.getDate());
    Calendar fin = Calendar.getInstance(Locale.FRANCE);
    fin.setTime(_fin.getDate());

    while (!debut.after(fin)) {
      if (debut.get(Calendar.DAY_OF_WEEK) == jour + 1) {
        v.addElement(new DateFr(debut.getTime()));
      }
      debut.add(Calendar.DATE, 1);
    }
    return v;
  }

  public Vector<RehearsalCard> getPassList() throws SQLException {
    return RehearsalCardIO.findAll("", dc);
  }

  /**
   * Gets the preferred account for {@code key}.
   *
   * @param key
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
