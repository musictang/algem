/*
 * @(#)AccountUtil.java	2.7.h 22/02/13
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
package net.algem.accounting;

import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.swing.JTable;
import net.algem.billing.Invoice;
import net.algem.billing.InvoiceItem;
import net.algem.billing.Quote;
import net.algem.config.Preference;
import net.algem.contact.PersonFile;
import net.algem.contact.member.Member;
import net.algem.planning.DateFr;
import net.algem.util.DataConnection;

/**
 * Utility class for orderline operations.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.h
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
  public static OrderLine setOrderLine(PersonFile pf, DateFr date, Preference pref, double amount) {

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
    e.setDate(date);
    e.setOrder(0);

    e.setAccount(new Account((Integer) pref.getValues()[0]));
    e.setCostAccount(new Account((String) pref.getValues()[1]));
    //e.setSchool("MT");//XXX on devrait pouvoir spécifier l'école
    e.setLabel("p" + payer + " a" + pf.getId() + " " + date.toString()); // on ajoute la date au libellé
    e.setCurrency("E");
    e.setDocument("");
    e.setPaid(false);
    e.setModeOfPayment(""); // aucun règlement par défaut
    e.setTransfered(false);
    e.setAmount(getIntValue(amount));

    return e;
  }

  public static int getMemberShips(int m, DataConnection dc) throws SQLException {
    return OrderLineIO.countMemberShip(m, dc);
  }

  /**
   *
   * @param b bank code
   * @param g branch code
   * @param d 5 first digits of the account
   * @param c 6 last digits of the account
   * @return
   */
  /*
   * public static boolean checkRibKey(int b, int g, int c) { //return 97 - ((89
   * * b + 15 * g + 76 * d + 3 * c) % 97) == 0;
   *
   * return false; }
   *
   *
   * public String accountTo(String numero) { char lettres [] = {'A','B',
   * 'C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
   * char chiffres [] =
   * {'1','2','3','4','5','6','7','8','9','1','2','3','4','5','6','7','8','9','2','3','4','5','6','7','8','9'};
   * char compte [] = numero.toCharArray(); for (int i = 0; i < compte.length;
   * i++) { for (int j = 0 ; j < lettres.length ; j++) { if (compte[i] ==
   * lettres[j]) { compte[i] = chiffres[j]; break; } } } return new
   * String(compte);
  }
   */
  /**
   * Checking BIC.
   * @link  http://fr.wikipedia.org/wiki/Relev%C3%A9_d%27identit%C3%A9_bancaire#Algorithme_de_v.C3.A9rification_en_Java
   *
   * @param bic on 23 digits
   * @return true if rib % 97 = 0
   */
  public static boolean isBicOk(String bic) {
    StringBuilder extendedRib = new StringBuilder(bic.length());
    for (char currentChar : bic.toCharArray()) {
      //Works on base 36 (26 lettres + 10 chiffres)
      int currentCharValue = Character.digit(currentChar, Character.MAX_RADIX);
      //Convert character to simple digit
      extendedRib.append(currentCharValue < 10 ? currentCharValue : (currentCharValue + (int) StrictMath.pow(2, (currentCharValue - 10) / 9)) % 10);
    }

    return new BigDecimal(extendedRib.toString()).remainder(new BigDecimal(97)).intValue() == 0;
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
   * As a precaution, orderlines are marked non tranferred.
   *
   * @param dc
   * @param e
   * @throws SQLException
   */
  public static OrderLine createEntry(OrderLine e, DataConnection dc) throws SQLException {
    OrderLine c = null;
    e.setTransfered(false);
    if (isPersonalAccount(e.getAccount()) && !hasPersonalEntry(e, dc)) { // compte de classe 4
      c = createOrderLine(e);
      OrderLineIO.insert(e, dc);//première échéance à reglement FAC
      OrderLineIO.insert(c, dc);
    } else {
      OrderLineIO.insert(e, dc);
    }
    return c;
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
   * Specifies if the account {@code c} is a personal account.
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
   * Specifies if the account {@code c} is a revenue account.
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
   * Specifies if the account {@code c} is a customer account.
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

  public static List<OrderLine> getInvoiceOrderLines(List<OrderLine> l, String n) {

    List<OrderLine> fl = new ArrayList<OrderLine>();

    for (OrderLine e : l) {
      if (e.getInvoice() != null && e.getInvoice().equals(n)) {
        fl.add(e);
      }
    }
    return fl;
  }

  /**
   * Adds orderlines and items to invoice when created from a selection.
   *
   * @param f invoice
   * @param orderLines
   */
  public static void setInvoiceOrderLines(Invoice f, Collection<OrderLine> orderLines) {
    int i = 0;
    for (OrderLine e : orderLines) {
      if (ModeOfPayment.FAC.toString().equals(e.getModeOfPayment())) {
        f.addItem(new InvoiceItem(e)); // un reglement "FAC" correspond à un item de facturation        
      } else {
        f.setDescription(e.getLabel());
      }
      f.addOrderLine(e); // on garde la trace des échéances sélectionnées
      if (i++ == 0) {
        // IMPORTANT : le payeur enregistré dans l'échéance est prioritaire
        // par rapport à celui de la fiche
        f.setPayer(e.getPayer());
        // on utilise le numéro d'adhérent enregistré dans l'échéance
        f.setMember(e.getMember());
      }
    }
    
  }

  public static void setQuoteOrderLines(Quote d, Collection<OrderLine> orderLines) {
    if (orderLines != null) {
      int i = 0;
      for (OrderLine e : orderLines) {
        d.addItem(new InvoiceItem(e));
        if (i++ == 0) {
          d.setPayer(e.getPayer());
          d.setMember(e.getMember());
        }
      }
    }
  }
}
