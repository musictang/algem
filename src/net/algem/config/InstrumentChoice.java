/*
 * @(#)InstrumentChoice.java	2.7.a 21/11/12
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
package net.algem.config;

import java.util.ArrayList;
import java.util.List;
import net.algem.util.model.GemModel;
import net.algem.util.ui.GemChoice;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class InstrumentChoice
        extends GemChoice {

  private List<Instrument> list;
  
  public InstrumentChoice(List<Instrument> instruments) {

    list = instruments;
    for (Instrument i : instruments) {
      addItem(i);
    }
  }

  public InstrumentChoice() {

    list = new ArrayList<>();
    addItem(new Instrument());
  }

  public void setList(List<Instrument> instruments) {
    list = instruments;
    removeAllItems();
    for (Instrument i : instruments) {
      addItem(i);
    }
      
  }
          
  @Override
  public int getKey() {
    return ((GemModel) getSelectedItem()).getId();
  }

  @Override
  public void setKey(int k) {
   
   for(int i = 0, n = list.size(); i < n; i++) {
     Instrument ins = list.get(i);
     if (ins.getId() == k) {
       setSelectedItem(ins);
       break;
     }
   }
  }
}
