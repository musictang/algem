/*
 * @(#)PersonSubscriptionCardView.java 2.7.a 14/01/13
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
package net.algem.contact.member;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.algem.accounting.Account;
import net.algem.accounting.AccountChoice;
import net.algem.planning.DateFr;
import net.algem.planning.DateRangePanel;
import net.algem.planning.Hour;
import net.algem.util.BundleUtil;
import net.algem.util.model.GemList;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Subscription card view.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class PersonSubscriptionCardView
        extends GemPanel
        implements ChangeListener, ActionListener
{

  private DateRangePanel date;
  private RehearsalPassChoice rehearsalCardChoice;
  private GemLabel nhr; //nombre d'heures restantes
  private JSpinner sessions;
  private SpinnerModel spmodel = new SpinnerNumberModel(0, -10, 10, 1);
  private JComboBox durationChoice;
  private JLabel hmin;
  private GemField restField;
  private int rest;
  private AccountChoice account;
  private PersonSubscriptionCard personCard;

  public PersonSubscriptionCardView(Vector<RehearsalPass> vc, GemList<Account> la) throws SQLException {
    date = new DateRangePanel(DateRangePanel.SIMPLE_DATE, null);
    //date.setEditable(false);
    date.setDate(new Date());
    //date.setPreferredSize(new Dimension(60,20));
    sessions = new JSpinner(spmodel);
    sessions.setPreferredSize(new Dimension(50, 20));
    sessions.addChangeListener(this);

    rehearsalCardChoice = new RehearsalPassChoice(vc);
    rehearsalCardChoice.addActionListener(this);

    durationChoice = new JComboBox(new Integer[]{30, 60});
    durationChoice.setSelectedIndex(1);// durée séance de 60 minutes par défaut
    durationChoice.addActionListener(this);

    account = new AccountChoice(la);

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.label")), 0, 0, 1, 1, GridBagHelper.EAST);
    gb.add(date, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel("carte"), 0, 1, 1, 1, GridBagHelper.EAST);
    gb.add(rehearsalCardChoice, 1, 1, 1, 1, GridBagHelper.WEST);
    JPanel panel = new JPanel(new FlowLayout());
    gb.add(new GemLabel("ajout/retrait séances"), 0, 2, 1, 1, GridBagHelper.EAST);


    panel.add(sessions);
    panel.add(new GemLabel(" X "));
    panel.add(durationChoice);
    panel.add(new GemLabel("durée séance"));
    gb.add(panel, 1, 2, 1, 1, GridBagHelper.WEST);
    hmin = new JLabel();
    gb.add(new GemLabel("Total ajout/retrait : "), 0, 3, 1, 1, GridBagHelper.EAST);
    gb.add(hmin, 1, 3, 1, 1, GridBagHelper.WEST);
    restField = new GemField();
    gb.add(new GemLabel("Restant : "), 0, 4, 1, 1, GridBagHelper.EAST);
    gb.add(restField, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Account.label")), 0, 5, 1, 1, GridBagHelper.EAST);
    gb.add(account, 1, 5, 1, 1, GridBagHelper.WEST);
  }

  /**
   * Sets the card.
   *
   * @param c PersonSubscriptionCard (not null)
   */
  public void set(PersonSubscriptionCard c) {
    personCard = c;

    if (personCard.isNewCard()) {
      date.setDate(new Date());
    } else {
      rehearsalCardChoice.setKey(personCard.getPassId());
      date.setDate(personCard.getPurchaseDate());
      date.getStartField().setEditable(false);

    }
    account.setSelectedIndex(0);//XXX preference account
    setRest(0);
    displayRest();
  }

  /**
   * Gets a new card or updates actual card.
   *
   * @return PersonSubscriptionCard (not null)
   */
  public PersonSubscriptionCard get() {
    if (personCard.isNewCard()) {
      personCard.setPurchaseDate(date.get());
      personCard.setPassId(getRehearsalCard().getId());
    }
    personCard.setRest(rest);
    return personCard;
  }

  public RehearsalPass getRehearsalCard() {
    return (RehearsalPass) rehearsalCardChoice.getSelectedItem();
  }

  /**
   * Gets the duration of session (30 or 60 min).
   *
   * @return a number of minutes
   */
  public int getDuration() {
    return (Integer) durationChoice.getSelectedItem();
  }

  private void setDuration(int duration) {
    this.durationChoice.setSelectedItem(duration);
  }

  /**
   *
   * @return une DateFr
   */
  public DateFr getDate() {
    return date.getStartFr();
  }

  /**
   * Number of minutes to add or substract from the card.
   *
   * @return a number of minutes
   */
  public int getSessions() {
    Integer d = (Integer) sessions.getValue();
    return d * getDuration();
  }

  /**
   * Gets a representation string of remainder hours.
   *
   * @return a String
   */
  public String getFormatHour(int mins) {
    String signe = mins < 0 ? "-" : "";
    Hour h = new Hour(Math.abs(mins));
    return signe + h.toSimpleString();
  }

  /**
   * Updates spinner.
   *
   * @param s
   */
  public void setSessions(int s) {
    this.sessions.setValue(s);
  }

  /**
   * Modify the number of remainder minutes (if card not null).
   *
   * @param offset the duration to add or substract
   */
  public void setRest(int offset) {
    int dureeCarte = getRehearsalCard().getTotalTime();
    if (personCard.isNewCard()) {
      rest = dureeCarte - offset;
    } else {
      rest = personCard.getRest() - offset;
    }
    if (rest > dureeCarte) {
      rest = dureeCarte;
    } else if (rest < 0) {
      rest = 0;
    }

  }

  public Account getAccount() {
    return (Account) account.getSelectedItem();
//    return new Account(p);
  }

  public void clear() {
    setRest(0);
    sessions.setValue(0);
    hmin.setText("");
    setDuration(60);
  }

  /**
   * ChangeListener
   *
   * @param e
   */
  @Override
  public void stateChanged(ChangeEvent e) {
    if (e.getSource() == sessions) {
      updateRest();
    }
  }

  /**
   * ActionListener
   *
   * @param e
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == durationChoice && personCard != null) {
      updateRest();
    } else if (e.getSource() == rehearsalCardChoice) {
      reset();
    }
  }

  /**
   * Operations launched after adding or substracting the number of sessions.
   */
  private void updateRest() {
    int dc = getSessions();
    setRest(dc);
    String h = getFormatHour(dc);
    hmin.setText(h);
    displayRest();
  }

  private void displayRest() {
    Hour h = new Hour(rest);
    restField.setText(h.toSimpleString());
  }

  private void reset() {
    clear();
    displayRest();
  }
}
