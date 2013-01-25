/*
 * @(#)GemChoiceFilterModel.java	2.7.a 14/01/13
 * 
 * Copyright (c) 1998 Musiques Tangentes. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.event.ListDataEvent;
import net.algem.util.model.GemList;
import net.algem.util.model.GemModel;

/**
 * Filter model for combo boxes.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0 11/02/1998
 */
public abstract class GemChoiceFilterModel
        extends GemChoiceModel {

  protected List<Integer> indices = new ArrayList<Integer>();

  public <T extends GemModel> GemChoiceFilterModel(GemList<T> list) {
    super(list);
  }

  public <T extends GemModel> GemChoiceFilterModel(Vector<T> list) {
    this(new GemList<T>(list));
  }

  @Override
  public int getSize() {
    return indices.size();
  }
  
  public int getSelectedIndex() {
    for(int i = 0; i < getSize(); i++) {
      if (getElementAt(i).equals(selected)) {
        return i;
      }
    }
    return 0;
  }

  @Override
  public Object getElementAt(int index) {
    return super.getElementAt(indices.get(index));
  }

  @Override
  public void intervalAdded(ListDataEvent evt)
  {
    GemModel m = (GemModel) list.getElementAt(evt.getIndex0());
    if (isFilterOk(m)) {
      indices.add(new Integer(evt.getIndex0()));
      fireIntervalAdded(this, indices.size() - 1, indices.size() - 1);
    }
  }

  @Override
  public void intervalRemoved(ListDataEvent evt) {
    load(list);
    fireContentsChanged(this, -1, -1);
  }

  @Override
  public void contentsChanged(ListDataEvent evt) {
    load(list);
    fireContentsChanged(this, -1, -1);
  }

  public <T extends GemModel> void load(GemList<T> l) {
    indices.clear();
    for (int i = 0; i < l.getSize(); i++) {
      GemModel m = (GemModel) l.getElementAt(i);
      if (isFilterOk(m)) {
        indices.add(i);
      }
    }
  }

  public abstract boolean isFilterOk(GemModel m);
}
