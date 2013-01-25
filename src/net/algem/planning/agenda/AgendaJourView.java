/*
 * @(#)AgendaJourView.java	1.0a 07/07/1999
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
 

package net.algem.planning.agenda;

import	java.awt.BorderLayout;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Date;
import net.algem.planning.PlanningLib;
import net.algem.util.ui.GemPanel;

/**
 * comment
 *
 * @version 1.0a 07/07/1999
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 */

public class AgendaJourView
	extends GemPanel
	implements AdjustmentListener
{
	static int		MAXPLAGE=28;

	CanvasDayAgenda	cv;
	Scrollbar		sb;

	//----------------------------------------------------
	public AgendaJourView(String dg)
	//----------------------------------------------------
	{
		super();

		sb = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, 0);
		sb.addAdjustmentListener(this);

		cv = new CanvasDayAgenda(MAXPLAGE);

		setLayout(new BorderLayout());
		add(dg,sb);
		add("Center",cv);
	}

	//----------------------------------------------------
	public void setDate(Date d)
	//----------------------------------------------------
	{
		cv.setDate(d);
		compscroll();
	}

	//----------------------------------------------------
	public void set(PlanningLib p)
	//----------------------------------------------------
	{
		cv.set(p);
	}

	//----------------------------------------------------
	public CanvasDayAgenda getCanvas()
	//----------------------------------------------------
	{
		return cv;
	}

	//----------------------------------------------------
	public void adjustmentValueChanged(AdjustmentEvent e)
	//----------------------------------------------------
	{
		cv.setTop(e.getValue());
		compscroll();
	}

	//----------------------------------------------------
	private void compscroll()
	//----------------------------------------------------
	{
		int r = cv.getRowCount();
		sb.setValues(cv.getTop(), r==0?1:r, 0, MAXPLAGE);
	}

	//----------------------------------------------------
	public void setBounds(int nx, int ny, int nw, int nh)
	//----------------------------------------------------
	{
/*
		if (nw != d.width || nh != d.height)
		{
			in = getInsets();
			sb.setBounds(width+in.left, in.top, sbwidth, height);
			respace();
*/
		super.setBounds(nx, ny, nw, nh);
	}

}
