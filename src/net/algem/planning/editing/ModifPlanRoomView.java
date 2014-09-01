/*
 * @(#)ModifPlanRoomView.java	2.8.k 25/07/13
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
package net.algem.planning.editing;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import net.algem.contact.Note;
import net.algem.contact.NoteDlg;
import net.algem.contact.teacher.TeacherActiveChoiceModel;
import net.algem.contact.teacher.TeacherChoiceModel;
import net.algem.room.Room;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.k
 * @since 1.0a 07/07/1999
 */
public class ModifPlanRoomView
        extends ModifPlanView
        implements ActionListener
{

  private GemField before;
  private RoomChoice after;
  private JCheckBox checkAbsence;
  private Note noteAbs;
  private String textAbs;

  public ModifPlanRoomView(DataCache _dc, String label) {
    super(_dc, label);

    before = new GemField(20);
    before.setEditable(false);
    after = new RoomChoice(dataCache.getList(Model.Room));
    
    checkAbsence = new JCheckBox("Mémoriser absence");
    checkAbsence.setBorder(null);
    checkAbsence.addActionListener(this);
    after.addActionListener(this);
    
    gb.add(new GemLabel(BundleUtil.getLabel("Current.room.label")), 0, 2, 1, 1, GridBagHelper.EAST);
    gb.add(before, 1, 2, 3, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("New.room.label")), 0, 3, 1, 1, GridBagHelper.EAST);
    gb.add(after, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(checkAbsence, 1, 4, 3, 1, GridBagHelper.WEST);
    checkAbsence.setEnabled(false);
  }

  @Override
  public void setId(int sid) {
    id = sid;
    after.setKey(id);
    before.setText(((Room) after.getSelectedItem()).getName());
  }

  @Override
  public int getId() {
    return after.getKey();
  }
  
  public void loadNoteAbsence () {
    if (checkAbsence.isSelected()==true) {
      noteAbs = new Note (textAbs);
      Frame f = new Frame("Motif Absence");
      NoteDlg nd = new NoteDlg(f);
      nd.show();
    }
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    if (src == after) {
      if ((after.getKey() == 8) || (after.getKey() == 21) || (after.getKey() == 27)) {
       checkAbsence.setEnabled(true);
      }
      else {
        checkAbsence.setEnabled(false);
      }
    }
    else if (src == checkAbsence) {
      loadNoteAbsence ();
    }
  }

}
