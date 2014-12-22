/*
 * @(#)AccountUtil.java	2.9.2 19/12/14
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
package net.algem.accounting;

import java.awt.print.PrinterException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.JTable;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.config.Preference;
import net.algem.contact.PersonFile;
import net.algem.contact.member.Member;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;

/**
 * Utility class for orderline operations.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 * @since 2.0r
 */
public class AccountUtil {

  /**
   * First digit of personal account (compte d'attente).
   */
  public static final char PERSONAL_ACCOUNT_FIRST_DIGIT = '4';
  /**
   * First letter of customer account.
   */
  public static final String CUSTOMER_ACCOUNT_FIRST_LETTER = "C";
  /**
   * First digit of revenue account.
   */
  public static final char REVENUE_ACCOUNT_FIRST_DIGIT = '7';
  /**
   * Prefix for customer account.
   */
  public static final String CUSTOMER_ACCOUNT_PREFIX = "411";


  /**
   * Converts an amount to a positive integer.
   *
   * @param amount
   * @return an integer
   */
  public static int getIntValue(double amount) {
    return (int) (round(amount) * 100);
  }

  /**
   * Rounds a double.
   *
   * @param val
   * @return a double
   */
  public static double round(double val) {
    return Math.rint(val * 100) / 100;
    //return Math.floor(val*100 + 0.5) / 100;
  }

  /**
   * Sets an orderline for a single rehearsal.
   *
   * @param pf
   * @param amount
   * @return un OrderLine
   */
  public static OrderLine setRehearsalOrderLine(PersonFile pf, DateFr date, Preference pref, double amount) {

    OrderLine e = new OrderLine();
    e.setMember(pf.getId());
    int payer;
    Member member = pf.getMember();
    if (member != null && member.getPayer() > 0) {
      payer = member.getPayer();
    } else {
      payer = pf.getId();
    }
    e.setPayer(payer);
    e.setDate(new DateFr(new Date()));// current date may be different than schedule date
    e.setOrder(0);
    String s = ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey());
    e.setSchool(Integer.parseInt(s));
    e.setAccount(new Account((Integer) pref.getValues()[0]));
    e.setCostAccount(new Account((String) pref.getValues()[1]));
    e.setLabel("p" + payer + " a" + pf.getId() + " " + date.toString()); // register schedule date
    e.setCurrency("E");
    e.setDocument("");
    e.setPaid(false);
    e.setModeOfPayment(""); // aucun règlement par défaut
    e.setTransfered(false);
    e.setAmount(getIntValue(amount));

    return e;
  }

  public static OrderLine setGroupOrderLine(int group, PersonFile pf, DateFr date, Preference pref, double amount) {
    OrderLine ol = setRehearsalOrderLine(pf, date, pref, amount);
    ol.setGroup(group);
    return ol;
  }

  public static int getMemberShips(int m, DataConnection dc) throws SQLException {
    return OrderLineIO.countMemberShip(m, dc);
  }

  /**
   * Generates the complementary orderline for personal account.
   *
   * @param e first orderline
   * @return a second orderline
   */
  public static OrderLine createOrderLine(OrderLine e) {

    OrderLine n = new OrderLine(e);
    n.setPaid(false);
    n.setModeOfPayment(ModeOfPayment.CHQ.toString());// règlement par défaut before paiement

    e.setAmount(-(n.getAmount()));// montant à créditer
    e.setModeOfPayment(ModeOfPayment.FAC.toString());

    return n;
  }

  /**
   * Creates an orderline.
   * If the account is of type personal, a complementary orderline is also created.
   * As a precaution, orderlines are marked not transfered.
   *
   * @param e the order to save
   * @param dc data connection
   * @return a new payment order line
   * @throws SQLException
   */
  public static OrderLine createEntry(OrderLine e, DataConnection dc) throws SQLException {
    OrderLine p = null;
    e.setTransfered(false);
    if (isPersonalAccount(e.getAccount()) && !hasPersonalEntry(e, dc)) { // compte de classe 4
      p = createOrderLine(e);
      OrderLineIO.insert(e, dc);//première échéance à reglement FAC
      OrderLineIO.insert(p, dc);
    } else {
      OrderLineIO.insert(e, dc);
    }
    return p;
  }

  public static OrderLine createPersonalEntry(OrderLine ol, DataConnection dc) throws SQLException {

    ol.setTransfered(false);
    ol.setModeOfPayment(ModeOfPayment.FAC.toString());
    OrderLineIO.insert(ol, dc);
    return ol;
  }

  /**
   * Checks if an orderline exists in database.
   *
   * @param dc datacache
   * @param e orderline
   * @return true if this orderline exists
   */
  private static boolean hasPersonalEntry(OrderLine e, DataConnection dc) {
    OrderLine n = OrderLineIO.find(e, dc);
    return n != null;
  }

  /**
   * Specifies if the account {@literal c} is a personal account.
   *
   * Personal account begins by digit {@link AccountUtil#PERSONAL_ACCOUNT_FIRST_DIGIT}.
   * @param c account
   * @return true if personal account
   */
  public static boolean isPersonalAccount(Account c) {
    if (c == null || c.getNumber() == null || c.getNumber().isEmpty()) {
      return false;
    }
    char firstChar = c.getNumber().charAt(0);
    return firstChar == PERSONAL_ACCOUNT_FIRST_DIGIT
            || CUSTOMER_ACCOUNT_FIRST_LETTER.equalsIgnoreCase(String.valueOf(firstChar));
  }

  /**
   * Specifies if the account {@literal c} is a revenue account.
   * Revenue account begins by digit {@link AccountUtil#REVENUE_ACCOUNT_FIRST_DIGIT}.
   *
   * @param c account
   * @return true if revenue account
   */
  public static boolean isRevenueAccount(Account c) {
    if (c == null || c.getNumber() == null || c.getNumber().isEmpty()) {
      return false;
    }
    return c.getNumber().charAt(0) == REVENUE_ACCOUNT_FIRST_DIGIT;
  }

  /**
   * Specifies if the account {@literal c} is a customer account.
   *
   * @param c account
   * @return true if client
   */
  public static boolean isCustomerAccount(Account c) {
    if (c == null || c.getNumber() == null || c.getNumber().isEmpty()) {
      return false;
    }
    return c.getNumber().startsWith(CUSTOMER_ACCOUNT_PREFIX);
  }

  /**
   * Simple printing of a table with page number.
   *
   * @param table
   * @throws PrinterException
   * @since 2.2.p
   */
  public static void print(JTable table) throws PrinterException {
    MessageFormat footer = new MessageFormat("Page {0,number,integer}");
    table.print(JTable.PrintMode.FIT_WIDTH, null, footer);
  }

  /**
   * Retrieves the default format for displaying amount with currency.
   *
   * @return a numberformat
   */
  public static NumberFormat getDefaultCurrencyFormat() {

    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.FRANCE);
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);

    return nf;
  }

  /**
   * Retrieves the default format for displaying amounts.
   *
   * @return a numberformat
   */
  public static NumberFormat getDefaultNumberFormat() {

    NumberFormat nf = NumberFormat.getNumberInstance();
    //nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);

    return nf;
  }

}
