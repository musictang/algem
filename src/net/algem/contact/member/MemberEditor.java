/*
 * @(#)MemberEditor.java	2.8.c 14/05/13
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
package net.algem.contact.member;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Calendar;
import java.util.Locale;
import net.algem.config.CategoryOccupChoice;
import net.algem.contact.InstrumentView;
import net.algem.contact.Person;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonIO;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.model.Reloadable;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * Member file editor.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.c
 */
public class MemberEditor
        extends FileTab
        implements FocusListener, ActionListener, Reloadable
{

  private GemChoice occupation;
  private DateFrField birth;
  private GemNumericField age;
  private GemNumericField nMemberships;
  private GemNumericField payer;
  private GemField payerName;
  private GemNumericField practice;
  private GemNumericField level;
  private int id;
  private InstrumentView instrument;

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
    payerName = new GemField(30);
    payerName.setEditable(false);
    payerName.setBackground(Color.lightGray);
    practice = new GemNumericField(3);
    level = new GemNumericField(3);

    age = new GemNumericField(3);
    age.setEditable(false);
    age.setBackground(Color.lightGray);

    GemPanel p = new GemPanel();
    p.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(p);
    gb.insets = GridBagHelper.SMALL_INSETS;
    //gb.add(new GemLabel(BundleUtil.getLabel("Instruments.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Occupation.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.of.birth.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Membership.number.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Practical.experience.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Level.label")), 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Payer.label")), 0, 7, 1, 1, GridBagHelper.WEST);

    gb.add(instrument, 1, 0, 3, 1, GridBagHelper.WEST);
    gb.add(occupation, 1, 2, 2, 1, GridBagHelper.WEST);
    gb.add(birth, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(age, 2, 3, 1, 1, GridBagHelper.WEST);
    gb.add(nMemberships, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(practice, 1, 5, 1, 1, GridBagHelper.WEST);
    gb.add(level, 1, 6, 2, 1, GridBagHelper.WEST);
    gb.add(payer, 1, 7, 1, 1, GridBagHelper.WEST);
    gb.add(payerName, 2, 7, 1, 1, GridBagHelper.WEST);
    
    this.setLayout(new BorderLayout());
    add(p, BorderLayout.CENTER);
  }

  public void searchPayer() {
    try {
      loadPayeur(Integer.parseInt(payer.getText()));
    } catch (Exception e) {
      payer.setText("");
      payerName.setText("inconnu !");
    }
  }

	@Override
  public void focusGained(FocusEvent evt) {
  }

	@Override
  public void focusLost(FocusEvent evt) {
    if (evt.getSource() == payer) {
      searchPayer();
    } else if (evt.getSource() == birth) {
      Calendar cal = Calendar.getInstance(Locale.FRANCE);
      int a = cal.get(Calendar.YEAR) - birth.get().getYear();
      age.setText(String.valueOf(a));
    }
  }

	@Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == payer) {
      searchPayer();
    }
  }

  public String getPayer() {
    return payer.getText();
  }

  public Member getMember() {
    Member m = new Member(id);

    m.setOccupation((String) occupation.getSelectedItem());
    m.setBirth(new DateFr(birth.getDateFr()));
    try {
      m.setMembershipCount(Integer.parseInt(nMemberships.getText()));
    } catch (Exception e) {
      m.setMembershipCount(0);
    }
    try {
      m.setPractice(Integer.parseInt(practice.getText()));
    } catch (Exception e) {
      m.setPractice(0);
    }
    try {
      m.setLevel(Integer.parseInt(level.getText()));
    } catch (Exception e) {
      m.setLevel(0);
    }
    try {
      m.setPayer(Integer.parseInt(payer.getText()));
    } catch (Exception e) {
      m.setPayer(0);
    }
    m.setInstruments(instrument.get());
    return m;
  }

  public void set(Member m) {
    id = m.getId();
//    member = m;
    instrument.set(m.getInstruments());
//    instrument1.setSelectedItem((String) m.getInstrument1());
//    instrument2.setSelectedItem((String) m.getInstrument2());
    occupation.setSelectedItem((String) m.getOccupation());
    if (m.getBirth() != null) {
      birth.set(m.getBirth());
      Calendar cal = Calendar.getInstance(Locale.FRANCE);
      int a = cal.get(Calendar.YEAR) - m.getBirth().getYear();
      age.setText(String.valueOf(a));
    } else {
      age.setText("0");
    }
    // vérifier nombre de lignes d'adhésions dans l'échéancier
    nMemberships.setText(String.valueOf(m.getMembershipCount()));
    practice.setText(String.valueOf(m.getPractice()));
    level.setText(String.valueOf(m.getLevel()));
    payer.setText(String.valueOf(m.getPayer()));
    loadPayeur(m.getPayer());
  }

  public void setPayer(int _id, String name) {
    payer.setText(String.valueOf(_id));
    if (name != null) {
      payerName.setText(name);
    } else {
      payerName.setText("");
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
    Person p = ((PersonIO) DataCache.getDao(Model.Person)).findId(_id);
    if (p != null) {
      String org = p.getOrganization();
      payerName.setText(org == null || org.isEmpty() ? p.getFirstnameName() : org);
    } else {
      payerName.setText(BundleUtil.getLabel("Unknown.label"));
    }
  }

  public void clear() {
    id = 0;
//    member = null;
//    instrument1.setSelectedIndex(0);
//    instrument2.setSelectedIndex(0);
    occupation.setSelectedIndex(0);
    birth.setText(DateFr.NULLDATE);
    age.setText("");
    nMemberships.setText("");
    payer.setText("");
    payerName.setText("");
  }

  @Override
  public boolean isLoaded() {
    return id != 0;
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
