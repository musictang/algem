/*
 * @(#)CodePostalCtrl.java	2.6.a 01/08/2012
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
package net.algem.contact;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import net.algem.util.DataConnection;
import net.algem.util.ui.GemField;

/**
 * Search controller for a city from its postal code.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class CodePostalCtrl
	implements ActionListener, FocusListener {

	private DataConnection dc;
	private GemField ville;

	public CodePostalCtrl(DataConnection dc) {
		this.dc = dc;
	}

	public void setVille(GemField _ville) {
		ville = _ville;
	}

	@Override
	public Object clone() {
		return new CodePostalCtrl(dc);
	}

	public void chercheVille(GemField cdp) {
		City v = CityIO.findCdp(cdp.getText(), dc);

		if (v != null) {
			ville.setText(v.getCity());
		}
	}

	@Override
	public void focusGained(FocusEvent evt) {
	}

	@Override
	public void focusLost(FocusEvent evt) {
		if (ville != null && ville.getText().length() == 0) {
			chercheVille((GemField) evt.getSource());
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		chercheVille((GemField) evt.getSource());
	}
}
