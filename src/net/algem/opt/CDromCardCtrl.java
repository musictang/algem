/*
 * @(#)CDromCardCtrl.java	2.8.w 08/07/14
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
package net.algem.opt;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.ui.CardCtrl;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @deprecated 
 */
public class CDromCardCtrl
	extends CardCtrl {

	DataCache cache;
	CDromView cv;
	CDromListView pv;
	CDrom cdrom;

	public CDromCardCtrl(DataCache _dc) {
		super();
		cache = _dc;

		cv = new CDromView();
		pv = new CDromListView(cache);

		addCard("CDrom", cv);
		addCard("titres", pv);
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
		try {
			CDromIO.update(cdrom, DataCache.getDataConnection());
		} catch (SQLException e1) {
			GemLogger.logException("Update CDrom", e1, this);
			return false;
		}
		if (actionListener != null) {
			actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
		}
		return true;
	}

	public void clear() {
		cv.clear();
		pv.clear();
	}

	@Override
	public boolean loadCard(Object o) {
		clear();
		if (o == null || !(o instanceof CDrom)) {
			return false;
		}

		cdrom = (CDrom) o;
		try {
			cv.set(cdrom);
			pv.load(cdrom.getId());

			select(0);
		} catch (Exception e) {
			GemLogger.logException("lecture ficher CDrom", e, this);
			return false;
		}
		return true;
	}

	@Override
	public boolean loadId(int id) {
		return loadCard(CDromIO.findId(id, DataCache.getDataConnection()));
	}

	CDrom get() {
		return cv.get();
	}
}
