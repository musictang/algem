/*
 * @(#)UserView.java	2.15.10 28/09/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
 * @version 2.15.10
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
  private GemNumericField desktop;
  private GemField emailAgent;
  private GemField webAgent;
  private GemField textAgent;
  private GemField tableAgent;

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
    password1.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        JPasswordField src = (JPasswordField) e.getSource();
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

    password2.addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        JPasswordField src = (JPasswordField) e.getSource();
        checkAndWarn(src, password1);
      }
    });

    profile = new JComboBox(Profile.values());

    desktop = new GemNumericField(6);
    desktop.setMinimumSize(new Dimension(60, id.getPreferredSize().height));

    emailAgent = new GemField(60);
    emailAgent.setMinimumSize(new Dimension(250, name.getPreferredSize().height));
    
    webAgent = new GemField(60);
    webAgent.setMinimumSize(new Dimension(250, name.getPreferredSize().height));
    
    textAgent = new GemField(60);
    textAgent.setMinimumSize(new Dimension(250, name.getPreferredSize().height));
    
    tableAgent = new GemField(60);
    tableAgent.setMinimumSize(new Dimension(250, name.getPreferredSize().height));
    
    
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
    gb.add(new GemLabel(BundleUtil.getLabel("Desktop.label")), 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("EmailAgent.label")), 0, 7, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("WebAgent.label")), 0, 8, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("TextAgent.label")), 0, 9, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("TableAgent.label")), 0, 10, 1, 1, GridBagHelper.WEST);
    
    gb.add(name, 1, 0, 2, 1, GridBagHelper.WEST);
    gb.add(login, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(password1, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(echoPass, 2, 2, 1, 1, GridBagHelper.WEST);
    gb.add(password2, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(profile, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(errorStatus, 0, 5, 3, 1, GridBagHelper.WEST);
    gb.add(desktop, 1, 6, 1, 1, GridBagHelper.WEST);
    gb.add(emailAgent, 1, 7, 1, 1, GridBagHelper.WEST);
    gb.add(webAgent, 1, 8, 1, 1, GridBagHelper.WEST);
    gb.add(textAgent, 1, 9, 1, 1, GridBagHelper.WEST);
    gb.add(tableAgent, 1, 10, 1, 1, GridBagHelper.WEST);
  }

  public User get() {
    // login, pass, profile
    User u = new User(person);
    u.setLogin(login.getText());
    u.setPassword(String.valueOf(password1.getPassword()));
    u.setProfile(((Profile) profile.getSelectedItem()).getId());
    try {
    u.setDesktop(Integer.valueOf(desktop.getText()));
    } catch (NumberFormatException ex) {
        u.setDesktop(1);
    }
    u.setEmailAgent(emailAgent.getText());
    u.setWebAgent(webAgent.getText());
    u.setTextAgent(textAgent.getText());
    u.setTableAgent(tableAgent.getText());
    return u;
  }

  public void set(User u) {
    name.setText(u.getFirstName() + " " + u.getName());
//    password1.setText(u.getPassword());
    login.setText(u.getLogin());
    profile.setSelectedItem(Profile.get(u.getProfile()));
    desktop.setText(String.valueOf(u.getDesktop()));
    emailAgent.setText(u.getEmailAgent());
    webAgent.setText(u.getWebAgent());
    textAgent.setText(u.getTextAgent());
    tableAgent.setText(u.getTableAgent());
  }

  public void clear() {
    id.setText(null);
    name.setText(null);
    password1.setText(null);
    password2.setText(null);
    echoPass.setSelected(false);
    login.setText(null);
    profile.setSelectedIndex(0);
    desktop.setText(null);
    emailAgent.setText(null);
    webAgent.setText(null);
    textAgent.setText(null);
    tableAgent.setText(null);
  }

  private String getErrorKey(RuleResult result, int strength) {
    String err = "";

    if (!result.isValid()) {
      for (RuleResultDetail detail : result.getDetails()) {
        String errorCode = detail.getErrorCode();
        switch (strength) {
          case RuleFactory.LOW:
            if ("TOO_SHORT".equals(errorCode)) {
              return "Password." + errorCode;
            } else {
              err = errorCode;
            }
            break;
          case RuleFactory.MEDIUM:
            if ("TOO_SHORT".equals(errorCode)
              || "TOO_LONG".equals(errorCode)
              || "INSUFFICIENT_UPPERCASE".equals(errorCode)
              || "INSUFFICIENT_LOWERCASE".equals(errorCode)) {
              return "Password." + errorCode;
            }
            break;
          case RuleFactory.STRONG:
            if ("TOO_SHORT".equals(errorCode)
              || "INSUFFICIENT_LOWERCASE".equals(errorCode)
              || "INSUFFICIENT_UPPERCASE".equals(errorCode)
              || "INSUFFICIENT_DIGIT".equals(errorCode)
              || "INSUFFICIENT_SPECIAL".equals(errorCode)) {
              return "Password." + errorCode;
            } else {
              err = errorCode;
            }
            break;
        }
      }

    }
    return err;
  }

  private Color getRuleColor(String err, int strength) {
    if (err == null || err.isEmpty()) {
      return UIManager.getColor("PasswordField.background");
    }

    if (RuleFactory.STRONG == strength) {
      if ("Password.TOO_SHORT".equals(err)
        || "Password.TOO_LONG".equals(err)
        || "Password.INSUFFICIENT_LOWERCASE".equals(err)
        || "Password.INSUFFICIENT_UPPERCASE".equals(err)
        || "Password.INSUFFICIENT_DIGIT".equals(err)
        || "Password.INSUFFICIENT_SPECIAL".equals(err)) {
        return Color.RED;
      }
    } else {
      if ("Password.TOO_SHORT".equals(err)
        || "Password.TOO_LONG".equals(err)
        || "Password.INSUFFICIENT_LOWERCASE".equals(err)
        || "Password.INSUFFICIENT_DIGIT".equals(err)) {
        return Color.RED;
      }
    }
    return Color.ORANGE;
  }

  private void checkAndWarn(JPasswordField one, JPasswordField two) {
    String p = String.copyValueOf(one.getPassword());
    RuleResult result = validator.validate(new PasswordData(p));
    String err = getErrorKey(result, RuleFactory.MEDIUM);//TODO config
    Color ruleColor = getRuleColor(err, RuleFactory.MEDIUM);
    one.setBackground(result.isValid() ? Color.GREEN : ruleColor);
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
