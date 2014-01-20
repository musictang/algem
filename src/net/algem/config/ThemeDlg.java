/*
 * @(#)ThemeDlg.java	2.8.r 17/01/14
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
package net.algem.config;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.r
 * @since 2.8.r 17/01/14
 */
public class ThemeDlg
	extends JDialog
	implements ActionListener {

	private JComboBox select;
	private GemButton btCancel;
	private GemButton btValidation;
	private static final String METAL = "javax.swing.plaf.metal.MetalLookAndFeel";
	private static final String PLASTIC = "com.jgoodies.looks.plastic.Plastic3DLookAndFeel";
	private static final String WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

	public ThemeDlg(Frame owner, boolean modal) {
		super(owner, modal);
		select = new JComboBox(new String[]{"Metal", "Plastic 3D", "Windows"});
		GemPanel buttons = new GemPanel(new GridLayout(1, 2));
		btCancel = new GemButton(GemCommand.CANCEL_CMD);
		btCancel.addActionListener(this);
		btValidation = new GemButton(GemCommand.VALIDATION_CMD);
		btValidation.addActionListener(this);
		buttons.add(btValidation);
		buttons.add(btCancel);

		setLayout(new BorderLayout());
		add(new GemLabel("Thème"), BorderLayout.WEST);
		add(select, BorderLayout.EAST);
		add(buttons, BorderLayout.SOUTH);
		setSize(GemModule.XS_SIZE);
		setLocationRelativeTo(owner);
//		pack();
		setVisible(true);
	}

	public ThemeDlg(Frame owner) {
		super(owner);
	}

	public ThemeDlg() {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == btValidation) {
			switch (select.getSelectedIndex()) {
				case 0:
					setTheme(METAL);
					break;
				case 1:
					setTheme(PLASTIC);
					break;
				case 2:
					setTheme(WINDOWS);
					break;
				default:
					break;
			}

		} else {
			setVisible(false);
		}
	}

	private void setTheme(String theme) {
		try {
			UIManager.setLookAndFeel(theme);
			SwingUtilities.updateComponentTreeUI(getOwner());
		} catch (Exception ex) {
			GemLogger.logException(ex);
		}
	}
}
