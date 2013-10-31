/*
 * @(#)AuthDlg.java	2.8.p 30/10/13
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


package net.algem.security;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPasswordField;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.ui.*;

/**
 * Authentication dialog.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.p
 * @since 2.8.p 30/10/13
 */
public class AuthDlg 
	extends JDialog
	implements ActionListener
{

	private JPasswordField pass;
	private GemField login;
	private GemButton btValidation;
	private GemButton btCancel;
	private boolean validation;

	public AuthDlg(Frame owner) {
		super(owner, true);
		setTitle("Identification");
		setLayout(new BorderLayout());
		
		GemPanel p = new GemPanel(new GridBagLayout());
		p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		login = new GemField(10);
		pass = new JPasswordField(10);
		
		GridBagHelper gb = new GridBagHelper(p);
		gb.insets = GridBagHelper.SMALL_INSETS;
		
		GemPanel buttons = new GemPanel(new GridLayout(1,2));
		gb.add(new GemLabel(BundleUtil.getLabel("Login.label")), 0, 0, 1, 1, GridBagHelper.WEST);
		gb.add(new GemLabel(BundleUtil.getLabel("Password.label")), 0, 1, 1, 1, GridBagHelper.WEST);
		gb.add(login, 1, 0, 1, 1, GridBagHelper.WEST);
		gb.add(pass, 1, 1, 1, 1, GridBagHelper.WEST);
		
		btValidation = new GemButton(GemCommand.VALIDATE_CMD);
		btValidation.addActionListener(this);
		getRootPane().setDefaultButton(btValidation);// enter
		btCancel = new GemButton(GemCommand.CANCEL_CMD);
		btCancel.addActionListener(this);
		buttons.add(btValidation);
		buttons.add(btCancel);
		add(p, BorderLayout.CENTER);
		add(buttons, BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(owner);

		setVisible(true);
		
	}

	public String getLogin() {
		return login.getText();
	}

	/**
	 * Gets the clear text password.
	 * @return a string
	 */
	public String getPass() {
		char[] p = pass.getPassword();
		return new String(p);
	}
	
	public void close() {
		dispose();
	}
	
	public boolean isValidation() {
		return validation;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == btValidation) {
			validation = true;
		} else {
			validation = false;
		}
		setVisible(false);
	}
	
	
}
