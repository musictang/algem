/*
 * @(#)MemberEnrolmentView.java	2.10.0 13/05/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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

package net.algem.enrolment;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import net.algem.contact.Contact;
import net.algem.contact.ContactIO;
import net.algem.contact.PersonFile;
import net.algem.contact.PersonFileEditor;
import net.algem.contact.PersonFileIO;
import net.algem.group.Musician;
import net.algem.group.MusicianTableModel;
import net.algem.planning.DateRangePanel;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemNumericField;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * View the list of enrolled members.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.10.0 13/05/16
 */
public abstract class MemberEnrolmentView
        extends GemPanel
{

  protected MusicianTableModel membersTableModel;
  protected JTable memberTable;
  protected EnrolmentService service;
  protected DateRangePanel datePanel;
  protected DataCache dataCache;
  protected int id;
  protected GemLabel title;
  protected GemDesktop desktop;
  protected GemNumericField total;

  public MemberEnrolmentView(final GemDesktop desktop, EnrolmentService service) {
    this.desktop = desktop;
    this.service = service;
    this.dataCache = desktop.getDataCache();
    membersTableModel = new MusicianTableModel(dataCache);

    memberTable = new JTable(membersTableModel)
    {
      @Override
      public void processMouseEvent(MouseEvent evt) {
        if (evt.getID() == MouseEvent.MOUSE_CLICKED && evt.getClickCount() > 1) {
          desktop.setWaitCursor();
          loadMember();
          desktop.setDefaultCursor();
        } else {
          super.processMouseEvent(evt);
        }
      }

      @Override
      public boolean isCellEditable(int rowIndex, int colIndex) {
        return false;
      }
    };

    memberTable.setAutoCreateRowSorter(true);

    TableColumnModel cm = memberTable.getColumnModel();
    cm.getColumn(0).setPreferredWidth(30);
    cm.getColumn(1).setPreferredWidth(120);
    cm.getColumn(2).setPreferredWidth(120);
    cm.getColumn(3).setPreferredWidth(120);

    JScrollPane pm = new JScrollPane(memberTable);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = new Insets(5, 5, 5, 5);

    GemButton btLoad = new GemButton(GemCommand.LOAD_CMD);
    datePanel = new DateRangePanel(dataCache.getStartOfYear(), dataCache.getEndOfYear(), BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    btLoad.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e) {
        load();
      }
    });
    total = new GemNumericField(3);
    total.setEditable(false);
    title = new GemLabel();
    gb.add(title, 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(datePanel, 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(btLoad, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel("Total : "), 2,1,1,1,GridBagHelper.WEST);
    gb.add(total, 3, 1, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    gb.add(pm, 0, 2, 4, 1, GridBagHelper.BOTH, 1.0, 1.0);

  }

  private void loadMember() {
    int row = memberTable.getSelectedRow();
    if (row < 0) {
      return;
    }
    int n = memberTable.convertRowIndexToModel(row);
    if (n < 0) {
      return;
    }

    Musician m = (Musician) membersTableModel.getItem(n);
    // il est nécessaire de récupérer les adresses, tel et email éventuels du contact
    Contact c = ContactIO.findId(m.getId(), DataCache.getDataConnection());
    PersonFile pf = new PersonFile(c);
    try {
      ((PersonFileIO) DataCache.getDao(Model.PersonFile)).complete(pf);
    } catch (SQLException ex) {
      GemLogger.logException("complete dossier musicien liste", ex);
    }
    PersonFileEditor editor = new PersonFileEditor(pf);
    desktop.addModule(editor);
  }

  /**
   * Initial loading.
   *
   * @param id
   * @param title 
   */
  public void load(int id, String title) {
    this.id = id;

    datePanel.setStart(dataCache.getStartOfYear());
    datePanel.setEnd(dataCache.getEndOfYear());
    this.title.setText(title);
    clear();
    load(id, dataCache.getStartOfYear().getDate(), dataCache.getEndOfYear().getDate());
  }
  
  protected void load() {
    Date start = datePanel.getStart();
    Date end = datePanel.getEnd();
    if (id == 0) {
      return;
    }
    clear();
    load(id, start, end);
  }
  
  /**
   * Load the list of members between {@code start} and {@code end}.
   * @param id course id
   * @param start start date
   * @param end end date
   */
  protected abstract void load(int id, Date start, Date end);

  public void clear() {
    membersTableModel.clear();
    total.setText(null);
  }
}
