/*
 * @(#)CalendarDlg.java	2.6.a 19/09/12
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
package net.algem.planning;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JDialog;
import net.algem.util.GemCommand;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.PopupDlg;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class CalendarDlg
        implements ActionListener
{

  private Frame parent;
  private JDialog dlg;
  private GemLabel title;
  private boolean validation;
  private GemButton btCancel;
  private GemButton btValidate;
  private CalendarView calView;


  public CalendarDlg(Component c, String t) 
  {
    parent = PopupDlg.getTopFrame(c);
    title = new GemLabel(t);
    validation = false;

    calView = new CalendarView();
    calView.addActionListener(this);

    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);
    btValidate = new GemButton(GemCommand.VALIDATE_CMD);
    btValidate.addActionListener(this);

    GemPanel buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 2));
    buttons.add(btCancel);
    buttons.add(btValidate);

    dlg = new JDialog(parent, true);

    dlg.getContentPane().add("North", title);
    dlg.getContentPane().add("Center", calView);
    dlg.getContentPane().add("South", buttons);
    dlg.pack();

    Point p = c.getLocation();
    dlg.setLocation(p);
  }


  public void setDate(Date d) 
  {
    calView.setDate(d);
  }


  public void saisie() 
  {
    dlg.setVisible(true);
  }


  public boolean isValidate() 
  {
    return validation;
  }


  public Date getDate() 
  {
    return calView.getDate();
  }


  @Override
  public void actionPerformed(ActionEvent evt) 
  {
    if (evt.getActionCommand().equals("click")) {
      validation = true;
    } else if (evt.getActionCommand().equals(GemCommand.VALIDATE_CMD)) {
      validation = true;
    } else if (evt.getActionCommand().equals("date")) {
      return;
    } else {
      validation = false;
    }
    dlg.setVisible(false);
    //dlg.dispose();
  }
}
