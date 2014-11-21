/*
 * @(#)EnrolmentTreeCellRenderer.java	2.9.1 18/11/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

import java.awt.Component;
import java.awt.Font;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import net.algem.enrolment.CourseEnrolmentNode;
import net.algem.enrolment.CourseOrder;

/**
 * Specific decoration of the leaves in the tree.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 * @since 2.9.1 18/11/14
 */
public class EnrolmentTreeCellRenderer extends DefaultTreeCellRenderer
{

  private static final Font nodeFont = new Font("Lucida Sans", Font.BOLD, 12);
  private static final Font leafFont = new Font("Lucida Sans", Font.PLAIN, 12);
  
  public EnrolmentTreeCellRenderer() {
  }

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                boolean sel, boolean expanded, boolean leaf,
                                                int row, boolean hasFocus) {
    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    if (leaf && value instanceof CourseEnrolmentNode) {
      CourseOrder co = ((CourseEnrolmentNode) value).getCourseOrder();
      if (co != null && co.getAction() == 0) {
        setForeground(textSelectionColor.brighter());
        setFont(leafFont.deriveFont(Font.ITALIC));
      } else {
        setForeground(textSelectionColor);
        setFont(leafFont);
      }
    } else {
      setForeground(textSelectionColor);
      setFont(nodeFont);
    }

    return this;
  }
}