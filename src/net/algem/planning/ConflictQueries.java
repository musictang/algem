/*
 * @(#)ConflictQueries.java 2.8.y.1 08/10/14
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

/**
 * Set of requests used in conflict detection when schedule is busy.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.y.1
 */
public class ConflictQueries
{

  public static String getRoomConflictSelection(String date, String hStart, String hEnd, int roomId) {
    return getConflictSelection(date, hStart, hEnd) + " AND (lieux = " + roomId + ")";
  }

  public static String getMemberRehearsalSelection(String date, String hStart, String hEnd, int memberId) {
    return getConflictSelection(date, hStart, hEnd) + " AND (idper = " + memberId + ")";
  }

  public static String getMemberScheduleSelection(String date, String hStart, String hEnd, int memberId) {
    return ",plage WHERE planning.jour = '" + date + "'"
            + " AND plage.idplanning = planning.id"
            + " AND plage.adherent = " + memberId
            + " AND ((plage.debut >= '" + hStart + "' AND plage.debut < '" + hEnd + "')"
            + " OR (plage.fin > '" + hStart + "' AND plage.fin <= '" + hEnd + "')"
            + " OR (plage.debut <= '" + hStart + "' AND plage.fin >= '" + hEnd + "'))";

  }
  
  public static String getRangeOverlapSelection(DateFr start, int member, Hour hStart, Hour hEnd, int action) {
    return "pg WHERE pg.adherent != " + member
         + " AND ((pg.debut >= '" + hStart + "' AND pg.debut < '" + hEnd + "')"
            + " OR (pg.fin > '" + hStart + "' AND pg.fin <= '" + hEnd + "')"
            + " OR (pg.debut <= '" + hStart + "' AND pg.fin >= '" + hEnd + "'))"
        + " AND idplanning IN (SELECT id FROM " + ScheduleIO.TABLE 
        + " WHERE action = " + action + " AND jour >= '" + start + "')";
  }

  public static String getGroupConflictSelection(String date, String hdebut, String hfin, int groupId) {
    return getConflictSelection(date, hdebut, hfin)
            + " AND idper = " + groupId
            + " AND ptype = " + Schedule.GROUP;
  }

  public static String getRoomTeacherConflictSelection(String date, String hStart, String hEnd, int roomId, int teacherId) {
    return getConflictSelection(date, hStart, hEnd) + " AND (lieux = " + roomId + " OR idper = " + teacherId + ")";
  }

  public static String getTeacherConflictSelection(String date, String hStart, String hEnd, int teacherId) {
    return getConflictSelection(date, hStart, hEnd) + " AND (idper= " + teacherId + ")";
  }

  public static String getBreakConflict(int idPlan, String hStart, String hEnd) {
    /* return " WHERE pg.date='" + date + "'" + " AND ((pg.start >= '" + hdeb + "' AND
     * pg.start < '" + hfin + "')" + " OR (pg.end > '" + hdeb + "' AND pg.end <=
     * '" + hfin + "')" + " OR (pg.start <= '" + hdeb + "' AND pg.end >= '" +
     * hfin + "'))" + " AND (pg.prof= " + idprof + ")"; */
    return "pg WHERE pg.idplanning = " + idPlan
            + " AND ((pg.debut >= '" + hStart + "' AND pg.debut < '" + hEnd + "')"
            + " OR (pg.fin > '" + hStart + "' AND pg.fin <= '" + hEnd + "')"
            + " OR (pg.debut <= '" + hStart + "' AND pg.fin >= '" + hEnd + "'))";
  }
  
   private static String getConflictSelection(String date, String hStart, String hEnd) {
    return "WHERE jour = '" + date + "'"
            + " AND ((debut >= '" + hStart + "' AND debut < '" + hEnd + "')"
            + " OR (fin > '" + hStart + "' AND fin <= '" + hEnd + "')"
            + " OR (debut <= '" + hStart + "' AND fin >= '" + hEnd + "'))";
  }
   
   public static String getSqlQueryOverlap(String hStart, String hEnd) {
     return " AND ((debut >= '" + hStart + "' AND debut < '" + hEnd + "')"
            + " OR (fin > '" + hStart + "' AND fin <= '" + hEnd + "')"
            + " OR (debut <= '" + hStart + "' AND fin >= '" + hEnd + "'))";
   }
   
   public static String getSqlStatementOverlap() {
     return " AND ((debut >= ? AND debut < ?)"
            + " OR (fin > ? AND fin <= ?)"
            + " OR (debut <= ? AND fin >= ?))";
   }
   
}
