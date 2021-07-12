/*
 * @(#)EnrolmentWishEditablePanel.java	2.17.0 16/03/19
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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import net.algem.contact.Person;
import net.algem.util.ImageUtil;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * 
 * @version 2.17.0
 * @since 2.17.0 16/03/19
 */
public class EnrolmentWishEditablePanel extends JPanel {

    EnrolmentWish oldValue = null;
    GemButton showButton;
    GemButton deleteButton;
    HourField duration;
    Person student;
    GemButton bStudent;
    GemLabel preference;
    int id;
    String note;
    JCheckBox checked;
    GemPanel center;
    GemPanel buttons;
    
    ImageIcon editIcon;
    ImageIcon deleteIcon;

    //TODO ordre point intérogation = proposition par Polynotes
    public EnrolmentWishEditablePanel(ActionListener listener) {

        deleteIcon = ImageUtil.createImageIcon(ImageUtil.DELETE_ICON);
        editIcon = ImageUtil.createImageIcon(ImageUtil.EDIT_ICON);
        showButton = new GemButton(editIcon);
        showButton.setActionCommand("EditablePanelShowButton");
        showButton.setMargin(new Insets(0, 0, 0, 0));
        showButton.setBorder(BorderFactory.createEmptyBorder());
//        showButton.setToolTipText(BundleUtil.getLabel("Enrolment.wish.details.tip"));  //ça marche pas, ni dans le tablecellrenderer

        deleteButton = new GemButton(deleteIcon);
        deleteButton.setActionCommand("EditablePanelDeleteButton");
        deleteButton.setMargin(new Insets(0, 0, 0, 0));
        deleteButton.setBorder(BorderFactory.createEmptyBorder());
//        deleteButton.setToolTipText(BundleUtil.getLabel("Enrolment.wish.delete.tip"));  //ça marche pas, ni dans le tablecellrenderer

        buttons = new GemPanel();
        buttons.setLayout(new GridLayout(1, 2, 0, 0));
        buttons.add(showButton);
        buttons.add(deleteButton);

        duration = new HourField();
        bStudent = new GemButton("");
        bStudent.setBorder(BorderFactory.createEmptyBorder());
        bStudent.setContentAreaFilled(false);
        bStudent.setActionCommand("EditablePanelStudentButton");

        preference = new GemLabel();
        checked = new JCheckBox();
        checked.setOpaque(false);
        checked.setActionCommand("EditablePanelCheckbox");

        center = new GemPanel();
        center.setLayout(new BorderLayout());
        center.add(duration, BorderLayout.WEST);
        center.add(bStudent, BorderLayout.CENTER);
        center.add(preference, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(buttons, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(checked, BorderLayout.EAST);

        showButton.addActionListener(listener);
        deleteButton.addActionListener(listener);
        checked.addActionListener(listener);
        bStudent.addActionListener(listener);
    }
    
    public boolean isChecked() 
    {
        return checked.isSelected();
    }
    
    public void setChecked(boolean checked) {
        this.checked.setSelected(checked);
    }

    public Hour getDuration() 
    {
        return duration.get();
    }
    
    public void setDuration(Hour duration) 
    {
        this.duration.set(duration);
    }
    
    public String getPreference() { 
        return preference.getText();
    }
    
    public void setPreference(String preference) { 
        this.preference.setText(preference);
    }
    
    public String getNote() { 
        return note;
    }
    
    public void setNote(String note) { 
        this.note = note;
    }
    
    public int getStudentId() {
        if (student != null) {
            return student.getId();
        } else {
            return 0;
        }
    }
            
    public String getStudentLabel() {
        return student.getNameFirstname();
    }
            
    public Person getStudent() {
        return student;
    }
            
    public void setWish(EnrolmentWish w) {
        student = w.getStudent();
        bStudent.setText(student.getNameFirstname());
        duration.set(w.getDuration());
        preference.setText(String.valueOf(w.getPreference()));
        note = w.getNote();
    }

    public void setStudent(Person student) {
        this.student = student;
        bStudent.setText(student.getNameFirstname());

    }
        
    public void setChoice(EnrolmentWish choice, Color color) {
        oldValue = choice;
        duration.set(choice.getDuration());
        if (choice.getStudent() != null) {
            bStudent.setText(choice.getStudent().getNameFirstname());
            student = new Person(choice.getStudent().getId(), choice.getStudent().getName(), choice.getStudent().getFirstName(), "M");
        } else {
            student = null;
            bStudent.setText("");
        }
        if (choice.getPreference() > 0) {
            preference.setText(String.valueOf(choice.getPreference()));
        } else {
            preference.setText("");
        }
        checked.setSelected(choice.isSelected());
        note = choice.getNote();

        setBackground(color);
    }

    public EnrolmentWish getChoice() {
        try {
            EnrolmentWish choice = (EnrolmentWish)oldValue.clone();
            choice.setDuration(duration.get());
            choice.setStudent(student);
            try {
                choice.setPreference((short)Integer.parseInt(preference.getText()));
            } catch (NumberFormatException ignore) {}
            choice.setSelected(checked.isSelected());
            choice.setNote(note);
            return choice;
        } catch (CloneNotSupportedException c) {
            System.out.println("getChoice clone :"+c);
        }
        return null;
    }

    @Override
    public void setBackground(Color c) {
        super.setBackground(c);
        if (duration != null) duration.setBackground(c);
        if (center != null) center.setBackground(c);
        if (bStudent != null) bStudent.setBackground(c);
        if (checked != null) checked.setBackground(c);
        if (buttons != null) buttons.setBackground(c);
    }
}
