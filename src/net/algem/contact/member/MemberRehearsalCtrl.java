/*
 * @(#)MemberRehearsalCtrl.java	2.8.b 14/05/13
 *
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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
import javax.swing.JOptionPane;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonFileEditor;
import net.algem.contact.PersonFileEvent;
import net.algem.planning.*;
import net.algem.planning.editing.MemberRehearsalView;
import net.algem.planning.editing.ModifPlanEvent;
import net.algem.room.Room;
import net.algem.room.RoomIO;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabDialog;
import net.algem.util.ui.PopupDlg;

/**
 * Single rehearsal controller for a member.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.b
 * @since 1.0a 12/12/2001
 */
public class MemberRehearsalCtrl
        extends FileTabDialog
{

  private PersonFile personFile;
  private MemberRehearsalView view;
  private ActionListener actionListener;
  private MemberService service;

  public MemberRehearsalCtrl(GemDesktop _desktop) {
    super(_desktop);
    service = new MemberService(dataCache.getDataConnection());
  }

  public MemberRehearsalCtrl(GemDesktop _desktop, ActionListener _listener, PersonFile _dossier) {
    this(_desktop);
    personFile = _dossier;
    actionListener = _listener;

    view = new MemberRehearsalView(dataCache);
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
   * @param dialog dialog for selecting a subscription
   *
   * @return an amount
   * @throws SQLException
   */
  float setRehearsalCard(PersonFile pFile, DateFr date, int length, PopupDlg dialog) throws SQLException {
    float amount = 0.0F;
    PersonSubscriptionCard nc = null;
    PersonFileEvent event = null;
    PersonSubscriptionCard abo = pFile.getSubscriptionCard();
    if (abo == null) {//aucune carte n'existe pour cette personne
      RehearsalCard choice = chooseCard(dialog);
      nc = createNewCard(choice, length, pFile.getId(), date);//XXX choix peut etre null
      event = new PersonFileEvent(nc, PersonFileEvent.SUBSCRIPTION_CARD_CHANGED);
      amount = choice.getAmount();
    } else {
      int remainder = calcRemainder(abo.getRest(), length);
      if (remainder < 0) { // plus de place sur la carte
        abo.setRest(0);
        RehearsalCard card = chooseCard(dialog);
        nc = createNewCard(card, Math.abs(remainder), abo.getIdper(), date);
        amount = card.getAmount();
      } else {
        abo.setRest(remainder);
      }
      // update abo
      service.update(abo);
      if (abo.getRest() <= 0 && nc != null) {
        event = new PersonFileEvent(nc, PersonFileEvent.SUBSCRIPTION_CARD_CHANGED);
      } else {
        event = new PersonFileEvent(abo, PersonFileEvent.SUBSCRIPTION_CARD_CHANGED);
      }
    }
    if (actionListener != null) {
      ((PersonFileEditor) actionListener).contentsChanged(event);
    }
    return amount;
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
  PersonSubscriptionCard createNewCard(RehearsalCard card, int length, int idper, DateFr date) throws SQLException {

    PersonSubscriptionCard subscriptionCard = new PersonSubscriptionCard();
    subscriptionCard.setIdper(idper);
    subscriptionCard.setRehearsalCardId(card.getId());
    subscriptionCard.setPurchaseDate(date);
    subscriptionCard.setRest(calcRemainder(card, length));

    service.create(subscriptionCard);
    return subscriptionCard;
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
      JOptionPane.showMessageDialog(view,
              dateError,
              entryError,
              JOptionPane.ERROR_MESSAGE);
      return false;
    }
    if (date.before(dataCache.getStartOfPeriod())
            || date.after(dataCache.getEndOfPeriod())) {
      JOptionPane.showMessageDialog(view,
              MessageUtil.getMessage("date.out.of.period"),
              entryError,
              JOptionPane.ERROR_MESSAGE);
      return false;
    }
    Hour hdeb = view.getHourStart();
    Hour hfin = view.getHourEnd();
    if (hdeb.toString().equals("00:00")
            || hfin.toString().equals("00:00")
            || !(hfin.after(hdeb))) {
      JOptionPane.showMessageDialog(view,
              MessageUtil.getMessage("hour.range.error"),
              entryError,
              JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }

  private boolean save() throws MemberException {

    ScheduleDTO p = new ScheduleDTO();

    p.setDay(view.getDate().toString());
    p.setStart(view.getHourStart().toString());
    p.setEnd(view.getHourEnd().toString());
    p.setPersonId(personFile.getId());
    p.setPlace(view.getRoom());

    if (!isFree(p)) {
      return false;
    }

    boolean subscription = view.withCard();

    p.setType(Schedule.MEMBER_SCHEDULE);
    p.setNote(0);
    try {
      service.saveRehearsal(p);
      //ajout échéance et mise à jour choix abonnement
      if (subscription) {
        int length = view.getHourStart().getLength(view.getHourEnd());
        PopupDlg dialog = new RehearsalCardDlg(view, service.getPassList());
        // recherche d'une choix d'abonnement pour cet adhérent
        float amount = setRehearsalCard(personFile, view.getDate(), length, dialog);
        if (amount > 0.0f) {
          service.saveOrderLine(personFile, view.getDate(), amount);
        }
      } else {
        // calcul montant repet
        Room s = ((RoomIO) DataCache.getDao(Model.Room)).findId(view.getRoom());
        double amount = RehearsalUtil.calcSingleRehearsalAmount(view.getHourStart(), view.getHourEnd(), s.getRate(), 1, dc);
        if (amount > 0.0) {
          service.saveOrderLine(personFile, view.getDate(), amount);
        }
      }
    } catch (MemberException e) {
      throw e;
    } catch (SQLException sqe) {
      throw new MemberException(sqe.getMessage());
    }
    return true;
  }

  private boolean isFree(ScheduleDTO p) {
    // room checking
    String query = ConflictQueries.getRoomConflictSelection(p.getDay(), p.getStart(), p.getEnd(), p.getPlace());
    if (ScheduleIO.count(query, dc) > 0) {
      JOptionPane.showMessageDialog(null,
              "salle occupée",
              "Conflit planning",
              JOptionPane.ERROR_MESSAGE);
      return false;
    }
    // rehearsal member checking
    query = ConflictQueries.getMemberRehearsalSelection(p.getDay(), p.getStart(), p.getEnd(), p.getPersonId());
    if (ScheduleIO.count(query, dc) > 0) {
      JOptionPane.showMessageDialog(null,
              "Adhérent occupé",
              "Conflit planning",
              JOptionPane.ERROR_MESSAGE);
      return false;
    }

    // course member checking
    query = ConflictQueries.getMemberScheduleSelection(p.getDay(), p.getStart(), p.getEnd(), p.getPersonId());
    if (ScheduleIO.count(query, dc) > 0) {
      JOptionPane.showMessageDialog(null,
              "Adhérent occupé",
              "Conflit planning",
              JOptionPane.ERROR_MESSAGE);
      return false;
    }
    return true;
  }
}
