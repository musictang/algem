/*
 * @(#)EnrolmentWish.java	2.17.0 13/03/19
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import net.algem.contact.Person;
import net.algem.util.model.GemModel;

/**
 * EnrolmentWish entity for table reinscription
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0
 * @since 2.17.0 13/03/2019
 */
public class EnrolmentWish
        implements GemModel, Cloneable {

    private static final long serialVersionUID = 1L;

    protected int id;
    protected LocalDate creationDate;
    protected int teacher;
    protected String teacherLabel;
    protected short day;
    protected String dayLabel;
    protected int course;
    protected String courseLabel;
    protected Person student;
    protected int studentId;
    protected short preference;
    protected Hour hour;
    protected Hour duration;
    protected String note="";
    protected int column;
    protected boolean selected;
    protected LocalDateTime dateMailInfo;
    protected LocalDateTime dateMailConfirm;
    protected int action;


    public EnrolmentWish() {
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public int getTeacher() {
        return teacher;
    }

    public void setTeacher(int teacher) {
        this.teacher = teacher;
    }

    public short getDay() {
        return day;
    }

    public void setDay(short day) {
        this.day = day;
    }

    public Person getStudent() {
        return student;
    }

    public int getStudentId() {
        return studentId;
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setStudent(Person student) {
        this.student = student;
        if (student != null) {
            studentId = student.getId();
        } else {
            studentId = 0;
        }
    }

    public short getPreference() {
        return preference;
    }

    public void setPreference(short preference) {
        this.preference = preference;
    }

    public Hour getHour() {
        return hour;
    }

    public void setHour(Hour hour) {
        this.hour = hour;
    }

    public Hour getDuration() {
        return duration;
    }

    public void setDuration(Hour duration) {
        this.duration = duration;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getTeacherLabel() {
        return teacherLabel;
    }

    public void setTeacherLabel(String teacherLabel) {
        this.teacherLabel = teacherLabel;
    }

    public String getDayLabel() {
        return dayLabel;
    }

    public void setDayLabel(String dayLabel) {
        this.dayLabel = dayLabel;
    }

    public String getCourseLabel() {
        return courseLabel;
    }

    public void setCourseLabel(String courseLabel) {
        this.courseLabel = courseLabel;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getDateMailInfo() {
        return dateMailInfo;
    }

    public void setDateMailInfo(LocalDateTime dateMailInfo) {
        this.dateMailInfo = dateMailInfo;
    }

    public boolean isMailInfoSended() {
        return dateMailInfo != null;
    }

    public LocalDateTime getDateMailConfirm() {
        return dateMailConfirm;
    }

    public void setDateMailConfirm(LocalDateTime dateMailConfig) {
        this.dateMailConfirm = dateMailConfig;
    }
    
    public boolean isMailConfirmSended() {
        return dateMailConfirm != null;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

        
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int i) {
        id = i;
    }

    @Override
    public String toString() {
        return "EnrolmentWish{" + "id=" + id + ", prof=" + teacher + ", jour=" + day + ", cours=" + course + ", student=" + student + ", preference=" + preference + ", hour=" + hour + ", duration=" + duration + ", selected=" + selected + '}';
    }
    
    @Override
    public Object clone()throws CloneNotSupportedException{  
        return super.clone();  
    }  

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.id;
        hash = 23 * hash + this.teacher;
        hash = 23 * hash + this.day;
        hash = 23 * hash + this.course;
        hash = 23 * hash + this.studentId;
        hash = 23 * hash + this.preference;
        hash = 23 * hash + Objects.hashCode(this.hour);
        hash = 23 * hash + Objects.hashCode(this.duration);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EnrolmentWish other = (EnrolmentWish) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }
    
    
}
