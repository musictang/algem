/*
 * @(#)PostitModule.java	2.15.11 09/10/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util.postit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JInternalFrame;
import net.algem.planning.DateFr;
import net.algem.security.UserService;
import net.algem.util.GemLogger;
import net.algem.util.module.DefaultGemView;
import net.algem.util.module.GemModule;
import static net.algem.planning.day.DayScheduleCtrl.DAY_SCHEDULE_WINDOW_HEIGHT;

/**
 * Internal frame used to display postits.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.11
 */
public class PostitModule
        extends GemModule
        implements VetoableChangeListener
{

  public static final int POSTIT_MODULE_WIDTH = 110;
  static protected Dimension POSTIT_SIZE = new Dimension(POSTIT_MODULE_WIDTH, DAY_SCHEDULE_WINDOW_HEIGHT);
  protected int lastRead;
  protected PostitCanvas postitCanvas;
  protected UserService service;

  public PostitModule(String label) {
    super(label);
    postitCanvas = new PostitCanvas();
  }

  public PostitModule(UserService service) {
    super("Postit");
    this.service = service;
  }

  @Override
  public void init() {
    view = new DefaultGemView(desktop, "Postit");

    postitCanvas = new PostitCanvas();
    postitCanvas.addActionListener(this);
    lastRead = 0;
    view.add(postitCanvas, BorderLayout.CENTER);
    view.setMaximizable(false);
    view.setClosable(false);
    view.setIconifiable(false);
    view.addVetoableChangeListener(this);
    view.setSize(POSTIT_SIZE);
  }

  @Override
  public String getSID() {
    return String.valueOf(lastRead);
  }

  /**
   * Loads current postits.
   * A receiver == 0 implies a public status.
   * Private postits are seen by current user only.
   */
  public void loadPostits() {

      int userId = dataCache.getUser().getId();

    List<Postit> v = service.getPostits(userId, lastRead);
    Iterator<Postit> enu = v.iterator();
    while (enu.hasNext()) {
      Postit p = enu.next();

      DateFr toDay = new DateFr(new java.util.Date());
      if (toDay.after(p.getTerm())) {
        try {
          service.delete(p);
        } catch (SQLException ex) {
          GemLogger.logException("Erreur suppression postit", ex);
        }
        continue;
      }
      postitCanvas.add(p);
    }
  }

  public void loadPostits(List<Postit> postits) {
    Iterator<Postit> enu = postits.iterator();
    while (enu.hasNext()) {
      Postit p = enu.next();

      DateFr toDay = new DateFr(new java.util.Date());
      if (toDay.after(p.getTerm())) {
        try {
          service.delete(p);
        } catch (SQLException ex) {
          GemLogger.logException("Erreur suppression postit", ex);
        }
        continue;
      }
      postitCanvas.add(p);
    }
  }

  List<Postit> getPostitsFromCanvas() {
    List<Postit> postitComponents = new ArrayList<>();
    List<PostitPosition> positions = postitCanvas.getPositions();
    for (PostitPosition p : positions) {
        postitComponents.add(p.getPostit());
    }
    return postitComponents;
  }

  @Override
  public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
    String name = evt.getPropertyName();
    if (name.equals(JInternalFrame.IS_CLOSED_PROPERTY)
            || name.equals(JInternalFrame.IS_ICON_PROPERTY)) {
      throw new PropertyVetoException("pas d'accord", evt);
    }
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() instanceof PostitPosition) {
      PostitDlg dlg = new PostitDlg(desktop.getFrame());
      PostitPosition pp = (PostitPosition) evt.getSource();
      dlg.setPost(pp.getPostit());
      dlg.entry();
      if (dlg.isSuppression()) {
        try {
          Postit p = pp.getPostit();
          if (Postit.BOOKING != p.getType()) {
            service.delete(p);
          }
          postitCanvas.remove(pp);
        } catch (SQLException e) {
          GemLogger.logException("suppression postit", e, desktop.getFrame());
        }
      } else if (dlg.isModif()) {
        try {
          Postit p = pp.getPostit();
          if (Postit.BOOKING != p.getType()) {
            Postit mp = dlg.get();
            p.setType(mp.getType());
            p.setTerm(mp.getTerm());
            p.setText(mp.getText());
            service.update(p);
          }
        } catch (SQLException ex) {
          GemLogger.logException("update postit", ex);
        }
      }
      postitCanvas.repaint();
    }

  }

  public void addPostit(Postit p) {
    postitCanvas.add(p);
    lastRead = p.getId();
    GemLogger.log("PostitModule : last postit read = " + lastRead);
  }

  public void clear() {
    lastRead = 0;
    postitCanvas.clear();
  }

}
