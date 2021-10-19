/*
 * @(#)BankCtrl.java	2.6.a 14/09/12
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
package net.algem.bank;

import java.awt.event.ActionEvent;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.ui.CardCtrl;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.6.a
 */
public class BankCtrl
	extends CardCtrl {

	private DataConnection dc;
	private BankView bankView;
	private Bank bank;

	public BankCtrl(DataConnection dc) {

		this.dc = dc;
		bankView = new BankView();

		addCard("fiche banque", bankView);
		select(0);
	}

	@Override
	public boolean next() {
		switch (step) {
			default:
				select(step + 1);
				break;
		}
		return true;
	}

	@Override
	public boolean cancel() {
		if (actionListener != null) {
			actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlAbandon"));
		}
		return true;
	}

	@Override
	public boolean prev() {
		switch (step) {
			default:
				select(step - 1);
				break;
		}
		return true;
	}

	@Override
	public boolean validation() {
		if (bank == null) {
			return false;
		}

		if (bank.equals(getBank())) {
			return true;
		}

		bank = getBank();

		try {
			BankIO.update(bank, dc);

		} catch (Exception e1) {
			GemLogger.logException("update banque", e1, contentPane);
			return false;
		}
		if (actionListener != null) {
			actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
		}
		return true;
	}

	public void clear() {
		bankView.clear();
	}

	@Override
	public boolean loadCard(Object o) {
		clear();
		if (o == null || !(o instanceof Bank)) {
			return false;
		}

		bank = (Bank) o;
		bankView.setBank(bank);

		return true;
	}

	@Override
	public boolean loadId(int id) {
		return loadCard(BankIO.findCode(String.valueOf(id), dc));
	}

	public Bank getBank() {
		return bankView.getBank();
	}
}
