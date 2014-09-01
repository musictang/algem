/*
 * @(#)RehearsalUtil.java	2.8.w 09/07/14
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

import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.room.RoomRate;
import net.algem.util.DataConnection;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.w
 */
public class RehearsalUtil
{

  /**
   * Gets the off-peak hour limit.
   * @param dc
   * @return an hour
   */
  public static Hour getEndOffpeakHour(DataConnection dc) {

    String end = ConfigUtil.getConf(ConfigKey.OFFPEAK_HOUR.getKey());
    if (end == null || end.equals("00:00")) {
      return new Hour("17:00"); // default
    } else {
      return new Hour(end);
    }
  }

  /**
   * Calculates the total price for a single rehearsal.
   * @param start hour start
   * @param end hour end
   * @param roomPrice room rate
   * @param nm number of participants
   * @param dc
   * @return a double
   */
  public static double calcSingleRehearsalAmount(Hour start, Hour end, RoomRate roomPrice, int nm, DataConnection dc) {
    if (roomPrice == null || roomPrice.getType() == null) {
      return 0.0;
    }

    switch (roomPrice.getType()) {
      case HORAIRE:
        System.out.println("calcul horaire");
        return calcHourRate(start, end, roomPrice, dc);
      case PERSONNE:
        System.out.println("calcul personne");
        return calcPersonRate(start, end, roomPrice, nm);
    }

    return 0.0;
  }

  /**
   * Calculates the total value of an order line for an Hour rate.
   * @param start
   * @param end
   * @param roomRate
   * @param dc
   * @return a total
   */
  public static double calcHourRate(Hour start, Hour end, RoomRate roomRate, DataConnection dc) {

    Hour endOffPeak = getEndOffpeakHour(dc);
    double total = 0.0;
    if (end.le(endOffPeak)) {
      total = (roomRate.getNh() * start.getLength(end)) / 60;
    } else if (start.ge(endOffPeak)) {
      total = (roomRate.getPh() * start.getLength(end)) / 60;
    } // si la réservation débute en période creuse et se termine en période plein tarif
    else {
      int before = start.getLength(endOffPeak);
      int past = endOffPeak.getLength(end);
      double amountBefore = (roomRate.getNh() * before) / 60;
      double amountPast = (roomRate.getPh() * past) / 60;
      total = amountBefore + amountPast;
    }

    return total;
  }

  /**
   * Calculates the total value of an order line for a Person rate.
   * 
   * @param start
   * @param end
   * @param rate
   * @param nm number of participants
   * @return a total
   */
  public static double calcPersonRate(Hour start, Hour end, RoomRate rate, int nm) {

    int duration = start.getLength(end);
    double total_plafond = (rate.getMax() * duration) / 60;
    //Pas de distinction entre heure pleine et heure creuse dans le tarif par personne
    double total = rate.getPh() * nm;

    if (total > total_plafond) {
      return total_plafond;
    }
    return total;
  }

  public static boolean isCancelledBefore(DateFr date, int nhours) {
    int delay = 3600 * nhours * 1000; // en millisecondes

    DateFr today = new DateFr(new java.util.Date());
    // la comparaison doit se faire entre 2 DateFr
    if (today.getTime() + delay > date.getTime()) {
      return false;
    }
    return true;
  }
}
