/*
 * @(#)GridBagHelper.java	2.6.a 25/09/12
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

/**
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class GridBagHelper
{

  /** Default padding for a component in GridBagHelper. */
  public static Insets SMALL_INSETS = new Insets(2, 2, 2, 0);
  
  public static final int RELATIVE = GridBagConstraints.RELATIVE;
  public static final int REMAINDER = GridBagConstraints.REMAINDER;
  public static final int HORIZONTAL = GridBagConstraints.HORIZONTAL;
  public static final int VERTICAL = GridBagConstraints.VERTICAL;
  public static final int BOTH = GridBagConstraints.BOTH;
  public static final int NONE = GridBagConstraints.NONE;
  public static final int CENTER = GridBagConstraints.CENTER;
  public static final int EAST = GridBagConstraints.EAST;
  public static final int NORTH = GridBagConstraints.NORTH;
  public static final int NORTHEAST = GridBagConstraints.NORTHEAST;
  public static final int NORTHWEST = GridBagConstraints.NORTHWEST;
  public static final int SOUTH = GridBagConstraints.SOUTH;
  public static final int SOUTHEAST = GridBagConstraints.SOUTHEAST;
  public static final int SOUTHWEST = GridBagConstraints.SOUTHWEST;
  public static final int WEST = GridBagConstraints.WEST;
  
  public Insets insets;
  
  private Container container;
  private GridBagLayout layout;
  private GridBagConstraints constraints;
  private int gridx, gridy;
  private int gridwidth, gridheight;
  private int fill;
  private int ipadx, ipady;
  
  private int anchor;
  private double weightx, weighty;

  public GridBagHelper(Container cont) {
    container = cont;
    layout = (GridBagLayout) container.getLayout();

    constraints = new GridBagConstraints();

    gridx = constraints.gridx;
    gridy = constraints.gridy;

    gridwidth = constraints.gridwidth;
    gridheight = constraints.gridheight;

    fill = constraints.fill;

    ipadx = constraints.ipadx;
    ipady = constraints.ipady;

    insets = constraints.insets;

    anchor = constraints.anchor;

    weightx = constraints.weightx;
    weighty = constraints.weighty;
  }

  public void add(Component component,
          int gridx, int gridy, int gridwidth, int gridheight) {
    add(component, gridx, gridy, gridwidth, gridheight, fill, ipadx, ipady, insets, anchor, weightx, weighty);
  }

  public void add(Component component,
          int gridx, int gridy, int gridwidth, int gridheight,
          int anchor) {
    add(component, gridx, gridy, gridwidth, gridheight, fill, ipadx, ipady, insets, anchor, weightx, weighty);
  }

  public void add(Component component,
          int gridx, int gridy, int gridwidth, int gridheight, int fill, int anchor) {
    add(component, gridx, gridy, gridwidth, gridheight, fill, ipadx, ipady, insets, anchor, weightx, weighty);
  }

  public void add(Component component,
          int gridx, int gridy, int gridwidth, int gridheight, int fill, int anchor, double weightx, double weighty) {
    add(component, gridx, gridy, gridwidth, gridheight, fill, ipadx, ipady, insets, anchor, weightx, weighty);
  }
  
  public void add(Component component,
          int gridx, int gridy, int gridwidth, int gridheight,
          Insets insets, int anchor) {
    add(component, gridx, gridy, gridwidth, gridheight, fill, ipadx, ipady, insets, anchor, weightx, weighty);
  }

  public void add(Component component,
          int gridx, int gridy, int gridwidth, int gridheight,
          int fill, double weightx, double weighty) {
    add(component, gridx, gridy, gridwidth, gridheight, fill, ipadx, ipady, insets, anchor, weightx, weighty);
  }

  public void add(Component component,
          int gridx, int gridy, int gridwidth, int gridheight,
          int fill, double weightx, double weighty, int padx, int pady) {
    add(component, gridx, gridy, gridwidth, gridheight, fill, padx, pady, insets, anchor, weightx, weighty);
  }

  /**
   *
   * @param component
   * @param gridx cell x
   * @param gridy col y
   * @param gridwidth
   * @param gridheight
   * @param insets padding
   * @param fill
   * @param weightx
   * @param weighty
   */
  public void add(Component component,
          int gridx, int gridy, int gridwidth, int gridheight,
          Insets insets, int fill, double weightx, double weighty) {
    add(component, gridx, gridy, gridwidth, gridheight, fill, ipadx, ipady, insets, anchor, weightx, weighty);
  }

  public void add(Component component,
          int gridx, int gridy,
          int gridwidth, int gridheight,
          int fill,
          int ipadx, int ipady,
          Insets insets,
          int anchor,
          double weightx, double weighty) {
    constraints.gridx = gridx;
    constraints.gridy = gridy;

    constraints.gridwidth = gridwidth;
    constraints.gridheight = gridheight;

    constraints.fill = fill;

    constraints.ipadx = ipadx;
    constraints.ipady = ipady;

    if (insets != null) {
      constraints.insets = insets;
    }

    constraints.anchor = anchor;

    constraints.weightx = weightx;
    constraints.weighty = weighty;

    layout.setConstraints(component, constraints);
    container.add(component);
  }
  

}
