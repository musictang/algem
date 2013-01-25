/*
 * @(#)PersonFile.java 2.7.a 29/11/12
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
package net.algem.contact;

import java.util.Collection;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import net.algem.bank.Bic;
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
 * @version 2.7.a
 * @since 1.0a 12/08/2009
 */
public class PersonFile
  implements GemModel
{
  private Contact contact, oldContact;
  private Member member, oldMember;
  private Teacher teacher, oldTeacher;
  private Bic bic, oldBic;
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
    fireContentsChanged(_contact, PersonFileEvent.CONTACT_CHANGED);
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
    fireContentsChanged(m, PersonFileEvent.MEMBER_CHANGED);
  }

  public void removeMember() {
    fireContentsChanged(member, PersonFileEvent.MEMBER_REMOVED);
    member = oldMember = null;
  }

  public void addMember(Member m) {
    member = m;
    fireContentsChanged(m, PersonFileEvent.MEMBER_ADDED);
  }

  public Teacher getTeacher() {
    return teacher;
  }

  public Teacher getOldTeacher() {
    return oldTeacher;
  }

  public void setTeacher(Teacher t) {
    oldTeacher = teacher;
    teacher = t;
    fireContentsChanged(t, PersonFileEvent.TEACHER_CHANGED);
  }

  public void removeTeacher() {
    fireContentsChanged(teacher, PersonFileEvent.TEACHER_REMOVED);
    teacher = oldTeacher = null;
  }

  public void addTeacher(Teacher t) {
    teacher = t;
    fireContentsChanged(teacher, PersonFileEvent.TEACHER_ADDED);
  }

  /**
   * Rib.
   * @param b 
   */
  public void setBic(Bic b) {
    oldBic = bic;
    bic = b;
    fireContentsChanged(b, PersonFileEvent.BANK_CHANGED);
  }

  public Bic getBic() {
    return bic;
  }

  public Bic getOldBic() {
    return oldBic;
  }

  public void removeBic() {
    fireContentsChanged(bic, PersonFileEvent.BANK_REMOVED);
    bic = oldBic = null;
  }

  public void addBic(Bic b) {
    bic = b;
    fireContentsChanged(b, PersonFileEvent.BANK_ADDED);
  }

  public void setSubscriptionCard(PersonSubscriptionCard card) {
    this.subscriptionCard = card;
  }

  public PersonSubscriptionCard getSubscriptionCard() {
    return subscriptionCard;
  }

  public boolean isModified() {
    if (contact.getId() == 0 && contact.getName().length() > 0) {
      return true;
    } else if (oldContact != null && !contact.equals(oldContact)) {
      return true;
    } else if (member != null && !member.equals(oldMember)) {
      return true;
    } else if (teacher != null && !teacher.equals(oldTeacher)) {
      return true;
    } //Modification du test (oldBic != null) . Correction bug création rib 2.0d
    else if (bic != null && !bic.equals(oldBic)) {
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
    if (bic != null && !bic.isEmpty()) {
      if (!bic.hasCorrectLength()) {
        error.append(MessageUtil.getMessage("rib.length.warning"));
        bic = oldBic;// @since 2.2.q restoration en cas d'invalidité
      } else if (!bic.isValid()) {
        error.append(MessageUtil.getMessage("invalid.rib", new Object[]{bic.toString()}));
        bic = oldBic;// @since 2.2.q
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

  public void addDossierPersonneListener(PersonFileListener l) {
    listenerList.add(PersonFileListener.class, l);
  }

  public void removeDossierPersonneListener(PersonFileListener l) {
    listenerList.remove(PersonFileListener.class, l);
  }

  protected void fireContentsChanged(Object source, int event) {
    Object[] listeners = listenerList.getListenerList();
    PersonFileEvent e = new PersonFileEvent(this, event);

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
    oldBic = null;
  }

  public Object[] backUpOldValues() {
    return new Object[]{getOldContact(), getOldMember(), getOldTeacher(), getOldBic()};
  }

  public void restoreOldValues(Object[] values) {
    oldContact = (Contact) values[0];
    oldMember = (Member) values[1];
    oldTeacher = (Teacher) values[2];
    oldBic = (Bic) values[3];
  }

  public void setOldValues() {
    oldContact = getContact();
    oldMember = getMember();
    oldTeacher = getTeacher();
    oldBic = getBic();
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
