/*
 * @(#)ColorPlan.java	2.8.v 25/06/14
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

package net.algem.config;

import java.awt.Color;

/**
 * Default colors of schedule elements.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 */
public enum ColorPlan
{
  // commented : old default colors
  ACTION("plan.action.color", Color.WHITE.getRGB()),
//  WORKSHOP("plan.atelier.color", Color.WHITE.getRGB()),
  WORKSHOP("plan.atelier.color", -526420),
  TRAINING("plan.stage.color", 16250748), //247,247,124
//  COURSE_CO("plan.cours.collectif.color", Color.RED.getRGB()),
  COURSE_CO("plan.cours.collectif.color", -44462),
//  INSTRUMENT_CO("plan.cours.instr.collectif.color", Color.ORANGE.getRGB()),
  INSTRUMENT_CO("plan.cours.instr.collectif.color", -32743),
//  COURSE_INDIVIDUAL("plan.cours.individuel.color", Color.GREEN.getRGB()),
  COURSE_INDIVIDUAL("plan.cours.individuel.color", -16723879),
//  CATCHING_UP("plan.cours.rattrapage.color", Color.BLACK.getRGB()),
  CATCHING_UP("plan.cours.rattrapage.color", -10405575),
//  RANGE("plan.plage.color", Color.YELLOW.getRGB()),//-208128
  RANGE("plan.plage.color", -208128),
//  MEMBER_REHEARSAL("plan.repet.adherent.color", Color.BLUE.getRGB()),
  MEMBER_REHEARSAL("plan.repet.adherent.color", -13395457),
//  GROUP_REHEARSAL("plan.repet.groupe.color", Color.BLUE.getRGB()),
  GROUP_REHEARSAL("plan.repet.groupe.color", -14591745),
  STUDIO("plan.studio.color", -5160193),//153,0,255 (alternative : 6711039 //102,102,255 | 10027263)

  ACTION_LABEL("plan.action.label.color", Color.GREEN.getRGB()),
//  MEMBER_LABEL("plan.adherent.label.color", Color.YELLOW.getRGB()),
  MEMBER_LABEL("plan.adherent.label.color", -256),
//  WORKSHOP_LABEL("plan.atelier.label.color", Color.BLACK.getRGB()),
  WORKSHOP_LABEL("plan.atelier.label.color", -16777216),
  TRAINING_LABEL("plan.stage.label.color", -16777216),
//  COURSE_INDIVIDUAL_LABEL("plan.cours.individuel.label.color", Color.BLACK.getRGB()),
  COURSE_INDIVIDUAL_LABEL("plan.cours.individuel.label.color", -16777216),
//  COURSE_CO_LABEL("plan.cours.collectif.label.color", Color.BLACK.getRGB()),
  COURSE_CO_LABEL("plan.cours.collectif.label.color", -16777216),
//  INSTRUMENT_CO_LABEL("plan.cours.instr.collectif.label.color", Color.BLACK.getRGB()),
  INSTRUMENT_CO_LABEL("plan.cours.instr.collectif.label.color", -16777216),
  FLAG("plan.flag.color",Color.YELLOW.getRGB()),
//  GROUP_LABEL("plan.groupe.label.color", Color.WHITE.getRGB()),
  GROUP_LABEL("plan.groupe.label.color", -1),
  LABEL("plan.label.color", Color.BLACK.getRGB()),
  CATCHING_UP_LABEL("plan.rattrapage.label.color", Color.GREEN.getRGB()),
  STUDIO_LABEL("plan.studio.label.color", Color.WHITE.getRGB());

  private final String key;
  private final int color;

  /**
   *
   * @param key key name in preferences file
   * @param color an integer representing the default color
   */
  ColorPlan(String key, int color) {
    this.key = key;
    this.color = color;
  }

  public String getKey() {
    return key;
  }

  public int getDefaultColor(){
    return color;
  }
}
