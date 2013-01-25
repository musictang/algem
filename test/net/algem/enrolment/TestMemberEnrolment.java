/*
 * @(#)TestMemberEnrolment.java	2.6.a 08/10/12
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
package net.algem.enrolment;

import java.util.Vector;
import junit.framework.TestCase;
import net.algem.accounting.AccountUtil;
import net.algem.planning.DateFr;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TestMemberEnrolment extends TestCase {

  public TestMemberEnrolment(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testOrderQuarterDates()
  {

    DateFr startDateOrder = new DateFr("09-09-2008");
    DateFr endDateOrder = new DateFr("14-12-2008");
    Vector dates = MemberEnrolment.getOrderQuarterDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 09-09-2008", 1 == dates.size());
    assertEquals("09-09-2008 1","15-10-2008", dates.elementAt(0).toString());

    dates.clear();
    /*********************/
    startDateOrder = new DateFr("15-12-2008");
    endDateOrder = new DateFr("28-06-2009");
    dates = MemberEnrolment.getOrderQuarterDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 15-12-2008", 2 == dates.size());
    assertEquals("15-12-2008 1", "15-01-2009", dates.elementAt(0).toString());
    assertEquals("15-12-2008 1", "15-04-2009", dates.elementAt(1).toString());
    dates.clear();
     /*********************/
    startDateOrder = new DateFr("02-02-2009");
    endDateOrder = new DateFr("28-06-2009");
    dates = MemberEnrolment.getOrderQuarterDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 02-02-2009", 2 == dates.size());
    assertEquals("02-02-2009 1", "15-02-2009", dates.elementAt(0).toString());
    assertEquals("02-02-2009 2", "15-04-2009", dates.elementAt(1).toString());
    dates.clear();

     /*********************/
    startDateOrder = new DateFr("20-09-2008");
    endDateOrder = new DateFr("28-06-2009");
    dates = MemberEnrolment.getOrderQuarterDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 20-09-2008", 3 == dates.size());
    assertEquals("20-09-2008 1","15-10-2008", dates.elementAt(0).toString());
    assertEquals("20-09-2008 2", "15-01-2009", dates.elementAt(1).toString());
    assertEquals("20-09-2008 3", "15-04-2009", dates.elementAt(2).toString());
    dates.clear();

     /*********************/
    startDateOrder = new DateFr("05-01-2009");
    endDateOrder = new DateFr("28-06-2009");
    dates = MemberEnrolment.getOrderQuarterDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 05-01-2009", 2 == dates.size());
    assertEquals("05-01-2009 1", "15-01-2009", dates.elementAt(0).toString());
    assertEquals("05-01-2009 2", "15-04-2009", dates.elementAt(1).toString());
    dates.clear();

      /*********************/
    startDateOrder = new DateFr("12-01-2009");
    endDateOrder = new DateFr("26-03-2009");
    dates = MemberEnrolment.getOrderQuarterDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 05-01-2009", 1 == dates.size());
    assertEquals("05-01-2009 1","15-02-2009", dates.elementAt(0).toString());

    dates.clear();

     /*********************/
    startDateOrder = new DateFr("11-04-2009");
    endDateOrder = new DateFr("28-06-2009");
    dates = MemberEnrolment.getOrderQuarterDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 10-04-2009", 1 == dates.size());
    assertEquals("10-04-2009 1", "15-05-2009", dates.elementAt(0).toString());

    dates.clear();

     /*********************/
    startDateOrder = new DateFr("11-06-2009");
    endDateOrder = new DateFr("28-06-2009");
    dates = MemberEnrolment.getOrderQuarterDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 11-06-2009", 1 == dates.size());
    assertEquals("11-06-2009 1", "15-07-2009", dates.elementAt(0).toString());

    dates.clear();
  }

  public void testIncMonth()
  {
    DateFr df = new DateFr("15-12-2008");
    df.incMonth(1);
    assertTrue("mois janvier ??", 1 == df.getMonth());
    assertTrue("annee 2009 ??", 2009 == df.getYear());

  }

  public void testCalcNumberOfMonths() {
    int expected = 8;
    int n = 0;
    //int n = MemberEnrolment.calcNumberOfMonths(10,6);//mois début, mois fin
    n = MemberEnrolment.calcNumberOfMonths(new DateFr("15-10-2010"), new DateFr("02-06-2011"));
    assertTrue("Attendu : "+expected+" retourné : "+n, expected == n);

    expected = 2;
    //n = MemberEnrolment.calcNumberOfMonths(10,12);
    n = MemberEnrolment.calcNumberOfMonths(new DateFr("15-10-2010"), new DateFr("02-12-2010"));
    assertTrue("Attendu : "+expected+" retourné : "+n, expected == n);

    expected = 4;
    //n = MemberEnrolment.calcNumberOfMonths(12,3);
    n = MemberEnrolment.calcNumberOfMonths(new DateFr("10-12-2010"), new DateFr("12-03-2011"));
    assertTrue("Attendu : "+expected+" retourné : "+n, expected == n);

    expected = 5;
    //n = MemberEnrolment.calcNumberOfMonths(2,6);// inversion des mois
    n = MemberEnrolment.calcNumberOfMonths(new DateFr("10-02-2011"), new DateFr("30-06-2011"));
    assertTrue("Attendu : "+expected+" retourné : "+n, expected == n);
    
    expected = 1;
    n = MemberEnrolment.calcNumberOfMonths(new DateFr("15-07-2011"), new DateFr("02-07-2011"));//mois début, mois fin
    assertTrue("Attendu : "+expected+" retourné : "+n, expected == n);
  }

  public void testOrderMonthDates() {
    int expected = 9;
    Vector<DateFr> dates = MemberEnrolment.getOrderMonthDates(new DateFr("12-09-2008"), new DateFr("28-06-2009"));
    assertTrue("vecteur null ??", null != dates);
    assertTrue("Attendu : "+expected+", reçu "+dates.size(), expected == dates.size());
    assertEquals("date premiere echeance ??", "15-10-2008", dates.elementAt(0).toString());
    assertEquals("date derniere echeance ??", "15-06-2009", dates.elementAt(dates.size()-1).toString());
    dates.clear();
    /*--------------------------------------------------------------------------------------*/
    expected = 7;
    dates = MemberEnrolment.getOrderMonthDates(new DateFr("09-12-2008"), new DateFr("28-06-2009"));
    assertTrue("Attendu : "+expected+", reçu "+dates.size(), 7 == dates.size());
    assertEquals("date premiere echeance ??", "15-12-2008", dates.elementAt(0).toString());
    assertEquals("date derniere echeance ??", "15-06-2009", dates.elementAt(dates.size()-1).toString());
    dates.clear();
    /*--------------------------------------------------------------------------------------*/
    expected = 2;
    dates = MemberEnrolment.getOrderMonthDates(new DateFr("11-01-2009"), new DateFr("29-03-2009"));
    assertTrue("Attendu : "+expected+", reçu "+dates.size(), 2 == dates.size());
    assertEquals("date premiere echeance ??", "15-02-2009", dates.elementAt(0).toString());
    assertEquals("date derniere echeance ??", "15-03-2009", dates.elementAt(dates.size()-1).toString());
    dates.clear();
    /*--------------------------------------------------------------------------------------*/
    expected = 1;
    dates = MemberEnrolment.getOrderMonthDates(new DateFr("20-05-2009"), new DateFr("28-06-2009"));
    assertTrue("Attendu : "+expected+", reçu "+dates.size(), 1 == dates.size());
    assertEquals("date premiere echeance ??", "15-06-2009", dates.elementAt(0).toString());
    //assertEquals("date derniere echeance ??", "15-07-2009", dates.elementAt(dates.size()-1).toString());
    dates.clear();
    /*--------------------------------------------------------------------------------------*/
    expected = 6;
    dates = MemberEnrolment.getOrderMonthDates(new DateFr("02-09-2008"), new DateFr("29-03-2009"));
    assertTrue("Attendu : "+expected+", reçu "+dates.size(), 6 == dates.size());
    assertEquals("date premiere echeance ??", "15-10-2008", dates.elementAt(0).toString());
    assertEquals("date derniere echeance ??", "15-03-2009", dates.elementAt(dates.size()-1).toString());
    dates.clear();

    dates = MemberEnrolment.getOrderMonthDates(new DateFr("28-06-2011"), new DateFr("02-07-2011"));
    expected = 1;
    assertTrue("Attendu : "+expected+", reçu "+dates.size(), expected == dates.size());
    assertEquals("date premiere echeance ??", "15-07-2011", dates.elementAt(0).toString());
    assertEquals("date derniere echeance ??", "15-07-2011", dates.elementAt(dates.size()-1).toString());
    dates.clear();
  }

  public void testCalcFirstOrderLineAmount()
  {

    double montant = MemberEnrolment.calcFirstOrderLineAmount(242.50, 33, 3,"TRIM");

    double expected = 242.50;
    assertTrue("expected = "+expected+", res="+montant, expected == montant);

    montant = MemberEnrolment.calcFirstOrderLineAmount(242.50, 20, 2,"TRIM");
    expected = AccountUtil.round(198.40909100);
    assertTrue("expected = "+expected+", res="+montant, expected == AccountUtil.round(montant));

    montant = MemberEnrolment.calcFirstOrderLineAmount(125.0, 8, 3,"MOIS");
    expected = AccountUtil.round(22.72727272);
    assertTrue("expected = "+expected+", res="+montant, expected == AccountUtil.round(montant));

  }
}
