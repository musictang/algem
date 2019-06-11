/*
 * @(#)AtelierInstrumentsController.java 2.9.2 02/02/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning.editing.instruments;

import net.algem.planning.Action;
import net.algem.planning.ReloadDetailEvent;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;

import java.util.List;

/**
 * @author <a href="mailto:alexandre.delattre.biz@gmail.com">Alexd</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 * @since 2.9.2
 */
public class AtelierInstrumentsController implements AtelierInstrumentsDialog.Callback {
    private final GemDesktop desktop;
    private final Action courseAction;
    private final AtelierInstrumentsService service;

    public AtelierInstrumentsController(GemDesktop desktop, Action courseAction) {
        this.desktop = desktop;
        this.courseAction = courseAction;
        this.service = desktop.getDataCache().getAtelierInstrumentsService();
    }

    public void run() {
        AtelierInstrumentsDialog dialog = new AtelierInstrumentsDialog(service, this);
        dialog.setLocationRelativeTo(desktop.getFrame());
        dialog.pack();
        try {
            dialog.setData(service.getInstrumentsAllocation(courseAction));
        } catch (Exception e) {
            GemLogger.logException(e);
        }
        dialog.setVisible(true);
    }

    @Override
    public void onOkSelected(List<AtelierInstrumentsService.PersonInstrumentRow> rows) {
        try {
            service.setInstrumentsAllocation(courseAction, rows);
            desktop.postEvent(new ReloadDetailEvent(this));
        } catch (Exception e) {
            GemLogger.logException(e);
        }
    }
}
