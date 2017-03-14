/*
 * @(#)InstrumentView.java 2.7.k 01/03/13
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

import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import net.algem.config.Instrument;
import net.algem.util.BundleUtil;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.k
 * @since 2.7.a 21/11/12
 */
public class InstrumentView
  extends InfoView
{
  private List<Instrument> list;

  public InstrumentView(List<Instrument> list) {
    super(BundleUtil.getLabel("Instruments.label"), false);
    this.list = list;
  }

  public InstrumentView(List<Instrument> list, String label) {
    super(label, false);
    this.list = list;
  }

  public void set(List<Integer> instruments) {
    if (instruments == null) {
      addRow();
    } else {
      for (int i = 0 ; i < instruments.size(); i++) {
        int instr = instruments.get(i);
        addRow();
        ((InstrumentPanel) rows.get(i)).setInstrument(instr);
      }
    }
  }

  @Override
  protected void addRow() {
    InstrumentPanel ip = new InstrumentPanel(list);
    rows.add(ip);
    add(Box.createVerticalStrut(5));
    add(ip);
    revalidate();
  }

  public List<Integer> get() {
    List<Integer> li = new ArrayList<Integer>();
    for (int i = 0 ; i < rows.size(); i++) {
      InstrumentPanel ip = (InstrumentPanel) rows.get(i);
      if (ip.getInstrument() > 0) {
        li.add(ip.getInstrument());
      }
    }
    return li.isEmpty() ? null : li;

  }

}
