/*
 * @(#)LicenceDlg.java 2.8.w 10/09/14
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

package net.algem.util.help;

import java.awt.Component;
import java.io.IOException;
import javax.swing.JScrollPane;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;

/**
 * Licence viewer.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 2.8.w 10/09/14
 */
public class LicenceDlg
extends AbstractHelpDlg
{

  private HtmlEditor editorPane;
  
  public LicenceDlg(GemDesktop desktop, String title) {
    super(desktop.getFrame(), title);
    setSize(GemModule.DEFAULT_SIZE);
    double w = getSize().getWidth();
    setLocation(desktop.getFrame().getWidth() - (int) w, 100);
    setVisible(true);
  }

  @Override
  protected Component getContent() {
    editorPane = new HtmlEditor("text/html", null);
    try {
      editorPane.setPage(getResource("licence.html"));
    } catch (IOException ex) {
      GemLogger.log(ex.getMessage());
    }
    JScrollPane scroll = new JScrollPane(editorPane);
    
    return scroll;  
  }


}
