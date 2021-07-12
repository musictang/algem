/*
 * @(#)EnrolmentWishIHM.java	2.17.0 16/03/19
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

import net.algem.contact.teacher.Teacher;
import net.algem.course.Course;
import net.algem.planning.CourseSchedule;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;

/**
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * 
 * @version 2.17.0
 * @since 2.17.0 16/03/19
 */
public interface EnrolmentWishIHM {

    public abstract int getTeacherChoice();

    public abstract int getStudentChoice();

    public abstract String getCurrentTeacherLabel();

    public abstract int getDayChoice();
    
    public abstract String getDayChoiceLabel(int i);
    
    public abstract String getDayChoiceLabel();
    
    public abstract int getParticularCourseChoice();
    
    public abstract CourseSchedule getGroupCourseChoice();
    
    public abstract String getCurrentCourseLabel();
    
    public abstract Course getCurrentParticularCourse();
    
    public abstract Teacher getCurrentTeacher();

    public DateFr getReferenceDate();

    public DateFr getEndReferenceDate();
}
