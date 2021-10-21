/*
 * @(#)EnrolmentWishFormDlg.java	2.17.0 16/03/19
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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.JDialog;
import net.algem.contact.Person;
import net.algem.contact.PersonChoice;
import net.algem.util.BundleUtil;
import net.algem.util.model.GemList;
import net.algem.util.model.GemModel;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GemChoiceLabel;
import net.algem.util.ui.GemChoiceModel;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GemTextArea;
import net.algem.util.ui.GridBagHelper;

/**
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * 
 * @version 2.17.0
 * @since 2.17.0 16/03/19
 */
public class EnrolmentWishFormDlg
        extends JDialog
        implements ActionListener {

    private static final long serialVersionUID = 1L;
    boolean validation;

    EnrolmentWishService wishService;
    int oldStudentId;
    
    PersonChoice student;
    GemLabel teacherLabel;
    GemLabel courseLabel;
    GemLabel dayLabel;
    GemLabel hourLabel;
    
    GemField teacher;
    GemField course;
    GemField day;
    GemField type;
    HourField hour;
    HourField duration;
    Hour oldDuration;
//    JComboBox preference = new JComboBox();
    GemList preferences = new GemList();
    GemChoice preference;
    GemTextArea note;
    
    GemButton btOk;
    GemButton btCancel;

       
    public EnrolmentWishFormDlg(Frame owner, EnrolmentWishService service, String _column, String _teacher, String _course, String _day, Hour _hour) {
        super(owner, BundleUtil.getLabel("Enrolment.wish.label")+" - "+_column, true);

        this.wishService = service;

        student = new PersonChoice(wishService.getStudentsWithOrders());
        student.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                 if(e.getStateChange() == ItemEvent.SELECTED){
                  PersonChoice p = (PersonChoice)e.getSource();
                  initPreferences();
                  setPreferenceReserved(wishService.getWishesFromStudent(p.getKey()), 0);
                  preference.setSelectedIndex(0);
               }  
            }
        });

        teacher = new GemField(_teacher);
        teacher.setEditable(false);
        course = new GemField(_course);
        course.setEditable(false);
        day = new GemField(_day);
        day.setEditable(false);
        type = new GemField("Particulier");
        type.setEditable(false);
        hour = new HourField(_hour);
        hour.setEditable(false);
        
        duration = new HourField();
        note  = new GemTextArea(3,60);
        
        preference = new WishChoice(new GemChoiceModel(preferences));
        initPreferences();
        preference.setKey(1);

        btOk = new GemButton(BundleUtil.getLabel("Action.validation.label"));
        btOk.addActionListener(this);

        btCancel = new GemButton(BundleUtil.getLabel("Action.cancel.label"));
        btCancel.addActionListener(this);

        GemPanel topPanel = new GemPanel(new GridBagLayout());
        GridBagHelper gb = new GridBagHelper(topPanel);        
        gb.add(new GemLabel(BundleUtil.getLabel("Teacher.label")),0,0,1,1,GridBagHelper.WEST);
        gb.add(teacher,1,0,1,1,GridBagHelper.WEST);
        gb.add(new GemLabel(BundleUtil.getLabel("Course.label")),2,0,1,1,GridBagHelper.WEST);
        gb.add(course,3,0,1,1,GridBagHelper.WEST);
        gb.add(new GemLabel(BundleUtil.getLabel("Day.label")),4,0,1,1,GridBagHelper.WEST);
        gb.add(day,5,0,1,1,GridBagHelper.WEST);
        gb.add(new GemLabel(BundleUtil.getLabel("Type.label")),2,1,1,1,GridBagHelper.WEST);
        gb.add(type,3,1,1,1,GridBagHelper.WEST);
        gb.add(new GemLabel(BundleUtil.getLabel("Hour.label")),4,1,1,1,GridBagHelper.WEST);
        gb.add(hour,5,1,1,1,GridBagHelper.WEST);

        GemPanel choicePanel = new GemPanel();
        choicePanel.setLayout(new FlowLayout());
        choicePanel.add(student);
        choicePanel.add(duration);
        choicePanel.add(preference);
        
        GemPanel notesPanel = new GemPanel(new BorderLayout());
        notesPanel.add(new GemLabel(BundleUtil.getLabel("Notes.label")), BorderLayout.WEST);
        notesPanel.add(note, BorderLayout.CENTER);

        GemPanel centerPanel = new GemPanel(new BorderLayout());
        centerPanel.add(choicePanel, BorderLayout.NORTH);
        centerPanel.add(notesPanel, BorderLayout.CENTER);

        GemPanel buttonPanel = new GemPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(btOk);
        buttonPanel.add(btCancel);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
    }

    public void setOnlyPreferenceEditable() {
        student.setEnabled(false);
        duration.setEnabled(false);
        preference.requestFocus();
    }
    
    public void actionPerformed(ActionEvent e) {
        validation = (e.getSource() == btOk);
        close();
    }

    protected void close() {
        setVisible(false);
        dispose();
    }

    public boolean isValidation() {
        return validation;
    }

    private void setPreferenceReserved(List<EnrolmentWish> prefs, int keepSelected) {
        for (EnrolmentWish w : prefs) {
            if (keepSelected == 0 || keepSelected != w.preference) {
                GemModel v = preferences.getItem(w.preference);
                preferences.removeElement(v);
            }
        }
    }
    
    public void setStudent(Person student, int current) {
        if (student != null) {
            this.student.setKey(student.getId());
            this.student.setEnabled(false);
            initPreferences();
            setPreferenceReserved(wishService.getWishesFromStudent(student.getId()), current);
            if (current != 0) {
                preference.setKey(current);
            } else {
                preference.setSelectedIndex(0);
            }
        } else {
            initPreferences();
            setPreferenceReserved(wishService.getWishesFromStudent(this.student.getKey()), 0);
            if (preference.getModel().getSize() > 0)
                preference.setSelectedIndex(0);
        }
    }

    public Person getStudent() {
        return (Person)student.getSelectedItem();
    }

    public Hour getDuration() {
        return duration.get();
    }

    public Hour getOldDuration() {
        return oldDuration;
    }

    public void setDuration(Hour duration) {
        this.duration.set(duration);
        oldDuration = duration;
    }

    public int getPreference() {
        return preference.getKey();
    }

    public void setPreference(int preference) {
        this.preference.setKey(preference);
    }
    
    public String getNote() {
        return note.getText();
    }
    
    public void setNote(String note) {
        this.note.setText(note);
    }

    private void initPreferences() {
      preferences = new GemList();
      preferences.addElement(new GemChoiceLabel(1, BundleUtil.getLabel("Enrolment.wish.preference1.label")));
      preferences.addElement(new GemChoiceLabel(2, BundleUtil.getLabel("Enrolment.wish.preference2.label")));
      preferences.addElement(new GemChoiceLabel(3, BundleUtil.getLabel("Enrolment.wish.preference3.label")));
      preferences.addElement(new GemChoiceLabel(4, BundleUtil.getLabel("Enrolment.wish.preference4.label")));
      preference.setModel(new GemChoiceModel(preferences));
    }
}
