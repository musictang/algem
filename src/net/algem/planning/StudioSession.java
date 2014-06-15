/*
 * @(#)StudioSession.java	2.8.v 13/06/14
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

package net.algem.planning;

import java.util.List;
import net.algem.config.GemParam;

/**
 * A StudioSession object includes elements required for planning a studio session.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.8.v 29/05/14
 */
public class StudioSession
{

  private int studio;
  private int group;
  private int [] rooms;
  private int [] technicians;
  private List<GemDateTime> dates;
  private GemParam category;

  public StudioSession() {
  }

  public int getGroup() {
    return group;
  }

  public void setGroup(int group) {
    this.group = group;
  }

  public int[] getRooms() {
    return rooms;
  }

  public void setRooms(int[] rooms) {
    this.rooms = rooms;
  }

  public int getStudio() {
    return studio;
  }

  public void setStudio(int studio) {
    this.studio = studio;
  }

  public int[] getTechnicians() {
    return technicians;
  }

  public void setTechnicians(int[] technicians) {
    this.technicians = technicians;
  }

  public List<GemDateTime> getDates() {
    return dates;
  }

  public void setDates(List<GemDateTime> dates) {
    this.dates = dates;
  }

  public GemParam getCategory() {
    return category;
  }

  public void setCategory(GemParam category) {
    this.category = category;
  }

}
