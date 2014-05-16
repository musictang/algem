/*
 * @(#)MemberRehearsalView.java	2.8.t 16/05/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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

import java.awt.GridBagLayout;
import java.util.Date;
import javax.swing.JCheckBox;
import net.algem.contact.Person;
import net.algem.planning.DateFr;
import net.algem.planning.DateRangePanel;
import net.algem.planning.Hour;
import net.algem.planning.HourRangePanel;
import net.algem.room.RoomChoice;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Member single rehearsal panel entry.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.8.t
 */
public class MemberRehearsalView
        extends GemPanel
{

  private GemField memberField;
  private DateRangePanel datePanel;
  private HourRangePanel hourPanel;
  private RoomChoice roomChoice;
  private JCheckBox withCard;

  public MemberRehearsalView(DataCache dc) {

    memberField = new GemField(35);
    memberField.setEditable(false);
    datePanel = new DateRangePanel(DateRangePanel.SIMPLE_DATE, null);
    hourPanel = new HourRangePanel(3 * 60);
    roomChoice = new RoomChoice(dc.getList(Model.Room));
    withCard = new JCheckBox(BundleUtil.getLabel("Subscription.label"));
    withCard.setBorder(null);
    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.insets = GridBagHelper.SMALL_INSETS;
    
    gb.add(new GemLabel(BundleUtil.getLabel("Member.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Hour.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Room.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    //gb.add(new GemLabel(BundleUtil.getLabel("Subscription.label")), 0, 4, 1, 1, GridBagHelper.EAST);
    
    gb.add(memberField, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(datePanel, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(hourPanel, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(roomChoice, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(withCard, 1, 4, 1, 1, GridBagHelper.WEST);

  }

  public int getMemberId() {
    int n = 0;
    try {
      n = Integer.parseInt(memberField.getText());
    } catch (Exception ex) {
      GemLogger.logException(ex);
    }
    return n;
  }

  public int getRoom() {
    return roomChoice.getKey();
  }

  public DateFr getDate() {
    return datePanel.get();
  }

  public Hour getHourStart() {
    return hourPanel.getStart();
  }

  public Hour getHourEnd() {
    return hourPanel.getEnd();
  }

  public void set(Person _adh) {
    memberField.setText(_adh.getId() + " " + _adh.getFirstName() + " " + _adh.getName());
  }

  public boolean withCard() {
    return withCard.isSelected();
  }

  public void clear() {
    datePanel.setDate(new Date());
    hourPanel.clear();
    roomChoice.setSelectedIndex(0);
  }
}
