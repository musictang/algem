/*
 * @(#)GroupEditor.java 2.8.n 04/10/13
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

package net.algem.group;

import java.awt.BorderLayout;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.n
 */
public class GroupEditor 
  extends FileTab {

  private boolean loaded;
  private GroupView gv;

  public GroupEditor(GemDesktop desktop, GemGroupService service, Group g) {
    super(desktop);
    loaded = (g != null && g.getId() > 0);
    gv = new GroupView(this.desktop, service);
    desktop.addGemEventListener(gv);
    gv.setGroup(g);
    this.setLayout(new BorderLayout());
    add(gv, BorderLayout.CENTER);
  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  Group getGroup() {
    return gv.get();
  }

  void setId(int id) {
    gv.setId(id);
  }

  @Override
  public void load() {
    //throw new UnsupportedOperationException("Not supported yet.");
  }

  protected void dispose() {
    desktop.removeGemEventListener(gv);
  }
  
}
