/*
 * @(#)GemButton.java	2.6.a 31/07/12
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
package net.algem.util.ui;

import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Generic button.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class GemButton
	extends JButton {

	Insets insets;

	public GemButton(String s) {
		super(s);
	}

	public GemButton(ImageIcon icon) {
		super(icon);
		insets = new Insets(0, 1, 0, 1);
		if (getIcon() == null) {
			setText("OK");
		}
	}

	@Override
	public Insets getInsets() {
		return insets != null ? insets : super.getInsets();
	}
}
