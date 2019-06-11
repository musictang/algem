/*
 * @(#) EstabActivationCtrl.java Algem 2.15.2 27/09/17
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
 */

package net.algem.planning;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.algem.room.EstabTableModel;
import net.algem.room.Establishment;
import net.algem.room.EstablishmentIO;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.JTableModel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.2
 * @since 2.11.0 27/09/2016
 */
public class EstabActivationCtrl
        extends JDialog

{

  private JTable table;
  private JTableModel model;
  private GemDesktop desktop;
  private boolean changed;

  public EstabActivationCtrl(GemDesktop desktop, boolean modal) {
    super(desktop.getFrame(), BundleUtil.getLabel("Menu.establishment.label"), modal);
    this.desktop = desktop;
  }

  public void initUI() {
    model = new EstabTableModel();

    table = new JTable(model);
    table.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        updateStatus();
      }
    });

    load();
    JScrollPane js = new JScrollPane(table);

    GemButton btOk = new GemButton(GemCommand.CLOSE_CMD);
    btOk.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
      }
    });
    setLayout(new BorderLayout());
    add(js,BorderLayout.CENTER);
    add(btOk,BorderLayout.SOUTH);

    setSize(new Dimension(GemModule.XS_SIZE));
    pack();
    setLocationRelativeTo(desktop.getFrame());
    setVisible(true);
  }

  public boolean hasChanged() {
    return changed;
  }

  private void load() {
    try {
      List<Establishment> v = EstablishmentIO.find(" AND e.idper = " + desktop.getDataCache().getUser().getId() + " ORDER BY p.nom", DataCache.getDataConnection());

      for (Establishment e : v) {
        model.addItem(e);
      }
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
    }
  }

  private void updateStatus() {
    Establishment et = (Establishment) model.getItem(table.convertRowIndexToModel(table.getSelectedRow()));
    if (et != null) {
      try {
        EstablishmentIO.updateStatus(et.getId(), et.isActive(), desktop.getDataCache().getUser().getId(), DataCache.getDataConnection());
        if (et.isActive()) {
          desktop.getDataCache().add(et); // do not remote propagation
        } else {
          desktop.getDataCache().remove(et); // do not remote propagation
        }
        changed = true;
      } catch (SQLException ex) {
        changed = false;
        GemLogger.log(ex.getMessage());
      }
    }
  }
}
