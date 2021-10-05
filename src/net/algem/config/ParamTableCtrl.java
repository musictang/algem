/*
 * @(#)ParamTableCtrl  2.14.0 08/06/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes All Rights Reserved.
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
package net.algem.config;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Enumeration;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 * Abstract controller for parameter persistence.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 1.0a 21/08/2009
 */
public abstract class ParamTableCtrl
        implements ActionListener
{

  static int READING_MODE = 0;
  static int CREATION_MODE = 1;
  static int MODIFICATION_MODE = 2;
  protected GemDesktop desktop;
  protected DataConnection dc;
  protected String title;
  protected ParamTableView table;
  protected ParamView mask;
  private boolean editKey;
  protected GemPanel contentPane;
  protected GemPanel wCard;
  protected int mode;
  protected int minId;
  protected Param current;

  public ParamTableCtrl() {
  }

  /**
   *
   * @param desktop
   * @param title
   * @param editKey editable key
   * @param activable
   * @param minId
   */
  public ParamTableCtrl(GemDesktop desktop , String title, boolean editKey, boolean activable, int minId) {
    this.desktop = desktop;
    this.dc = DataCache.getDataConnection();
    this.title = title;
    this.editKey = editKey;
    this.minId = minId;
    setView(activable);
    initView();
  }

  /**
   *
   * @param desktop
   * @param title
   * @param editKey editable key
   * @param activable
   */
  public ParamTableCtrl(GemDesktop desktop , String title, boolean editKey, boolean activable) {
    this(desktop, title, editKey, activable, 0);
  }


  public ParamTableCtrl(GemDesktop desktop, String title, boolean editKey) {
    this(desktop, title, editKey, false, 0);
  }

  public ParamTableCtrl(GemDesktop desktop, String title, boolean editKey, int minId) {
    this(desktop, title, editKey, false, minId);
  }

  protected void setView(boolean activable) {
    if (activable) {
      table = new ParamTableView(title, new ActivableParamTableModel());
      mask = new ParamView(true);
    } else {
      table = new ParamTableView(title, new ParamTableModel());
      mask = new ParamView(false);
    }
    table.setColumnModel();
  }

  private void initView() {
    table.addActionListener(this);
    table.addMouseListener(new ParamSelectionListener(minId));
    mask.addActionListener(this);
    mask.setKeyEditable(editKey);

    contentPane = new GemPanel();
    contentPane.setPreferredSize(new Dimension(300,500));
    wCard = new GemPanel();
    wCard.setLayout(new CardLayout());

    wCard.add("liste", table);
    wCard.add("masque", mask);
    ((CardLayout) wCard.getLayout()).show(wCard, "liste");

    contentPane.setLayout(new BorderLayout());
    contentPane.add(wCard, BorderLayout.CENTER);
  }

  /**
   * Specifies if the key may be changed.
   * The default value is FALSE.
   * @return true if key may be changed in #MODIFICATION mode
   */
  protected boolean isKeyModif() {
    return false;
  }

  public void load(Enumeration<? extends Param> list) {
    while (list.hasMoreElements()) {
      Param p = list.nextElement();
      table.addRow(p);
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {

    String cmd = evt.getActionCommand();
    if (cmd.equals(GemCommand.SAVE_CMD)) {
      if (mode == CREATION_MODE) {
        try {
          Param p = mask.get();
          insertion(p);
          table.addRow(p);
        } catch (SQLException e) {
          GemLogger.logException("creation " + title, e, contentPane);
        } catch (ParamException pe) {
          MessagePopup.warning(contentPane, pe.getMessage());
        }
      } else if (mode == MODIFICATION_MODE) {
        try {
          Param p = mask.get();
          modification(current, p);
          table.modRow(p);
        } catch (SQLException e) {
          GemLogger.logException("modification " + title, e, contentPane);
        } catch (ParamException pe) {
          MessagePopup.warning(contentPane, pe.getMessage());
        }
      }
      mode = READING_MODE;
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
    } else if (cmd.equals(GemCommand.CANCEL_CMD)) {
      mask.clear();
      mode = READING_MODE;
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
    } else if (cmd.equals(GemCommand.ADD_CMD)) {
      mask.clear();
      mode = CREATION_MODE;
      mask.setKeyEditable(editKey);
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
    } else if (cmd.equals(GemCommand.DELETE_CMD)) {
      try {
        suppression(current);
        table.deleteCurrent();
      } catch (SQLException e) {
        GemLogger.logException("suppression " + title, e, contentPane);
      } catch (Exception ex) {
        if (ex.getMessage() != null) {
          MessagePopup.warning(contentPane, ex.getMessage());
        }
      }

      mode = READING_MODE;
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
    } else if (cmd.equals(GemCommand.CLOSE_CMD)) {
      desktop.removeCurrentModule();
    }
  }
  
  public GemPanel getContentPane() {
      return contentPane;
  }

  @Override
  public String toString() {
    return getClass().getName();
  }

  public abstract void load();

  /**
   *
   * @param current actual
   * @param p updated
   * @throws SQLException
   * @throws ParamException
   */
  public abstract void modification(Param current, Param p) throws SQLException, ParamException;

  public abstract void insertion(Param p) throws SQLException, ParamException;

  public abstract void suppression(Param p) throws Exception;

  public class ParamSelectionListener extends MouseAdapter {

    /**
     * Id from which a modification is possible.
     * Id is exclusive.
     */
    private final int minId;

    public ParamSelectionListener(int minId) {
      this.minId = minId;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int n = table.getSelectedRow();
        current = table.getItem(n);
        if (current.getId() > minId) {
          mask.set(current);
          mask.setKeyEditable(isKeyModif());
          ((CardLayout) wCard.getLayout()).show(wCard, "masque");
          mode = MODIFICATION_MODE;
        }
      }
  }
}
