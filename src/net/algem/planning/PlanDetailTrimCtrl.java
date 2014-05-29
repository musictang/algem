/*
 * @(#)PlanDetailTrimCtrl.java	2.6.a 04/08/2012
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

import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import net.algem.util.DataCache;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;


/**
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 * @deprecated 
 */
public abstract class PlanDetailTrimCtrl
        extends GemPanel
        implements ActionListener, ItemListener
{

  protected DataCache cache;
  protected CanvasPlanTrim plan;
  protected GemChoice choix;
  protected DateBar barre;
  protected Date curDate;
  protected int curId;
  protected int curOID;
  protected String libelId;
  protected GemField datelib;
  protected GemField status;
  protected String[] moisLibel;
  protected Calendar cal;

  public PlanDetailTrimCtrl(DataCache dc, String l) {
    super();

    libelId = l;
    cache = dc;

    cal = Calendar.getInstance(Locale.FRANCE);
  }

  public void init() {
    plan = new CanvasPlanTrim();

    choix.addItemListener(this);

    curDate = new Date();
    datelib = new GemField(24);
    datelib.setEditable(false);

    cal.setTime(new Date());

    moisLibel = new DateFormatSymbols(Locale.FRANCE).getMonths();
    datelib.setText(moisLibel[cal.get(Calendar.MONTH)] + " " + cal.get(Calendar.YEAR));

    status = new GemField();
    status.setFont(new Font("Helvetica", Font.BOLD, 12));
    status.setEditable(false);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(datelib, 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(choix, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(plan, 0, 1, 2, 1, GridBagHelper.BOTH, 1.0, 1.0);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() instanceof DateBar) {
      load(choix.getKey(), ((DateBar) evt.getSource()).getDate());
    } else if (evt.getActionCommand().equals("Date")) {
      load(choix.getKey(), (Date) evt.getSource());
    }
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    if (e.getSource() instanceof GemChoice) {
      load(choix.getKey(), curDate);
    }
  }

  int load(int id, Date date) {
    cal.setTime(date);
    int an = cal.get(Calendar.YEAR);
    int mois = cal.get(Calendar.MONTH) + 1;

    String attrib = Schedule.attribFromLabel(libelId);
    String query = "WHERE " + attrib + "=" + id
            + " AND jour >= '01/01/" + an + "'"
            + " AND jour <= '31/12/" + an + "'";

    Vector<Schedule> pl = ScheduleIO.findCourse(query, cache.getDataConnection());

    plan.loadPlanning(pl);
    curDate = date;
    curId = id;
    //XXXchoix.setKey(id);

    datelib.setText(moisLibel[mois - 1] + " " + an);

    return (pl != null ? pl.size() : 0);
  }
}
