/*
 * @(#)RoomEditor.java 2.6.a 24/09/12
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
package net.algem.room;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BoxLayout;
import net.algem.contact.Note;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.1.j 27 juin 2011
 */
public class RoomEditor
        extends FileTab
{

  private Room room;
  private RoomView view;
  private GemLabel note;

  public RoomEditor(GemDesktop desktop, Room room) {
    super(desktop);
    this.room = room;
    view = new RoomView(desktop.getDataCache());
    view.set(room);
    this.setLayout(new BorderLayout());
    add(view, BorderLayout.CENTER);

    GemPanel notePanel = new GemBorderPanel();
    notePanel.setLayout(new BoxLayout(notePanel, BoxLayout.X_AXIS));

    note = new GemLabel();
    note.setForeground(Color.RED);

    notePanel.add(note);
    add(notePanel, BorderLayout.SOUTH);

  }

  @Override
  public boolean isLoaded() {
    return room != null && room.getId() > 0;
  }

  public void set(Room r) {
    this.room = r;
    view.set(r);
  }

  public Room getRoom() {
    return view.get();
  }

  public void setNote(Note n) {
    assert n != null;
    String s = n.getText().replace('\n', ' ');
    note.setText(s);
  }

  @Override
  public void load() {
    //throw new UnsupportedOperationException("Not supported yet.");
  }

}
