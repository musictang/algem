/*
 * @(#)MemberEditor.java	2.17.0 04/06/2019
 *                              2.16.0 05/03/19
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
package net.algem.contact.member;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;
import net.algem.Algem;
import net.algem.config.CategoryOccupChoice;
import net.algem.contact.InstrumentView;
import net.algem.contact.Person;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonIO;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.planning.DateLib;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.model.Reloadable;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Member file editor.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.0
 */
public class MemberEditor
        extends FileTab
        implements FocusListener, ActionListener, Reloadable {

    private GemChoice occupation;
    private DateFrField birth;
    private GemNumericField age;
    private GemNumericField nMemberships;
    private GemNumericField payer;
    private GemNumericField family;
    private GemField payerName;
    private GemField familyName;
    private GemNumericField practice;
    private GemNumericField level;
    private GemField insurance;
    private GemField insuranceRef;
    private int id;
    private InstrumentView instrument;
    private Member currentMember = null;

    public MemberEditor(GemDesktop _desktop, int _id) {
        super(_desktop);
        id = _id;
        instrument = new InstrumentView(dataCache.getInstruments());
        occupation = new CategoryOccupChoice(dataCache.getOccupationalCat());
        birth = new DateFrField();
        birth.addFocusListener(this);
        nMemberships = new GemNumericField(3);
        payer = new GemNumericField(6);
        payer.addFocusListener(this);
        payer.addActionListener(this);
        payerName = new GemField(20);
        payerName.setEditable(false);
        payerName.setBackground(Color.lightGray);
        family = new GemNumericField(6);
        family.addFocusListener(this);
        family.addActionListener(this);
        familyName = new GemField(20);
        familyName.setEditable(false);
        familyName.setBackground(Color.lightGray);
        practice = new GemNumericField(3);
        level = new GemNumericField(3);
        insurance = new GemField(20);
        insuranceRef = new GemField(20);

        age = new GemNumericField(3);
        age.setEditable(false);
        age.setBackground(Color.lightGray);

        GemPanel p = new GemPanel();
        p.setLayout(new GridBagLayout());
        GridBagHelper gb = new GridBagHelper(p);
        //gb.add(new GemLabel(BundleUtil.getLabel("Instruments.label")), 0, 0, 1, 1, GridBagHelper.WEST);
        gb.add(new GemLabel(BundleUtil.getLabel("Occupation.label")), 0, 2, 1, 1, GridBagHelper.WEST);
        gb.add(new GemLabel(BundleUtil.getLabel("Date.of.birth.label")), 0, 3, 1, 1, GridBagHelper.WEST);
        gb.add(new GemLabel(BundleUtil.getLabel("Membership.number.label")), 0, 4, 1, 1, GridBagHelper.WEST);
        gb.add(new GemLabel(BundleUtil.getLabel("Practical.experience.label")), 0, 5, 1, 1, GridBagHelper.WEST);
        gb.add(new GemLabel(BundleUtil.getLabel("Level.label")), 0, 6, 1, 1, GridBagHelper.WEST);
        gb.add(new GemLabel(BundleUtil.getLabel("Payer.label")), 0, 7, 1, 1, GridBagHelper.WEST);
        if (Algem.isFeatureEnabled("cc-mdl")) {
            gb.add(new GemLabel(BundleUtil.getLabel("Family.label")), 0, 8, 1, 1, GridBagHelper.WEST);
        }
        GemLabel insuranceL = new GemLabel(BundleUtil.getLabel("Insurance.label"));
        insuranceL.setToolTipText(BundleUtil.getLabel("Insurance.tip"));
        GemLabel insuranceRefL = new GemLabel(BundleUtil.getLabel("Insurance.ref.label"));
        insuranceRefL.setToolTipText(BundleUtil.getLabel("Insurance.ref.tip"));
        gb.add(insuranceL, 0, 9, 1, 1, GridBagHelper.WEST);
        gb.add(insuranceRefL, 0, 10, 1, 1, GridBagHelper.WEST);

        gb.add(instrument, 1, 0, 3, 1, GridBagHelper.WEST);
        occupation.setPreferredSize(new Dimension(200, occupation.getPreferredSize().height));
        gb.add(occupation, 1, 2, 2, 1, GridBagHelper.WEST);
        gb.add(birth, 1, 3, 1, 1, GridBagHelper.WEST);
        gb.add(age, 2, 3, 1, 1, GridBagHelper.WEST);
        gb.add(nMemberships, 1, 4, 1, 1, GridBagHelper.WEST);
        gb.add(practice, 1, 5, 1, 1, GridBagHelper.WEST);
        gb.add(level, 1, 6, 2, 1, GridBagHelper.WEST);
        gb.add(payer, 1, 7, 1, 1, GridBagHelper.WEST);
        gb.add(payerName, 2, 7, 1, 1, GridBagHelper.WEST);
        if (Algem.isFeatureEnabled("cc-mdl")) {
            gb.add(family, 1, 8, 1, 1, GridBagHelper.WEST);
            gb.add(familyName, 2, 8, 1, 1, GridBagHelper.WEST);
        }
        gb.add(insurance, 1, 9, 2, 1, GridBagHelper.WEST);
        gb.add(insuranceRef, 1, 10, 2, 1, GridBagHelper.WEST);

        this.setLayout(new BorderLayout());
        add(p, BorderLayout.CENTER);
    }

    public void searchPayer() {
        try {
            loadPayeur(Integer.parseInt(payer.getText()));
        } catch (NumberFormatException e) {
            payer.setText("");
            payerName.setText(BundleUtil.getLabel("Unknown.label"));
        }
    }

    public void searchFamily() {
        try {
            loadFamily(Integer.parseInt(family.getText()));
        } catch (NumberFormatException e) {
            family.setText("");
            familyName.setText(BundleUtil.getLabel("Unknown.label"));
        }
    }

    @Override
    public void focusGained(FocusEvent evt) {
    }

    @Override
    public void focusLost(FocusEvent evt) {
        if (evt.getSource() == payer) {
            searchPayer();
        } else if (evt.getSource() == family) {
            searchFamily();
        } else if (evt.getSource() == birth) {
            age.setText(String.valueOf(DateLib.getAge(birth.get())));
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == payer) {
            searchPayer();
        }
        if (evt.getSource() == family) {
            searchFamily();
        }
    }

    public String getPayer() {
        return payer.getText();
    }

    public String getFamily() {
        return family.getText();
    }

    public Member getMember() {
        Member m = currentMember;

        m.setOccupation((String) occupation.getSelectedItem());
        m.setBirth(new DateFr(birth.getDateFr()));
        try {
            m.setMembershipCount(Integer.parseInt(nMemberships.getText()));
        } catch (NumberFormatException e) {
            m.setMembershipCount(0);
        }
        try {
            m.setPractice(Integer.parseInt(practice.getText()));
        } catch (NumberFormatException e) {
            m.setPractice(0);
        }
        try {
            m.setLevel(Integer.parseInt(level.getText()));
        } catch (NumberFormatException e) {
            m.setLevel(0);
        }
        try {
            m.setPayer(Integer.parseInt(payer.getText()));
        } catch (NumberFormatException e) {
            m.setPayer(0);
        }
        try {
            m.setFamily(Integer.parseInt(family.getText()));
        } catch (NumberFormatException e) {
            m.setFamily(0);
        }
        m.setInstruments(instrument.get());
        m.setInsurance(insurance.getText().isEmpty() ? null : insurance.getText());
        m.setInsuranceRef(insuranceRef.getText().isEmpty() ? null : insuranceRef.getText());
        return m;
    }

    public void set(Member m) {
        id = m.getId();
        instrument.set(m.getInstruments());
        occupation.setSelectedItem((String) m.getOccupation());
        if (m.getBirth() != null) {
            birth.set(m.getBirth());
            age.setText(String.valueOf(DateLib.getAge(m.getBirth())));
        } else {
            age.setText("0");
        }
        // vérifier nombre de lignes d'adhésions dans l'échéancier
        nMemberships.setText(String.valueOf(m.getMembershipCount()));
        practice.setText(String.valueOf(m.getPractice()));
        level.setText(String.valueOf(m.getLevel()));
        payer.setText(String.valueOf(m.getPayer()));
        loadPayeur(m.getPayer());
        if (Algem.isFeatureEnabled("cc-mdl")) { //ASUIVRE===============SUFFISANT ??? ===========
            family.setText(String.valueOf(m.getFamily()));
            loadFamily(m.getFamily());
        }
        insurance.setText(m.getInsurance());
        insuranceRef.setText(m.getInsuranceRef());
        currentMember = m;
    }

    public void setPayer(int _id, String name) {
        payer.setText(String.valueOf(_id));
        if (name != null) {
            payerName.setText(name);
        } else {
            payerName.setText("");
        }
    }

    public void setFamily(int _id, String name) {
        family.setText(String.valueOf(_id));
        if (name != null) {
            familyName.setText(name);
        } else {
            familyName.setText("");
        }
    }

    public void setMembershipNumber(int n) {
        nMemberships.setText(String.valueOf(n));
    }

    public void loadPayeur(int _id) {
        if (_id == id) {
            payerName.setText(BundleUtil.getLabel("Himself.label"));
            return;
        }
        PersonFile pf = DataCache.getPersonFile(_id);
        if (pf == null) {
            MessagePopup.warning(this, MessageUtil.getMessage("not.existing.payer.link"));
            payerName.setText(BundleUtil.getLabel("Unknown.label"));
            GemLogger.log("Error link payer fiche:"+_id);
            
        } else {
            String org = pf.getContact().getOrganization() != null ? pf.getContact().getOrganization().getCompanyName() : null;
            payerName.setText(org == null || org.isEmpty() ? pf.getContact().getFirstnameName() : org);
        }
    }

    public void loadFamily(int _id) {
        if (_id == id) {
            familyName.setText(BundleUtil.getLabel("Himself.label"));
            return;
        }
        PersonFile pf = DataCache.getPersonFile(_id);;
        if (pf == null) {
            MessagePopup.warning(this, MessageUtil.getMessage("not.existing.familly.link"));
            familyName.setText(BundleUtil.getLabel("Unknown.label"));
            GemLogger.log("Error link family fiche:"+_id);
        } else {
            String org = pf.getContact().getOrganization() != null ? pf.getContact().getOrganization().getCompanyName() : null;
            familyName.setText(org == null || org.isEmpty() ? pf.getContact().getFirstnameName() : org);
        }
    }

    public void clear() {
        id = 0;
        occupation.setSelectedIndex(0);
        birth.setText(DateFr.NULLDATE);
        age.setText("");
        nMemberships.setText("");
        payer.setText("");
        payerName.setText("");
        family.setText("");
        familyName.setText("");
        insurance.setText(null);
        insuranceRef.setText(null);
    }

    @Override
    public boolean isLoaded() {
        return id > 0;
    }

    @Override
    public void load() {
    }

    @Override
    public void reload(PersonFile d) {
        clear();
        set(d.getOldMember());
    }

}
