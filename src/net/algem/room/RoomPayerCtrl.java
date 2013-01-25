/*
 * @(#)RoomPayerCtrl.java 2.6.a 24/09/12
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import net.algem.contact.Person;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemNumericField;
import net.algem.util.ui.GemPanel;

/**
 * TODO reuse in person file
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.2.b
 */
public class RoomPayerCtrl
        extends GemPanel
        implements ActionListener
{

  private GemNumericField payerId;
  private GemField payerName;
  private Room room;

  public RoomPayerCtrl() {

    payerId = new GemNumericField(8);
    payerId.addActionListener(this);
    payerName = new GemField(30);
    payerName.setEditable(false);
    payerName.setBackground(Color.lightGray);

    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    add(payerId);
    add(payerName);
  }

  public Person getPayer() {
    return new Person(getIdPayer(), getPayerName());
  }

  public void set(Person p) {
    if (p == null || p.getId() == 0) {
      p = room.getContact();
    }
    payerId.setText(String.valueOf(p.getId()));
    if (p.getId() == room.getContact().getId()) {
      payerName.setText(MessageUtil.getMessage("payer.link.himself"));
    }
    else {
      payerName.setText(p.getNameFirstname());

    }
    room.setPayer(p);
  }

  public void clear() {
    payerId.setText("");
    payerName.setText("");
  }

  public void setRoom(Room r) {
    this.room = r;
    set(r.getPayer());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Person p = RoomService.getPayer(getIdPayer());
    if (p != null) {
      set(p);
    } else {
      clear();
    }
  }

  private int getIdPayer() {
    try {
      return Integer.parseInt(payerId.getText());
    } catch(NumberFormatException n) {
      return 0;
    }
  }

  private String getPayerName() {
    return payerName.getText();
  }

}
