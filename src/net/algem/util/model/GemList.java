/*
 * @(#)GemList.java 2.9.4.13 27/10/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

package net.algem.util.model;

import java.util.*;
import javax.swing.AbstractListModel;

/**
 * Base class for list of GemModel objects.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @param <T>
 * @since 2.5.a 28/06/12
 */
public class GemList<T extends GemModel>
  extends AbstractListModel
{

  protected List<T> list;

  public GemList() {
    this.list = new ArrayList<T>();
  }
  
  public GemList(List<T> list) {
    this.list = list;//new ArrayList<T>(list);
  }

  @Override
  public int getSize() {
    return list.size();
  }

  /**
   * Gets element at {@code index}.
   * @param index
   * @return an object
   */
  @Override
  public Object getElementAt(int index) {
    return list.get(index);
  }
  
  /**
   * Gets {@code obj} id.
   * @param obj
   * @return an integer
   */
  public int getId(T obj) {
    int index = list.indexOf(obj);
    if (index < 0) {
      index = 0;
    }
    return list.get(index).getId();
  }
  
  /**
   * Gets {@code obj} index in the list.
   * @param obj
   * @return an integer
   */
  public int indexOf(Object obj) {
    if (!(obj instanceof GemModel)) {
      return -1;
    }
    for (int i = 0 ; i < list.size(); i++) {
      GemModel m = list.get(i);
      if (m.getId() == ((GemModel) obj).getId()) {
        return i;
      }
    }
    return -1;
    // the result of List.indexOf may be not valid if object's properties have been updated
    // it is preferable to check only the id as above
    // return list.indexOf(obj);
  }
  
  /**
   * Gets element in the list which id is {@code id}.
   * @param id
   * @return an object of type GemModel
   */
  public GemModel getItem(int id) {
    for (Iterator<T> it = list.iterator(); it.hasNext();) {
      T m = it.next();
      if (m.getId() == id) {
        return m;
      }
    }
    return null;
  }
  
  /**
   * Gets the list of elements.
   * @return a list
   */
  public List<T> getData() {
    return list;
  }

  /**
   * Sets the element {@code obj} at position {@code index}.
   * @param obj
   * @param index 
   */
  public void setElementAt(T obj, int index) {
    list.set(index, obj);
    fireContentsChanged(this, index, index);
  }
  
  public void update(T obj, Comparator<T> comp) {
    
    int idx = indexOf(obj);
    if (idx > -1) {
      setElementAt(obj, idx);
      if (comp != null) {
        Collections.sort(list, comp);
      }
    }
  }
  
  /**
   * Adds element {@code obj} at end of list.
   * @param obj
   */
  public void addElement(T obj) {
    int index = list.size();
    list.add(obj);
    fireIntervalAdded(this, index, index);
  }

  /**
   * Deletes the element {@code obj} from the list.
   * @param obj 
   */
  public void removeElement(T obj) {
    int index = list.indexOf(obj);
    if (index > -1) {
      list.remove(obj);
      fireIntervalRemoved(obj, index, index);
    }
  }

}
