/*
 * @(#)ParamChoice.java	2.8.f 24/05/13
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
package net.algem.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.algem.util.ui.GemChoice;

/**
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.f
 * @since 2.0r
 */
public class ParamChoice
        extends GemChoice
{

  protected Map<String, Param> _categories;

  public ParamChoice() {
  }

  public <T extends Param> ParamChoice(Collection<T> parameters) {
    
    _categories = new HashMap<String, Param>();

    for (Param p : parameters) {
      addItem(p);
      _categories.put(p.getKey(), p);
    }
  }

  @Override
  public int getKey() {
    String key = ((Param) getSelectedItem()).getKey();
    return Integer.parseInt(key);
  }

  @Override
  public void setKey(int k) {
    Param p = _categories.get(String.valueOf(k));
    setSelectedItem(p);
  }

  public void setKey(String k) {
    Param p = _categories.get(k);
    setSelectedItem(p);
  }

  public String getValue() {
    Param p = (Param) getSelectedItem();
    return p.getValue();
  }

  public void setValue(String val) {

    for (Param p : _categories.values()) {
      if (p.getValue().equals(val.trim())) {
        setSelectedItem(p);
        break;
      }
    }
  }

}
