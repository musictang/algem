/*
 * @(#)MemberRehearsalView.java	2.8.w 17/07/14
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
package net.algem.contact.member;

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
import net.algem.util.GemLogger;
import net.algem.util.model.GemList;
import net.algem.room.Room;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Member single rehearsal panel entry.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.8.w
 */
public class MemberRehearsalView
        extends GemPanel
{

  private GemField memberField;
  private DateRangePanel datePanel;
  private HourRangePanel hourPanel;
  private RoomChoice roomChoice;
  private JCheckBox withCard;

  public MemberRehearsalView(GemList<Room> roomList) {

    memberField = new GemField(35);
    memberField.setEditable(false);
    datePanel = new DateRangePanel(DateRangePanel.SIMPLE_DATE, null);
    hourPanel = new HourRangePanel(3 * 60);
    roomChoice = new RoomChoice(roomList);
    withCard = new JCheckBox(BundleUtil.getLabel("Subscription.label"));
    withCard.setBorder(null);
    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    
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

  int getMemberId() {
    int n = 0;
    try {
      n = Integer.parseInt(memberField.getText());
    } catch (NumberFormatException ex) {
      GemLogger.logException(ex);
    }
    return n;
  }

  int getRoom() {
    return roomChoice.getKey();
  }

  DateFr getDate() {
    return datePanel.get();
  }

  Hour getHourStart() {
    return hourPanel.getStart();
  }

  Hour getHourEnd() {
    return hourPanel.getEnd();
  }

  void set(Person per) {
    memberField.setText(per.getId() + " " + per.getFirstName() + " " + per.getName());
  }

  boolean withCard() {
    return withCard.isSelected();
  }

  void clear() {
    datePanel.setDate(new Date());
    hourPanel.clear();
    roomChoice.setSelectedIndex(0);
  }
}
