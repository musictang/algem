/*
 * @(#)TeacherEditor.java	2.8.m 06/09/13
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
package net.algem.contact.teacher;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import net.algem.contact.PersonFile;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.GemButton;

/**
 * Teacher editor tab.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.m
 */
public class TeacherEditor
        extends FileTab
{

  private PersonFile dossier;
  private TeacherView view;
  private GemButton deleteBt;
  private boolean loaded;

  public TeacherEditor(GemDesktop _desktop, PersonFile dossier) {
    super(_desktop);
    this.dossier = dossier;
    view = new TeacherView(dataCache.getInstruments());

    deleteBt = new GemButton(GemCommand.DELETE_CMD);
    deleteBt.setActionCommand("TeacherDelete");

    this.setLayout(new BorderLayout());
    add(view, BorderLayout.CENTER);
    add(deleteBt, BorderLayout.SOUTH);
  }

  public void addActionListener(ActionListener listener) {
    deleteBt.addActionListener(listener);
  }
  
  private void set(Teacher t) {
    if (t == null) {
      t = new Teacher(dossier.getId());      
    }
    view.set(t);
  }

  public Teacher get() {
    Teacher t = view.get();
    if (dossier.getContact() != null) {
      t.setName(dossier.getContact().getName());
      t.setFirstName(dossier.getContact().getFirstName());
    }
    return t;
  }

  public void clear() {
    view.clear();
    // loaded = false;
  }

  @Override
  public boolean isLoaded() {
    return loaded;
  }

  @Override
  public void load() {
    set(dossier.getTeacher());
    loaded = true;
  }

}
