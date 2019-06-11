/*
 * @(#)ModifPlanHourView.java	2.8.w 05/09/14
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

import net.algem.planning.Hour;
import net.algem.planning.HourRangePanel;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;


/**
 * Hour modification view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 1.0a 07/07/1999
 */
public class ModifPlanHourView
        extends ModifPlanView
{

  private HourRangePanel before;
  private HourRangePanel after;

  public ModifPlanHourView(DataCache dataCache, String label) {
    super(dataCache, label);

//    before = new GemField(DEF_FIELD_WIDTH);
    before = new HourRangePanel();
//    if (courseLabel.getBorder() instanceof LineBorder) {
//      LineBorder b = (LineBorder) courseLabel.getBorder();
      before.setBorder(GemField.getDefaultBorder());
//    }LineBorder
//    before.setBorder(BorderFactory.createLineBorder(courseLabel.getBorder());
    before.setEditable(false);
    after = new HourRangePanel();
//    before.setPreferredSize(new Dimension(after.getWidth(), before.getPreferredSize().height));

    gb.add(new GemLabel(BundleUtil.getLabel("Current.hour.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(before, 1, 2, 2, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("New.hour.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(after, 1, 3, 2, 1, GridBagHelper.WEST);
  }

  void setHour(Hour start, Hour end) {
//    before.setText(BundleUtil.getLabel("Hour.From.label") + " " 
//            + start + " " + BundleUtil.getLabel("Hour.To.label") + " " + end);
    before.setStart(start);
    before.setEnd(end);
    after.setStart(start);
    after.setEnd(end);
  }

  @Override
  public void setId(int i) {
  }

  @Override
  public int getId() {
    return 0;
  }

  Hour getHourEnd() {
    return after.getEnd();
  }

  Hour getHourStart() {
    return after.getStart();
  }

}
