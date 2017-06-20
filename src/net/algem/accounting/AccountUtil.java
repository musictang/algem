/*
 * @(#)AccountUtil.java	2.14.0 20/06/17
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
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;

/**
 * Utility class for orderline operations.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
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
//    return (int) (round(amount) * 100); // bug arrondi
    return (int) Math.round(amount * 100);
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
   * @param pf person file
   * @param date schedule date
   * @param pref accounts preference
   * @param amount order amount
   * @param link link to this rehearsal (card id if subscription, scheduleId if not)
   * @return a single orderline
   */
  public static OrderLine setRehearsalOrderLine(PersonFile pf, DateFr date, Preference pref, double amount, int link) {

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
    //e.setDate(new DateFr(new Date()));// current date may be different than schedule date
    // Il est préférable que la date d'échéance corresponde à la date de répétition
    // La plupart du temps, l'échéance est encaissée le jour de la répétition
    e.setDate(date);
    e.setOrder(link);
    String s = ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey());
    e.setSchool(Integer.parseInt(s));
    e.setAccount(new Account((Integer) pref.getValues()[0]));
    e.setCostAccount(new Account((String) pref.getValues()[1]));
    String reservation = " (" + BundleUtil.getLabel("Reserved.label").substring(0,3).toLowerCase() + ". " + new DateFr(new Date()) + ")";
    e.setLabel("p" + payer + " a" + pf.getId() +  reservation);
    e.setCurrency("E");
    e.setDocument("");
    e.setPaid(false);
    e.setModeOfPayment(""); // aucun règlement par défaut
    e.setTransfered(false);
    e.setAmount(getIntValue(amount));

    return e;
  }

  public static OrderLine setGroupOrderLine(int group, PersonFile pf, DateFr date, Preference pref, double amount) {
    OrderLine ol = setRehearsalOrderLine(pf, date, pref, amount, 0);
    ol.setGroup(group);
    return ol;
  }

  public static int getMemberShips(int m, DataConnection dc) throws SQLException {
    return OrderLineIO.countMemberShip(m, dc);
  }

  /**
   * Generates counterpart orderline for personal account.
   *
   * @param e first orderline
   * @return a second orderline
   */
  public static OrderLine createOrderLine(OrderLine e) {

    OrderLine n = new OrderLine(e);
    n.setPaid(false);
    String mode = e.getModeOfPayment();

    if (!ModeOfPayment.FAC.toString().equals(mode)) {
      n.setModeOfPayment(mode);
    } else {
      n.setModeOfPayment(ModeOfPayment.CHQ.toString());// règlement par défaut
      n.setAmount(-(e.getAmount()));
      n.setTax(0.0f);
    }

    /*if (e.getAmount() > 0) {
      e.setAmount(-(n.getAmount()));// montant à créditer
      e.setModeOfPayment(ModeOfPayment.FAC.toString());
    }*/
    return n;
  }

  /**
   * Creates an orderline.
   * If the account is of type personal, a complementary orderline is also created.
   * As a precaution, orderlines are marked not transfered.
   *
   * @param e the order to save
   * @param counterpart also add counterpart
   * @param dc data connection
   * @return a new payment order line
   * @throws SQLException
   */
  public static OrderLine createEntry(final OrderLine e, boolean counterpart, final DataConnection dc) throws SQLException {
    e.setTransfered(false);
    if (ModeOfPayment.FAC.toString().equals(e.getModeOfPayment()) && counterpart && isPersonalAccount(e.getAccount()) && !existsEntry(e, dc)) { // compte de classe 4
      final OrderLine p = createOrderLine(e);
      try {
        return dc.withTransaction(new DataConnection.SQLRunnable<OrderLine>()
        {
          @Override
          public OrderLine run(DataConnection conn) throws Exception {
            OrderLineIO.insert(e, dc);//première échéance à reglement FAC
            OrderLineIO.insert(p, dc);
            return p;
          }
        });
      } catch (Exception ex) {
        throw new SQLException(ex.getMessage());
      }
      
    } else {
      OrderLineIO.insert(e, dc);
    }
    return null;
  }

  public static OrderLine createEntry(OrderLine e, DataConnection dc) throws SQLException {
    return createEntry(e, true, dc);
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
  private static boolean existsEntry(OrderLine e, DataConnection dc) {
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
//    NumberFormat nf = NumberFormat.get(Locale.getDefault());
    Locale l = Locale.forLanguageTag(ConfigUtil.getConf(ConfigKey.LANGUAGE_CODE.getKey()));
    NumberFormat nf = NumberFormat.getCurrencyInstance(l);
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

  /**
   * Gets a format depending on {@code minFraction} digits and {@code maxFraction} digits.
   * @param minFraction min fraction digits
   * @param maxFraction max fraction digits
   * @return a format
   */
  public static NumberFormat getNumberFormat(int minFraction, int maxFraction) {

    NumberFormat nf = NumberFormat.getNumberInstance();
    nf.setMinimumFractionDigits(minFraction);
    nf.setMaximumFractionDigits(maxFraction);

    return nf;
  }

}
