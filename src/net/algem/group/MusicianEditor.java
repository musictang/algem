/*
 * @(#)MusicianEditor.java 2.6.a 31/07/12
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
package net.algem.group;

import java.awt.BorderLayout;
import java.util.Collections;
import java.util.Vector;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;

/**
 * Musicians tab.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class MusicianEditor
        extends FileTab
{

  private MusicianListView musiciansView;
  private Vector<Musician> musicians;

  public MusicianEditor(GemDesktop desktop, Vector<Musician> vm) {
    super(desktop);

    musiciansView = new MusicianListView(desktop);
    if (vm != null) {
      for (Musician m : vm) {
        musiciansView.addRow(m);
      }     
    }
    update();
    this.setLayout(new BorderLayout());
    add(musiciansView, BorderLayout.CENTER);
  }

  /**
   *
   * @return la liste des musicians
   */
  public Vector<Musician> getMusicians() {
    return musiciansView.get();
  }

  /**
   * Checks if the list of musicians has changed since the loading.
   * @return true if changed
   */
  public boolean hasChanged() {

    Vector<Musician> m = getMusicians();
    if (musicians.size() != m.size()) {
      return true;
    }
    for (int i = 0; i < musicians.size(); i++) {
      if (!musicians.elementAt(i).equals(m.elementAt(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Reload the list of musicians.
   * This list is read-only.
   */
  public void update() {
    musicians = new Vector(Collections.unmodifiableCollection(getMusicians()));
  }

  @Override
  public boolean isLoaded() {
    return true;
  }

  @Override
  public void load() {
  }
}
