/*
 * @(#)CourseModulePanel.java	2.8.a 14/03/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

package net.algem.course;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JLabel;
import net.algem.contact.InfoPanel;
import net.algem.planning.Hour;
import net.algem.planning.HourField;
import net.algem.util.ui.ButtonRemove;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.a
 * @since 2.8.a 13/03/2013
 */
public class CourseModulePanel 
  extends InfoPanel 
{
  private CourseModuleInfo info;

  public CourseModulePanel(CourseModuleInfo info, ActionListener listener) {
    setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;

    HourField hf = new HourField(new Hour(info.getTimeLength()));
    
//    JLabel jl = new JLabel(info.get);
    JLabel jl = new JLabel();
    jl.setPreferredSize(new Dimension(150, 10));
    ButtonRemove minus = new ButtonRemove(this);
    minus.addActionListener(listener);
    
    gb.add(Box.createVerticalStrut(4), 0, 0, 4, 1, GridBagHelper.WEST);
    gb.add(jl, 0, 1, 2, 1, GridBagHelper.WEST);
    gb.add(hf, 2, 1, 1, 1);
    gb.add(minus, 3, 1, 1, 1, GridBagHelper.EAST);
    
  }

}