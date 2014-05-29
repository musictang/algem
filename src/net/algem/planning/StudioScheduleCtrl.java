/*
 * @(#)StudioScheduleCtrl.java	2.8.v 21/05/14
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

package net.algem.planning;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.GemPanel;

/**
 * This controller is used to planify one or more rooms at differents times and for different technicians.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 * @since 2.8.v 21/05/14
 */
public class StudioScheduleCtrl 
  extends CardCtrl
{

  protected ConflictListView conflictsView;
  private GemDesktop desktop;
  private PlanningService service;
  private Action action;
  private List<GemDateTime> dates;
  private StudioScheduleView studioView;
  
  public StudioScheduleCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    service = new PlanningService(desktop.getDataCache().getDataConnection());
  }
  
  public void init() {
    studioView = new StudioScheduleView(desktop.getDataCache());
    JScrollPane scroll = new JScrollPane(studioView);
    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    GemPanel gp = new GemPanel(new BorderLayout());
    gp.add(scroll, BorderLayout.CENTER);

    conflictsView = new ConflictListView();
    addCard(null, gp);
    addCard(BundleUtil.getLabel("Conflict.verification.label"), conflictsView);
    select(0);
  }
  
  @Override
  public boolean next() {
    select(step + 1);
    if (step == 1) {
      String t = MessageUtil.getMessage("invalid.choice");
//      try {
//        action = checkAction();
//      } catch (PlanningException pe) {
//        JOptionPane.showMessageDialog(this, pe.getMessage(), t, JOptionPane.ERROR_MESSAGE);
//        return prev();
//      }
      conflictsView.clear();
//      int n = testConflicts(action);
//      if (n > 0) {
//        btNext.setText("");//bouton validation
//      }
    }
    return true;
  }

  @Override
  public boolean prev() {
    select(step - 1);
    return true;
  }
  
  @Override
  public boolean cancel() {
    if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
    }
    return true;
  }

  @Override
  public boolean validation() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean loadId(int id) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean loadCard(Object p) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
