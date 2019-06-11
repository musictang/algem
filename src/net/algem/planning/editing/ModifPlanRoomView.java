/*
 * @(#)ModifPlanRoomView.java	2.15.10 01/10/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning.editing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import net.algem.room.Room;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 * Form view used to change the location of selected schedule.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.10
 * @since 1.0a 07/07/1999
 */
public class ModifPlanRoomView
        extends ModifPlanView
        implements ActionListener
{

  private GemField before;
  private RoomChoice after;
  //private JCheckBox checkAbsence;
  //private GemLabel noteLabel;
  //private GemField noteAbs;

  public ModifPlanRoomView(DataCache dataCache, String label) {
    super(dataCache, label);

    dateRange.setEnabled(false, 0);
    before = new GemField(DEF_FIELD_WIDTH);
    before.setEditable(false);
    after = new RoomChoice(dataCache.getList(Model.Room));
    after.addActionListener(this);
    Dimension prefSize = new Dimension(before.getPreferredSize().width, after.getPreferredSize().height);
    after.setPreferredSize(prefSize);
    /*checkAbsence = new JCheckBox(BundleUtil.getLabel("Teacher.notif.absence"));
    checkAbsence.setBorder(null);
    checkAbsence.addActionListener(this);*/

    gb.add(new GemLabel(BundleUtil.getLabel("Current.room.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(before, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("New.room.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(after, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(Box.createVerticalStrut(10), 0, 4, 2, 1, GridBagHelper.WEST);
    /*gb.add(checkAbsence, 0, 5, 2, 1, GridBagHelper.WEST);
    checkAbsence.setEnabled(false);*/
  }

  @Override
  public void setId(int sid) {
    id = sid;
    after.setKey(id);
    before.setText(((Room) after.getSelectedItem()).getName());
  }

  @Override
  /**
   * Gets the id of the new selected room.
   */
  public int getId() {
    return after.getKey();
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    /*Object src = evt.getSource();
    if (src == after) {
      try {
        Room r = (Room) DataCache.findId(after.getKey(), Model.Room);
        if (r != null && r.isCatchingUp()) {
          //checkAbsence.setEnabled(true);
        } else {
          //checkAbsence.setEnabled(false);
          //checkAbsence.setSelected(false);
          removeNote();
        }
      } catch (SQLException ex) {
        GemLogger.log(Level.WARNING, ex.getMessage());
      }
    }
    else if (src == checkAbsence) {
      loadNoteAbsence();
    }*/
  }

  /**
   * Adds or remove a note component depending on the state of {@code checkAbsence}.
   * @deprecated
   */
  private void loadNoteAbsence() {
    /*if (checkAbsence.isSelected()) {
      addNote();
    } else {
      removeNote();
    }*/
  }

  /**
   * Adds a note component.
   * @deprecated
   */
  private void addNote() {
    /*if (noteAbs == null) {
      noteAbs = new GemField(DEF_FIELD_WIDTH);
      noteLabel = new GemLabel(BundleUtil.getLabel("Reason.label"));
      gb.add(noteLabel, 0, 6, 1, 1, GridBagHelper.WEST);
      gb.add(noteAbs, 1, 6, 1, 1, GridBagHelper.WEST);
      revalidate();
    }*/
  }

  /**
   * Removes the note component.
   * @deprecated
   */
  private void removeNote() {
    /*if (noteAbs != null) {
      Component c = gb.getContainer();
      if (c instanceof JPanel) {
        ((JPanel) gb.getContainer()).remove(noteLabel);
        ((JPanel) gb.getContainer()).remove(noteAbs);
        noteLabel = null;
        noteAbs = null;
        revalidate();
      }
    }*/
  }

}
