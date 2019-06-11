/*
 * @(#)RehearsalCancelView.java	2.8.k 27/08/13
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
package net.algem.planning.editing;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.util.Date;
import javax.swing.BorderFactory;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.planning.Schedule;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * View used in rehearsal cancellation dialog.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.k
 * @since 1.0a 07/07/1999
 */
public class RehearsalCancelView
        extends GemPanel
{

  private GridBagHelper gb;
  private DateFrField start;
  private DateFrField end;

  public RehearsalCancelView(Schedule plan) {

    start = new DateFrField(plan.getDate());
    end = new DateFrField(plan.getDate());
    String title = null;
    if (plan.getType() == Schedule.MEMBER) {
      title = BundleUtil.getLabel("Member.label") + " " + plan.getIdPerson();
    } else {
      title = BundleUtil.getLabel("Group.label") + " " + plan.getIdPerson();
    }

    GemPanel tp = new GemPanel();
    tp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    tp.add(new GemLabel(BundleUtil.getLabel("Rehearsal.cancel.label") + " " + title));
    
    GemPanel body = new GemPanel();
    body.setLayout(new GridBagLayout());
    gb = new GridBagHelper(body);

    gb.add(new GemLabel(BundleUtil.getLabel("Date.From.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(start, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.To.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(end, 1, 1, 1, 1, GridBagHelper.WEST);

    setLayout(new BorderLayout());
    add(tp, BorderLayout.NORTH);
    add(body, BorderLayout.CENTER);
  }

  public void setStart(Date d) {
    start.set(d);
  }

  public DateFr getStart() {
    return start.getDateFr();
  }

  public void setEnd(Date d) {
    end.set(d);
  }

  public DateFr getEnd() {
    return end.getDateFr();
  }

}
