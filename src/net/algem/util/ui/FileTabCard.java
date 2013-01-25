/*
 * @(#)CardCtrl.java	2.6.a 31/07/12
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
package net.algem.util.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public abstract class FileTabCard
        extends FileTab
        implements ActionListener	//,KeyListener
{

  protected GemPanel buttons;
  protected Vector<GemPanel> panels;
  protected Vector<String> titles;
  protected int step;
  protected GemLabel title;
  protected GemPanel wCard;
  protected GemButton btCancel;
  protected GemButton btNext;
  protected GemButton btBack;
  protected GemBar bar;
  protected ActionListener actionListener;

  public FileTabCard(GemDesktop _desktop) {
    super(_desktop);

    panels = new Vector<GemPanel>();
    titles = new Vector<String>();

    wCard = new GemPanel();
    wCard.setLayout(new CardLayout());

    bar = new GemBar();

    buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));
    buttons.add(btBack = new GemButton(GemCommand.BACK_CMD));
    buttons.add(btCancel = new GemButton(GemCommand.CANCEL_CMD));
    buttons.add(btNext = new GemButton(GemCommand.NEXT_CMD));
    btBack.addActionListener(this);
    btCancel.addActionListener(this);
    btNext.addActionListener(this);

    title = new GemLabel("", GemLabel.CENTER);
    title.setFont(new Font("Helvetica", Font.PLAIN, 14));
    /**/
    Insets in = new Insets(0, 0, 0, 0);
    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.add(bar, 0, 0, 1, 1, in, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    //gb.add(bar,0,0,1,1,gb.WEST);
    gb.add(title, 0, 1, 1, 1, in, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    gb.add(wCard, 0, 2, 1, 1, in, GridBagHelper.BOTH, 1.0, 1.0);
    gb.add(buttons, 0, 3, 1, 1, in, GridBagHelper.HORIZONTAL, 1.0, 0.0);
    /**/
  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  public abstract boolean next();

  public abstract boolean back();

  public abstract void cancel();

  public abstract void validation();

  public void addCard(String t, GemPanel p) {
    wCard.add("etape" + panels.size(), p);
    panels.addElement(p);
    titles.addElement(t);
  }

  public void select(int no) {
    if (no < 0 || no >= panels.size()) {
      return;
    }

    ((CardLayout) wCard.getLayout()).show(wCard, "etape" + no);

    String t = (String) titles.elementAt(no);
    title.setText(t);

    step = no;
    if (step == 0) {
      btBack.setEnabled(false);
    } else {
      btBack.setEnabled(true);
    }
    if (step == panels.size() - 1) {
      btNext.setText(GemCommand.VALIDATE_CMD);
    } else {
      btNext.setText(GemCommand.NEXT_CMD);
    }
    //((GemPanel)panels.elementAt(no)).requestFocus();
  }

	@Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    if (cmd.equals(GemCommand.CANCEL_CMD)) {
      cancel();
    } else if (cmd.equals(GemCommand.BACK_CMD)) {
      if (step > 0) {
        back();
      }
    } else if (cmd.equals(GemCommand.NEXT_CMD)) {
      if (step < panels.size() - 1) {
        next();
      }
    } else if (cmd.equals(GemCommand.VALIDATE_CMD)) {
      validation();
    }
  }
}

