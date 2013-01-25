/*
 * @(#)AttendanceSheetDlg.java	2.7.a 03/12/12
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
package net.algem.edition;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemChoice;
import net.algem.util.ui.GridBagHelper;
import net.algem.util.ui.MessagePopup;

/**
 * Printing dialog for attendance sheet.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class AttendanceSheetDlg
        implements ActionListener
{

  private DataCache dataCache;
  private JDialog dialog;
  private DateFrField startDate;
  private DateFrField endDate;
  private GemButton btCancel;
  private GemButton btEdition;
//  private JPanel boutons;
  private Teacher teacher;
  private GemChoice estabChoice;

  public AttendanceSheetDlg(Component _parent, DataCache _cache, Teacher _prof) {
    dataCache = _cache;
    teacher = _prof;

    String title = "Feuille présence ";
    if (teacher != null) {
      title += teacher.toString();
    }

    dialog = new JDialog((Frame) null, title, true);
    JPanel panel = new JPanel();

    estabChoice = new EstabChoice(dataCache.getList(Model.Establishment));
    startDate = new DateFrField();
    endDate = new DateFrField();

    panel.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(panel);

    gb.add(new JLabel(BundleUtil.getLabel("School.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(estabChoice, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Date.From.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(startDate, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Date.To.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(endDate, 1, 2, 1, 1, GridBagHelper.WEST);

    btEdition = new GemButton(GemCommand.EDIT_CMD);
    btEdition.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    JPanel buttons = new JPanel();
    buttons.add(btCancel);
    buttons.add(btEdition);

    //setLayout(new BorderLayout());
    dialog.getContentPane().add(new JLabel("Edition Feuille Présence"), BorderLayout.NORTH);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.getContentPane().add(buttons, BorderLayout.SOUTH);
    dialog.setSize(300, 200);
    dialog.setVisible(true);
  }

  public AttendanceSheetDlg(Component _parent, DataCache _cache) {
    this(_parent, _cache, null);
  }

  public int getEstab() {
    //return 3501 + etabChoix.getSelectedIndex() ;
    return estabChoice.getKey();
  }

  public DateFr getStart() {
    return startDate.getDateFr();
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btCancel) {
      //hide();
      dialog.setVisible(false);
      dialog.dispose();
    } else if (evt.getSource() == btEdition) {
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
