/*
 * @(#)PersonFile.java 2.8.m 06/09/13
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
package net.algem.contact;

import java.util.Collection;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import net.algem.bank.BankUtil;
import net.algem.bank.Rib;
import net.algem.contact.member.Member;
import net.algem.contact.member.PersonSubscriptionCard;
import net.algem.contact.teacher.Teacher;
import net.algem.group.Group;
import net.algem.util.MessageUtil;
import net.algem.util.model.GemModel;

/**
 * Person management.
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.m
 * @since 1.0a 12/08/2009
 */
public class PersonFile
  implements GemModel
{
  private Contact contact, oldContact;
  private Member member, oldMember;
  private Teacher teacher, oldTeacher;
  private Rib rib, oldRib;
  protected EventListenerList listenerList = new EventListenerList();
  private PersonSubscriptionCard subscriptionCard;
  private Collection<Group> groups;

  public PersonFile() {
  }

  public PersonFile(Contact _contact) {
    contact = oldContact = _contact;
  }

  @Override
  public void setId(int id) {
    if (contact != null) {
      contact.setId(id);
    }
  }
  
  @Override
  public int getId() {
    return contact == null ? 0 : contact.getId();
  }

  public Contact getContact() {
    return contact;
  }

  public Contact getOldContact() {
    return oldContact;
  }

  public void setContact(Contact _contact) {
    //oldContact = contact;
    contact = _contact;
//    if (_contact != null) {
//      fireContentsChanged(_contact, PersonFileEvent.CONTACT_CHANGED);
//    }
  }

  public Member getMember() {
    return member;
  }

  public Member getOldMember() {
    return oldMember;
  }

  public void setMember(Member m) {

    if (oldMember == null) {
      oldMember = m;
    }

    this.member = m;
  }

  public void removeMember() {
    member = oldMember = null;
  }

  public void addMember(Member m) {
    member = m;
  }

  public Teacher getTeacher() {
    return teacher;
  }

  public Teacher getOldTeacher() {
    return oldTeacher;
  }

  public void setTeacher(Teacher t) {
    teacher = t;
  }

  public void setOldTeacher(Teacher t) {
    this.oldTeacher = t;
  }
  
  public void loadTeacher(Teacher t) {
    teacher = oldTeacher = t;
  }

  public void removeTeacher() {
    teacher = oldTeacher = null;
  }

  /**
   * Rib.
   * @param b 
   */
  public void setRib(Rib b) {
    oldRib = rib;
    rib = b;
  }

  public Rib getRib() {
    return rib;
  }

  public Rib getOldRib() {
    return oldRib;
  }

  public void removeRib() {
//    fireContentsChanged(rib, PersonFileEvent.BANK_REMOVED);
    rib = oldRib = null;  
  }

  public void addRib(Rib b) {
    rib = b;
  }

  public void setSubscriptionCard(PersonSubscriptionCard card) {
    this.subscriptionCard = card;
  }

  public PersonSubscriptionCard getSubscriptionCard() {
    return subscriptionCard;
  }

  public boolean hasChanged() {
    if (contact.getId() == 0 && contact.getName().length() > 0) {
      return true;
    } else if (oldContact != null && !contact.equals(oldContact)) {
      return true;
    } else if (member != null && !member.equals(oldMember)) {
      return true;
    } else if (teacher != null && !teacher.equals(oldTeacher)) {
      return true;
    } //Modification du test (oldBic != null) . Correction bug création rib 2.0d
    else if (rib != null && !rib.equals(oldRib)) {
      return true;
    }
    return false;
  }

  /**
   * Vérification de la validité d'une fiche personne.
   * @return un message d'erreurs ou null sinon
   */
  public String hasErrors() {
    StringBuilder error = new StringBuilder();
    String r = null;
    if (!contact.isValid()) {
      error.append(MessageUtil.getMessage("invalid.contact"));
    }
    if (member != null && !member.isValid()) {
      error.append(MessageUtil.getMessage("invalid.member"));
    }
    if (teacher != null && !teacher.isValid()) {
      error.append(MessageUtil.getMessage("invalid.teacher"));
    }
    if (rib != null && !rib.isEmpty()) {
      if (!rib.hasCorrectLength()) {
        error.append(MessageUtil.getMessage("rib.length.warning"));
        rib = oldRib;// @since 2.2.q restoration en cas d'invalidité
      } else if (!BankUtil.isRibOk(rib.toString())) {
        error.append(MessageUtil.getMessage("invalid.rib", new Object[]{rib.toString()}));
        rib = oldRib;// @since 2.2.q
      }
    }
    return error.length() > 0 ? error.toString() : null;
  }

  public boolean isPayerLinked() {
    if (getContact() == null) {
      return false;
    }
    return getContact().getAddress() == null && getContact().getTele() == null;
  }

  public void addPersonFileListener(PersonFileListener l) {
    listenerList.add(PersonFileListener.class, l);
  }

  public void removeDossierPersonneListener(PersonFileListener l) {
    listenerList.remove(PersonFileListener.class, l);
  }

  protected void fireContentsChanged(Object source, int event) {
    Object[] listeners = listenerList.getListenerList();
    PersonFileEvent e = new PersonFileEvent(source, event);
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == PersonFileListener.class) {
        ((PersonFileListener) listeners[i + 1]).contentsChanged(e);
      }
    }
  }

  @Override
  public String toString() {
    return contact.toString();
  }

  public void clearOldValues() {
    oldContact = null;
    oldMember = null;
    oldTeacher = null;
    oldRib = null;
  }

  public Object[] backUpOldValues() {
    return new Object[]{getOldContact(), getOldMember(), getOldTeacher(), getOldRib()};
  }

  public void restoreOldValues(Object[] values) {
    oldContact = (Contact) values[0];
    oldMember = (Member) values[1];
    oldTeacher = (Teacher) values[2];
    oldRib = (Rib) values[3];
  }

  public void setOldValues() {
    oldContact = getContact();
    oldMember = getMember();
    oldTeacher = getTeacher();
    oldRib = getRib();
  }

  public void setGroups(Vector<Group> groups) {
    this.groups = groups;
  }

  public Collection<Group> getGroups() {
    return groups;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PersonFile other = (PersonFile) obj;
    if (this.getId() != other.getId()) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 37 * hash + this.getId();
    return hash;
  }
  
}
