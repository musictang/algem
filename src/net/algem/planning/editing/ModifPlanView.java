/*
 * @(#)ModifPlanView.java	2.8.w 05/09/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

import java.awt.AWTEventMulticaster;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import net.algem.planning.DateFr;
import net.algem.planning.DateRangePanel;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Abstract class for planning modification views.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 1.0a 07/07/1999
 */
public abstract class ModifPlanView
        extends GemPanel
{

  public final static Border DEFAULT_BORDER = BorderFactory.createEmptyBorder(10, 10, 10, 10);
  protected final static int DEF_FIELD_WIDTH = 22;
  
  protected DataCache dataCache;
  protected GridBagHelper gb;
  protected int id;
  protected GemField courseLabel;
  protected DateRangePanel dateRange;
  protected ActionListener actionListener;
  

  public ModifPlanView(DataCache dataCache) {
    this(dataCache, BundleUtil.getLabel("Schedule.default.modification.label"));
  }

  public ModifPlanView(DataCache dataCache, String label) {

    this.dataCache = dataCache;
    setBorder(DEFAULT_BORDER);
    courseLabel = new GemField(DEF_FIELD_WIDTH);
    courseLabel.setEditable(false);

    dateRange = new DateRangePanel(DateRangePanel.RANGE_DATE, GemField.getDefaultBorder());

    GemPanel p = new GemPanel();
    p.setLayout(new GridBagLayout());
    gb = new GridBagHelper(p);
    gb.insets = new Insets(4, 2, 4, 2);
    gb.add(new GemLabel(label), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(courseLabel, 1, 0, 3, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.From.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(dateRange, 1, 1, 3, 1, GridBagHelper.WEST);

    add(p);
  }

  public abstract void setId(int i);

  public abstract int getId();

  public void setTitle(String s) {
    courseLabel.setText(s);
  }

  public void setStart(Date d) {
    dateRange.setStart(d);
  }

  public DateFr getStart() {
    return dateRange.getStartFr();
  }

  public void setEnd(Date d) {
    dateRange.setEnd(d);
  }

  public DateFr getEnd() {
    return dateRange.getEndFr();
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  public void clear() {
  }
}
