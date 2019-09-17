/*
 * @(#)AttendanceQuarterSheet.java	2.17.1 15/09/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
import java.util.ArrayList;
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
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;

/**
 * Printing canvas for attendance sheet.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.1
 * @since 2.17.1 15/09/2019
 */
public class AttendanceQuaterSheet
        extends Canvas {

    private static final int PAGE_HEIGTH = 800;//page height page 595*841
    private static final int PAGE_WIDTH = 550;//page height page 595*841
    private static final int DAY_WIDTH = 30;//ex 120 en landscape
    private static final int FIRST_COL = 200;//ex 160
    private static final int NB_COL = 12;//ex 5
    private static final int LINE_HEIGTH = 20;//ex 30
    private static final int ADH_HEIGTH = 15;//ex 15
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

    public AttendanceQuaterSheet(Component c, DataCache dataCache) {
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
        pa.setOrientationRequested(PageAttributes.OrientationRequestedType.PORTRAIT);
        while (c.getParent() != null) {
            c = c.getParent();
        }
        if (c instanceof Frame) {
            parent = (Frame) c;
        }
        prn = tk.getPrintJob(parent, BundleUtil.getLabel("Attendance.sheet.label"), ja, pa);
        System.out.println("prn.getPageDimension() " + prn.getPageDimension());
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
        if (teacher.getId() == 0) {
            return;
        }
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

        if (teacher.getId() == 0) {
            return;
        }
        dateRange = range;
        this.teacher = teacher;

        try {
            Vector<? extends ScheduleObject> vpl = service.getCourseSchedule(
                    teacher.getId(),
                    etab.getId(),
                    dateRange.getStart().toString(),
                    dateRange.getEnd().toString()
            );
            if (vpl.isEmpty()) {
                return;
            }

            g = prn.getGraphics();
            g.setColor(Color.black);

            g.setFont(normalFont);
            String head1 = MessageUtil.getMessage("attendance.sheet.head1", new Object[]{dateRange.getStart(), dateRange.getEnd()});
            g.drawString(head1, 250, 40);
            g.drawString(teacher.toString(), 150, 60);
            g.drawString(MessageUtil.getMessage("attendance.sheet.head2", etab.getName()), 300, 60);

            line = mgh;
            java.util.List<Vector<ScheduleObject>> vplList = new ArrayList<Vector<ScheduleObject>>();

            while (vpl.size() > 0) {
                Vector<ScheduleObject> v = new Vector<ScheduleObject>();
                for (int j = 0; j < vpl.size(); j++) {
                    CourseSchedule p = (CourseSchedule) vpl.elementAt(0);
                    if (p.equiv(vpl.elementAt(j))) {
                        v.addElement(vpl.elementAt(j));
                    }
                }
                vpl.removeAll(v);
                vplList.add(v);
            }

            for (int i = 0; i < vplList.size(); i++) {
                Vector<ScheduleObject> v = vplList.get(i);
                Schedule pl = v.elementAt(0);
                Course course = planningService.getCourseFromAction(pl.getIdAction());
                Room room = ((RoomIO) DataCache.getDao(Model.Room)).findId(pl.getIdRoom());
                courseHeader(course, room, v);
                if (course.isCollective()) {
                    detailCollective(course, room, range, v);
                } else {
                    detailRange(course, room, v);
                }

                line += LINE_HEIGTH;
            }
        } catch (SQLException e) {
            GemLogger.log(getClass().getName(), "edite", e);
        } finally {
            if (g != null) {
                g.dispose();
            }
        }

    }

    public void courseHeader(Course course, Room room, Vector<ScheduleObject> vpl) {

        //System.out.println("coursHeader course=" + course + " line=" + line);
        if (line + 50 > PAGE_HEIGTH) {
            g.dispose();
            g = prn.getGraphics();
            g.setFont(normalFont);
            g.drawString(MessageUtil.getMessage("attendance.sheet.recipient.label"), 150, 40);
            g.drawString(dateRange.getStart() + " au " + dateRange.getEnd(), 300, 40);
            g.drawString(teacher.toString(), 250, 60);
            line = mgh;
        }

        g.setFont(normalFont);
        Schedule plt = vpl.elementAt(0);
        /* LIBELLE DU COURS SUIVI de la salle et DE SON HORAIRE */
        String msg = "";
        if (course.isCollective()) {
            msg = MessageUtil.getMessage("attendance.sheet.collective.label", new Object[]{
                course.getTitle(),
                room.getName(),
                plt.getStart(),
                plt.getEnd()
            });
        } else { // pas de libelle horaire pour les cours individuels
            msg = MessageUtil.getMessage("attendance.sheet.individual.label", new Object[]{
                course.getTitle(),
                room.getName()
            });
        }
        g.drawString(msg, 25, line);
        g.drawLine(25, line + 5, PAGE_WIDTH, line + 5);  // 15->5
        /* HEADERS */
        g.drawString(MessageUtil.getMessage("attendance.sheet.student.label"), 25, line + 15);// 25->15
        int col = FIRST_COL;
        /* LIBELLE DES JOURS DE LA SEMAINE */
        for (int i = 0; i < vpl.size(); i++, col += DAY_WIDTH) {
            Schedule pl = vpl.elementAt(i);
            cal.setTime(pl.getDate().getDate());
            g.drawString(dayLabels[cal.get(Calendar.DAY_OF_WEEK)] + pl.getDate().getDay(), col, line + 15);// 25 -> 15
        }
        g.drawLine(25, line + LINE_HEIGTH, PAGE_WIDTH, line + LINE_HEIGTH); // 30->LINE_HEIGTH
        line += LINE_HEIGTH;
    }

    public void detailCollective(Course course, Room room, DateRange _range, Vector<ScheduleObject> vpl) {
        boolean np = false;
        int topl = line; // haut de colonne
        line += 10; // première ligne de texte
        g.setFont(smallFont);
        Schedule plt = vpl.elementAt(0);
        Vector<PersonFile> v = EnrolmentService.findMembersByPlanning(plt);
        for (int i = 0; i < v.size(); i++) {
            PersonFile d = v.elementAt(i);
            int col = FIRST_COL;
            if (line + 5 > PAGE_HEIGTH) {
                for (int j = 0; j < NB_COL; j++, col += DAY_WIDTH) {
                    g.drawLine(col, topl, col, line);
                    //g.drawLine(col + 10, topl, col + 10, line);// topl hauteur de départ
                }
                g.dispose();//new page
                g = prn.getGraphics();
                g.setFont(normalFont);
                g.drawString(MessageUtil.getMessage("attendance.sheet.head1", new Object[]{dateRange.getStart(), dateRange.getEnd()}), 250, 40);
                g.drawString(teacher.toString(), 250, 60);
                line = mgh;
                topl = mgh + 5;
                if (i <= v.size() - 1) {
                    courseHeader(course, room, vpl);
                } else {
                    np = true;
                }
                line += 10;
                g.setFont(smallFont);
            }

            String instrumentName = "";
            if (Algem.isFeatureEnabled("course_instruments")) {
                try {
                    Instrument instrument = dataCache.getAtelierInstrumentsService().getAllocatedInstrument(plt.getIdAction(), d.getContact().getId());
                    if (instrument != null) {
                        instrumentName = " - " + instrument.getName();
                    }
                } catch (Exception e) {
                    GemLogger.log(e.getMessage());
                }
            }

            g.drawString(d.getContact().getFirstnameName() + instrumentName, 25, line);
            col = FIRST_COL;
            //for (int j = 0; j < 5; j++, col += DAY_WIDTH) {
            //  g.drawLine(col, line + 5, col + 10, line + 5);
            //}
            g.drawLine(25, line + 5, PAGE_WIDTH, line + 5);
            line += ADH_HEIGTH; // espacement inter noms
        }
        //espacement supplémentaire pour les éventuels nouveaux élèves
        line += LINE_HEIGTH - ADH_HEIGTH;
        g.drawLine(25, line, PAGE_WIDTH, line);
        if (!np) {
            int col = FIRST_COL;
            for (int i = 0; i < NB_COL; i++, col += DAY_WIDTH) {
                g.drawLine(col, topl, col, line);
                //g.drawLine(col + 10, topl, col + 10, line);
            }
        }
    }

    public void detailRange(Course course, Room room, Vector<ScheduleObject> vpl) throws SQLException {
        boolean np = false; // new page
        int topl = line;
        g.setFont(smallFont);

        Schedule plt = vpl.elementAt(0);
        String query2 = "pg WHERE pg.idplanning = " + plt.getId() + " ORDER BY pg.debut";
        Vector<ScheduleRange> v = ScheduleRangeIO.find(query2, DataCache.getDataConnection());
        for (int i = 0; i < v.size(); i++) {
            ScheduleRange h = v.elementAt(i);
            Person p = ((PersonIO) DataCache.getDao(Model.Person)).findById(h.getMemberId());
            String s = h.getStart().toString() + "-" + h.getEnd().toString();
            if (p != null) {
                s += " " + p.getFirstName() + " " + p.getName();
            }
            g.drawString(s, 25, line += 10);
            /*
      g.drawString(h.getStart().toString() + "-" + h.getEnd().toString(), 25, line += 10);
      if (p != null) {
        g.drawString(p.getFirstName() + " " + p.getName(), 25, line += 10);
      }
             */
            line += ADH_HEIGTH - 10;
            g.drawLine(25, line, PAGE_WIDTH, line);
            if (line + 5 > PAGE_HEIGTH) {

                int col = FIRST_COL;
                for (int j = 0; j < NB_COL; j++, col += DAY_WIDTH) {
                    g.drawLine(col, topl, col, line);
                }
                g.dispose();
                g = prn.getGraphics();
                g.setFont(normalFont);
                g.drawString(MessageUtil.getMessage("attendance.sheet.recipient.label"), 150, 40);
                g.drawString(dateRange.getStart() + " au " + dateRange.getEnd(), 300, 40);
                g.drawString(teacher.toString(), 250, 60);
                line = mgh;
                topl = mgh + 5;

                if (i < v.size() - 1) {
                    courseHeader(course, room, vpl);
                } else {
                    np = true;
                }
                g.setFont(smallFont);
            }
        }

        //espacement supplémentaire pour les éventuels nouveaux élèves
        /*line += LINE_HEIGTH; 
    g.drawLine(25, line, 800, line); */
        if (!np) {
            int col = FIRST_COL;
            for (int k = 0; k < NB_COL; k++, col += DAY_WIDTH) {
                g.drawLine(col, topl, col, line);
            }
        }
    }

}
