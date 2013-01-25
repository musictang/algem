/*
 * @(#)SearchCtrl.java	2.6.a 03/10/12
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;

/**
 * Abstract controller for searching.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.6.a
 * @since 1.0a 07/07/1999
 * 
 */
public abstract class SearchCtrl
        extends GemPanel
        implements ActionListener, MouseListener
{
  public static final String TRANSLATE_FROM = "àâäéèêëîïôöùûüç";
  public static final String TRANSLATE_TO   = "aaaeeeeiioouuuc";
  protected final static String EMPTY_LIST = MessageUtil.getMessage("search.empty.list.status");
  protected Thread thread;
  protected boolean abort;
  protected DataConnection dc;
  protected CardCtrl mask;
  protected ListCtrl list;
  protected SearchView searchView;
  protected GemPanel wCard;
  protected GemLabel title;
  protected ActionListener actionListener;
  

  public SearchCtrl(DataConnection dc, String tit) {

    this.dc = dc;
    title = new GemLabel(tit);
    title.setFont(new Font("Helvetica", Font.PLAIN, 18));

    wCard = new GemPanel();
    wCard.setLayout(new CardLayout());

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    Insets in = new Insets(0, 0, 0, 0);

    gb.add(title, 0, 0, 1, 1, in, GridBagHelper.CENTER);
    gb.add(wCard, 0, 1, 1, 1, in, GridBagHelper.BOTH, 1.0, 1.0);
  }

  public abstract void init();

  public abstract void search();

  public void removeActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  public void addActionListener(ActionListener l) {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  public void setStatus(String message) {
    searchView.setStatus(message);
  }

  public void addIco(Image img, boolean stick, String label) {
    //barre.addIcon(img,label);
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    String cmd = evt.getActionCommand();
    if (GemCommand.SEARCH_CMD.equals(cmd)) {
      setCursor(new Cursor(Cursor.WAIT_CURSOR));
      searchView.setStatus("");
      list.clear();
      search();
      setCursor(Cursor.getDefaultCursor());
    } else if (GemCommand.NEW_SEARCH_CMD.equals(cmd)) {
      abort = true;
      ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
    } else if ("CtrlAbandon".equals(cmd) || "CtrlValider".equals(cmd)) {
      if (list.nbLines() > 0) {
        ((CardLayout) wCard.getLayout()).show(wCard, "liste");
      } else {
        searchView.clear();
        ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
      }
    } else if (GemCommand.CANCEL_CMD.equals(cmd)) {
      if (actionListener != null) {
        actionListener.actionPerformed(evt);
      }
    } else if (GemCommand.ERASE_CMD.equals(cmd)) {
      searchView.clear();
    }
  }

  public void load(int id) {
    ((CardLayout) wCard.getLayout()).show(wCard, "masque");
    mask.loadId(id);
  }
  
  public int getId() {
    try {
    return Integer.parseInt(searchView.getField(0));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  /**
   * Runs when selected line is clicked.
   * Method load() is redefined in subtype classes.
   * @param e
   */
  @Override
  public void mouseClicked(MouseEvent e) {
    int id = list.getSelectedID();//pourquoi
    if (id > 0) {
      load(id);
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}

