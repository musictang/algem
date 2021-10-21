/*
 * @(#)ConflictView.java		2.6.a 19/09/12
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
package net.algem.planning;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;
import javax.swing.JScrollPane;
import net.algem.util.model.GemList;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0b 05/03/2001
 */
public class ConflictView
        extends GemPanel
{

  private JList list;
  private List<String> values;

  public ConflictView() {
    values = new ArrayList<>();
    list = new JList(new GemList(values));
    JScrollPane sp = new JScrollPane(list);
    setLayout(new BorderLayout());
    add(sp, BorderLayout.CENTER);
  }

  public void setTotal(int n) {
    values = new ArrayList<>();
    values.add(n + " sessions à modifier");
  }

  public void setConclusion(String s) {
    values.add(s);
    list.setListData(values.toArray());
  }

  public void addScheduleRange(ScheduleRange p) {
    values.add("Vérification " + p);
  }

  public void addSchedule(Schedule p) {
    values.add("Vérification " + p);
  }

  public void addConflict(ScheduleRange p) {
    values.add("-->Conflit: " + p.getDay() + " " + p.getStart() + " " + p.getEnd());
  }

  public void addConflict(Schedule p, PlanningLib pv) {
    String label = pv.getStart() + " "
            + pv.getEnd() + " "
            + pv.getCourse() + " "
            + pv.getRoom() + " "
            + pv.getTeacher();

    values.add("-->Conflit: " + label);
  }
}
