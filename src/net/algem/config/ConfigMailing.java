/*
 * @(#)ConfigMailing.java 2.17.3d 02/06/20
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
package net.algem.config;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import net.algem.util.BundleUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Organization parameters and contact.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.3d
 * @since 2.17.0 08/04/2019
 */
public class ConfigMailing
  extends ConfigPanel {


  private Config c1,c2,c3,c4,c5,c6,c7;

  private JTextField server;
  private JTextField port;
  private JTextField user;
  private JTextField sender; //ajout adresse expéditeur le 02/06/20
  private JPasswordField password;
  private JCheckBox authentification;
  private static String[] protocols = new String[] {"Aucun","SSL","TLS"};
  private JComboBox<String> security ;
  

  private GemDesktop desktop;

  public ConfigMailing(String title, Map<String, Config> cm) {
    super(title, cm);
    this.desktop = desktop;
    init();
  }

  private void init() {
        
    c1 = confs.get(ConfigKey.SMTP_SERVER_NAME.getKey());
    c2 = confs.get(ConfigKey.SMTP_SERVER_PORT.getKey());
    c3 = confs.get(ConfigKey.SMTP_SERVER_USER.getKey());
    c4 = confs.get(ConfigKey.SMTP_SERVER_PSWD.getKey());
    c5 = confs.get(ConfigKey.SMTP_SERVER_SECURITY.getKey());
    c6 = confs.get(ConfigKey.SMTP_SERVER_AUTH.getKey());
    c7 = confs.get(ConfigKey.SMTP_SERVER_SENDER.getKey());
    if (c7 == null) c7 = confs.get(ConfigKey.SMTP_SERVER_USER.getKey());
    
    server = new JTextField(20);
    port = new JTextField(4);
    user = new JTextField(32);
    sender = new JTextField(32);
    password = new JPasswordField(16);
    password.setEchoChar('*');
    authentification = new JCheckBox();
    security = new JComboBox<>(protocols);
      
      content = new GemPanel(new BorderLayout());

      JPanel panel = new JPanel(new GridBagLayout());
      GridBagHelper gb = new GridBagHelper(panel);

      server.setText(c1.getValue());
      port.setText(c2.getValue());
      user.setText(c3.getValue());
      password.setText(c4.getValue());
      security.setSelectedItem(c5.getValue());
      authentification.setSelected(c6.getValue().equals("true") ? true : false);
      sender.setText(c7.getValue());
      
      gb.add(new GemLabel(BundleUtil.getLabel("ConfEditor.smtp.server.name.label")), 0, 0, 1, 1, GridBagHelper.WEST);
      gb.add(server, 1, 0, 1, 1, GridBagHelper.WEST);
      gb.add(new GemLabel(BundleUtil.getLabel("ConfEditor.smtp.server.port.label")), 0, 1, 1, 1, GridBagHelper.WEST);
      gb.add(port, 1, 1, 1, 1, GridBagHelper.WEST);
      gb.add(new GemLabel(BundleUtil.getLabel("ConfEditor.smtp.server.security.label")), 0, 2, 1, 1, GridBagHelper.WEST);
      gb.add(security, 1, 2, 1, 1, GridBagHelper.WEST);
      gb.add(new GemLabel(BundleUtil.getLabel("ConfEditor.smtp.server.authentification.label")), 0, 3, 1, 1, GridBagHelper.WEST);
      gb.add(authentification, 1, 3, 1, 1, GridBagHelper.WEST);
      gb.add(new GemLabel(BundleUtil.getLabel("ConfEditor.smtp.server.user.label")), 0, 4, 1, 1, GridBagHelper.WEST);
      gb.add(user, 1, 4, 1, 1, GridBagHelper.WEST);
      gb.add(new GemLabel(BundleUtil.getLabel("ConfEditor.smtp.server.password.label")), 0, 5, 1, 1, GridBagHelper.WEST);
      gb.add(password, 1, 5, 1, 1, GridBagHelper.WEST);
      gb.add(new GemLabel(BundleUtil.getLabel("ConfEditor.smtp.server.sender.label")), 0, 6, 1, 1, GridBagHelper.WEST);
      gb.add(sender, 1, 6, 1, 1, GridBagHelper.WEST);

      content.add(panel, BorderLayout.WEST);

      add(content);
  }

  @Override
  public List<Config> get() {
    List<Config> conf = new ArrayList<Config>();
    c1.setValue(server.getText());
    c2.setValue(port.getText());
    c3.setValue(user.getText());
    c4.setValue(new String(password.getPassword()));
    c5.setValue((String)security.getSelectedItem());
    c6.setValue(authentification.isSelected() ? "true" : "false");
    c7.setValue(sender.getText());

    conf.add(c1);
    conf.add(c2);
    conf.add(c3);
    conf.add(c4);
    conf.add(c5);
    conf.add(c6);
    conf.add(c7);

    return conf;
  }


}
