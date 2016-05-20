/*
 * @(#)EnrolmentOrderUtil.java	2.10.0 18/05/16
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import net.algem.accounting.*;
import net.algem.config.*;
import net.algem.contact.PersonFile;
import net.algem.course.Module;
import net.algem.course.ModuleIO;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.model.Model;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
 * @since 2.8.a 01/04/2013
 */
public class EnrolmentOrderUtil {

  private static int LAST_MONTH_DD = 6;
  private static int DEFAULT_DUE_DAY = Integer.parseInt(ConfigUtil.getConf(ConfigKey.DEFAULT_DUE_DAY.getKey()));
  private PersonFile dossier;
  private double total;
  private DataConnection dc;

  public EnrolmentOrderUtil(PersonFile dossier, DataConnection dc) {
    this.dossier = dossier;
    this.dc = dc;
  }

  public void setTotalOrderLine(double total) {
    this.total = total;
  }

  /**
   * Saves the order lines.
   * This depends on organization configuration.
   *
   * @param moduleOrder
   * @param schoolId
   * @return the number of saved order lines
   * @throws java.sql.SQLException
   * @throws net.algem.accounting.NullAccountException
   */
  public int saveOrderLines(ModuleOrder moduleOrder, int schoolId, boolean billing) throws SQLException, NullAccountException {

    String label = "p" + dossier.getMember().getPayer() + " a" + dossier.getId();

    Module mod = ((ModuleIO) DataCache.getDao(Model.Module)).findId(moduleOrder.getModule());
    // le payeur, le type de reglement et le numero d'inscription sont déjà récupérés par le constructeur
    OrderLine orderLine = new OrderLine(moduleOrder);
    //numero adherent
    orderLine.setMember(dossier.getId());
    //libelle
    orderLine.setLabel(label);
    //compte et analytique
    Account [] prefAccount = getPrefAccount(mod, dc);
    Account p = prefAccount[0];
    Account a = prefAccount[1];
    if (p != null && a != null) {
      orderLine.setAccount(p);
      orderLine.setCostAccount(a);
    } else {
      throw new NullAccountException(MessageUtil.getMessage("no.default.cost.account"));
    }

    orderLine.setSchool(schoolId);
    List<OrderLine> lines = getOrderLines(moduleOrder, orderLine);
    if (lines.size() == 1 && (PayFrequency.QUARTER.equals(moduleOrder.getPayment()) || PayFrequency.MONTH.equals(moduleOrder.getPayment()))){
      //montant total de l'échéance * nombre d'échéances par défaut
      lines.get(0).setAmount(AccountUtil.getIntValue(total * getDefaultPayFrequency(moduleOrder.getPayment())));
    }

    String c = ConfigUtil.getConf(ConfigKey.ROUND_FRACTIONAL_PAYMENTS.getKey());
    boolean rounded = c == null || c.isEmpty() ? false : c.toLowerCase().startsWith("t");
    int totalFraction = 0;
    for (int i = 0, len = lines.size(); i < len; i++) {
      OrderLine ol = lines.get(i);
      if (rounded) {
        int d = ol.getAmount() % 100;
        if (i < len - 1) {
          ol.setAmount(ol.getAmount() - d);
          totalFraction += d;
        } else {
          ol.setAmount(ol.getAmount() + totalFraction);
        }
      }
      AccountUtil.createEntry(ol, false, dc);
    }
    if (lines.size() > 0 && (billing || AccountUtil.isPersonalAccount(lines.get(0).getAccount()))) {
      int totalBilling = 0;
      for (OrderLine o: lines) {
        totalBilling += o.getAmount();
      }
      OrderLine b = lines.get(0);
      b.setAmount(-totalBilling);
      b.setPaid(true);
      b.setModeOfPayment(ModeOfPayment.FAC.toString());
      AccountUtil.createEntry(b, false, dc);
    }
    return lines.size();
  }

  /**
   * Creates a list of order lines.
   * @param mo
   * @param e a single order line
   * @return the size of order lines collection
   * @throws SQLException
   */
  List<OrderLine> getOrderLines(ModuleOrder mo, OrderLine e) throws SQLException {

    List<OrderLine> orderLines = new ArrayList<OrderLine>();
    if (ModeOfPayment.NUL.toString().equals(mo.getModeOfPayment())) {
      return orderLines;
    }
    if (PricingPeriod.NULL.equals(mo.getPricing()) || PricingPeriod.HOUR.equals(mo.getPricing()) || PayFrequency.YEAR.equals(mo.getPayment())) {
      e.setAmount(AccountUtil.getIntValue(total));
      DateFr de = mo.getStart();
      de.incMonth(1);
      //de.setDay(15); // TODO set day in config
      de.setDay(DEFAULT_DUE_DAY);
      e.setDate(de);
      //insertion echeances
      orderLines.add(e);
    } else {
      if (PayFrequency.QUARTER.equals(mo.getPayment())) {// echeance trimestrielle
        List<DateFr> dates = getQuarterPaymentDates(mo.getStart(), mo.getEnd());
        /*if (AccountUtil.isPersonalAccount(e.getAccount())) {
         addPersonalOrderLine(e, dates);
         setPaymentOrderLine(e, moduleOrder.getPayment());
         }*/
        orderLines = setQuarterOrderLines(mo, e, dates);
      } else if (PayFrequency.MONTH.equals(mo.getPayment())) {// echeance mensuelle
        List<DateFr> dates = getMonthPaymentDates(mo.getStart(), mo.getEnd());
        /*if (AccountUtil.isPersonalAccount(e.getAccount())) {
         addPersonalOrderLine(e, dates);
         setPaymentOrderLine(e, moduleOrder.getPayment());
         }*/
        orderLines = setMonthOrderLines(mo, e, dates);
      }
    }
    return orderLines;

  }

  public void saveStandardOrderLines(ModuleOrder mo) throws SQLException {
    AccountingService service = new AccountingService(dc);
    List<OrderLine> std = service.findStandardOrderLines();
    Map<Integer,List<OrderLine>> totalAccountMap = new HashMap<>();

    if (std.size() > 0) {
      for (OrderLine o : std) {
        if (service.exists(o)) {
          continue;
        }
        o.setMember(dossier.getId());
        o.setPayer(mo.getPayer());
        o.setDate(mo.getStart());
        o.setPaid(false);
        o.setTransfered(false);
        o.setOrder(mo.getIdOrder());
        List<OrderLine> ol = totalAccountMap.get(o.getAccount().getId());
        if (ol == null) {
          ol = new ArrayList<OrderLine>();
          totalAccountMap.put(o.getAccount().getId(), ol);
        }
        ol.add(o);
        AccountUtil.createEntry(o, false, dc);
      }
      String billing = ConfigUtil.getConf(ConfigKey.CHARGE_ENROLMENT_LINES.getKey());
      if (billing.toLowerCase().startsWith("t")) {
        for (Map.Entry<Integer,List<OrderLine>> entry : totalAccountMap.entrySet()) {
          int t = 0;
          for (OrderLine o : entry.getValue()) {
            t += o.getAmount();
          }
          OrderLine b = entry.getValue().get(0);
          b.setMember(dossier.getId());
          b.setPayer(mo.getPayer());
          b.setDate(mo.getStart());
          b.setPaid(true);
          b.setTransfered(false);
          b.setOrder(mo.getIdOrder());
          b.setModeOfPayment(ModeOfPayment.FAC.toString());
          b.setAmount(-t);
          AccountUtil.createEntry(b, false, dc);
        }
      }
    }
  }


  /**
   * Gets the date of the first payment.
   * Optionnaly updates the first month of payment.
   * @param orderDateStart
   * @return a date
   */
  private DateFr getFirstDateOfPayment(DateFr orderDateStart) {

    DateFr first = new DateFr(orderDateStart);
    //first.setDay(15);
    first.setDay(DEFAULT_DUE_DAY);
    // report to next month if first payment not in delay
    if (isFirstPaymentAfter(orderDateStart)) {
      first.incMonth(1);
    }
    return first;
  }

  /**
   * Checks if the first date of payment is after some day or month.
   * @param orderDateStart
   * @return true if after
   */
  private boolean isFirstPaymentAfter(DateFr orderDateStart) {
    // orig : orderDateStart.getDay()  > 10
    return (orderDateStart.getDay() > (DEFAULT_DUE_DAY - 5) || orderDateStart.getMonth() == 9);
  }

  /**
   *
   * @param e
   * @param dates
   * @throws SQLException
   * @deprecated
   */
  private void addPersonalOrderLine(OrderLine e, List<DateFr> dates) throws SQLException {
      e.setPaid(true);
      e.setAmount(-(total * dates.size()));
      e.setDate(dates.get(0));
      AccountUtil.createPersonalEntry(e, dc);
  }

  /**
   *
   * @param e
   * @param payment
   * @throws SQLException
   * @deprecated
   */
  private void setPaymentOrderLine(OrderLine e, String payment) throws SQLException {
    //payment line
    e.setAmount(total);
    e.setModeOfPayment(payment);
    e.setPaid(false);
    int pr = PersonalRevenueAccountIO.find(e.getAccount().getId(), dc);
    Account p = AccountIO.find(pr, dc);
    e.setAccount(p);
  }

  /**
   * Updates the module order with the actual number of due dates.
   * @param n number of due dates
   * @param mo module order
   * @throws SQLException
   */
  void updateModuleOrder(int n, ModuleOrder mo) throws SQLException {
    if (mo != null && n > 0) {
      mo.setNOrderLines(n);
      ModuleOrderIO.update(mo, dc);
    }
  }

  /**
   * Gets the number of payments for the rate period {@code p}.
   * @param p payment frequency
   * @return an integer
   */
  private int getDefaultPayFrequency(PayFrequency p) {
    switch (p) {
      case QUARTER:
        return 3;
      case MONTH:
        return 9;
      case YEAR:
        return 1;
      case SEMESTER:
        return 2;
      default:
        return 0;
    }
  }


  /**
   * Gets the preferred accounts.
   * @param m module instance
   * @param dc dataConnection
   * @return an array of 2 elements
   * @throws SQLException
   */
  private Account[] getPrefAccount(Module m, DataConnection dc) throws SQLException {
     int key = 0;
     String analytics = "";
    if (m.isLeisure()) {
      Preference p = AccountPrefIO.find(AccountPrefIO.LEISURE, dc);
      key = (Integer) p.getValues()[0];
      analytics = (String) p.getValues()[1];
    } else if (m.isProfessional()) {
      Preference p = AccountPrefIO.find(AccountPrefIO.PRO, dc);
      key = (Integer) p.getValues()[0];
      analytics = (String) p.getValues()[1];
    }
    Account p = AccountIO.find(key, dc);
    Param a = ParamTableIO.findByKey(CostAccountCtrl.tableName, CostAccountCtrl.columnKey, analytics, dc);

    return new Account[] {p, new Account(a)};
  }

  /**
   * Gets a list of dates for quarterly payment.
   *
   * @param orderDateStart
   * @param orderDateEnd
   * @return a list of dates
   */
  public Vector<DateFr> getQuarterPaymentDates(DateFr orderDateStart, DateFr orderDateEnd) {

    Vector<DateFr> dates = new Vector<DateFr>();

    int nbMonths = 0;
    int nbOrderLines = 0;

    DateFr firstOrderDate = getFirstDateOfPayment(orderDateStart);

    int orderStartMonth = orderDateStart.getMonth();

    dates.add(new DateFr(firstOrderDate));
    if (isFirstPaymentAfter(orderDateStart) && orderStartMonth != 9 && orderStartMonth != 12 && orderStartMonth != 3) {
      nbMonths = calcNumberOfMonths(orderDateStart, orderDateEnd);
    } else {
      nbMonths = calcNumberOfMonths(firstOrderDate, orderDateEnd);// first payment
    }
    if (nbMonths <= 3) {
      nbOrderLines = 1;
    } else if (nbMonths <= 6) {
      nbOrderLines = 2;
    } else {
      nbOrderLines = 3;
    }
    //System.out.println("nombre echeances trimestre: " + nbOrderLines);
    // ajout des échéances
    for (int i = 1; i < nbOrderLines; i++) {
      switch (firstOrderDate.getMonth()) {
        case 10:
        case 1:
        case 4:
          firstOrderDate.incMonth(3);
          break;
        case 11:
        case 2:
          firstOrderDate.incMonth(2);
          break;
        case 12:
        case 3:
          firstOrderDate.incMonth(1);
          break;
      }
      dates.add(new DateFr(firstOrderDate));
    }
    return dates;
  }

  /**
   * Gets a list of order lines for quarter dues.
   * @param moduleOrder
   * @param e single order line
   * @return a list of order lines
   */
  ArrayList<OrderLine> setQuarterOrderLines(ModuleOrder moduleOrder, OrderLine e, List<DateFr> dates) {
    ArrayList<OrderLine> orderLines = new ArrayList<OrderLine>();
//    Vector<DateFr> dates = getQuarterPaymentDates(moduleOrder.getStart(), moduleOrder.getEnd());
    int firstDocumentNumber = 0;
    try {
      firstDocumentNumber = Integer.parseInt(ConfigUtil.getConf(ConfigKey.ACCOUNTING_DOCUMENT_NUMBER.getKey()));
    } catch (NumberFormatException nfe) {
      System.err.println(getClass().getName() + "#setQuarterOrderLines " + nfe.getMessage());
    }

    int documentNumber = firstDocumentNumber;

    // DESACTIVATION CALCUL PRORATA
    // double montantPremiereEcheance = calcFirstOrderLineAmount(totalBase, maxCours, nombreEcheances, "TRIM");
    // e.setAmount(AccountUtil.getIntValue(montantPremiereEcheance));
    e.setAmount(AccountUtil.getIntValue(total));
    e.setDate((DateFr) dates.get(0));
    if (moduleOrder.getModeOfPayment().equals("PRL")) {
      documentNumber = calcDocumentNumber(firstDocumentNumber, ((DateFr) dates.get(0)).getMonth());
      e.setDocument("PRL" + String.valueOf(documentNumber));
    } else {
      e.setDocument(moduleOrder.getModeOfPayment() + 1);// pas nécessaire
    }
    orderLines.add(new OrderLine(e));// first
    for (int i = 1; i < dates.size(); i++) {
      //libelle numero piece
      if (moduleOrder.getModeOfPayment().equals("PRL")) {
        documentNumber = calcDocumentNumber(firstDocumentNumber, ((DateFr) dates.get(i)).getMonth());
        e.setDocument("PRL" + String.valueOf(documentNumber));
      } else {
        e.setDocument(moduleOrder.getModeOfPayment() + (i + 1));
      }
      e.setDate((DateFr) dates.get(i));
      orderLines.add(new OrderLine(e)); // others
    }
    return orderLines;
  }

  /**
   * Gets a list of dates for monthly payment.
   * @param startOrderDate
   * @param endOrderDate
   * @return a list of dates
   */
  Vector<DateFr> getMonthPaymentDates(DateFr startOrderDate, DateFr endOrderDate) {

    Vector<DateFr> dates = new Vector<DateFr>();

    DateFr firstOrderLine = getFirstDateOfPayment(startOrderDate);
    dates.add(new DateFr(firstOrderLine));

    int n = calcNumberOfMonths(firstOrderLine, endOrderDate);
    for (int i = 1; i < n; i++) {
      firstOrderLine.incMonth(1);
      dates.add(new DateFr(firstOrderLine));
    }

    return dates;
  }

   /**
   * Gets a list of order lines for month dues.
   * @param moduleOrder
   * @param e
   * @return a list of order lines
   */
  ArrayList<OrderLine> setMonthOrderLines(ModuleOrder moduleOrder, OrderLine e, List<DateFr> dates) {
    ArrayList<OrderLine> orderLines = new ArrayList<OrderLine>();
//    Vector<DateFr> orderDates = getMonthPaymentDates(moduleOrder.getStart(), moduleOrder.getEnd());
    int firstDocumentNumber = 0;
    try {
      firstDocumentNumber = Integer.parseInt(ConfigUtil.getConf(ConfigKey.ACCOUNTING_DOCUMENT_NUMBER.getKey()));
    } catch (NumberFormatException ne) {
      System.err.println("Format Numero.piece " + ne.getMessage());
    }
    int documentNumber = firstDocumentNumber;
    int orderLinesNumber = 0;
    if (dates != null) {
      orderLinesNumber = dates.size();
    }
    // DESACTIVATION CALCUL PRORATA
//		double montantPremiereEcheance = calcFirstOrderLineAmount(totalBase, maxCours, nombreEcheances, "MOIS");
//		e.setAmount(AccountUtil.getIntValue(montantPremiereEcheance));
    e.setAmount(AccountUtil.getIntValue(total));
    e.setDate(dates.get(0));
    documentNumber = calcDocumentNumber(firstDocumentNumber, ((DateFr) dates.get(0)).getMonth());
    if ("PRL".equals(moduleOrder.getModeOfPayment())) {
      e.setDocument("PRL" + String.valueOf(documentNumber));
    } else {
      e.setDocument(moduleOrder.getModeOfPayment() + 1);
    }
    orderLines.add(new OrderLine(e));

    for (int i = 1; i < orderLinesNumber; i++) {
      e.setDate((DateFr) dates.get(i));
      if ("PRL".equals(moduleOrder.getModeOfPayment())) {
        documentNumber = calcDocumentNumber(firstDocumentNumber, ((DateFr) dates.get(i)).getMonth());
        e.setDocument("PRL" + String.valueOf(documentNumber));
      } else {
        e.setDocument(moduleOrder.getModeOfPayment() + (i + 1));
      }
      orderLines.add(new OrderLine(e));
    }
    return orderLines;
  }

  /**
   * Calculates the amount of the first date due.
   * @param total amount for the period
   * @param maxSessions max sessions
   * @param nbOrderLines number of dues
   * @param type periodicity
   * @return a double amount
   */
  public static double calcFirstOrderLineAmount(double total, int maxSessions, int nbOrderLines, String type) {

    double fistAmount = total;
    double sessionPrice = 0.0;

    if (type.equals("TRIM")) {
      sessionPrice = total / 11; // 11 séances par trimestre
    } else {
      sessionPrice = ((total * 3) / 11);
    }

    if (nbOrderLines > 0) {
      if (maxSessions > 0) {
        // au prorata du nombre de commandes_cours effectifs
        // (prixSeance * maxCours) supposé supérieur à (total * (necheances - 1)
        fistAmount = (sessionPrice * maxSessions) - (total * (nbOrderLines - 1));
      }
    } else {
      fistAmount = 0.0;
    }

    return fistAmount;
  }

  /**
   * Calculates an intervall between two months.
   *
   * @param monthStart
   * @param monthEnd
   * @return an integer
   */
  public static int calcNumberOfMonths(int monthStart, int monthEnd) {
    if (monthEnd > LAST_MONTH_DD && monthEnd < 9) {
      monthEnd = LAST_MONTH_DD;
    }
    if (monthStart > monthEnd) {
      return monthEnd + (13 - monthStart);
    } else {
      return Math.abs((monthEnd - monthStart)) + 1;
    }
  }

  /**
   * Calculates a number of months between 2 dates.
   * @param start start due date
   * @param end end due date
   * @return an integer
   */
  public static int calcNumberOfMonths(DateFr start, DateFr end) {
    DateFr d = new DateFr(start);
    DateFr f = new DateFr(end);
    if (d.getMonth() == f.getMonth()) {
      return 1;
    }
    int m = 0;
    while (d.compareTo(f) <= 0) {
      m++;
      d.incMonth(1);
    }
    return m;
  }

  /**
   * Calculates the document number for debiting.
   *
   * @param month first month
   * @return an integer
   */
  private static int calcDocumentNumber(int num, int month) {
    int number = num;
    if (month >= 9 && month <= 12) {
      return number + (month - 10);
    } else {
      return number + 2 + month;
    }
  }



}
