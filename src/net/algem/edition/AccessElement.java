/*
 * @(#)AccessElement.java 2.6.a 17/09/12
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
 */

package net.algem.edition;

import java.awt.Graphics;

/**
 * Acces and rights info element for member card.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class AccessElement extends DrawableElement {

	private String[] text = new String[3];

	public AccessElement(int x, int y) {
		super(x, y);
		text[0] = "Les informations contenues sur la présente carte d'adhérent sont obligatoires. Elles pourront donner lieu à l'exercice du droit individuel";
		text[1] = "d'accès et de rectification auprès de nos services dans les conditions prévues par la commission informatique et liberté (art. 27 de la loi";
		text[2] = "n°78-17 du 06/01/78).";
	}

	@Override
	protected void draw(Graphics g) {
		g.setFont(SANS_XSMALL);
		for(int i = 0, yOffset = 0; i < text.length ; i++, yOffset += 8) {
			g.drawString(text[i], x, y + yOffset);
		}

	}
}
