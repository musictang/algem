/*
 * @(#)PersonSearchView.java    2.17.0p 28/06/2019
 *                              2.15.6 29/11/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
package net.algem.contact;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import net.algem.Algem;
import net.algem.config.Instrument;
import net.algem.config.InstrumentChoice;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.*;

/**
 * View used to search a contact whose type is other than
 * {@code  net.algem.contact.Person.BANK} and  {@code net.algem.contact.Person.ESTABLISHMENT}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.0p
 * @since 1.0a 07/07/1999
 */
public class PersonSearchView
        extends SearchView
{

  private GemNumericField number;
  private GemField org;
  private GemField name;
  private GemField firstname;
  private GemField pseudo;
  private GemField telephone;
  private GemField email;
  private GemField site;
  private GemPanel mask;
  private InstrumentChoice instrument;      //ERIC 2.17
  private GemNumericField cycle;    //ERIC 2.17
  //private List<Instrument> instruments;

  ButtonGroup filter;
  private JRadioButton checkTeacherOnly;
  private JRadioButton checkMemberOnly;

  public PersonSearchView() {

  }

  @Override
  public GemPanel init() {
    number = new GemNumericField(6);
    number.setMinimumSize(new Dimension(70, number.getPreferredSize().height));
    number.addActionListener(this);
    org = new GemField(15);
    Dimension min = new Dimension(170, org.getPreferredSize().height);
    org.setMinimumSize(min);
    org.addActionListener(this);
    name = new GemField(15);
    name.setMinimumSize(min);
    name.addActionListener(this);
    firstname = new GemField(15);
    firstname.setMinimumSize(min);
    firstname.addActionListener(this);
    pseudo = new GemField(15);
    pseudo.setMinimumSize(min);
    pseudo.addActionListener(this);
    telephone = new GemField(15);
    telephone.setMinimumSize(min);
    telephone.addActionListener(this);
    email = new GemField(15);
    email.setMinimumSize(min);
    email.addActionListener(this);
    site = new GemField(15);
    site.setMinimumSize(min);
    site.addActionListener(this);
//ERIC 2.17
    instrument = new InstrumentChoice();    //desktop.getDataCache().getInstruments());

    cycle = new GemNumericField(2);
    //cycle.setMinimumSize(new Dimension(70, cycle.getPreferredSize().height));
    cycle.addActionListener(this);
    
    btErase = new GemButton(GemCommand.ERASE_CMD);
    btErase.setToolTipText(BundleUtil.getLabel("Person.search.erase.tip"));
    btErase.addActionListener(this);

    mask = new GemPanel();
    mask.setLayout(new GridBagLayout());

    GridBagHelper gb = new GridBagHelper(mask);
    gb.add(new GemLabel(BundleUtil.getLabel("Number.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Organization.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Name.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("First.name.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Nickname.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Telephone.label")), 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Email.label")), 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Website.label")), 0, 7, 1, 1, GridBagHelper.WEST);

    gb.add(number, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(org, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(name, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(firstname, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(pseudo, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(telephone, 1, 5, 1, 1, GridBagHelper.WEST);
    gb.add(email, 1, 6, 1, 1, GridBagHelper.WEST);
    gb.add(site, 1, 7, 1, 1, GridBagHelper.WEST);
    
    if (Algem.isFeatureEnabled("cc-mdl")) {
        gb.add(new GemLabel(BundleUtil.getLabel("Cycle.label")), 0, 8, 1, 1, GridBagHelper.WEST);
        gb.add(new GemLabel(BundleUtil.getLabel("Instrument.label")), 0, 9, 1, 1, GridBagHelper.WEST);
        gb.add(cycle, 1, 8, 1, 1, GridBagHelper.WEST);
        gb.add(instrument, 1, 9, 1, 1, GridBagHelper.WEST);
        gb.add(checkTeacherOnly = new JRadioButton(BundleUtil.getLabel("Person.search.teacher.filter.label")), 0,10,2,1, GridBagHelper.WEST);
        gb.add(checkMemberOnly = new JRadioButton(BundleUtil.getLabel("Person.search.member.filter.label")), 0,11,2,1, GridBagHelper.WEST);
    } else {
        gb.add(checkTeacherOnly = new JRadioButton(BundleUtil.getLabel("Person.search.teacher.filter.label")), 0,8,2,1, GridBagHelper.WEST);
        gb.add(checkMemberOnly = new JRadioButton(BundleUtil.getLabel("Person.search.member.filter.label")), 0,9,2,1, GridBagHelper.WEST);
    }
    filter = new ButtonGroup();
    filter.add(checkTeacherOnly);
    filter.add(checkMemberOnly);
    gb.add(btErase, 2, 10, 1, 1, GridBagHelper.WEST);

    return mask;
  }
  
  public void setInstruments(Vector<Instrument> instruments) {
      instrument.setList(instruments);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    //System.out.println("T:PersonSearchView.action evt:"+evt);
    if (actionListener == null) {
      return;
    }
    if (evt.getSource() == name
            || evt.getSource() == org
            || evt.getSource() == number
            || evt.getSource() == telephone
            || evt.getSource() == firstname
            || evt.getSource() == pseudo
            || evt.getSource() == email
            || evt.getSource() == site
            || evt.getSource() == cycle) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.SEARCH_CMD));
    } else {
      actionListener.actionPerformed(evt);
    }
  }

  @Override
  public String getField(int n) {
    String s = null;
    switch (n) {
      case 0:
        s = number.getText().trim();
        break;
      case 1:
        s = org.getText().trim();
        break;
      case 2:
        s = name.getText().trim();
        break;
      case 3:
        s = firstname.getText().trim();
        break;
      case 4:
        s = telephone.getText().trim();
        break;
      case 5:
        s = email.getText().trim();
        break;
      case 6:
        s = site.getText().trim();
        break;
      case 7:
        s = pseudo.getText().trim();
        break;
      case 8:
        s = cycle.getText().trim();
        break;
      case 9:
        s = instrument.getKey() != 0 ? String.valueOf(instrument.getKey()) : null;
        break;    }
    if (s != null && s.length() > 0) {
      return s;
    } else {
      return null;
    }
  }

  boolean isFilteredByTeacher() {
    return checkTeacherOnly.isSelected();
  }

  boolean isFilteredByMember() {
    return checkMemberOnly.isSelected();
  }

  @Override
  public void clear() {
    number.setText("");
    org.setText("");
    name.setText("");
    firstname.setText("");
    pseudo.setText("");
    telephone.setText("");
    email.setText("");
    site.setText("");
    cycle.setText("");
    instrument.setSelectedIndex(0);
    filter.clearSelection();
  }
}
