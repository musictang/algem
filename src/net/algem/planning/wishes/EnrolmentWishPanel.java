/*
 * @(#)EnrolmentWishPanel.java	2.17.0 16/03/19
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import net.algem.contact.Person;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;

/**
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * 
 * @version 2.17.0
 * @since 2.17.0 16/03/19
 */
public class EnrolmentWishPanel extends GemPanel {

    HourField duration;
    Person student;
    GemButton bStudent;

    public EnrolmentWishPanel(ActionListener listener) {
        
        setLayout(new BorderLayout(5, 0));
        duration = new HourField();
        bStudent = new GemButton("");
        bStudent.setActionCommand("WishPanelMailButton");
        bStudent.setMargin(new Insets(0, 0, 0, 0));
        bStudent.setBorder(BorderFactory.createEmptyBorder());
        bStudent.setBorderPainted(false);
        bStudent.setContentAreaFilled(false);
        bStudent.addActionListener(listener);
        
        add(duration, BorderLayout.WEST);
        add(bStudent, BorderLayout.CENTER);

    }

    public void setCurrent(EnrolmentSelected current, Color color) {
        if (current != null) {
            duration.set(current.getDuration());
            student = current.getStudent();
            bStudent.setText(student != null ? student.getNameFirstname() : "");

            setBackground(color);
            duration.setBackground(color);
            bStudent.setBackground(color);
        }
    }

    public EnrolmentSelected getCurrent() {
        EnrolmentSelected current = new EnrolmentSelected();
        current.setDuration(duration.get());
        current.setStudent(student);

        return current;
    }
    
    public int getStudentId() {
        return student != null ? student.getId() : 0;
    }
    
    public Person getStudent() {
        return student;
    }
}
