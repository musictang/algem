/*
 * @(#)RehearsalCancelDlg.java	2.6.a 21/09/12
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
package net.algem.planning.editing;

import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import net.algem.planning.ConflictView;
import net.algem.planning.DateFr;
import net.algem.planning.ScheduleObject;
import net.algem.util.ui.GemPanel;

/**
 * comment
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class RehearsalCancelDlg
        extends ModifPlanDlg
        implements ActionListener
{

  private GemPanel wCard;
  private String currentCard;
  private ConflictView cv;
  private boolean updateDone;
  private RehearsalCancelView pv;

  public RehearsalCancelDlg(Frame f, ScheduleObject plan) {
    super(f);
    validation = false;
    dlg = new JDialog(parent, true);

    wCard = new GemPanel();
    wCard.setLayout(new CardLayout());

    cv = new ConflictView();

    wCard.add("conflit", cv);
    currentCard = "conflit";

    pv = new RehearsalCancelView(plan);

    wCard.add("valeur", pv);
    selectCard("valeur");

    addContent(wCard, "Schedule.rehearsal.cancellation.title");
  }

  public boolean isUpdateDone() {
    return updateDone;
  }

  public void selectCard(String name) {
    currentCard = name;
    ((CardLayout) wCard.getLayout()).show(wCard, name);
  }

  public void entry(Point p) {
    dlg.setLocation(p);
    dlg.setVisible(true);
  }

  @Override
  public void show() {
    dlg.setVisible(true);
  }

  public DateFr getDateStart() {
    return pv.getStart();
  }

  public DateFr getDateEnd() {
    return pv.getEnd();
  }

  @Override
  public boolean isEntryValid() {
    return true;
  }

  @Override
  public boolean isValidate() {
    return validation;
  }

}
