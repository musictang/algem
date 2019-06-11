/*
 * @(#)TestMemberEnrolment.java	2.9.3.2 10/03/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
import net.algem.TestProperties;
import net.algem.accounting.AccountUtil;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.util.DataConnection;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.3.2
 */
public class TestMemberEnrolment {

  private DataConnection dc;
  private EnrolmentOrderUtil util;
  
  public TestMemberEnrolment() {

  }

  @Before
  public void setUp() throws Exception {
    dc = TestProperties.getDataConnection();
    util = new EnrolmentOrderUtil(null, dc);
  }
  
  @After
  public void tearDown() {
  }

  @Test
  public void testCourseOrder() {
    int estab1 = 3501;
    int estab2 = 3502;
    
    CourseOrder co = new CourseOrder();
    co.setEstab(estab1);
    
    CourseOrder co2 = new CourseOrder();
    co2.setEstab(estab2);
    
    assertFalse(co.getEstab() == co2.getEstab());
    co2 = co;
    assertTrue(co.getEstab() == co2.getEstab());
    assertTrue(co2.getEstab() == estab1);
    
    co2.setEstab(estab2);
    assertTrue(co.getEstab() == co2.getEstab());
    assertTrue(co.getEstab() == estab2);
  }


  @Test
  public void testOrderQuarterDates()
  {

    DateFr startDateOrder = new DateFr("09-09-2008");
    DateFr endDateOrder = new DateFr("14-12-2008");
    Vector<DateFr> dates = util.getQuarterPaymentDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 09-09-2008", 1 == dates.size());
    assertEquals("09-09-2008 1","15-10-2008", dates.elementAt(0).toString());

    dates.clear();
    /*********************/
    startDateOrder = new DateFr("15-12-2008");
    endDateOrder = new DateFr("28-06-2009");
    dates = util.getQuarterPaymentDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 15-12-2008", 2 == dates.size());
    assertEquals("15-12-2008 1", "15-01-2009", dates.elementAt(0).toString());
    assertEquals("15-12-2008 1", "15-04-2009", dates.elementAt(1).toString());
    dates.clear();
     /*********************/
    startDateOrder = new DateFr("02-02-2009");
    endDateOrder = new DateFr("28-06-2009");
    dates = util.getQuarterPaymentDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 02-02-2009", 2 == dates.size());
    assertEquals("02-02-2009 1", "15-02-2009", dates.elementAt(0).toString());
    assertEquals("02-02-2009 2", "15-04-2009", dates.elementAt(1).toString());
    dates.clear();

     /*********************/
    startDateOrder = new DateFr("20-09-2008");
    endDateOrder = new DateFr("28-06-2009");
    dates = util.getQuarterPaymentDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 20-09-2008", 3 == dates.size());
    assertEquals("20-09-2008 1","15-10-2008", dates.elementAt(0).toString());
    assertEquals("20-09-2008 2", "15-01-2009", dates.elementAt(1).toString());
    assertEquals("20-09-2008 3", "15-04-2009", dates.elementAt(2).toString());
    dates.clear();

     /*********************/
    startDateOrder = new DateFr("05-01-2009");
    endDateOrder = new DateFr("28-06-2009");
    dates = util.getQuarterPaymentDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 05-01-2009", 2 == dates.size());
    assertEquals("05-01-2009 1", "15-01-2009", dates.elementAt(0).toString());
    assertEquals("05-01-2009 2", "15-04-2009", dates.elementAt(1).toString());
    dates.clear();

      /*********************/
    startDateOrder = new DateFr("12-01-2009");
    endDateOrder = new DateFr("26-03-2009");
    dates = util.getQuarterPaymentDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 05-01-2009", 1 == dates.size());
    assertEquals("05-01-2009 1","15-02-2009", dates.elementAt(0).toString());

    dates.clear();

     /*********************/
    startDateOrder = new DateFr("11-04-2009");
    endDateOrder = new DateFr("28-06-2009");
    dates = util.getQuarterPaymentDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 10-04-2009", 1 == dates.size());
    assertEquals("10-04-2009 1", "15-05-2009", dates.elementAt(0).toString());

    dates.clear();

     /*********************/
    startDateOrder = new DateFr("11-06-2009");
    endDateOrder = new DateFr("28-06-2009");
    dates = util.getQuarterPaymentDates(startDateOrder, endDateOrder);
    assertTrue("not null", dates != null);
    assertTrue("nombre d'échéances erronné 11-06-2009", 1 == dates.size());
    assertEquals("11-06-2009 1", "15-07-2009", dates.elementAt(0).toString());

    dates.clear();
  }

  @Test
  public void testIncMonth()
  {
    DateFr df = new DateFr("15-12-2008");
    df.incMonth(1);
    assertTrue("mois janvier ??", 1 == df.getMonth());
    assertTrue("annee 2009 ??", 2009 == df.getYear());

  }

  @Test
  public void testCalcNumberOfMonths() {
    int expected = 8;
    int n = 0;
    //int n = EnrolmentOrderUtil.calcNumberOfMonths(10,6);//mois début, mois fin
    n = EnrolmentOrderUtil.calcNumberOfMonths(new DateFr("15-10-2010"), new DateFr("02-06-2011"));
    assertTrue("Attendu : "+expected+" retourné : "+n, expected == n);

    expected = 2;
    //n = EnrolmentOrderUtil.calcNumberOfMonths(10,12);
    n = EnrolmentOrderUtil.calcNumberOfMonths(new DateFr("15-10-2010"), new DateFr("02-12-2010"));
    assertTrue("Attendu : "+expected+" retourné : "+n, expected == n);

    expected = 4;
    //n = EnrolmentOrderUtil.calcNumberOfMonths(12,3);
    n = EnrolmentOrderUtil.calcNumberOfMonths(new DateFr("10-12-2010"), new DateFr("12-03-2011"));
    assertTrue("Attendu : "+expected+" retourné : "+n, expected == n);

    expected = 5;
    //n = EnrolmentOrderUtil.calcNumberOfMonths(2,6);// inversion des mois
    n = EnrolmentOrderUtil.calcNumberOfMonths(new DateFr("10-02-2011"), new DateFr("30-06-2011"));
    assertTrue("Attendu : "+expected+" retourné : "+n, expected == n);
    
    expected = 1;
    n = EnrolmentOrderUtil.calcNumberOfMonths(new DateFr("15-07-2011"), new DateFr("02-07-2011"));//mois début, mois fin
    assertTrue("Attendu : "+expected+" retourné : "+n, expected == n);
  }

  @Test
  public void testOrderMonthDates() {
    int expected = 9;
    Vector<DateFr> dates = util.getMonthPaymentDates(new DateFr("12-09-2008"), new DateFr("28-06-2009"));
    assertTrue("vecteur null ??", null != dates);
    assertTrue("Attendu : "+expected+", reçu "+dates.size(), expected == dates.size());
    assertEquals("date premiere echeance ??", "15-10-2008", dates.elementAt(0).toString());
    assertEquals("date derniere echeance ??", "15-06-2009", dates.elementAt(dates.size()-1).toString());
    dates.clear();
    /*--------------------------------------------------------------------------------------*/
    expected = 7;
    dates = util.getMonthPaymentDates(new DateFr("09-12-2008"), new DateFr("28-06-2009"));
    assertTrue("Attendu : "+expected+", reçu "+dates.size(), 7 == dates.size());
    assertEquals("date premiere echeance ??", "15-12-2008", dates.elementAt(0).toString());
    assertEquals("date derniere echeance ??", "15-06-2009", dates.elementAt(dates.size()-1).toString());
    dates.clear();
    /*--------------------------------------------------------------------------------------*/
    expected = 2;
    dates = util.getMonthPaymentDates(new DateFr("11-01-2009"), new DateFr("29-03-2009"));
    assertTrue("Attendu : "+expected+", reçu "+dates.size(), 2 == dates.size());
    assertEquals("date premiere echeance ??", "15-02-2009", dates.elementAt(0).toString());
    assertEquals("date derniere echeance ??", "15-03-2009", dates.elementAt(dates.size()-1).toString());
    dates.clear();
    /*--------------------------------------------------------------------------------------*/
    expected = 1;
    dates = util.getMonthPaymentDates(new DateFr("20-05-2009"), new DateFr("28-06-2009"));
    assertTrue("Attendu : "+expected+", reçu "+dates.size(), 1 == dates.size());
    assertEquals("date premiere echeance ??", "15-06-2009", dates.elementAt(0).toString());
    //assertEquals("date derniere echeance ??", "15-07-2009", dates.elementAt(dates.size()-1).toString());
    dates.clear();
    /*--------------------------------------------------------------------------------------*/
    expected = 6;
    dates = util.getMonthPaymentDates(new DateFr("02-09-2008"), new DateFr("29-03-2009"));
    assertTrue("Attendu : "+expected+", reçu "+dates.size(), 6 == dates.size());
    assertEquals("date premiere echeance ??", "15-10-2008", dates.elementAt(0).toString());
    assertEquals("date derniere echeance ??", "15-03-2009", dates.elementAt(dates.size()-1).toString());
    dates.clear();

    dates = util.getMonthPaymentDates(new DateFr("28-06-2011"), new DateFr("02-07-2011"));
    expected = 1;
    assertTrue("Attendu : "+expected+", reçu "+dates.size(), expected == dates.size());
    assertEquals("date premiere echeance ??", "15-07-2011", dates.elementAt(0).toString());
    assertEquals("date derniere echeance ??", "15-07-2011", dates.elementAt(dates.size()-1).toString());
    dates.clear();
  }


  @Test
  public void testCalcFirstOrderLineAmount()
  {

    double montant = EnrolmentOrderUtil.calcFirstOrderLineAmount(242.50, 33, 3,"TRIM");

    double expected = 242.50;
    assertTrue("expected = "+expected+", res="+montant, expected == montant);

    montant = EnrolmentOrderUtil.calcFirstOrderLineAmount(242.50, 20, 2,"TRIM");
    expected = AccountUtil.round(198.40909100);
    assertTrue("expected = "+expected+", res="+montant, expected == AccountUtil.round(montant));

    montant = EnrolmentOrderUtil.calcFirstOrderLineAmount(125.0, 8, 3,"MOIS");
    expected = AccountUtil.round(22.72727272);
    assertTrue("expected = "+expected+", res="+montant, expected == AccountUtil.round(montant));

  }
  
  @Test
  public void testModuleTime() {
    int min = 105;
    double expected = 1.75;
    double time = Hour.minutesToDecimal(min);
    assertTrue("result = " + time, expected == time);
    min = 106;
    expected = 1.77;
    time = Hour.minutesToDecimal(min);
    assertTrue("result = " + time, expected == time);
    
    min = 107;
    expected = 1.78;
    time = Hour.minutesToDecimal(min);
    assertTrue("result = " + time, expected == time);
    
    min = 109;
    expected = 1.82;
    time = Hour.minutesToDecimal(min);
    assertTrue("result = " + time, expected == time);
    
    min = 30;
    expected = 0.5;
    time = Hour.minutesToDecimal(min);
    assertTrue("result = " + time, expected == time);
    
    min = 45;
    expected = 0.75;
    time = Hour.minutesToDecimal(min);
    assertTrue("result = " + time, expected == time);

    time = 0.75;
    min = Hour.decimalToMinutes(time);
    expected = 45;
    assertTrue("result = " + time, expected == min);
    
    time = 1.78;
    expected = 107;
    min = Hour.decimalToMinutes(time);
    assertTrue("result = " + time, expected == min);
  }
  
}
