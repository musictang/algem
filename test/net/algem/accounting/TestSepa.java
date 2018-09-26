/*
 * @(#)TestSepa.java	2.15.9 04/06/18
 *
 * Copyright (c) 1999-2018 Musiques Tangentes. All Rights Reserved.
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import net.algem.TestProperties;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.TextUtil;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * TODO : redefine tests
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.9
 * @since 2.8.r 24/12/13
 */
public class TestSepa
{



  @Ignore
  public void testRum() {
    int id1 = 1234;
    int id2 = 1234;
    int id3 = 1234;
    int id4 = 123;
    int id5 = 12345;

    DateFormat df = new SimpleDateFormat("ddMMyy");
    DateFr d = new DateFr("15-09-2013");

    String rumid1 = generateRum(id1, new DateFr(), df);
    String rumid2 = generateRum(id2, null, df);
    String rumid3 = generateRum(id3, new DateFr("16-09-2013"), df);
    String rumid4 = generateRum(id4, new DateFr("16-09-2000"), df);
    String rumid5 = generateRum(id5, new DateFr("16-09-2027"), df);
//		String rumid4 = "M" + id3 + " " + df.format(d.getTime()) + " " + Integer.MAX_VALUE;

    System.out.println(rumid1);
    System.out.println(rumid2);
    System.out.println(rumid3);
    System.out.println(rumid4);
    System.out.println(rumid5);
    assertFalse(rumid1.equals(rumid2));
    assertFalse(rumid1.equals(rumid3));
    assertFalse(rumid2.equals(rumid3));
    assertFalse(rumid1.equals(rumid4) && rumid2.equals(rumid4) && rumid3.equals(rumid4));
    assertFalse(rumid1.equals(rumid5) && rumid2.equals(rumid5) && rumid3.equals(rumid5) && rumid4.equals(rumid5));
  }

  /**
   * Generates Reference Unique of Mandat.
   *
   * @param idper person id
   * @param date date of signature (or date of payment if signature unknown)
   * @param df date format
   * @return a variable String shorter or equal to 35 characters
   */
  private String generateRum(int idper, DateFr date, DateFormat df) {
    DateFr d = (date == null ? new DateFr(new Date()) : new DateFr(date));
    SecureRandom rand = new SecureRandom();
    String r = String.valueOf(Math.abs(rand.nextInt()));
    // M(andat) prefix + randomInteger + date + idper
    return "M"
            + TextUtil.padWithLeadingZeros(r, 10)
            + " " + df.format(d.getTime())
            + " " + idper;
  }

  /**
   * Checks if {@code input} array has duplicates.
   *
   * @param input array of integer
   * @return true if duplicates are found or false if each value in the array is unique
   */
  private boolean hasDuplicates(int[] input) {
    List inputList = Arrays.asList(input);
    Set inputSet = new HashSet(inputList);
    return inputSet.size() < inputList.size();
  }

  /**
   * Returns a string representing the date in ISO 8601 format.
   *
   * Different standards may need different levels of granularity in the date and time,
   * so this profile defines six levels.
   * Standards that reference this profile should specify one or more of these granularities.
   * If a given standard allows more than one granularity, it should specify the meaning of the dates
   * and times with reduced precision, for example, the result of comparing two dates with different precisions.
   *
   * The formats are as follows. Exactly the components shown here must be present, with exactly this punctuation. Note that the "T" appears literally in the string, to indicate the beginning of the time element, as specified in ISO 8601.
   *
   * Year:
   * YYYY (eg 1997)
   * Year and month:
   * YYYY-MM (eg 1997-07)
   * Complete date:
   * YYYY-MM-DD (eg 1997-07-16)
   * Complete date plus hours and minutes:
   * YYYY-MM-DDThh:mmTZD (eg 1997-07-16T19:20+01:00)
   * Complete date plus hours, minutes and seconds:
   * YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
   * Complete date plus hours, minutes, seconds and a decimal fraction of a
   * second
   * YYYY-MM-DDThh:mm:ss.sTZD (eg 1997-07-16T19:20:30.45+01:00)
   *
   * where:
   *
   * YYYY = four-digit year
   * MM = two-digit month (01=January, etc.)
   * DD = two-digit day of month (01 through 31)
   * hh = two digits of hour (00 through 23) (am/pm NOT allowed)
   * mm = two digits of minute (00 through 59)
   * ss = two digits of second (00 through 59)
   * s = one or more digits representing a decimal fraction of a second
   * TZD = time zone designator (Z or +hh:mm or -hh:mm)
   *
   * @return
   */
  private String getIsoDateTime(DateFr date) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    if (date == null || date.toString().equals(DateFr.NULLDATE)) {
      return df.format(new Date());
    }

    return df.format(date.getTime());
  }

  private String getIsoDateTime() {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    return df.format(new Date());
  }

  private String getIsoDate(DateFr date) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    if (date == null || date.toString().equals(DateFr.NULLDATE)) {
      return df.format(new Date());
    }

    return df.format(date.getTime());
  }

  @Ignore
  public void testIsoDateTime() {
    DateFr date = new DateFr("15-09-2013");
    String defaultTime = "14:00:00";
    String expected = "2013-09-15T" + defaultTime;
    String d1 = getIsoDateTime(date);
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(date.getDate());
//		System.out.println(cal.getTime());
    assertTrue(expected.equals(d1));
    date = new DateFr("01-01-2014");
    d1 = getIsoDateTime(date);
    expected = "2014-01-01T" + defaultTime;
    assertTrue(expected.equals(d1));
    date = new DateFr();
    d1 = getIsoDateTime(date);
    expected = "2013-12-26T" + defaultTime;
    System.out.println(d1);
    assertFalse(expected.equals(d1));
    d1 = getIsoDateTime(null);
    expected = "2013-12-26T" + defaultTime;
    System.out.println(d1);
    assertFalse(expected.equals(d1));
  }

  @Ignore
  public void testRandomInt() {

    SecureRandom rand = new SecureRandom();

    int[] randoms = new int[1000000];
    int loop = 0;
    do {
      for (int i = 0; i < randoms.length; i++) {
        randoms[i] = Math.abs(rand.nextInt());
      }
      loop++;
      System.out.println("pass " + loop);

      if (hasDuplicates(randoms)) {
        throw new AssertionError("Duplicates !");
      }
    } while (loop < 10);
  }

  private String getGroupHeader(int nbOfTxs, String nm) {

    StringBuilder sb = new StringBuilder("<GrpHdr>");
    String uid = getMessageId();
//		String hexStringWithInsertedHyphens =  uid.toString().replaceFirst( "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5" );
    sb.append("<MsgId>MSGID ").append(uid).append("</MsgId>");
    sb.append("<CreDtTm>").append(getIsoDateTime()).append("</CreDtTm>");
    sb.append("<NbOfTxs>").append(nbOfTxs).append("</NbOfTxs>");
    sb.append("<InitgPty><Nm>").append(nm).append("</Nm></InitgPty></GrpHdr>");
    return sb.toString();
  }


  @Ignore
  public void testTxElement() {
    DDMandate debtor = new DDMandate(1234);
    debtor.setName("UNTEL");
    debtor.setDateSign(new DateFr("15-01-2014"));
    debtor.setRum("M1446136132 150114 1234");
    debtor.setIcs("FR00ZZZ123456");
    debtor.setBic("CEPAFRPP751");
    debtor.setIban("FR76425590000521027378707KK");

    String tx = getTxElement(debtor, 300.00);
    System.out.println(tx);
  }


  @Ignore
  public void testGroupHeader() {
//    SepaXmlBuilder sepa = new SepaXmlBuilder(DirectDebitService.getInstance(dc));
//    String sb = sepa.getGroupHeader();
//    System.out.println(sb.toString());
  }


  @Test
  public void testLength() {
    short max = 6;
    String untel = "ABRACADABRADANTESQUE";
    assertTrue(untel.substring(0, max).equals("ABRACA"));
    untel = "ABRACAD";
    assertTrue(untel.substring(0, max).equals("ABRACA"));
    untel = "ABRACA";
    assertTrue(untel.substring(0, max).equals("ABRACA"));

    StringBuilder sb = new StringBuilder();
    assertTrue(sb.toString() != null);
    assertTrue(sb.toString().isEmpty());

    DDMandate dd = new DDMandate(1234);
    Date now = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(now);
    // set last -3 years - 1 day
    cal.add(Calendar.YEAR, -3);
    cal.add(Calendar.DAY_OF_MONTH, -1);
    dd.setLastDebit(new DateFr(cal.getTime()));
    assertFalse(dd.isValid());
    // set last -3 years + 1 day
    cal.add(Calendar.DAY_OF_MONTH, 2);
    dd.setLastDebit(new DateFr(cal.getTime()));

    assertTrue(dd.isValid());
    // set last +3 years + 4 months
    cal.add(Calendar.YEAR, 3);
    cal.add(Calendar.MONTH, 4);
    dd.setLastDebit(new DateFr(cal.getTime()));
    // set last = now
    assertTrue(dd.isValid());
    dd.setLastDebit(new DateFr(now));
    assertTrue(dd.isValid());
  }

  private String getTxElement(DDMandate debtor, double amount) {

    StringBuilder sb = new StringBuilder();
    indent(sb, 3);
    sb.append("<DrctDbtTxInf>");
    indent(sb, 4);
    sb.append("<PmtId><EndToEndId>").append(getEnd2EndId()).append("</EndToEndId></PmtId>");
    indent(sb, 4);
    sb.append("<InstdAmt Ccy=\"EUR\">").append(amount).append("</InstdAmt>");
    indent(sb, 4);
    sb.append("<DrctDbtTx><MndtRltdInf><MndtId>").append(debtor.getRum()).append("</MndtId>");
    indent(sb, 4);
    sb.append("<DtOfSgntr>").append(getIsoDate(debtor.getDateSign())).append("</DtOfSgntr><AmdmntInd>false</AmdmntInd></MndtRltdInf></DrctDbtTx>");
    indent(sb, 4);
    sb.append("<DbtrAgt><FinInstnId><BIC>").append(debtor.getBic()).append("</BIC></FinInstnId></DbtrAgt>");
    indent(sb, 4);
    sb.append("<Dbtr><Nm>").append(debtor.getName()).append("</Nm></Dbtr>");
    indent(sb, 4);
    sb.append("<DbtrAcct><Id><IBAN>").append(debtor.getIban()).append("</IBAN></Id></DbtrAcct>");
    indent(sb, 3);
    sb.append("</DrctDbtTxInf>");

    return sb.toString();
  }

  private String getIsoDate(String date) {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    if (date == null || date.equals(DateFr.NULLDATE)) {
      return df.format(new Date());
    }

    return df.format(new DateFr(date).getDate());
  }

  private void indent(StringBuilder sb, int nbOfTabs) {
    //String tab = "  "; // 2 par défaut
    sb.append(TextUtil.LINE_SEPARATOR);
    for (int i = 0; i < nbOfTabs; i++) {
      sb.append("  ");
    }

  }

  private String getMessageId() {
    return "MSG ID " + String.valueOf(System.currentTimeMillis());
  }

  private String getPaymentInformationLabel(DateFr dateOfPayment, int batchN) {
//    String e = ((Param) schoolChoice.getSelectedItem()).getValue();
//    String school = (e == null) ? "MT" : e;
    String school = "MT";
//    DateFr d = new DateFr(ConfigUtil.getConf(ConfigKey.BEGINNING_YEAR.getKey(), dc));
//    DateFr f = new DateFr(ConfigUtil.getConf(ConfigKey.END_YEAR.getKey(), dc));
    DateFr d = new DateFr("01-09-2013");
    DateFr f = new DateFr("31-08-2014");
    int m = dateOfPayment.getMonth();
    DateFormat df = new SimpleDateFormat("MMM");

    return "COTIS " + school + " " + String.valueOf(d.getYear()) + "-" + String.valueOf(f.getYear()) + " " + df.format(dateOfPayment.getDate()) + " : " + batchN;
  }

  private String getEnd2EndId() {
    UUID uuid = UUID.randomUUID();
    long lg = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
    return Long.toString(lg, Character.MAX_RADIX);
  }
}
