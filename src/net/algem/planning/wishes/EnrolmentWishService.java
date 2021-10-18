/*
 * @(#)EnrolmentWishService.java	2.17.0 13/03/19
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
package net.algem.planning.wishes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import net.algem.contact.Person;
import net.algem.contact.PersonIO;
import net.algem.contact.teacher.TeacherService;
import net.algem.course.Course;
import net.algem.course.CourseChoiceTeacherModel;
import net.algem.course.CourseIO;
import net.algem.enrolment.CourseOrder;
import net.algem.enrolment.OrderIO;
import net.algem.planning.Action;
import net.algem.planning.CourseSchedulePrintDetail;
import net.algem.planning.DateFr;
import net.algem.planning.PlanningService;
import net.algem.planning.ScheduleObject;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;

/**
 * Service class for wishes.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0
 * @since 2.17.0 13/03/2019
 */
public class EnrolmentWishService {

    private DataCache dataCache;
    private DataConnection dc;
    private EnrolmentWishIO wishesIO;
    private PersonIO personIO;
    private PlanningService planningService;
    private TeacherService teacherService;
    private List<Course> courseByTeacher = new ArrayList();
    private List<EnrolmentWish> wishesByTeacher = new ArrayList();
    private List<EnrolmentWish> wishesByCourse = new ArrayList();

    private CourseChoiceTeacherModel courseChoiceModel;
    private GemList studentsWithOrders;
    private EnrolmentWishIHM view;
    private int currentTeacher;

    public EnrolmentWishService(DataCache dataCache) {
        this.dataCache = dataCache;
        dc = dataCache.getDataConnection();

        wishesIO = new EnrolmentWishIO(dc);
        personIO = new PersonIO(dc);
        planningService = new PlanningService(dc);
        teacherService = new TeacherService(planningService, dc);

        courseChoiceModel = new CourseChoiceTeacherModel(courseByTeacher);
        studentsWithOrders = new GemList(OrderIO.findMembersWithOrders(dc));
    }

    public void setView(EnrolmentWishIHM view) {
        this.view = view;
    }

    public void updateParticularWish(int column, Hour h, EnrolmentWish w) throws SQLException {
        if (w.getId() == 0) {
            w.setHour(h);
            w.setDay((short) view.getDayChoice());
            w.setTeacher(view.getTeacherChoice());
            w.setCourse(view.getParticularCourseChoice());
            w.setColumn(column);
            wishesIO.insert(w);
            wishesByTeacher.add(w);
        } else {
            wishesIO.update(w);
            EnrolmentWish wo = getTeacherWishById(w.getId());
            wishesByTeacher.remove(wo);
            wishesByTeacher.add(w);

        }
    }

    public void insertGroupWish(EnrolmentWish w) throws SQLException {
        wishesIO.insert(w);
        wishesByCourse.add(w);
    }

    public void updateGroupWish(EnrolmentWish w) throws SQLException {
        wishesIO.update(w);
        EnrolmentWish wo = getCourseWishById(w.getId());
        wishesByCourse.remove(wo);
        wishesByCourse.add(w);
    }

    public EnrolmentWish getCourseWishById(int id) {
        for (EnrolmentWish w : wishesByCourse) {
            if (w.getId() == id) {
                return w;
            }
        }
        return null;
    }

    public EnrolmentWish getTeacherWishById(int id) {
        for (EnrolmentWish w : wishesByTeacher) {
            if (w.getId() == id) {
                return w;
            }
        }
        return null;
    }

    public boolean selectParticularWish(EnrolmentWish w, boolean selected) throws SQLException {
        w.setSelected(selected);
        wishesIO.updateSelected(w);
        for (EnrolmentWish w2 : wishesByTeacher) {
            if (w2.getId() == w.getId()) {
                w2.setSelected(selected);
            }
        }
        return true;
    }

    public boolean selectGroupWish(EnrolmentWish w, boolean selected) throws SQLException {
        w.setSelected(selected);
        wishesIO.updateSelected(w);
        for (EnrolmentWish w2 : wishesByCourse) {
            if (w2.getId() == w.getId()) {
                w2.setSelected(selected);
            }
        }
        return true;
    }

    public boolean setMailInfoDate(int student, LocalDateTime mailDate) throws SQLException {
        wishesIO.updateMailInfoDate(student, mailDate);
        return true;
    }

    public boolean setMailConfirmDate(int student, LocalDateTime mailDate) throws SQLException {
        wishesIO.updateMailConfirmDate(student, mailDate);
        return true;
    }

    public boolean setMailConfirmDate(EnrolmentWish w, LocalDateTime mailDate) throws SQLException {
        w.setDateMailConfirm(mailDate);
        wishesIO.updateMailConfirmDate(w);
        for (EnrolmentWish w2 : wishesByTeacher) {
            if (w2.getId() == w.getId()) {
                w2.setDateMailConfirm(mailDate);
            }
        }
        return true;
    }

    public boolean deleteParticularWish(EnrolmentWish w) throws SQLException {
        wishesIO.delete(w);
        wishesByTeacher.remove(w);
        return true;
    }

    public boolean deleteGroupWish(EnrolmentWish w) throws SQLException {
        wishesIO.delete(w);
        wishesByTeacher.remove(w);
        return true;
    }

    public GemList getStudentsWithOrders() {

        return studentsWithOrders;
    }

    public List<String[]> getCurrentEnrolmentForStudent(int student) { //FIXME existe qq part ?
        List<String[]> v = new ArrayList<>();
        String query = "select distinct c.titre, extract (dow from pl.jour) as dow, pg.debut,pg.fin,p.nom from planning pl join plage pg on pg.idplanning = pl.id join action a on a.id = pl.action join cours c on c.id = a.cours join personne p on pl.idper=p.id where adherent=" + student
                + " and pl.jour between '" + dataCache.getStartOfPeriod() + "' and '" + dataCache.getEndOfPeriod() + "'";
        try {
            ResultSet rs = dc.executeQuery(query);
            while (rs.next()) {
                String[] cols = new String[5];
                cols[0] = rs.getString(1);
                cols[1] = PlanningService.WEEK_DAYS[rs.getInt(2) + 1];
                cols[2] = rs.getString(3);
                cols[3] = rs.getString(4);
                cols[4] = rs.getString(5);
                v.add(cols);
            }
        } catch (Exception ex) {
            GemLogger.logException("EnrolmentWishService.getCurrentEnrolmentForStudent", ex);
        }
        return v;
    }

    public CourseChoiceTeacherModel getCourseByTeacher(int teacher, int day) {
        String query = ", planning p, action a WHERE "
                + "p.action = a.id AND a.cours = c.id"
                + " AND p.idper = " + teacher
                + " AND p.jour >= '" + view.getReferenceDate() + "'"
                + " AND p.jour <= '" + view.getEndReferenceDate() + "'"
                + " AND extract (dow from p.jour) = " + day
                + " AND c.collectif = 'f'";
        //GemLogger.info(query);

        return new CourseChoiceTeacherModel(((CourseIO) DataCache.getDao(Model.Course)).find(query));
    }

    public List<? extends ScheduleObject> getScheduleForTeacher(int teacher, String jour) throws SQLException {
        return teacherService.getSchedule(teacher, jour, jour);
    }

    public GemList getTeachers() {
        return dataCache.getList(Model.Teacher);

    }

    public void setCurrentTeacher(int teacher) {
        if (teacher != currentTeacher) {
            try {
                wishesByTeacher = wishesIO.find("where prof=" + teacher);
            } catch (Exception e) {
                GemLogger.logException("EnrolmentWishService.setCurrentTeacher", e);
            }
            currentTeacher = teacher;
        }
    }

    public int getCurrentTeacher() {
        return currentTeacher;
    }

    public List<EnrolmentWish> getWishesFromTeacher(int teacher) {
        return wishesByTeacher;
    }

    public List<EnrolmentWish> getWishesFromCourse(int action) {
        wishesByCourse = new ArrayList();
        try {
            wishesByCourse = wishesIO.find("where action=" + action);
        } catch (SQLException e) {
            GemLogger.logException("EnrolmentWishService.getWishesFromCourse", e);
        }
        return wishesByCourse;
    }

    public List<EnrolmentWish> getWishesFromStudent(int student) {
        List<EnrolmentWish> liste = new ArrayList();
        for (EnrolmentWish w : wishesByTeacher) {
            if (w.getStudentId() == student && w.getTeacher() == currentTeacher) {
                liste.add(w);
            }
        }
        return liste;
    }

    public List<EnrolmentWish> findStudentWishes(int student) {

        try {
            return wishesIO.find("WHERE student=" + student + " ORDER by action, cours, preference");
        } catch (SQLException e) {
            GemLogger.logException("EnrolmentWishService.findStudentWishes", e);
        }
        return new ArrayList<>();
    }

    public List<EnrolmentWish> findStudentValidatedWishes(int student, CourseOrder co) {
        try {
            String code="";
            ResultSet rs = dc.executeQuery("select code from module_type where id="+co.getCode());
            if (rs.next()) {
                code = rs.getString(1);
            }
            String query = "WHERE student=" + student + " AND duration='" + co.getEnd() + "' AND selected='t' AND action"+(code.equals("INST") ? "=" : "!=") + "'0'";
            //System.out.println("EnrolmentWishService.findStudentValidatedWishes query="+query);
            return wishesIO.find(query);
        } catch (SQLException e) {
            GemLogger.logException("EnrolmentWishService.findStudentWishes", e);
        }
        return new ArrayList<>();
    }

    public List<EnrolmentWish> getParticularCourseRange(int course, int teacher, int dow) {
        List<EnrolmentWish> wishes = new ArrayList<>();
        try {
            wishes = wishesIO.find("WHERE cours=" + course + " AND prof=" + teacher + " AND jour=" + dow
                    + "AND selected='t'"
                    + " ORDER by hour");
        } catch (SQLException e) {
            GemLogger.logException("EnrolmentWishService.getParticularCourseRange", e);
        }
        return wishes;
    }

    public static List<CourseSchedulePrintDetail> getWeekGroupCourses(DateFr from, DateFr to) {
        String query = "SELECT DISTINCT ON (c.titre, dow, p.debut, p.fin) p.id,p.jour,extract('isodow' from p.jour) AS dow,p.debut,p.fin,p.idper,a.id,a.places,c.id,c.titre,per.nom,per.prenom"
                + " FROM planning p JOIN action a ON (p.action = a.id)"
                + " JOIN cours c ON (a.cours = c.id)"
                + " JOIN personne per ON (p.idper = per.id)"
                + " WHERE p.ptype in(1,6)" // regular and training courses only
                + " AND p.idper != 0" // sans prof à définir
                + " AND c.actif='t'"
                + " AND c.collectif='t'"
                + " AND p.jour BETWEEN '" + from + "' AND '" + to + "'";

        query += " ORDER BY c.titre, dow, p.debut";
        List<CourseSchedulePrintDetail> schedules = new ArrayList<>();
        try {
            ResultSet rs = DataCache.getDataConnection().executeQuery(query);
            while (rs.next()) {
                CourseSchedulePrintDetail s = new CourseSchedulePrintDetail();
                s.setId(rs.getInt(1));
                s.setDate(new DateFr(rs.getString(2)));
                s.setDow(rs.getInt(3));
                s.setStart(new net.algem.planning.Hour(rs.getString(4)));
                s.setEnd(new net.algem.planning.Hour(rs.getString(5)));
                s.setIdPerson(rs.getInt(6));
                Action a = new Action(rs.getInt(7));
                a.setPlaces((short) rs.getInt(8));
                s.setAction(a);
                Course c = new Course(rs.getInt(9));
                c.setTitle(rs.getString(10));
                s.setActivity(c);
                s.setPerson(new Person(rs.getInt(6), rs.getString(11), rs.getString(12), ""));
                schedules.add(s);
            }
        } catch (Exception e) {
            GemLogger.logException("EnrolmentWishService.getWeekGroupCourses", e);
        }
        return schedules;
    }

    public int countStudentWishes(int student) {

        return wishesIO.count(student);
    }

    public int countStudentGroupWishes(int student) {

        return wishesIO.countGroupWishes(student);
    }

    public int countStudentWishes(int student, int teacher, int course) {

        return wishesIO.count(student, teacher, course);
    }

    public int countStudentWishes(int student, int action) {

        return wishesIO.count(student, action);
    }

    public List<Integer> getUsedStudentPreferences(int student) {
        List<Integer> list = new ArrayList();
        for (EnrolmentWish w : wishesByTeacher) {
            if (w.getStudentId() == student && w.getTeacher() == currentTeacher && w.getCourse() == view.getParticularCourseChoice()) {
                list.add(Integer.valueOf(w.getPreference()));
            }
        }
        return list;
    }

    public boolean isStudentSelected(int student) { // pour couleur jaune
        for (EnrolmentWish w : wishesByTeacher) {
            if (w.getStudentId() == student && w.getTeacher() == currentTeacher && w.getCourse() == view.getParticularCourseChoice()) {
                if (w.isSelected()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void checkOtherSelected(int student) throws EnrolmentWishException {
        for (EnrolmentWish w : wishesByTeacher) {
            if (w.getStudentId() == student && w.getTeacher() == currentTeacher && w.getCourse() == view.getParticularCourseChoice()) {
                if (w.isSelected()) {
                    throw new EnrolmentWishException(MessageUtil.getMessage("enrolment.wish.conflict.selected", new Object[]{view.getDayChoiceLabel(w.getDay() - 2), w.getHour()}));
                }
            }
        }
    }

    public EnrolmentWish checkOtherDuration(int student, Hour duration, int column) throws EnrolmentWishException {
        for (EnrolmentWish w : wishesByTeacher) {
            if (w.getStudentId() == student && w.getTeacher() == currentTeacher && w.getCourse() == view.getParticularCourseChoice()) {
                if (!w.getDuration().equals(duration) && w.getColumn() != column) {
                    return w;
                }
            }
        }
        return null;
    }
}
