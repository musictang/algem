/*
 * @(#)StandardScheduleColorizer.java 2.9.4.14 17/12/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
 * along with Algem. If not, see http://www.gnu.org/licenses.
 *
 */
package net.algem.planning;

import java.awt.Color;
import net.algem.config.ColorPlan;
import net.algem.config.ColorPrefs;
import net.algem.contact.Person;
import net.algem.course.Course;
import net.algem.room.Room;
import net.algem.util.DataCache;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.14
 * @since 2.9.4.13 02/11/2015
 */
public class StandardScheduleColorizer
        implements ScheduleColorizer
{

  private final ColorPrefs colorPrefs;

  public StandardScheduleColorizer() {
    this(new ColorPrefs());
  }

  public StandardScheduleColorizer(ColorPrefs colorPrefs) {
    this.colorPrefs = colorPrefs;
  }

  /**
   * Gets the background color.
   *
   * @param p schedule
   * @return a color
   */
  @Override
  public Color getScheduleColor(ScheduleObject p) {
    Color ac = null;
    int type = p.getType();
    if (type != Schedule.COURSE && type != Schedule.WORKSHOP && type != Schedule.TRAINING) {
      return getDefaultScheduleColor(p);
    }
    if (p instanceof ScheduleRangeObject) {
      ac = getActionColor(((ScheduleRangeObject) p).getAction().getId());
    } else {
      ac = getActionColor(p.getIdAction());
    }
    if (ac != null) {
      return (p instanceof ScheduleRangeObject ? ColorPrefs.brighten(ac) : ac);
    }
    return getDefaultScheduleColor(p);
  }

  @Override
  public Color getDefaultScheduleColor(ScheduleObject p) {
    if (p instanceof ScheduleRangeObject) {
      return colorPrefs.getColor(ColorPlan.RANGE);
    }
    switch (p.getType()) {
      case Schedule.COURSE:
        Room s = ((CourseSchedule) p).getRoom();
        Course cc = ((CourseSchedule) p).getCourse();
        if (s.isCatchingUp()) {
          return colorPrefs.getColor(ColorPlan.CATCHING_UP);
        } else {
          if (cc != null && !cc.isCollective()) {
            return colorPrefs.getColor(ColorPlan.COURSE_INDIVIDUAL);
          } else {
            if (cc != null && cc.isInstCode()) {
              return colorPrefs.getColor(ColorPlan.INSTRUMENT_CO);
            } else {
              return colorPrefs.getColor(ColorPlan.COURSE_CO);
            }
          }
        }
      case Schedule.ACTION:
        return colorPrefs.getColor(ColorPlan.ACTION);
      case Schedule.MEMBER:
        return colorPrefs.getColor(ColorPlan.MEMBER_REHEARSAL);
      case Schedule.GROUP:
        return colorPrefs.getColor(ColorPlan.GROUP_REHEARSAL);
      case Schedule.WORKSHOP:
        return colorPrefs.getColor(ColorPlan.WORKSHOP);
      case Schedule.TRAINING:
        return colorPrefs.getColor(ColorPlan.TRAINING);
      case Schedule.STUDIO:
      case Schedule.TECH:
        return colorPrefs.getColor(ColorPlan.STUDIO);
      case Schedule.ADMINISTRATIVE:
        return colorPrefs.getColor(ColorPlan.ADMINISTRATIVE);
      default:
        return Color.WHITE;
    } // end switch couleurs
  }

  /**
   * Gets the text color for headers.
   *
   * @param p schedule
   * @return a color
   */
  @Override
  public Color getTextColor(ScheduleObject p) {
    int type = p.getType();
    if (type == Schedule.COURSE || type == Schedule.WORKSHOP || type == Schedule.TRAINING) {
      Color ac = getActionColor(p.getIdAction());
      if (ac != null) {
        return ColorPrefs.getForeground(ac);
      }
    }

    switch (p.getType()) {
      case Schedule.COURSE:
        Room r = p.getRoom();
        if (r.isCatchingUp()) {
          return colorPrefs.getColor(ColorPlan.CATCHING_UP_LABEL);
        } else if (((CourseSchedule) p).getCourse().isCollective()) {
          return colorPrefs.getColor(ColorPlan.COURSE_CO_LABEL);
        } else {
          return colorPrefs.getColor(ColorPlan.COURSE_INDIVIDUAL_LABEL);
        }
      case Schedule.WORKSHOP:
        return colorPrefs.getColor(ColorPlan.WORKSHOP_LABEL);
      case Schedule.TRAINING:
        return colorPrefs.getColor(ColorPlan.TRAINING_LABEL);
      case Schedule.GROUP:
        return colorPrefs.getColor(ColorPlan.GROUP_LABEL);
      case Schedule.MEMBER:
        return colorPrefs.getColor(ColorPlan.MEMBER_LABEL);
      case Schedule.STUDIO:
      case Schedule.TECH:
        return colorPrefs.getColor(ColorPlan.STUDIO_LABEL);
      case Schedule.ADMINISTRATIVE:
        return colorPrefs.getColor(ColorPlan.ADMINISTRATIVE_LABEL);
      default:
        return colorPrefs.getColor(ColorPlan.LABEL);
    }
  }

  /**
   * Gets schedule range color.
   *
   * @param p schedule
   * @param c basic color
   * @return a color
   */
  protected Color getScheduleRangeColor(ScheduleObject p, Color c) {
    if (p instanceof ScheduleRangeObject) {
      Person a = ((ScheduleRangeObject) p).getMember();
      if (a == null) {
        c = Color.GRAY; // nobody color
      } else if (a.getId() == 0) {
        c = c.darker();// break color
      }
    }
    return c;
  }

  private Color getActionColor(int action) {
    return DataCache.getActionColor(action);
  }

}
