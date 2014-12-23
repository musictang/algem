/*
 * @(#)MemberRehearsalCtrl.java	2.9.2 19/12/14
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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JOptionPane;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonFileEditor;
import net.algem.contact.PersonFileEvent;
import net.algem.planning.*;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.room.Room;
import net.algem.room.RoomIO;
import net.algem.room.RoomService;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabDialog;
import net.algem.util.ui.MessagePopup;
import net.algem.util.ui.PopupDlg;

/**
 * Single rehearsal controller for a member.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 * @since 1.0a 12/12/2001
 */
public class MemberRehearsalCtrl
        extends FileTabDialog
{

  private PersonFile personFile;
  private MemberRehearsalView view;
  private ActionListener actionListener;
  private MemberService memberService;

  public MemberRehearsalCtrl(GemDesktop desktop) {
    super(desktop);
    memberService = new MemberService(dc);
  }

  public MemberRehearsalCtrl(GemDesktop desktop, ActionListener listener, PersonFile dossier) {
    this(desktop);
    personFile = dossier;
    actionListener = listener;

    view = new MemberRehearsalView(dataCache.getList(Model.Room));
    view.set(personFile.getContact());

    setLayout(new BorderLayout());
    add(view, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
  }

  public void clear() {
    view.clear();
  }

  @Override
  public void load() {
    view.set(personFile.getContact());
  }

  @Override
  public boolean isLoaded() {
    return personFile != null;
  }

  @Override
  public void cancel() {
    actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "AdherentRepetitionPonctuelle.Abandon"));
  }

  @Override
  public void validation() {

    if (!isEntryValid(view.getDate())) {
      return;
    }
    try {
      if (!save()) {
        return;
      }
      JOptionPane.showMessageDialog(this,
              MessageUtil.getMessage("planning.update.info"),
              MessageUtil.getMessage("rehearsal.member.entry"),
              JOptionPane.INFORMATION_MESSAGE);
      desktop.postEvent(new ModifPlanEvent(this, view.getDate(), view.getDate()));
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "AdherentRepetitionPonctuelle.Validation"));
    } catch (MemberException ex) {
      GemLogger.logException(MessageUtil.getMessage("rehearsal.create.exception"), ex, this);
    }
  }

  /**
   * Updates member's card for single rehearsal.
   * If card doesn't exist, a new one is created.
   * Else, the remaining time is updated.
   * If there is not enough remaining time, a new card is created.
   *
   * @param pFile member's file
   * @date date of rehearsal
   * @param length rehearsal length
   * @param dlg dialog for selecting a subscription
   *
   * @return an amount
   * @throws SQLException
   */
  PersonSubscriptionCard updatePersonalCard(PersonFile pFile, RehearsalCard pass, ScheduleObject dto) throws SQLException {

    PersonSubscriptionCard currentCard = pFile.getSubscriptionCard();
    
    PersonSubscriptionCard nc = null;
    PersonFileEvent event = null;

    int timeLength = new Hour(dto.getStart()).getLength(new Hour(dto.getEnd()));
    if (currentCard == null) {//aucune carte n'existe pour cette personne
      nc = createNewCard(pass, timeLength, pFile.getId(), new DateFr(new Date()), dto);//XXX choix peut etre null
      event = new PersonFileEvent(nc, PersonFileEvent.SUBSCRIPTION_CARD_CHANGED);
    } else {
      currentCard.setSessions(memberService.getSessions(currentCard.getId()));
      int remainder = calcRemainder(currentCard.getRest(), timeLength);
      if (remainder < 0) { // plus de place sur la carte
        currentCard.setRest(0);
        Hour start = new Hour(dto.getStart());
        Hour offset = new Hour(start);
        offset.incMinute(remainder);
        Hour end = new Hour(dto.getEnd());
        dto.setStart(offset);
        dto.setEnd(end);
        nc = createNewCard(pass, Math.abs(remainder), currentCard.getIdper(), new DateFr(new Date()), dto);
        //current card session offset
        dto.setStart(start);
        dto.setEnd(offset);
      } else {
        currentCard.setRest(remainder);
      }
      // update abo
      currentCard.addSession(dto);
      memberService.update(currentCard);

      if (currentCard.getRest() <= 0 && nc != null) {
        event = new PersonFileEvent(nc, PersonFileEvent.SUBSCRIPTION_CARD_CHANGED);
      } else {
        event = new PersonFileEvent(currentCard, PersonFileEvent.SUBSCRIPTION_CARD_CHANGED);
      }
    }
    if (actionListener != null) {
      ((PersonFileEditor) actionListener).contentsChanged(event);
    }
    return nc;
  }

  /**
   *
   * @param card selected card
   * @param length rehearsal length
   * @return 0 if length is longer than the total length
   */
  int calcRemainder(RehearsalCard card, int length) {
    int totalLength = card.getTotalLength();
    if (totalLength > length) {
      return totalLength - length;
    }
    return 0;
  }

  /**
   *
   * @param length rehearsal length
   * @param remainder remainder length on the card
   * @return the new remainder length
   */
  int calcRemainder(int length, int remainder) {
    return length - remainder;
  }

  /**
   * Creates a new subscription card.
   *
   * @param card selected card
   * @param length rehearsal length
   * @param idper person's id
   * @throws SQLException
   */
  PersonSubscriptionCard createNewCard(RehearsalCard card, int length, int idper, DateFr date, ScheduleObject dto) throws SQLException {

    PersonSubscriptionCard c = new PersonSubscriptionCard();
    c.setIdper(idper);
    c.setPassId(card.getId());
    c.setPurchaseDate(date);
    c.setRest(calcRemainder(card, length));
    c.addSession(dto);

    memberService.create(c);
    return c;
  }

  /**
   * Selects a subscription.
   *
   * @param dialog
   * @return a rehearsal card
   */
  private RehearsalCard chooseCard(PopupDlg dialog) {
    dialog.show();
    if (dialog.isValidation()) {
      return ((RehearsalCardDlg) dialog).get();
    }
    return null;
  }

  private boolean isEntryValid(DateFr date) {

    String dateError = MessageUtil.getMessage("date.entry.error");//date incorrecte
    String entryError = MessageUtil.getMessage("entry.error");

    if (date.bufferEquals(DateFr.NULLDATE)) {
      MessagePopup.error(view, dateError, entryError);
      return false;
    }
    if (date.before(dataCache.getStartOfPeriod())
            || date.after(dataCache.getEndOfPeriod())) {
      MessagePopup.error(view, MessageUtil.getMessage("date.out.of.period"), entryError);
      return false;
    }

    Hour hStart = view.getHourStart();
    Hour hEnd = view.getHourEnd();

    if (hStart.toString().equals("00:00")
            || hEnd.toString().equals("00:00")
            || !(hEnd.after(hStart))) {
      MessagePopup.error(view, MessageUtil.getMessage("hour.range.error"), entryError);
      return false;
    }

    if (!RoomService.isClosed(view.getRoom(), date, hStart, hEnd)) {
      return false;
    }

    return true;
  }

  private boolean save() throws MemberException {
ScheduleObject so = new MemberRehearsalSchedule();
so.setDate(view.getDate());
so.setIdPerson(personFile.getId());
so.setStart(view.getHourStart());
so.setEnd(view.getHourEnd());
so.setIdRoom(view.getRoom());

    ScheduleDTO p = new ScheduleDTO();

    p.setDay(view.getDate().toString());
    p.setStart(view.getHourStart().toString());
    p.setEnd(view.getHourEnd().toString());
    p.setPersonId(personFile.getId());
    p.setPlace(view.getRoom());

    if (!isFree(so)) {
      return false;
    }

    boolean subscription = view.withCard();

    so.setType(Schedule.MEMBER);
    so.setNote(0);
    try {
      memberService.saveRehearsal(p);
      //ajout échéance et mise à jour choix abonnement
      if (subscription) {
        int length = view.getHourStart().getLength(view.getHourEnd());
        PopupDlg dlg = new RehearsalCardDlg(view, memberService.getPassList());
        // recherche d'une choix d'abonnement pour cet adhérent
        RehearsalCard pass = chooseCard(dlg);
        PersonSubscriptionCard newCard = updatePersonalCard(personFile, pass, so);
//        float amount = pass.getAmount();
        if (newCard != null) {
//        event = new PersonFileEvent(nc, PersonFileEvent.SUBSCRIPTION_CARD_CHANGED);
//      } else {
//        event = new PersonFileEvent(currentCard, PersonFileEvent.SUBSCRIPTION_CARD_CHANGED);
//      }
          memberService.saveRehearsalOrderLine(personFile, view.getDate(), pass.getAmount());
        }
      } else {
        // calcul montant repet
        Room s = ((RoomIO) DataCache.getDao(Model.Room)).findId(view.getRoom());
        double amount = RehearsalUtil.calcSingleRehearsalAmount(view.getHourStart(), view.getHourEnd(), s.getRate(), 1, dc);
        if (amount > 0.0) {
          memberService.saveRehearsalOrderLine(personFile, view.getDate(), amount);
        }
      }
    } catch (MemberException e) {
      throw e;
    } catch (SQLException sqe) {
      throw new MemberException(sqe.getMessage());
    }
    return true;
  }

  private boolean isFree(ScheduleObject p) {
    // room checking
    String query = ConflictQueries.getRoomConflictSelection(p.getDate().toString(), p.getStart().toString(), p.getEnd().toString(), p.getIdRoom());
    if (ScheduleIO.count(query, dc) > 0) {
      MessagePopup.error(view, BundleUtil.getLabel("Room.conflict.label"), BundleUtil.getLabel("Conflit.label"));
      return false;
    }
    // rehearsal member checking
    query = ConflictQueries.getMemberRehearsalSelection(p.getDate().toString(), p.getStart().toString(), p.getEnd().toString(), p.getIdPerson());
    if (ScheduleIO.count(query, dc) > 0) {
      MessagePopup.error(view, BundleUtil.getLabel("Member.conflict.label"), BundleUtil.getLabel("Conflit.label"));
      return false;
    }

    // course member checking
    query = ConflictQueries.getMemberScheduleSelection(p.getDate().toString(), p.getStart().toString(), p.getEnd().toString(), p.getIdPerson());
    if (ScheduleIO.count(query, dc) > 0) {
      MessagePopup.error(view, BundleUtil.getLabel("Member.conflict.label"), BundleUtil.getLabel("Conflit.label"));
      return false;
    }
    return true;
  }
}
