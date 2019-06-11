/*
 * @(#)GroupNoteDlg.java	2.6.a 12/09/12
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

import java.sql.SQLException;
import net.algem.contact.Note;
import net.algem.contact.NoteDlg;
import net.algem.contact.NoteEvent;
import net.algem.contact.Person;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;

/**
 * Note dialog for groups.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class GroupNoteDlg
        extends NoteDlg
{

  private GemDesktop desktop;
  private GemGroupService service;
  private Group group;

  public GroupNoteDlg(GemDesktop desktop, GemGroupService service) {
    super(desktop.getFrame());
    this.desktop = desktop;
    this.service = service;
  }

  public void loadNote(Group g) {

    if (g != null) {
      note = g.getNote();
      this.group = g;
    }

    if (note != null) {
      title.setText(MessageUtil.getMessage("group.note.modification", new Object[] {note.getId(), group.getId()}));
      text.setText(note.getText());
    } else {
      title.setText(MessageUtil.getMessage("group.note.creation", new Object[] {group.getId()}));
    }
  }

  @Override
  public boolean save() {
    try {
      String s = text.getText().trim();
      if (note == null) {
        if (s.length() > 0) {
          note = new Note(group.getId(), s, Person.GROUP);
          service.create(note);
          group.setNote(note);
        }
      } else {
        if (!s.equals(note.getText())) {
          note.setText(s);
          service.update(note);
        }
      }
      desktop.postEvent(new NoteEvent(this, note));
    } catch (SQLException e) {
      GemLogger.logException("enregistrement note groupe", e, body);
      return false;
    }
    return true;

  }
}
