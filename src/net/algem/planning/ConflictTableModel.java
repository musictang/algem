/*
 * @(#)ConflictTableModel.java	2.12.0 01/03/17
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
package net.algem.planning;

import javax.swing.ImageIcon;
import net.algem.util.BundleUtil;
import net.algem.util.ImageUtil;
import net.algem.util.MessageUtil;
import net.algem.util.ui.JTableModel;

/**
 * Conflict table model.
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.0
 */
public class ConflictTableModel
        extends JTableModel<ScheduleTestConflict>
{

  private final ImageIcon iconOK;
  private final ImageIcon iconERR;
  private PlanningService service;
  private UpdateConflictListener listener;

  public ConflictTableModel(PlanningService service) {
    this.service = service;
    header = new String[]{
      BundleUtil.getLabel("Date.label"),
      BundleUtil.getLabel("Start.label"),
      BundleUtil.getLabel("End.label"),
      BundleUtil.getLabel("Free.label"),
      BundleUtil.getLabel("Active.label"),
      BundleUtil.getLabel("Conflict.label")
    };
    iconOK = ImageUtil.createImageIcon(ImageUtil.NO_CONFLICT_ICON);
    iconERR = ImageUtil.createImageIcon(ImageUtil.CONFLICT_ICON);
  }

  public void addUpdateConflictListener(UpdateConflictListener listener) {
    this.listener = listener;
  }

  @Override
  public int getIdFromIndex(int i) {
    return -1;
  }

  @Override
  public Class getColumnClass(int column) {
    switch (column) {
      case 0:
      case 1:
      case 2:
        return String.class;
      case 3:
        return ImageIcon.class;
      case 4:
        return Boolean.class;
      case 5:
        return String.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return column == 0 || column == 4;
  }

  @Override
  public Object getValueAt(int row, int col) {
    ScheduleTestConflict p = tuples.elementAt(row);
    switch (col) {
      case 0:
        return p.getDate().toString();
      case 1:
        return p.getStart().toString();
      case 2:
        return p.getEnd().toString();
      case 3:
        return p.isConflict() ? iconERR: iconOK;
      case 4:
        return p.isActive() && !p.isConflict();
      case 5:
        StringBuilder lib = new StringBuilder();
        if (!p.isTeacherFree()) {
          lib.append(" [ ").append(MessageUtil.getMessage("busy.teacher.warning")).append(" ]");
        }
        if (!p.isRoomFree()) {
          lib.append(" [ ").append(MessageUtil.getMessage("busy.room.warning")).append(" ]");
        }
        if (!p.isMemberFree()) {
          lib.append(" [ ").append(MessageUtil.getMessage("busy.member.warning")).append(" ]");
        }
         if (!p.isGroupFree()) {
          lib.append(" [ ").append(MessageUtil.getMessage("busy.group.warning")).append(" ]");
        }
         if (!p.isActive()) {
           lib.append(" [ ").append(BundleUtil.getLabel("Disabled.label")).append(" ]");
         }
        if (p.getDetail() != null) {
          lib.append(p.getDetail());
        }
        return lib.toString();
    }
    return null;
  }

  @Override
  public void setValueAt(Object value, int row, int col) {
    ScheduleTestConflict c = tuples.elementAt(row);
    switch (col) {
      case 0:
        DateFr newDate = new DateFr((String)value);
        c.setDate(newDate);
        if (isFree(c)) {
          c.getAction().getDates().set(c.getDateIndex(), newDate);
          boolean unlock = true;
          for (ScheduleTestConflict tc : tuples) {
            if (!isFree(tc)) {
              unlock = false;
              break;
            }
          }
          listener.update(unlock);
        } else {
          listener.update(false);
        }
        fireTableRowsUpdated(row, row);
        break;
      case 4:
        boolean checked = (boolean) value;
        c.setActive(checked);
        if (checked) {
          c.getAction().getDates().set(c.getDateIndex(), new DateFr(c.getDate()));
        } else {
          c.getAction().getDates().set(c.getDateIndex(), new DateFr());// null date (must not be created)
        }
        fireTableRowsUpdated(row, row);
        break;
    }
  }

  private boolean isFree(ScheduleTestConflict stc) {
    boolean free = true;
    int room = stc.getAction().getRoom();
    int teacher = stc.getAction().getIdper();
    if (service != null) {
      if (service.isRoomFree(stc, room)) {
        stc.setRoomFree(true);
      } else {
        stc.setRoomFree(false);
        free = false;
      }
      // test prof
      if (teacher > 0) {
        if (service.isTeacherFree(stc, teacher)) {
          stc.setTeacherFree(true);
        } else {
          stc.setTeacherFree(false);
          free = false;
        }
      }
    }
    return free;
  }

}
