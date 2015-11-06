/*
 * @(#)AttendanceSheetDlg.java	2.9.4.13 02/11/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.edition;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import net.algem.contact.teacher.Teacher;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.planning.DateRange;
import net.algem.room.EstabChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;
import net.algem.util.ui.*;

/**
 * Printing dialog for attendance sheet.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class AttendanceSheetDlg
        implements ActionListener
{

  private DataCache dataCache;
  private JDialog dialog;
  private DateFrField startDate;
  private DateFrField endDate;
  private GemButton btCancel;
  private GemButton btPrint;
  private Teacher teacher;
  private GemChoice estabChoice;

  public AttendanceSheetDlg(Component _parent, DataCache _cache, Teacher _prof) {
    dataCache = _cache;
    teacher = _prof;

    String title = BundleUtil.getLabel("Menu.presence.file.label");
    if (teacher != null) {
      title += " " + teacher.toString();
    }

    dialog = new JDialog((Frame) null, title, true);
    dialog.setLayout(new BorderLayout());

    GemPanel panel = new GemPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    GridBagHelper gb = new GridBagHelper(panel);
    gb.insets = GridBagHelper.SMALL_INSETS;

    estabChoice = new EstabChoice(dataCache.getList(Model.Establishment));
    startDate = new DateFrField();
    endDate = new DateFrField();
    String e = BundleUtil.getLabel("Establishment.label");
    gb.add(new JLabel(e == null ? "" : e.substring(0, 4)), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(estabChoice, 1, 0, 1, 1, GridBagHelper.WEST);
    GemPanel datePanel = new GemPanel();
    
    datePanel.add(startDate);
    datePanel.add(new JLabel(BundleUtil.getLabel("Date.To.label")));
    datePanel.add(endDate);
    gb.add(new JLabel(BundleUtil.getLabel("Date.From.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(datePanel, 1, 1, 1, 1, GridBagHelper.WEST);

    btPrint = new GemButton(GemCommand.PRINT_CMD);
    btPrint.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    GemPanel buttons = new GemPanel(new GridLayout(1,2));
    buttons.add(btPrint);
    buttons.add(btCancel);

    dialog.add(panel, BorderLayout.CENTER);
    dialog.add(buttons, BorderLayout.SOUTH);
    dialog.setSize(400, 200);
    dialog.pack();

    dialog.setVisible(true);

  }

  public AttendanceSheetDlg(Component _parent, DataCache _cache) {
    this(_parent, _cache, null);
  }

  public int getEstab() {
    return estabChoice.getKey();
  }

  public DateFr getStart() {
    return startDate.getDateFr();
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btCancel) {
      dialog.setVisible(false);
      dialog.dispose();
    } else if (evt.getSource() == btPrint) {
      final DateRange pg = new DateRange(startDate.getDateFr(), endDate.getDateFr());
      if (!pg.isValid()) {
        MessagePopup.information(dialog, MessageUtil.getMessage("date.entry.error"));
        return;
      }
      dialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
      final AttendanceSheet sheet = new AttendanceSheet(dialog, dataCache);
      if (teacher != null) {
        sheet.edit(teacher, pg, getEstab());
      } else { // feuille de présence globale
        new Thread(new Runnable() {
          @Override
          public void run() {
            sheet.edit(pg, getEstab());
          }
        }).start();
      }
      dialog.setCursor(Cursor.getDefaultCursor());

      dialog.setVisible(false);
      dialog.dispose();
    }
  }
}
