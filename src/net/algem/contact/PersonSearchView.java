/*
 * @(#)PersonSearchView.java	2.9.4.13 15/10/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.*;

/**
 * View used to search a contact whose type is other than
 * {@code  net.algem.contact.Person.BANK} and  {@code net.algem.contact.Person.ESTABLISHMENT}.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
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

  public PersonSearchView() {

  }

  @Override
  public GemPanel init() {
    number = new GemNumericField(6);
    number.addActionListener(this);
    org = new GemField(15);
    org.addActionListener(this);
    name = new GemField(15);
    name.addActionListener(this);
    firstname = new GemField(15);
    firstname.addActionListener(this);
    pseudo = new GemField(15);
    pseudo.addActionListener(this);
    telephone = new GemField(15);
    telephone.addActionListener(this);
    email = new GemField(15);
    email.addActionListener(this);
    site = new GemField(15);
    site.addActionListener(this);

    btErase = new GemButton(GemCommand.ERASE_CMD);
    btErase.addActionListener(this);

    mask = new GemPanel();
    mask.setLayout(new GridBagLayout());

    GridBagHelper gb = new GridBagHelper(mask);
    gb.insets = GridBagHelper.SMALL_INSETS;
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
    gb.add(btErase, 2, 9, 1, 1, GridBagHelper.WEST);

    return mask;
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
            || evt.getSource() == site) {
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
        s = number.getText();
        break;
      case 1:
        s = org.getText();
        break;
      case 2:
        s = name.getText();
        break;
      case 3:
        s = firstname.getText();
        break;
      case 4:
        s = telephone.getText();
        break;
      case 5:
        s = email.getText();
        break;
      case 6:
        s = site.getText();
        break;
      case 7:
        s = pseudo.getText();
        break;
    }
    if (s != null && s.length() > 0) {
      return s;
    } else {
      return null;
    }
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
  }
}
