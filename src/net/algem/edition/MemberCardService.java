/*
 * @(#)MemberCardService.java 2.7.e 01/02/13
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
package net.algem.edition;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.imageio.ImageIO;
import net.algem.config.ConfigUtil;
import net.algem.contact.*;
import net.algem.contact.member.Member;
import net.algem.contact.member.MemberService;
import net.algem.course.Course;
import net.algem.enrolment.CourseOrder;
import net.algem.enrolment.Enrolment;
import net.algem.planning.DateFr;
import net.algem.planning.PlanningService;
import net.algem.planning.Schedule;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.ImageUtil;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;

/**
 * Service class for member card edition.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.e
 * @since 2.4.a 16/05/12
 */
public class MemberCardService
{

  private DataCache dataCache;
  private DataConnection dc;
  private PlanningService planningService;
  private MemberService memberService;

  public MemberCardService(GemDesktop desktop) {
    this.dataCache = desktop.getDataCache();
    this.dc = dataCache.getDataConnection();
    planningService = new PlanningService(dc);
    memberService = new MemberService(dc);
  }

  Address getAddress(PersonFile p) throws SQLException {
    Address a = null;
    Member m = p.getMember();
    a = p.getContact().getAddress();
    if (a == null && m.getPayer() != p.getId()) {
      List<Address> v = AddressIO.findId(m.getPayer(), dc);
      if (v != null && v.size() > 0) {
        a = v.get(0);
      }
    }
    return a;
  }

  List<Telephone> getTels(PersonFile p) throws SQLException {
    Member m = p.getMember();
    Vector<Telephone> tels = p.getContact().getTele();
    if (tels == null && m.getPayer() != m.getId()) {
      tels = TeleIO.findId(m.getPayer(), dc);
    }
    return tels;

  }

  String getBeginningYear() {
    String y = dataCache.getStartOfYear().toString();
    return y.substring(y.lastIndexOf('-') + 1);
  }

  String getEndYear() {
    String y = dataCache.getEndOfYear().toString();
    return y.substring(y.lastIndexOf('-') + 1);
  }

  /**
   * Get the list of course order for the person {@code p} in current school year.
   *
   * @param p the person file
   * @return a list of course order
   */
  List<CourseOrder> getCourseOrder(PersonFile p) {

    Vector<CourseOrder> vcc = new Vector<CourseOrder>();
    // on commence au 1er mai en raison des préinscriptions possibles
    String debut = "01-05-" + dataCache.getStartOfYear().getYear();
    Vector<Enrolment> vi = null;
    try {
      vi = memberService.getEnrolments(p.getId(), debut); // recherche des inscriptions
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    if (vi != null) {
      for (Enrolment i : vi) {
        vcc.addAll(i.getCourseOrder());
      }
    }
    return vcc.isEmpty() ? null : vcc;
  }

  String getDay(int day) {

    if (day == 0) {
      //jour = 1;
      return "";
    }
    return PlanningService.WEEK_DAYS[day];
  }

  String getTeacher(int idTeacher) {
    Person p = ((PersonIO) DataCache.getDao(Model.Person)).findId(idTeacher);
    return p == null || p.getId() == 0 ? "" : p.getFirstnameName().trim();
  }

  Course getCourse(CourseOrder cc) {
    try {
      return planningService.getCourseFromAction(cc.getAction());
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName() + "#getCours :" + ex.getMessage());
    }
    return null;
  }

  /**
   * Retrieves infos (teacher, week day) for the course followed up by member.
   * Search starts at the beginning of school year.
   * Initially, it started at the beginning of period.
   * The infos should be relative to first course in the year.
   *
   * @param courseId course id
   * @param memberId member id
   * @return an array of integers
   */
  public int[] getInfos(int courseId, int memberId) {
    return getInfos(courseId, memberId, dataCache.getStartOfYear());
  }

  int[] getInfos(int action, int memberId, DateFr startDate) {
    int dow = 0;
    int[] infos = new int[2];
    Calendar cal = Calendar.getInstance(Locale.FRANCE); // on force la locale ici
    String query = "SELECT planning.idper, planning.jour FROM planning, plage"
            + " WHERE planning.ptype = " + Schedule.COURSE_SCHEDULE
            + " AND planning.jour >= '" + startDate + "'"
            + " AND planning.action = " + action
            + " AND planning.id = plage.idplanning"
            + " AND plage.adherent = " + memberId
            + " ORDER BY jour LIMIT 1";
    try {
      ResultSet rs = dc.executeQuery(query);
      if (rs.next()) {
        int prof = rs.getInt(1);
        DateFr d = new DateFr(rs.getString(2));
        cal.setTime(d.getDate());
        dow = cal.get(Calendar.DAY_OF_WEEK);
        infos[0] = prof;
        infos[1] = dow;
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(e);
    }
    return infos;
  }

  List<PlanningInfo> getPlanningInfo(PersonFile p) {
    List<PlanningInfo> infos = new ArrayList<PlanningInfo>();
    List<CourseOrder> commandes = getCourseOrder(p);
    if (commandes != null) {
      for (CourseOrder cc : commandes) {
        Course c = getCourse(cc);
        if (c != null) {
          String titre = c.getTitle();
          int[] others = getInfos(cc.getAction(), p.getId());
          String prof = getTeacher(others[0]);
          String jour = getDay(others[1]);
          String debut = cc.getStart().toString();
          String fin = cc.getEnd().toString();
          if ("00:00".equals(debut)) {
            debut = "";
            fin = "";
          }
          PlanningInfo info = new PlanningInfo(titre, prof, jour, debut, fin);
          infos.add(info);
        }
      }
    }
    return infos;
  }

  String getInstrument(Member m) {
    if (m == null) {
      return "";
    }  
    return dataCache.getInstrumentName(m.getFirstInstrument());
  }

  BufferedImage getPhoto(PersonFile p) throws IOException {
    URL url_photo = getPhotoPath(p.getId());
    return getPhoto(url_photo);
  }

  String getConf(String conf) {
    return ConfigUtil.getConf(conf, dc);
  }

  private BufferedImage getPhoto(URL url) throws IOException {
    if (url == null) {
      return null;
    }
    BufferedImage img = ImageIO.read(url);
    if (img == null) {
      return null;
    }
    // recadrer si nécessaire
    if (ImageUtil.PHOTO_HEIGHT != img.getHeight()) {
      System.out.println(img.getHeight() + "rescaling !");
      BufferedImage bi2 = ImageUtil.rescale(img);
      img = ImageUtil.formatPhoto(bi2);
    }
    return img;
  }

  private URL getPhotoPath(int id) {
    String path = ImageUtil.PHOTO_PATH + id + ".jpg";
    return getClass().getResource(path);
  }
}
