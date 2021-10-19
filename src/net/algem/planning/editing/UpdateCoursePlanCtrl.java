/*
 * @(#)UpdateCoursePlanCtrl.java	2.12.0 01/03/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning.editing;

import javax.swing.JOptionPane;
import net.algem.planning.*;
import net.algem.util.BundleUtil;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.MessagePopup;

/**
 * comment
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 * @deprecated
 */
public class UpdateCoursePlanCtrl
	extends CourseScheduleCtrl {

	private ScheduleObject plan;

	public UpdateCoursePlanCtrl(GemDesktop _desktop, ScheduleObject plan) {
		super(_desktop);
		this.plan = plan;
	}

	@Override
	public void init() {
		av = new UpdateActionView(desktop, plan);
        ((UpdateActionView)av).init(plan);
		conflictsView = new ConflictListView(null);

		addCard("Initialisation des sessions", av);
		addCard("Vérification conflit", conflictsView);
		select(0);
	}

	@Override
	protected boolean save() {
		Action action = actions.get(0);
		if (action != null) {
                    if (action.getDates().isEmpty()) {
			JOptionPane.showMessageDialog(contentPane,
				MessageUtil.getMessage("empty.planning.create.warning"),
				BundleUtil.getLabel("Warning.label"),
				JOptionPane.ERROR_MESSAGE);
			return false;
                    }

		try {
			action.setId(plan.getIdAction());// on récupère le numéro d'action déjà planifiée
			service.replanify(action, plan);
			desktop.postEvent(new ModifPlanEvent(this, action.getStartDate(), action.getEndDate()));
			cancel();
			return true;
		} catch (PlanningException | NullPointerException ex) {
			MessagePopup.warning(contentPane,
				MessageUtil.getMessage("planning.course.create.exception") + " :\n" + ex.getMessage());
			return false;
		}
                } else {
                    return false;
                }
	}

	@Override
	public void clear() {
		av.clear();
		conflictsView.clear();
		select(0);
	}
}
