/*
 * @(#)MusicianEditor.java 2.15.2 27/09/17
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
package net.algem.group;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;

/**
 * Musicians tab.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.2
 */
public class MusicianEditor
        extends FileTab
{

  private MusicianListView musiciansView;
  private List<Musician> musicians;

  public MusicianEditor(GemDesktop desktop, List<Musician> vm) {
    super(desktop);
    musiciansView = new MusicianListView(desktop);
    if (vm != null) {
      for (Musician m : vm) {
        musiciansView.addRow(m);
      }
    }

    this.setLayout(new BorderLayout());
    add(musiciansView, BorderLayout.CENTER);
  }

  /**
   *
   * @return la liste des musicians
   */
  public List<Musician> getMusicians() {
    return musiciansView.get();
  }

  /**
   * Checks if the list of musicians has changed since the loading.
   * @return true if changed
   */
  public boolean hasChanged() {

    List<Musician> vm = getMusicians();
    if (musicians.size() != vm.size()) {
      return true;
    }
    for (int i = 0; i < musicians.size(); i++) {
      if (!musicians.get(i).equals(vm.get(i))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isLoaded() {
    return true;
  }

  /**
   * Reload the list of musicians.
   * This list is read-only.
   */
  @Override
  public void load() {
    musicians = new ArrayList(Collections.unmodifiableCollection(getMusicians()));
  }
}
