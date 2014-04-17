/*
 * @(#)ModifPlanActionView.java 2.8.t 16/04/14
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

import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import net.algem.config.*;
import net.algem.planning.Action;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.*;

/**
 * View planification parameters.
 * Modification of status, level, age range and number of places.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 2.5.a 22/06/12
 */
class ModifPlanActionView
  extends GemPanel
{
  private GemParamChoice status;
  private GemParamChoice level;
  private GemParamChoice ageRange;
  private GemNumericField places;

  public ModifPlanActionView(DataCache dataCache, Action a) throws SQLException {
    status = new GemParamChoice(new GemChoiceModel<Status>(dataCache.getList(Model.Status)));
    status.setKey(a.getStatus().getId());
    level = new GemParamChoice(new GemChoiceModel<Level>(dataCache.getList(Model.Level)));
    level.setKey(a.getLevel().getId());
    ageRange = new GemParamChoice(new GemChoiceModel<AgeRange>(dataCache.getList(Model.AgeRange)));
    ageRange.setKey(a.getAgeRange().getId());
    places = new GemNumericField(2);
    places.setText(String.valueOf(a.getPlaces()));

    GemPanel p = new GemPanel(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(p);
    gb.insets = new Insets(4, 2, 4, 2);
    gb.add(new GemLabel(BundleUtil.getLabel("Status.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(status, 1, 0, 3, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Level.label")), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(level, 1, 1, 3, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Menu.age.range.label")), 0, 2, 1, 1, GridBagHelper.EAST);
    gb.add(ageRange, 1, 2, 3, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Place.number.label")), 0, 3, 1, 1, GridBagHelper.EAST);
    gb.add(places, 1, 3, 3, 1, GridBagHelper.WEST);

    add(p);
  }

  public GemParam getLevel() {
    return (GemParam) level.getSelectedItem();
  }

  public GemParam getStatus() {
    return (GemParam) status.getSelectedItem();
  }

  public AgeRange getRange() {
    return (AgeRange) ageRange.getSelectedItem();
  }

  public short getPlaces() {
    try {
      return Short.parseShort(places.getText());
    } catch (NumberFormatException nfe) {
      return 0;
    }
  }

  public boolean isEntryValid() {
    short p = getPlaces();
    return getLevel().getId() >= 0 && getStatus().getId() >= 0 && p >= 0 && p < 500;
  }
}
