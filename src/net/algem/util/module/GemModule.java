/*
 * @(#)GemModule.java	2.12.0 13/03/17
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
 * 
 */

package net.algem.util.module;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.event.GemEvent;
import net.algem.util.event.GemEventListener;
import net.algem.util.model.GemCloseVetoException;
import net.algem.util.ui.GemTreeNode;

/**
 * Generic module.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.12.0
 * @since 1.0a 06/07/2002
 */
public abstract class GemModule
        implements GemEventListener, ActionListener
{

  public static final Dimension DEFAULT_SIZE = new Dimension(650, 450);
  public static final Dimension XXS_SIZE = new Dimension(240, 120);
  public static final Dimension XS_SIZE = new Dimension(320, 240);
  public static final Dimension S_SIZE = new Dimension(420, 400);
  public static final Dimension M_SIZE = new Dimension(750, 450);
  public static final Dimension L_SIZE = new Dimension(800, 500);
  public static final Dimension XL_SIZE = new Dimension(870, 500);
  public static final Dimension XXL_SIZE = new Dimension(905, 540);

  protected String label;
  protected DefaultGemView view;
  protected GemDesktop desktop;
  protected DataCache dataCache;
  protected Container container;

  protected GemTreeNode node;
  protected GemTreeNode treeNode;
  protected boolean removable=true;

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public GemTreeNode getTreeNode() {
        return treeNode;
    }

    public void setTreeNode(GemTreeNode treeNode) {
        this.treeNode = treeNode;
    }

    public GemTreeNode getNode() {
        return node;
    }

    public void setNode(GemTreeNode node) {
        this.node = node;
    }

  public GemModule(String label) {
    this.label = label;
  }

  /**
   * Panel encapsulation.
   * @param label
   * @param p content panel
   */
  public GemModule(String label, Container p) {
    this(label);
    this.container = p;
  }

  /**
   * Default initialization to redefine in subclasses.
   */
  public abstract void init();

  /**
   * GemEventListener to redefine.
   * @param evt
   */
  @Override
  public void postEvent(GemEvent evt) {}

  /**
   * ActionListener to redefine.
   * @param evt
   */
  @Override
  public void actionPerformed(ActionEvent evt) {}

  /**
   * Called by desktop when adding module.
   * Call init() in subtype classes for IHM creation.
   * @param desktop
   * @see net.algem.util.module.GemDesktopCtrl#addModule(net.algem.util.module.GemModule) 
   * 
   */
  public void setDesktop1(GemDesktop desktop) {
    this.desktop = desktop;
    dataCache = desktop.getDataCache();

    init();	// init module+IHM à redéfinir dans les modules

    view.addInternalFrameListener(new InternalFrameAdapter()
    {
      @Override
      public void internalFrameClosing(InternalFrameEvent evt) {
        try {
          close();
        } catch (GemCloseVetoException e) {
        }
      }
    });
  }

  public void setDesktop2(GemDesktop desktop) {
    this.desktop = desktop;
    dataCache = desktop.getDataCache();

    init();	// init module+IHM à redéfinir dans les modules
  }
  
  public GemDesktop getDesktop() {
    return desktop;
  }

  public DefaultGemView getView() {
    return view;
  }

  public String getSID() {
    return null;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return label;
  }

  /**
   * 
   * @throws net.algem.util.model.GemCloseVetoException
   */
  public void close() throws GemCloseVetoException {
    view.close();
    desktop.removeModule(this);
  }

  //FIXME EG 08/2009 dans GemView
  public JMenuItem getMenuItem(String menu) {
    //JMenuItem m = new JMenuItem(dataCache.getLabel(menu+".label"));
    JMenuItem m = dataCache.getMenu2(menu);
    /*m.setMnemonic(dataCache.getLabel(menu+".mnemo").charAt(0));
    m.getAccessibleContext().setAccessibleDescription(dataCache.getLabel(menu+".info"));
    m.setToolTipText(dataCache.getLabel(menu+".bulle"));*/
    //m.setEnabled((dataCache.authorize(menu)));
    m.setActionCommand(menu);
    m.addActionListener(this);

    return m;
  }

  public JMenu createJMenu(String nom) {
    JMenu m = new JMenu(BundleUtil.getLabel(nom+".label"));
    m.setMnemonic(BundleUtil.getLabel(nom+".mnemo").charAt(0));

    return m;
  }

  public void setSize(Dimension d) {
      if (view != null)    view.setSize(d);
  }

  public static String getClassName(Class c) {
    return c == null ? "" : c.getSimpleName();
  }
  
  /**
   * Saves some state information.
   * Subclasses may override this method to return specific information before serialization.
   * @return an array of objects
   */
  public Object[] getState() {
    return null;
  }
}
