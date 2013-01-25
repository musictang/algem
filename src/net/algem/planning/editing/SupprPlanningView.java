/*
 * @(#)SupprPlanningView.java	2.6.a 21/09/12
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
 */
package net.algem.planning.editing;

import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.Date;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.planning.Schedule;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class SupprPlanningView
        extends GemPanel
{

  private GemLabel title;
  private DateFrField start;
  private DateFrField end;
  private ActionListener actionListener;

  public SupprPlanningView(Schedule plan) {
    start = new DateFrField(plan.getDay());
    start.setEditable(false);
    end = new DateFrField(plan.getDay());
    setBorder(ModifPlanView.DEFAULT_BORDER);

    title = new GemLabel(BundleUtil.getLabel("Scheduling.label").toLowerCase() + " : " + plan.getIdAction());
    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(new GemLabel(GemCommand.DELETE_CMD+" "), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(title, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.From.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(start, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.To.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(end, 1, 2, 1, 1, GridBagHelper.WEST);
  }

  void setStart(Date d) {
    start.set(d);
  }

  DateFr getStart() {
    return start.getDateFr();
  }

  void setEnd(Date d) {
    end.set(d);
  }

  DateFr getEnd() {
    return end.getDateFr();
  }

}
