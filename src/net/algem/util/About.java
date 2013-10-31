/*
 * @(#)About.java 2.8.p 25/10/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import net.algem.security.User;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.*;

/**
 * About dialog.
 * Shows some system environment variables and other informations.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.p 25/10/13 19:46
 * @since 2.8.p 25/10/13 19:46
 */
public class About
        extends JDialog
        implements ActionListener
{

  private GemButton okBt;

  public About(GemDesktop desktop, String title) {
    super(desktop.getFrame(), title);
    Properties sysProps = System.getProperties();

    okBt = new GemButton(GemCommand.OK_CMD);
    okBt.addActionListener(this);
    setLayout(new BorderLayout());

    GemPanel infos = new GemPanel(new GridBagLayout());
    infos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    GridBagHelper gb = new GridBagHelper(infos);
		gb.insets = GridBagHelper.SMALL_INSETS;

    gb.add(new GemLabel("Système"), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel("Java version"), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel("Java home"), 0, 2, 1, 1, GridBagHelper.WEST);
		gb.add(new GemLabel("Utilisateur système"), 0, 3, 1, 1, GridBagHelper.WEST);
		gb.add(new GemLabel("Login"), 0, 4, 1, 1, GridBagHelper.WEST);
    /*gb.add(new GemLabel("Mémoire max JVM"), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel("Mémoire total JVM"), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel("Mémoire libre JVM"), 0, 5, 1, 1, GridBagHelper.WEST);*/
    String sysInfo = sysProps.getProperty("os.name")
            + " " + sysProps.getProperty("os.version")
            + " " + sysProps.getProperty("os.arch");
		GemField sys = new GemField(sysInfo,25);
		sys.setEditable(false);
		GemField jVersion = new GemField(sysProps.getProperty("java.version"),25);
		jVersion.setEditable(false);
		GemField jHome = new GemField(sysProps.getProperty("java.home"),25);
		jHome.setEditable(false);
		GemField userName = new GemField(sysProps.getProperty("user.name"),10);
		userName.setEditable(false);
		User u = desktop.getDataCache().getUser();
		GemField currentUser = new GemField(u.getLogin() + " ("+u.getFirstnameName()+")",25);
		currentUser.setEditable(false);
    gb.add(sys, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(jVersion, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(jHome, 1, 2, 1, 1, GridBagHelper.WEST);
		gb.add(userName, 1, 3, 1, 1, GridBagHelper.WEST);
		gb.add(currentUser, 1, 4, 1, 1, GridBagHelper.WEST);
    /*Runtime runtime = Runtime.getRuntime();
    double maxMem = Math.round((double) (runtime.maxMemory() / 1024) / 1024);
    double totalMem = Math.round((double) (runtime.totalMemory() / 1024) / 1024);
    double freeMem = Math.round((double) (runtime.freeMemory() / 1024) / 1024);
    gb.add(new GemField(String.valueOf(maxMem) + " MB"), 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemField(String.valueOf(totalMem) + " MB"), 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemField(String.valueOf(freeMem) + " MB"), 1, 5, 1, 1, GridBagHelper.WEST);*/


    add(infos, BorderLayout.CENTER);
    add(okBt, BorderLayout.SOUTH);

    pack();
		double w = getSize().getWidth();
		setLocation(desktop.getFrame().getWidth() - (int)w,100);
    setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == okBt) {
      okBt.removeActionListener(this);
      setVisible(false);
      dispose();
    }
  }

}
