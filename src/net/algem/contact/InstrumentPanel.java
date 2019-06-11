/*
 * @(#)InstrumentPanel.java 2.12.0 14/03/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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

import java.awt.Dimension;
import java.util.List;
import net.algem.config.Instrument;
import net.algem.config.InstrumentChoice;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 * @since 2.7.a 21/11/12
 */
public class InstrumentPanel
   extends InfoPanel
{

  private InstrumentChoice instrument;

  public InstrumentPanel(List<Instrument> instruments) {
    instrument = new InstrumentChoice(instruments);
    instrument.setPreferredSize(new Dimension(200, instrument.getPreferredSize().height));
    add(instrument);
  }

  public void setInstrument(int id) {
    instrument.setKey(id);
  }

  public int getInstrument() {
    return instrument.getKey();
  }

}
