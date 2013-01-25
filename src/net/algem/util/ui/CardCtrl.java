/*
 * @(#)CardCtrl.java	2.6.a 04/08/12
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
import javax.swing.JPanel;
import net.algem.util.GemCommand;
import net.algem.util.event.GemEventListener;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public abstract class CardCtrl
        extends GemPanel
        implements ActionListener	//,KeyListener
{

  protected GemPanel buttons;
  protected Vector<JPanel> panels;
  protected Vector<String> titles;
  protected int step;
  protected GemLabel title;
  protected GemPanel wCard;
  protected GemButton btCancel;
  protected GemButton btNext;
  protected GemButton btPrev;
  protected GemBar bar;
  protected ActionListener actionListener;
  protected GemLabel noteLabel;

  protected GemEventListener gemListener;

  public CardCtrl() {

    panels = new Vector<JPanel>();
    titles = new Vector<String>();

    title = new GemLabel("", GemLabel.CENTER);
    title.setFont(new Font("Helvetica", Font.PLAIN, 12));

    wCard = new GemPanel();
    wCard.setLayout(new CardLayout());

    setLayout(new BorderLayout());

    buttons = new GemPanel();
    buttons.setLayout(new GridLayout(1, 3));
    buttons.add(btPrev = new GemButton(GemCommand.BACK_CMD));
    buttons.add(btCancel = new GemButton(GemCommand.CANCEL_CMD));
    buttons.add(btNext = new GemButton(GemCommand.NEXT_CMD));
    btPrev.addActionListener(this);
    btCancel.addActionListener(this);
    btNext.addActionListener(this);

    GemPanel mid = new GemPanel(new BorderLayout());
    mid.add(title, BorderLayout.NORTH);
    mid.add(wCard, BorderLayout.CENTER);

    add(mid, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);

  }

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  /*

  public void addIco(Image img, boolean stick, String label)

  {
  bar.addIcon(img,label);
  }
   */
  public abstract boolean next();

  public abstract boolean cancel();

  public abstract boolean prev();

  public abstract boolean validation();

  public abstract boolean loadId(int id);

  public abstract boolean loadCard(Object p);

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

    String t = titles.elementAt(no);
    title.setText(t);

    step = no;

    if (step == 0 && !btPrev.getText().equals(GemCommand.DELETE_CMD)) {
      btPrev.setEnabled(false);
    } else {
      btPrev.setEnabled(true);
    }
    if (step == panels.size() - 1) {
      btNext.setText(GemCommand.VALIDATE_CMD);// dernière étape
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
    } /*
    else if (cmd.equals("CtrlAbandon"))
    {
    if (actionListener != null)
    actionListener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,GemCommand.CANCEL_CMD));
    }
     */
    else if (cmd.equals(GemCommand.BACK_CMD)) {
      if (step > 0) {
        prev();
      }
    } else if (cmd.equals(GemCommand.NEXT_CMD)) {
        if (step < panels.size() - 1) {
          next();
        }
    } else if (cmd.equals(GemCommand.VALIDATE_CMD)) {
        validation();
    } else if (step == 0 && cmd.equals(GemCommand.DELETE_CMD)) {
        prev();
    }

  }

  public void addNote(GemLabel noteLabel) {
    if (bar != null) {
      bar.add(noteLabel, RIGHT_ALIGNMENT);
    }
  }

  public void addGemEventListener(GemEventListener l) {
    gemListener = l;
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}

