/*
 * @(#)UserView.java	2.13.1 12/04/17
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
package net.algem.security;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import net.algem.contact.Person;
import net.algem.util.BundleUtil;
import net.algem.util.ui.*;
import org.passay.PasswordData;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.RuleResultDetail;

/**
 * User login modification view.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.1
 */
public class UserView
  extends GemBorderPanel {

  private Person person;
  private GemNumericField id;
  private GemField name;
  private GemField login;
  private JPasswordField password1;
  private JPasswordField password2;
  private JComboBox profile;
  private final Rule validator;
  private JButton command;
  private JLabel errorStatus;
  private JCheckBox echoPass;
  private Character echoChar;

  public UserView(final Rule validator) {
    super(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    this.validator = validator;
  }

  public UserView(Person p, final Rule validator, final JButton command) {
    this(validator);
    this.person = p;
    this.command = command;

    id = new GemNumericField(6);
    id.setMinimumSize(new Dimension(60, id.getPreferredSize().height));
    id.setText(String.valueOf(person.getId()));
    id.setEditable(false);

    name = new GemField(25);
    name.setMinimumSize(new Dimension(250, name.getPreferredSize().height));
    name.setText(person.getFirstName() + " " + person.getName());
    name.setEditable(false);

    String tip0 = BundleUtil.getLabel("Login.tip");
    login = new GemField(8);
    login.setToolTipText(tip0);
    login.setMinimumSize(new Dimension(100, login.getPreferredSize().height));

    String ptip1 = BundleUtil.getLabel("Password.tip");
    String ptip2 = BundleUtil.getLabel("Password.confirmation.tip");
    password1 = new JPasswordField(8);
    echoChar = password1.getEchoChar();
    password1.setMinimumSize(new Dimension(150, password1.getPreferredSize().height));
    password1.setToolTipText(ptip1);
    password1.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent event) {
        JPasswordField src = (JPasswordField) event.getSource();
        checkAndWarn(src, password2);
      }
    });

    password2 = new JPasswordField(8);
    password2.setMinimumSize(password1.getMinimumSize());
    password2.setToolTipText(ptip2);

    password2.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent event) {
        JPasswordField src = (JPasswordField) event.getSource();
        checkAndWarn(password1, src);
      }
    });

    profile = new JComboBox(Profile.values());

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(id, 0, 0, 1, 1, GridBagHelper.WEST);
    GemLabel loginLabel = new GemLabel(BundleUtil.getLabel("Login.label"));
    loginLabel.setToolTipText(tip0);
    gb.add(loginLabel, 0, 1, 1, 1, GridBagHelper.WEST);
    GemLabel p1Label = new GemLabel(BundleUtil.getLabel("Password.label"));
    p1Label.setToolTipText(ptip1);
    GemLabel p2Label = new GemLabel(BundleUtil.getLabel("Password.confirmation.label"));
    p2Label.setToolTipText(ptip2);

    errorStatus = new JLabel(" ");
    errorStatus.setForeground(Color.RED);
    echoPass = new JCheckBox(BundleUtil.getLabel("Display.label"));
    echoPass.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          password1.setEchoChar((char) 0);
          password2.setEchoChar((char) 0);
        } else {
          password1.setEchoChar(echoChar);
          password2.setEchoChar(echoChar);
        }
      }
    });
    gb.add(p1Label, 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(p2Label, 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Profile.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(name, 1, 0, 2, 1, GridBagHelper.WEST);
    gb.add(login, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(password1, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(echoPass, 2, 2, 1, 1, GridBagHelper.WEST);
    gb.add(password2, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(profile, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(errorStatus, 0, 5, 3, 1, GridBagHelper.WEST);
  }

  public User get() {
    // login, pass, profile
    User u = new User(person);
    u.setLogin(login.getText());
    u.setPassword(String.valueOf(password1.getPassword()));
    u.setProfile(((Profile) profile.getSelectedItem()).getId());
    return u;
  }

  public void set(User u) {
    name.setText(u.getFirstName() + " " + u.getName());
    password1.setText(u.getPassword());
    login.setText(u.getLogin());
    profile.setSelectedItem(Profile.get(u.getProfile()));
  }

  public void clear() {
    id.setText("");
    name.setText("");
    password1.setText("");
    password2.setText("");
    echoPass.setSelected(false);
    login.setText("");
    profile.setSelectedIndex(0);
  }

  private String getErrorKey(RuleResult result, int strength) {
    String err = "";

    if (!result.isValid()) {
      for (RuleResultDetail detail : result.getDetails()) {
        String d = detail.getErrorCode();
        switch (strength) {
          case RuleFactory.LOW:
            if ("TOO_SHORT".equals(d)) {
              return "Password." + d;
            } else {
              err = d;
            }
            break;
          case RuleFactory.MEDIUM:
            if ("TOO_SHORT".equals(d) || "INSUFFICIENT_LOWERCASE".equals(d)) {
              return "Password." + d;
            } else {
              err = d;
            }
            break;
          case RuleFactory.STRONG:
            if ("TOO_SHORT".equals(d) || "INSUFFICIENT_LOWERCASE".equals(d) || "INSUFFICIENT_UPPERCASE".equals(d) || "INSUFFICIENT_DIGIT".equals(d) || "INSUFFICIENT_SPECIAL".equals(d)) {
              return "Password." + d;
            } else {
              err = d;
            }
            break;
        }
      }

    }
    return err;
  }

  private Color getRuleColor(String err) {
    if (err == null || err.isEmpty()) {
      return UIManager.getColor("PasswordField.background");
    }

    //mandatory
    if ("Password.TOO_SHORT".equals(err)
      || "Password.INSUFFICIENT_LOWERCASE".equals(err)
      || "Password.INSUFFICIENT_UPPERCASE".equals(err)
      || "Password.INSUFFICIENT_DIGIT".equals(err)
      || "Password.INSUFFICIENT_SPECIAL".equals(err)) {
      return Color.RED;
    }
    return Color.ORANGE;
  }

  private void checkAndWarn(JPasswordField one, JPasswordField two) {
    String p = String.copyValueOf(one.getPassword());
    RuleResult result = validator.validate(new PasswordData(p));
    String err = getErrorKey(result, RuleFactory.MEDIUM);//TODO config
    Color ruleColor = getRuleColor(err);
    one.setBackground(result.isValid() ? Color.GREEN : getRuleColor(err));
    if (Color.ORANGE.equals(ruleColor)) {
      errorStatus.setText(BundleUtil.getLabel("Password.weak.warning"));
    } else if (Color.RED.equals(ruleColor)) {
      errorStatus.setText(BundleUtil.getLabel("Password.invalid.warning", new Object[]{BundleUtil.getLabel(err)}));
    } else {
      errorStatus.setText(" ");
    }

    boolean samepass = Arrays.equals(one.getPassword(), two.getPassword());
    two.setBackground(samepass ? Color.GREEN : Color.RED);
    if (command != null) {
      command.setEnabled(samepass && !ruleColor.equals(Color.RED));
    }

  }

}
