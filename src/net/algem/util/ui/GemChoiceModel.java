/*
 * @(#)GemChoiceModel.java 2.6.a 02/10/12
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
package net.algem.util.ui;

import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import net.algem.util.model.GemList;
import net.algem.util.model.GemModel;

/**
 * ComboBoxModel implementation.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.5.a 26/06/12
 */
public class GemChoiceModel
        extends AbstractListModel
        implements ComboBoxModel, ListDataListener {

  protected GemList list;
  protected Object selected;

  public GemChoiceModel(GemList list) {
    this.list = list;
    this.list.addListDataListener(this);
  }
  
  public <T extends GemModel> GemChoiceModel(Vector<T> v) {
   this(new GemList<T>(v));
  }

  @Override
  public int getSize() {
    return list.getSize();
  }
  
  public  void addElement(GemModel e) {
    list.addElement(e);
    fireContentsChanged(this, -1, -1);
  }

  @Override
  public Object getElementAt(int index) {
    return list.getElementAt(index);
  }
  
  public int indexOf(GemModel m) {
    return list.indexOf(m);
  }
  
  public void setElement(GemModel m) {
    int idx = list.indexOf(m);
    if (idx > -1) {
      list.setElementAt(m, idx);
    }
  }

  @Override
  public void setSelectedItem(Object n) {
    if (list.indexOf(n) >= 0) {
      selected = n;
    } else {
      selected = list.getElementAt(0);
    }
    fireContentsChanged(this, -1, -1); 
  }

  /**
   * Selects the element with id {@code k}.
   * @param k id
   */
  public void setSelectedItem(int k) {
    for (int i = 0 ; i < list.getSize() ; i++) {
      GemModel m = (GemModel) list.getElementAt(i);
      if (m.getId() == k) {
        setSelectedItem(m);
        break;
      }
    }
    
  }

  @Override
  public Object getSelectedItem() {
    return selected;
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
