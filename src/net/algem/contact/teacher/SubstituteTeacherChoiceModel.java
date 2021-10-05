/*
 * @(#)SubstituteTeacherChoiceModel.java	2.6.a 19/09/12
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
package net.algem.contact.teacher;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @since 2.0n
 */
public class SubstituteTeacherChoiceModel
        extends AbstractListModel
        implements ComboBoxModel, ListDataListener
{

  protected SubstituteTeacherList list;
  protected Object currentValue;

  public SubstituteTeacherChoiceModel(SubstituteTeacherList list) {
    this.list = list;
    if (list != null && list.getSize() > 0) {
      currentValue = list.getElementAt(0);
    }
    if (this.list != null)
    this.list.addListDataListener(this);
  }

  public SubstituteTeacher getTeacher(int id) {
    return list.getTeacher(id);
  }

  @Override
  public int getSize() {
    return list.getSize();
  }

  @Override
  public Object getElementAt(int index) {
    return list.getElementAt(index);
  }

  @Override
  public Object getSelectedItem() {
    return currentValue;
  }

  @Override
  public void setSelectedItem(Object o) {
    currentValue = o;
    fireContentsChanged(this, -1, -1); //XXX ItemSelectEvent
  }

  @Override
  public void intervalAdded(ListDataEvent evt) {
    fireIntervalAdded(this, evt.getIndex0(), evt.getIndex1());
  }

  @Override
  public void intervalRemoved(ListDataEvent evt) {
    fireIntervalRemoved(this, evt.getIndex0(), evt.getIndex1());
  }

  @Override
  public void contentsChanged(ListDataEvent evt) {
    fireContentsChanged(this, evt.getIndex0(), evt.getIndex1());
  }
}
