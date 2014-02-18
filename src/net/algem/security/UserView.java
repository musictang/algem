/*
 * @(#)UserView.java	2.8.s 18/02/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.security;

import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import net.algem.contact.Person;
import net.algem.util.BundleUtil;
import net.algem.util.ui.*;

/**
 * User login modification view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.s
 */
public class UserView
        extends GemBorderPanel
{

  private Person person;
  private GemNumericField id;
  private GemField name;
  private GemField login;
  private JPasswordField password;
  private JComboBox profile;

  public UserView() {
    super(BorderFactory.createEmptyBorder(20, 20, 20, 20));
  }

  public UserView(Person p) {

    this.person = p;

    id = new GemNumericField(6);
    id.setText(String.valueOf(person.getId()));
    id.setEditable(false);

    name = new GemField(25);
    name.setText(person.getFirstName() + " " + person.getName());
    name.setEditable(false);

    login = new GemField(8);
    password = new JPasswordField(8);

    profile = new JComboBox(UserIO.PROFIL_NAMES);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(id, 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Login.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Password.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Profile.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(name, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(login, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(password, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(profile, 1, 3, 1, 1, GridBagHelper.WEST);
  }

  public User get() {
    // login, pass, profile
    User u = new User(person);
    u.setLogin(login.getText());
    u.setPassword(String.valueOf(password.getPassword()));
    u.setProfile(profile.getSelectedIndex());
    return u;
  }

  public void set(User u) {
    name.setText(u.getFirstName() + " " + u.getName());
    password.setText(u.getPassword());// clear text password is null by default
    login.setText(u.getLogin());
    profile.setSelectedIndex(u.getProfile());
  }

  public void clear() {
    id.setText("");
    name.setText("");
    password.setText("");
    login.setText("");
    profile.setSelectedIndex(0);
  }
}
