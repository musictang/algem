/*
 * @(#)AttendanceSheet.java	2.8.w 08/07/14
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
package net.algem.edition;

import java.awt.*;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import net.algem.Algem;

import net.algem.config.Instrument;
import net.algem.contact.Person;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonIO;
import net.algem.contact.teacher.Teacher;
import net.algem.contact.teacher.TeacherService;
import net.algem.course.Course;
import net.algem.enrolment.EnrolmentService;
import net.algem.planning.*;
import net.algem.room.Establishment;
import net.algem.room.EstablishmentIO;
import net.algem.room.Room;
import net.algem.room.RoomIO;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;

/**
 * Printing canvas for attendance sheet.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 1.0a 07/07/1999
 */
public class AttendanceSheet
        extends Canvas
{

  private static final int HPAGES = 500;
  private int margeh = 30;
  private int marged = 50;
  private DateRange dateRange;
  private int line;
  private int mgh = 75;
  private int th;
  private FontMetrics fm;
  private Image bim;
  private Graphics bg;
  private Toolkit tk;
  private Calendar cal;
  private String[] monthLabel;
  private String[] dayLabels;
  private Teacher teacher;
  private PrintJob prn;
  private Graphics g;
  private Frame parent;
  private final Font normalFont;
  private final Font smallFont;
  private final Font boldFont;
  private final DataCache dataCache;
  private final TeacherService service;
  private final PlanningService planningService;

  public AttendanceSheet(Component c, DataCache dataCache) {
    this.dataCache = dataCache;
    planningService = new PlanningService(DataCache.getDataConnection());
    service = new TeacherService(DataCache.getDataConnection());

    tk = Toolkit.getDefaultToolkit();

    normalFont = new Font("TimesRoman", Font.PLAIN, 8);
    smallFont = new Font("TimesRoman", Font.PLAIN, 6);
    boldFont = new Font("TimesRoman", Font.BOLD, 8);

    cal = Calendar.getInstance(Locale.FRANCE);
    cal.setTime(new Date());

    monthLabel = new DateFormatSymbols(Locale.FRANCE).getMonths();
    dayLabels = new DateFormatSymbols(Locale.FRANCE).getShortWeekdays();

    JobAttributes ja = new JobAttributes();
    PageAttributes pa = new PageAttributes();
    pa.setMedia(PageAttributes.MediaType.A4);
    pa.setOrientationRequested(PageAttributes.OrientationRequestedType.LANDSCAPE);
    while (c.getParent() != null) {
      c = c.getParent();
    }
    if (c instanceof Frame) {
      parent = (Frame) c;
    }
    prn = tk.getPrintJob(parent, "Feuille présence", ja, pa);
  }

  public void edit(DateRange _plage, int etabId) {

    dateRange = _plage;
    Vector<Teacher> teachers = null;
    try {
      Establishment estab = EstablishmentIO.findId(etabId, DataCache.getDataConnection());
      teachers = service.findTeachers();
      if (teachers != null) {
        for (int i = 0; i < teachers.size(); i++) {
          //PersonFile p = teachers.elementAt(i);
          edit(teachers.elementAt(i), dateRange, estab);
        }
      }
      prn.end();
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  public void edit(Teacher teacher, DateRange _range, int estabId) {
    try {
      dateRange = _range;
      Establishment estab = (Establishment) DataCache.findId(estabId, Model.Establishment);
      edit(teacher, dateRange, estab);
      prn.end();
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  void edit(Teacher teacher, DateRange range, Establishment etab) {

    dateRange = range;
    this.teacher = teacher;

    try {
      Vector<? extends ScheduleObject> vpl =
              service.getCourseSchedule(teacher.getId(), etab.getId(),
              dateRange.getStart().toString(), dateRange.getEnd().toString());
      if (vpl.isEmpty()) {
        return;
      }

      g = prn.getGraphics();
      g.setColor(Color.black);

      g.setFont(normalFont);
      String head1 = "Feuille de présence du " + dateRange.getStart() + " au " + dateRange.getEnd();
      g.drawString(head1, 250, 40);
      g.drawString(teacher.toString(), 250, 60);
      g.drawString("Etablissement : " + etab.getName(), 400, 60);

      line = mgh;
      Vector<Vector<Schedule>> liste = new Vector<Vector<Schedule>>();

      while (vpl.size() > 0) {
        Vector<Schedule> v = new Vector<Schedule>();
        for (int j = 0; j < vpl.size(); j++) {
          CourseSchedule p = (CourseSchedule) vpl.elementAt(0);
          if (p.equiv(vpl.elementAt(j))) {
            v.addElement(vpl.elementAt(j));
          }
          //System.out.println("Boucle j "+j+" vector v : "+v);
        }
        vpl.removeAll(v);
        liste.addElement(v);
      }

      for (int i = 0; i < liste.size(); i++) {
        Vector<Schedule> v = liste.elementAt(i);
        Schedule pl = v.elementAt(0);
        Course course = planningService.getCourseFromAction(pl.getIdAction());
        Room room = ((RoomIO) DataCache.getDao(Model.Room)).findId(pl.getIdRoom());
        courseHeader(course, room, v);
        if (course.isCollective()) {
          detailCollective(course, room, range, v);
        } else {
          detailRange(course, room, v);
        }

        line += 30;
      }
    } catch (SQLException e) {
      GemLogger.log(getClass().getName(), "edite", e);
    } finally {
      if (g != null) {
        g.dispose();
      }
    }

  }

  public void courseHeader(Course course, Room room, Vector<Schedule> vpl) {

    g.setFont(normalFont);
    //23092003 g.drawString(cours.getTitle()+" salle "+salle.getName()+" "+dataCache.getHeureDebutCours(cours.getId()).toString()+"-"+dataCache.getHeureFinCours(cours.getId()).toString(),25,ligne);
    Schedule plt = vpl.elementAt(0);
    /* Modification 1.1a ajouter information plage horaire dans l'entete du cours collectif */
    /* LIBELLE DU COURS SUIVI de la salle et DE SON HORAIRE */
    if (course.isCollective()) {
      g.drawString(course.getTitle() + " salle " + room.getName() + " " + plt.getStart() + "-" + plt.getEnd(), 25, line);
    } else { // pas de libelle horaire pour les cours individuels
      g.drawString(course.getTitle() + " salle " + room.getName()/* +" "+plt.getStart()+"-"+plt.getEnd() */, 25, line);
    }
    g.drawLine(25, line + 5, 800, line + 5);  // 15->5
		/* HEADERS */
    g.drawString("Prénom NOM", 25, line + 15);// 25->15
    //g.drawLine(25,ligne+20,800,ligne+20); // 30->20
    int col = 180;
    /* LIBELLE DES JOURS DE LA SEMAINE */
    for (int i = 0; i < vpl.size(); i++, col += 120) {
      Schedule pl = vpl.elementAt(i);
      cal.setTime(pl.getDate().getDate());
      g.drawString(dayLabels[cal.get(Calendar.DAY_OF_WEEK)] + " " + pl.getDate().getDay(), col, line + 15);// 25 -> 15
    }
    g.drawLine(25, line + 20, 800, line + 20); // 30->20
    //ligne+=30;
    line += 20;
  }

  public void detailCollective(Course course, Room room, DateRange _range, Vector<Schedule> vpl) {
    int topl = line; // haut de colonne
    line += 10; // première ligne de texte
    g.setFont(smallFont);
    Schedule plt = vpl.elementAt(0);

    //Vector<DossierPersonne> v = DossierPersonneIO.findMembersByPlanning(dataCache, plt.getDate(), plt.getStart(), plt.getEnd(), cours.getId(), plt.getIdPerson());
    Vector<PersonFile> v = EnrolmentService.findMembersByPlanning(plt);
    for (int i = 0; i < v.size(); i++) {
      PersonFile d = v.elementAt(i);
      //
      int col = 160;
      //
      if (line + 5 > HPAGES) {
        //col=160;
        for (int j = 0; j < 5; j++, col += 120) {
          g.drawLine(col, topl, col, line);
          g.drawLine(col + 10, topl, col + 10, line);// topl hauteur de départ
        }
        g.dispose();
        g = prn.getGraphics();
        g.setFont(normalFont);
        g.drawString("Feuille de présence du " + dateRange.getStart() + " au " + dateRange.getEnd(), 250, 40);
        //g.drawString(moisLibel[cal.get(Calendar.MONTH)]+" "+cal.get(Calendar.YEAR),400,40);
        //g.drawString(plage.getStart() + " au " + plage.getEnd(), 400, 40);
        //g.drawString(moisLibel[mois-1]+" "+an,400,40);
        g.drawString(teacher.toString(), 250, 60);

        //topl = ligne = mgh;
        line = mgh;
        topl = mgh + 5;

        courseHeader(course, room, vpl);//topl = ligne;
        line += 10;
        g.setFont(smallFont);
      }

      String instrumentName = "";
      if (Algem.isFeatureEnabled("course_instruments")) {
        try {
          Instrument instrument = dataCache.getAtelierInstrumentsService().getAllocatedInstrument(plt.getIdAction(), d.getContact().getId());
          if (instrument != null) instrumentName = " - " + instrument.getName();
        } catch (Exception e) {
          GemLogger.log(e.getMessage());
        }
      }

      g.drawString(d.getContact().getFirstnameName() + instrumentName, 25, line);
      col = 160;
      for (int j = 0; j < 5; j++, col += 120) {
        g.drawLine(col, line + 5, col + 10, line + 5);
        //g.drawLine(col,ligne, col+10, ligne);
      }
      line += 15; // espacement inter noms
    }
    //
    line += 20; //espacement supplémentaire pour les éventuels nouveaux élèves
    g.drawLine(25, line, 800, line);
    int col = 160;
    for (int i = 0; i < 5; i++, col += 120) {
      g.drawLine(col, topl, col, line);
      g.drawLine(col + 10, topl, col + 10, line);
    }
  }

  public void detailRange(Course course, Room room, Vector<Schedule> vpl) throws SQLException {

//    System.out.println("detailRange=" + vpl);
    int topl = line;
    g.setFont(smallFont);

    Schedule plt = vpl.elementAt(0);
//    System.out.println("plt=" + plt);
//    System.out.println("cours=" + cours);
//    String query2 = " WHERE cours=" + cours.getId()
//            + " AND jour='" + plt.getDate() + "'"
//            + " AND prof=" + plt.getIdPerson()
//            + " AND debut >= '" + plt.getStart() + "'"
//            + " AND end <= '" + plt.getEnd() + "' ORDER BY debut";
    String query2 = "pg WHERE pg.idplanning = " + plt.getId() + " ORDER BY pg.debut";
    Vector<ScheduleRange> v = ScheduleRangeIO.find(query2, DataCache.getDataConnection());

    for (int i = 0; i < v.size(); i++) {
      ScheduleRange h = v.elementAt(i);
      Person p = ((PersonIO) DataCache.getDao(Model.Person)).findId(h.getMemberId());
      g.drawString(h.getStart().toString() + "-" + h.getEnd().toString(), 25, line += 10);
      if (p != null) {
        g.drawString(p.getFirstName() + " " + p.getName(), 25, line += 10);
      }
      //ligne+=10;
      line += 20;
      g.drawLine(25, line, 800, line);
      if (line + 5 > HPAGES) {
        int col = 160;
        for (int j = 0; j < 5; j++, col += 120) {
          g.drawLine(col, topl, col, line);
        }
        g.dispose();
        g = prn.getGraphics();
        g.setFont(normalFont);
        g.drawString("Feuille de présence pour ", 250, 40);
        //g.drawString(moisLibel[cal.get(Calendar.MONTH)]+" "+cal.get(Calendar.YEAR),400,40);
        g.drawString(dateRange.getStart() + " au " + dateRange.getEnd(), 400, 40);
        //g.drawString(moisLibel[mois-1]+" "+an,400,40);
        g.drawString(teacher.toString(), 250, 60);
        //topl = ligne = mgh; // on repart en haut de page
        line = mgh;
        topl = mgh + 5;
        courseHeader(course, room, vpl);
        g.setFont(smallFont);
      }
    }

    line += 20; //espacement supplémentaire pour les éventuels nouveaux élèves
    g.drawLine(25, line, 800, line);

    int col = 160;
    for (int i = 0; i < 5; i++, col += 120) {
      g.drawLine(col, topl, col, line);
    }
  }
}
