/*
 * @(#)MemberRehearsalCtrl.java	2.7.a 26/11/12
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
 * @version 2.7.a
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
   * Mise à day de la choix d'abonnement répétition individuelle.
   * Si la carte n'existe pas, une nouvelle carte est créée.
   * Sinon, le temps restant sur la carte est mis à day. Si le temps restant est insuffisant,
   * une nouvelle carte est créée.
   * @param duration durée de la répétition
   * @param pFile dossier adhérent
   * @date date de répétition
   * @param dialog dialogue de sélection de choix d'abonnement
   * @return le montant correspondant au tarif de la choix choisie
   * @throws SQLException
   */
  float setRehearsalCard(PersonFile pFile, DateFr date, int duration, PopupDlg dialog) throws SQLException {
    float amount = 0.0F;
    PersonSubscriptionCard nc = null;
    PersonFileEvent event = null;
    PersonSubscriptionCard abo = pFile.getSubscriptionCard();
    if (abo == null) {//aucune carte n'existe pour cette personne
      RehearsalCard choice = chooseCard(dialog);
      nc = createNewCard(choice, duration, pFile.getId(), date);//XXX choix peut etre null
      event = new PersonFileEvent(nc, PersonFileEvent.SUBSCRIPTION_CARD_CHANGED);
      amount = choice.getAmount();
    } else {
      int remainder = calcRemainder(abo.getRest(), duration);
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
   * @param choix d'abonnement
   * @param duration durée de la répétition
   * @return 0 si la durée de la répétition dépasse la durée totale de la choix, sinon la soustraction des 2
   */
  int calcRemainder(RehearsalCard card, int duration) {
    int totalDuration = card.getTotalDuration();
    if (totalDuration > duration) {
      return totalDuration - duration;
    }
    return 0;
  }

  /**
   *
   * @param duration de la répétition
   * @param remainder durée restante sur la choix d'abonnement
   * @return la nouvelle durée restante sur la choix
   */
  int calcRemainder(int duration, int remainder) {
    return duration - remainder;
  }

  /**
   * Création d'une nouvelle choix d'abonnement individuel.
   * @param choix d'abonnement sélectionnée
   * @param duration de la répétition
   * @param idper id de la personne
   * @throws SQLException
   */
  PersonSubscriptionCard createNewCard(RehearsalCard card, int duration, int idper, DateFr date) throws SQLException {
    //Calendar cal = Calendar.getInstance();

    PersonSubscriptionCard subscriptionCard = new PersonSubscriptionCard();
    subscriptionCard.setIdper(idper);
    subscriptionCard.setRehearsalCardId(card.getId());
    subscriptionCard.setPurchaseDate(date);
    subscriptionCard.setRest(calcRemainder(card, duration));

    service.create(subscriptionCard);
		return subscriptionCard;
  }

  /**
   * Choix d'une choix d'abonnement
   * @param dialog
   * @return la choix sélectionnée dans le dialogue
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

    if (date.equals(DateFr.NULLDATE)) {
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
        int duree = view.getHourStart().getDuration(view.getHourEnd());
        PopupDlg dialog = new RehearsalCardDlg(view, service.getPassList());
        // recherche d'une choix d'abonnement pour cet adhérent
        float montant = setRehearsalCard(personFile, view.getDate(), duree, dialog);
        if (montant > 0.0f) {
          service.saveOrderLine(personFile, view.getDate(), montant);
        }
      } else {
        // calcul montant repet
        Room s = ((RoomIO) DataCache.getDao(Model.Room)).findId(view.getRoom());
        double montant = RehearsalUtil.calcSingleRehearsalAmount(view.getHourStart(), view.getHourEnd(), s.getRate(), 1, dc);
        if (montant > 0.0) {
          service.saveOrderLine(personFile, view.getDate(), montant);
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
		// verification salle
    String query = ConflictQueries.getRoomConflictSelection(p.getDay(), p.getStart(), p.getEnd(), p.getPlace());
    if (ScheduleIO.count(query, dc) > 0) {
      JOptionPane.showMessageDialog(null,
                                    "salle occupée",
                                    "Conflit planning",
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }
		// verification repetition adherent
    query = ConflictQueries.getMemberRehearsalSelection(p.getDay(), p.getStart(), p.getEnd(), p.getPersonId());
    if (ScheduleIO.count(query, dc) > 0) {
      JOptionPane.showMessageDialog(null,
                                    "Adhérent occupé",
                                    "Conflit planning",
                                    JOptionPane.ERROR_MESSAGE);
      return false;
    }
		
		// verification cours adherent
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

