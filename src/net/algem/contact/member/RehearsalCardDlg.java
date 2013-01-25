/*
 * @(#)RehearsalCardDlg.java 2.6.a 18/09/12
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
package net.algem.contact.member;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Vector;
import javax.swing.JComboBox;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.PopupDlg;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class RehearsalCardDlg
	extends PopupDlg {

	private RehearsalCard card;
	private JComboBox choice;
	private GemBorderPanel background;

	public RehearsalCardDlg() {
	}

	public RehearsalCardDlg(Component c, Vector<RehearsalCard> vc) {
		super(c, "Choix abonnement", true);
		background = new GemBorderPanel();
		choice = new JComboBox(vc);

		background.setLayout(new BorderLayout());

		background.add(new GemLabel("Choix de la carte d'abonnement"), BorderLayout.NORTH);
		background.add(choice, BorderLayout.CENTER);

		init();

	}

	public RehearsalCard get() {
		if (choice != null) {
			RehearsalCard c = (RehearsalCard) choice.getSelectedItem();
			//System.out.println("carte name "+c.getLibelle());
			return (RehearsalCard) choice.getSelectedItem();
		}
		return card;
	}

	public void set(RehearsalCard c) {
		choice.setSelectedItem(c);
	}

	public void setCard(RehearsalCard c) {
		this.card = c;
	}

	@Override
	public GemPanel getMask() {
		return background;
	}
}
