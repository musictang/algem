///*
// * @(#)TestSepa.java	2.8.r 16/01/14
// *
// * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
// *
// * This file is part of Algem.
// * Algem is free software: you can redistribute it and/or modify it
// * under the terms of the GNU Affero General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * Algem is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// * GNU Affero General Public License for more details.
// *
// * You should have received a copy of the GNU Affero General Public License
// * along with Algem. If not, see <http://www.gnu.org/licenses/>.
// *
// */
//package net.algem.accounting;
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.security.SecureRandom;
//import java.sql.SQLException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import net.algem.TestProperties;
//import net.algem.planning.DateFr;
//import net.algem.util.DataCache;
//import net.algem.util.DataConnection;
//import net.algem.util.TextUtil;
//import org.dom4j.*;
//import org.dom4j.io.OutputFormat;
//import org.dom4j.io.XMLWriter;
//import static org.junit.Assert.*;
//import org.junit.*;
//
///**
// *
// * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
// * @version 2.8.r
// * @since 2.8.r 24/12/13
// */
//public class TestSepa
//{
//
//  private static DataConnection dc;
//  private DataCache dataCache;
//
//  public TestSepa() {
//  }
//
//  @BeforeClass
//  public static void setUpClass() throws Exception {
//    dc = TestProperties.getDataConnection();
//  }
//
//  @AfterClass
//  public static void tearDownClass() throws Exception {
//  }
//
//  @Before
//  public void setUp() {
//  }
//
//  @After
//  public void tearDown() {
//  }
//
//  @Ignore
//  public void testRum() {
//    int id1 = 1234;
//    int id2 = 1234;
//    int id3 = 1234;
//    int id4 = 123;
//    int id5 = 12345;
//
//    DateFormat df = new SimpleDateFormat("ddMMyy");
//    DateFr d = new DateFr("15-09-2013");
//
//    String rumid1 = generateRum(id1, new DateFr(), df);
//    String rumid2 = generateRum(id2, null, df);
//    String rumid3 = generateRum(id3, new DateFr("16-09-2013"), df);
//    String rumid4 = generateRum(id4, new DateFr("16-09-2000"), df);
//    String rumid5 = generateRum(id5, new DateFr("16-09-2027"), df);
////		String rumid4 = "M" + id3 + " " + df.format(d.getTime()) + " " + Integer.MAX_VALUE;
//
//    System.out.println(rumid1);
//    System.out.println(rumid2);
//    System.out.println(rumid3);
//    System.out.println(rumid4);
//    System.out.println(rumid5);
//    assertFalse(rumid1.equals(rumid2));
//    assertFalse(rumid1.equals(rumid3));
//    assertFalse(rumid2.equals(rumid3));
//    assertFalse(rumid1.equals(rumid4) && rumid2.equals(rumid4) && rumid3.equals(rumid4));
//    assertFalse(rumid1.equals(rumid5) && rumid2.equals(rumid5) && rumid3.equals(rumid5) && rumid4.equals(rumid5));
//  }
//
//  /**
//   * Generates Reference Unique of Mandat.
//   *
//   * @param idper person id
//   * @param date date of signature (or date of payment if signature unknown)
//   * @param df date format
//   * @return a variable String shorter or equal to 35 characters
//   */
//  private String generateRum(int idper, DateFr date, DateFormat df) {
//    DateFr d = (date == null ? new DateFr(new Date()) : new DateFr(date));
//    SecureRandom rand = new SecureRandom();
//    String r = String.valueOf(Math.abs(rand.nextInt()));
//    // M(andat) prefix + randomInteger + date + idper
//    return "M"
//            + TextUtil.padWithLeadingZeros(r, 10)
//            + " " + df.format(d.getTime())
//            + " " + idper;
//  }
//
//  /**
//   * Checks if {@code input} array has duplicates.
//   *
//   * @param input array of integer
//   * @return true if duplicates are found or false if each value in the array is unique
//   */
//  private boolean hasDuplicates(int[] input) {
//    List inputList = Arrays.asList(input);
//    Set inputSet = new HashSet(inputList);
//    return inputSet.size() < inputList.size();
//  }
//
//  /**
//   * Returns a string representing the date in ISO 8601 format.
//   *
//   * Different standards may need different levels of granularity in the date and time,
//   * so this profile defines six levels.
//   * Standards that reference this profile should specify one or more of these granularities.
//   * If a given standard allows more than one granularity, it should specify the meaning of the dates
//   * and times with reduced precision, for example, the result of comparing two dates with different precisions.
//   *
//   * The formats are as follows. Exactly the components shown here must be present, with exactly this punctuation. Note that the "T" appears literally in the string, to indicate the beginning of the time element, as specified in ISO 8601.
//   *
//   * Year:
//   * YYYY (eg 1997)
//   * Year and month:
//   * YYYY-MM (eg 1997-07)
//   * Complete date:
//   * YYYY-MM-DD (eg 1997-07-16)
//   * Complete date plus hours and minutes:
//   * YYYY-MM-DDThh:mmTZD (eg 1997-07-16T19:20+01:00)
//   * Complete date plus hours, minutes and seconds:
//   * YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
//   * Complete date plus hours, minutes, seconds and a decimal fraction of a
//   * second
//   * YYYY-MM-DDThh:mm:ss.sTZD (eg 1997-07-16T19:20:30.45+01:00)
//   *
//   * where:
//   *
//   * YYYY = four-digit year
//   * MM = two-digit month (01=January, etc.)
//   * DD = two-digit day of month (01 through 31)
//   * hh = two digits of hour (00 through 23) (am/pm NOT allowed)
//   * mm = two digits of minute (00 through 59)
//   * ss = two digits of second (00 through 59)
//   * s = one or more digits representing a decimal fraction of a second
//   * TZD = time zone designator (Z or +hh:mm or -hh:mm)
//   *
//   * @return
//   */
//  private String getIsoDateTime(DateFr date) {
//    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//    if (date == null || date.toString().equals(DateFr.NULLDATE)) {
//      return df.format(new Date());
//    }
//
//    return df.format(date.getTime());
//  }
//
//  private String getIsoDateTime() {
//    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//    return df.format(new Date());
//  }
//
//  private String getIsoDate(DateFr date) {
//    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//    if (date == null || date.toString().equals(DateFr.NULLDATE)) {
//      return df.format(new Date());
//    }
//
//    return df.format(date.getTime());
//  }
//
//  @Ignore
//  public void testIsoDateTime() {
//    DateFr date = new DateFr("15-09-2013");
//    String defaultTime = "14:00:00";
//    String expected = "2013-09-15T" + defaultTime;
//    String d1 = getIsoDateTime(date);
////		Calendar cal = Calendar.getInstance();
////		cal.setTime(date.getDate());
////		System.out.println(cal.getTime());
//    assertTrue(expected.equals(d1));
//    date = new DateFr("01-01-2014");
//    d1 = getIsoDateTime(date);
//    expected = "2014-01-01T" + defaultTime;
//    assertTrue(expected.equals(d1));
//    date = new DateFr();
//    d1 = getIsoDateTime(date);
//    expected = "2013-12-26T" + defaultTime;
//    System.out.println(d1);
//    assertFalse(expected.equals(d1));
//    d1 = getIsoDateTime(null);
//    expected = "2013-12-26T" + defaultTime;
//    System.out.println(d1);
//    assertFalse(expected.equals(d1));
//  }
//
//  @Ignore
//  public void testRandomInt() {
//
//    SecureRandom rand = new SecureRandom();
//
//    int[] randoms = new int[1000000];
//    int loop = 0;
//    do {
//      for (int i = 0; i < randoms.length; i++) {
//        randoms[i] = Math.abs(rand.nextInt());
//      }
//      loop++;
//      System.out.println("pass " + loop);
//
//      if (hasDuplicates(randoms)) {
//        throw new AssertionError("Duplicates !");
//      }
//    } while (loop < 10);
//  }
//
//  @Ignore
//  /* public void testXml() {
//   * String org = "Musiques Tangentes";
//   * String header = getGroupHeader(1, org);
//   * System.out.println(header);
//   * assertTrue(header.startsWith("<GrpHdr><MsgId>"));
//   * assertTrue(header.endsWith("<InitgPty><Nm>" + org + "</Nm></InitgPty></GrpHdr>"));
//   * String inf = getPaymentInformation("Musiques Tangentes", "FR7610041010050500013M02606", "BANKFRPP", "FR00ZZZ000144");
//   * System.out.println(inf);
//   * assertTrue(inf.startsWith("<PmtInfId>"));
//   * assertTrue(inf.endsWith("</CdtrSchmeId>"));
//   *
//   * String tx = getTransaction(1000.25, "M1446136132 160927 12345", new DateFr("15-09-2013"), "12345678", "UNTEL", "FR763004136210001234567811");
//   * System.out.println(tx);
//   * } */
//  private String getGroupHeader(int nbOfTxs, String nm) {
//
//    StringBuilder sb = new StringBuilder("<GrpHdr>");
//    String uid = getMessageId();
////		String hexStringWithInsertedHyphens =  uid.toString().replaceFirst( "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5" );
//    sb.append("<MsgId>MSGID ").append(uid).append("</MsgId>");
//    sb.append("<CreDtTm>").append(getIsoDateTime()).append("</CreDtTm>");
//    sb.append("<NbOfTxs>").append(nbOfTxs).append("</NbOfTxs>");
//    sb.append("<InitgPty><Nm>").append(nm).append("</Nm></InitgPty></GrpHdr>");
//    return sb.toString();
//  }
//
//  private Document getXMLDocument() {
//    String DEFAULT_NAMESPACE = "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02";
//    String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
//    String rootElement = "Document";
//
//    Namespace rootNs = new Namespace("", DEFAULT_NAMESPACE); // root namespace uri
//    Namespace xsiNs = new Namespace("xsi", XSI_NAMESPACE); // xsi namespace uri
//
//    Document document = DocumentHelper.createDocument();
//    QName rootQName = QName.get(rootElement, rootNs); // root element's name
//    Element root = document.addElement(rootQName);
//    root.add(xsiNs);
//    // pour tests seulement
//    root.addAttribute("xsi:schemaLocation", "urn:iso:std:iso:20022:tech:xsd:pain.008.001.02 file:/home/jm/Algem/src/git/test/net/algem/pain.008.001.02.xsd");
//
//    Element body = root.addElement("CstmrDrctDbtInitn");
//
////		addTxElement(body, 1000.25, "M1446136132 160927 12345", new DateFr("15-09-2013"), "BANKFRPP", "UNTEL", "FR763004136210001234567811");
//    // nombre total de transactions
//    Element grpHeader = body.addElement("GrpHdr");
//    // LOT FIRST
//    Element paymentInformation = body.addElement("PmtInf");
//    addPaymentInformation(paymentInformation, "Musiques Tangentes", "FR7610041010050500013M02606", "BANKFRPP", "FR00ZZZ000144");
//    Element drctDbtTxInf = paymentInformation.addElement("DrctDbtTxInf");
//    addTxElement(drctDbtTxInf, 1000.25, "M1446136132 160927 12345", new DateFr("15-09-2013"), "BANKFRPP", "UNTEL", "FR763004136210001234567811");
//
//    // LOT RCUR
//
//
//
//
//
//    addGroupHeader(grpHeader, 1, "Musiques Tangentes");
//    return document;
//  }
//
//  private void addGroupHeader(Element grpHeader, int nbOfTxs, String nm) {
//
//    String uid = getMessageId();
////		Element grpHeader = parent.addElement("GrpHdr");
//    grpHeader.addElement("MsgId").addText(uid);
//    grpHeader.addElement("CreDtTm").addText(getIsoDateTime());
//    grpHeader.addElement("NbOfTxs").addText(String.valueOf(nbOfTxs));
//    grpHeader.addElement("InitgPty").addElement("Nm").addText(nm);
//
//  }
//
//  @Ignore
//  public void testXMLDocument() throws IOException {
//    Document d = getXMLDocument();
////		System.out.println(d.asXML());
//    write(d);
//  }
//
//  private void write(Document document) throws IOException {
//    XMLWriter writer = null;
//    // lets write to a file
//        /* XMLWriter writer = new XMLWriter(
//     * new FileWriter( "output.xml" )
//     * );
//     * writer.write( document );
//     * writer.close(); */
//
//
//    // Pretty print the document to System.out
////		OutputFormat format = new OutputFormat("  ", true);
//    OutputFormat format = OutputFormat.createPrettyPrint();
////		format.setNewLineAfterDeclaration(false);
//    writer = new XMLWriter(System.out, format);
//    writer.write(document);
//
//    // Compact format to System.out
//		/* format = OutputFormat.createCompactFormat();
//     * writer = new XMLWriter(System.out, format);
//     * writer.write(document); */
//  }
//
//  private void addPaymentInformation(Element parent, String org, String cdtrAcct, String cdtrAgt, String ics) {
//    DateFr d = new DateFr("15-01-2013");
//
//    parent.addElement("PmtInfId").addText(getPaymentInformationLabel(d, 1));
//
//    parent.addElement("PmtMtd").addText("DD");
//    Element pmtTpInf = parent.addElement("PmtTpInf");
//    pmtTpInf.addElement("SvcLvl").addElement("Cd").addText("SEPA");
//    pmtTpInf.addElement("LclInstrm").addElement("Cd").addText("CORE");
//    // en fonction du statut présent dans la table  mandatsepa
//    pmtTpInf.addElement("SeqTp").addText("FRST");// RECR
//    parent.addElement("ReqdColltnDt").addText(getIsoDate(d));
//
//    parent.addElement("Cdtr").addElement("Nm").addText("org");
//    parent.addElement("CdtrAcct").addElement("Id").addElement("IBAN").addText(cdtrAcct);
//    parent.addElement("CdtrAgt").addElement("FinInstnId").addElement("BIC").addText(cdtrAgt);
//
//    Element other = parent.addElement("CdtrSchmeId").addElement("Id").addElement("PrvtId").addElement("Othr");
//    other.addElement("Id").addText(ics);
//    other.addElement("SchmeNm").addElement("Prtry").addText("SEPA");
//  }
//
//  @Ignore
//  public void testTransactionElement() {
//    addTxElement(null, 100, null, null, null, null, null);
//  }
//
//  private void addTxElement(Element parent, double amount, String rum, DateFr date, String dbtorAgent, String dbTorname, String iban) {
////		Element drctDbtTxInf = parent.addElement("DrctDbtTxInf");
//
////		Element drctDbtTxInf = new DefaultElement("DrctDbtTxInf");
//
//    parent.addElement("PmtId").addElement("EndToEndId").addText(getEnd2EndId());
//    parent.addElement("InstdAmt").addAttribute("Ccy", "EUR").addText(String.valueOf(amount));
//    Element drctDbtTx = parent.addElement("DrctDbtTx");
//    Element mndtRltdInf = drctDbtTx.addElement("MndtRltdInf");
//    mndtRltdInf.addElement("MndtId").addText(rum);
//    mndtRltdInf.addElement("DtOfSgntr").addText(getIsoDate(date));
//    // verifier si changement de n° ics par rapport à l'ics courant (optionnel)
//    mndtRltdInf.addElement("AmdmntInd").addText("false");
//    parent.addElement("DbtrAgt").addElement("FinInstnId").addElement("BIC").addText(dbtorAgent);
//    parent.addElement("Dbtr").addElement("Nm").addText(dbTorname);
//    parent.addElement("DbtrAcct").addElement("Id").addElement("IBAN").addText(iban);
//  }
//
//  @Ignore
//  public void testTxElement() {
//    DDMandate debtor = new DDMandate(1234);
//    debtor.setName("UNTEL");
//    debtor.setDateSign(new DateFr("15-01-2014"));
//    debtor.setRum("M1446136132 150114 1234");
//    debtor.setIcs("FR00ZZZ123456");
//    debtor.setBic("CEPAFRPP751");
//    debtor.setIban("FR76425590000521027378707KK");
//
//    String tx = getTxElement(debtor, 300.00);
//    System.out.println(tx);
//  }
//
//  @Ignore
//  public void testDirectDebitTx() throws DocumentException {
////		DirectDebitExportDlg dlg = new DirectDebitExportDlg(new Frame(),"test",dc);
//    SepaXmlBuilder sepa = new SepaXmlBuilder(DirectDebitService.getInstance(dc));
//    try {
//      String xml = sepa.getDirectDebitTransaction(166, 41185, "MFORLOISIR");
//      System.out.println(xml);
//      assertTrue(xml.trim().startsWith("<DrctDbtTxInf>"));
//      assertTrue(xml.trim().endsWith("</DrctDbtTxInf>"));
//
//      Document d = DocumentHelper.parseText(xml);
//      assertTrue(d != null && d.hasContent());
//      assertTrue(String.valueOf(d.nodeCount()), d.nodeCount() == 1);
//
//      Element root = d.getRootElement();
//      List elements = root.elements();
//      Element e0 = (Element) elements.get(0);
//      assertTrue(e0.getName(), e0.getName().equals("PmtId"));
//      assertTrue(e0.element("EndToEndId") != null);
//      assertTrue(e0.element("EndToEndId").getText() != null);
//      int end2endLength = e0.element("EndToEndId").getText().length();
//      assertTrue(String.valueOf(end2endLength), end2endLength >= 12);
//      assertTrue(((Element) elements.get(1)).getName(), ((Element) elements.get(1)).getName().equals("InstdAmt"));
//      Element mndtRltdInf = ((Element) elements.get(2)).element("MndtRltdInf");
//      assertTrue(mndtRltdInf.getName(), mndtRltdInf.getName().equals("MndtRltdInf"));
//      assertTrue(String.valueOf(mndtRltdInf.elements().size()), mndtRltdInf.elements().size() == 3);
//      assertTrue(((Element) elements.get(3)).getName(), ((Element) elements.get(3)).getName().equals("DbtrAgt"));
//      assertTrue(((Element) elements.get(4)).getName(), ((Element) elements.get(4)).getName().equals("Dbtr"));
//      assertTrue(((Element) elements.get(5)).getName(), ((Element) elements.get(5)).getName().equals("DbtrAcct"));
//
//    } catch (SQLException ex) {
//      throw new AssertionError(ex.getMessage());
//    }
//  }
//
//  @Ignore
//  public void testPaymentInformation() throws DocumentException {
//    SepaXmlBuilder sepa = new SepaXmlBuilder(DirectDebitService.getInstance(dc));
//    DateFr ddDate = new DateFr("15-01-2014");
//    sepa.setMessageId();
//    sepa.setTxRmtInf("TEST SEPA", ddDate);
//    StringBuilder sb = sepa.getPaymentInformation(ddDate, DDSeqType.FRST, 0, 21500, 3);
//    System.out.println(sb.toString());
//  }
//
//  @Ignore
//  public void testGroupHeader() {
//    SepaXmlBuilder sepa = new SepaXmlBuilder(DirectDebitService.getInstance(dc));
//    String sb = sepa.getGroupHeader();
//    System.out.println(sb.toString());
//  }
//
//  @Ignore
//  public void testPayment() throws DocumentException, SQLException {
//    //INSERT INTO echeancier2 VALUES (default,'2014-01-16',166,16321,0,'PRL','p166 a16321 test',12500,'',0,1,true,false,'E','AFORLOISIR',null);
//    //INSERT INTO echeancier2 VALUES (default,'2014-01-16',166,16321,0,'PRL','p166 a16321 test',20066,'',0,1,true,false,'E','AFORLOISIR',null);
//    //INSERT INTO echeancier2 VALUES (default,'2014-01-16',167,16321,0,'PRL','p166 a16321 test',40000,'',0,1,true,false,'E','AFORLOISIR',null);
//    //INSERT INTO prlsepa VALUES (default,167,'2013-12-28','2014-01-16','t','FRST','FR68ZZZ000144','M1477439561 150114 167');
//    //INSERT INTO banque VALUES ('12345','BANQUE TEST','t');
//    //INSERT INTO guichet VALUES ('12345','12345',20693,'BANQUE TEST AG','BPTPPPK');
//    //INSERT INTO personne VALUES (168,1,'DUPONT','Marcel','M','f','','f');
//    //INSERT INTO prlsepa VALUES (default,168,'2013-12-28','2014-01-16','t','RCUR','FR68ZZZ000144','M1477439561 150114 168');
//    //INSERT INTO rib VALUES (168,'12345','12345','1234567891A','16',20693,'FR7612345123451234567891A16');
//    //INSERT INTO echeancier2 VALUES (default,'2014-01-16',168,168,0,'PRL','p168 a16321 test',1240000,'',0,1,true,false,'E','AFORLOISIR',null);
//    //INSERT INTO prlsepa VALUES (default,169,'2013-12-28','2014-01-16','t','FRST','FR68ZZZ000144','M1477439561 150114 169');
//
//    //INSERT INTO echeancier2 VALUES (default,'2014-01-16',169,169,0,'PRL','p169 a16321 test',66600,'',0,1,true,false,'E','AFORLOISIR',null);
//    //
//    // pas de rib pour 167
//    // erreur bic pour 168 (notprovided)//  12345 12345 1234567891A 16
//    // erreur 169 pas d'iban pas de bic
//    // 1 FRST, 1 RCUR
//    Document doc = null;
//
//    SepaXmlBuilder sepa = new SepaXmlBuilder(DirectDebitService.getInstance(dc));
//    StringBuilder xml = new StringBuilder();
//    DateFr ddDate = new DateFr("16-01-2014");
//    sepa.setMessageId();
//    sepa.setTxRmtInf("TEST SEPA", ddDate);
//    String xmlDoc = sepa.getDocument();
//
//    List<String> payments = new ArrayList<String>();
//
//    String xmlPayment = sepa.getPayment(0, ddDate, DDSeqType.FRST, 0);
//    if (xmlPayment != null) {
//      doc = DocumentHelper.parseText(xmlPayment);
////			System.out.println(xmlPayment);
//      payments.add(xmlPayment);
//    }
//    xmlPayment = sepa.getPayment(0, ddDate, DDSeqType.RCUR, sepa.getBatch());
//    if (xmlPayment != null) {
//      doc = DocumentHelper.parseText(xmlPayment);
////			System.out.println(xmlPayment);
//      payments.add(xmlPayment);
//    }
//    System.out.println(sepa.getMailing());
//    System.out.println(sepa.getLog());
//    assertTrue(sepa.getNumberOfTx() == 2);
//    assertTrue(sepa.getTotalTx() == 1272566);
//    assertTrue(sepa.getMailing().length() > 0);
//    assertTrue(sepa.getMailing().toString().contains("166;"));
//    assertTrue(sepa.getMailing().toString().contains("168;"));
//    assertFalse(sepa.getMailing().toString().contains("167;"));
//    assertFalse(sepa.getMailing().toString().contains("169;"));
//    assertTrue(sepa.getLog().length() > 0);
//    assertTrue(sepa.getLog().toString().contains("167"));
//    assertTrue(sepa.getLog().toString().contains("168"));
//    assertTrue(sepa.getLog().toString().contains("169"));
//    assertFalse(sepa.getLog().toString().contains("166"));
////		System.out.println(sepa.getBatch());
//
//    assertTrue(sepa.getBatch() == 2);
//
//    String xmlHeader = sepa.getGroupHeader();
////		System.out.println(grp);
//
//    // affichage xml
//    xml.append(xmlDoc);
//    xml.append(xmlHeader);
//    for (String p : payments) {
//      xml.append(p);
//    }
//
//    xml.append(TextUtil.LINE_SEPARATOR).append("  </CstmrDrctDbtInitn>");
//    xml.append(TextUtil.LINE_SEPARATOR).append("</Document>");
//    System.out.println(xml.toString());
//
//  }
//
//  @Test
//  public void testLength() {
//    short max = 6;
//    String untel = "ABRACADABRADANTESQUE";
//    assertTrue(untel.substring(0, max).equals("ABRACA"));
//    untel = "ABRACAD";
//    assertTrue(untel.substring(0, max).equals("ABRACA"));
//    untel = "ABRACA";
//    assertTrue(untel.substring(0, max).equals("ABRACA"));
//
//    StringBuilder sb = new StringBuilder();
//    assertTrue(sb.toString() != null);
//    assertTrue(sb.toString().isEmpty());
//
//    DDMandate dd = new DDMandate(1234);
//    Date now = new Date();
//    Calendar cal = Calendar.getInstance();
//    cal.setTime(now);
//    // set last -3 years - 1 day
//    cal.add(Calendar.YEAR, -3);
//    cal.add(Calendar.DAY_OF_MONTH, -1);
//    dd.setLastDebit(new DateFr(cal.getTime()));
//    assertFalse(dd.isValid());
//    // set last -3 years + 1 day
//    cal.add(Calendar.DAY_OF_MONTH, 2);
//    dd.setLastDebit(new DateFr(cal.getTime()));
//
//    assertTrue(dd.isValid());
//    // set last +3 years + 4 months
//    cal.add(Calendar.YEAR, 3);
//    cal.add(Calendar.MONTH, 4);
//    dd.setLastDebit(new DateFr(cal.getTime()));
//    // set last = now
//    assertTrue(dd.isValid());
//    dd.setLastDebit(new DateFr(now));
//    assertTrue(dd.isValid());
//  }
//
//  private String getTxElement(DDMandate debtor, double amount) {
//
//    StringBuilder sb = new StringBuilder();
//    indent(sb, 3);
//    sb.append("<DrctDbtTxInf>");
//    indent(sb, 4);
//    sb.append("<PmtId><EndToEndId>").append(getEnd2EndId()).append("</EndToEndId></PmtId>");
//    indent(sb, 4);
//    sb.append("<InstdAmt Ccy=\"EUR\">").append(amount).append("</InstdAmt>");
//    indent(sb, 4);
//    sb.append("<DrctDbtTx><MndtRltdInf><MndtId>").append(debtor.getRum()).append("</MndtId>");
//    indent(sb, 4);
//    sb.append("<DtOfSgntr>").append(getIsoDate(debtor.getDateSign())).append("</DtOfSgntr><AmdmntInd>false</AmdmntInd></MndtRltdInf></DrctDbtTx>");
//    indent(sb, 4);
//    sb.append("<DbtrAgt><FinInstnId><BIC>").append(debtor.getBic()).append("</BIC></FinInstnId></DbtrAgt>");
//    indent(sb, 4);
//    sb.append("<Dbtr><Nm>").append(debtor.getName()).append("</Nm></Dbtr>");
//    indent(sb, 4);
//    sb.append("<DbtrAcct><Id><IBAN>").append(debtor.getIban()).append("</IBAN></Id></DbtrAcct>");
//    indent(sb, 3);
//    sb.append("</DrctDbtTxInf>");
//
//    return sb.toString();
//  }
//
//  private String getIsoDate(String date) {
//    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//    if (date == null || date.equals(DateFr.NULLDATE)) {
//      return df.format(new Date());
//    }
//
//    return df.format(new DateFr(date).getDate());
//  }
//
//  private void indent(StringBuilder sb, int nbOfTabs) {
//    //String tab = "  "; // 2 par défaut
//    sb.append(TextUtil.LINE_SEPARATOR);
//    for (int i = 0; i < nbOfTabs; i++) {
//      sb.append("  ");
//    }
//
//  }
//
//  private String getMessageId() {
//    return "MSG ID " + String.valueOf(System.currentTimeMillis());
//  }
//
//  private String getPaymentInformationLabel(DateFr dateOfPayment, int batchN) {
////    String e = ((Param) schoolChoice.getSelectedItem()).getValue();
////    String school = (e == null) ? "MT" : e;
//    String school = "MT";
////    DateFr d = new DateFr(ConfigUtil.getConf(ConfigKey.BEGINNING_YEAR.getKey(), dc));
////    DateFr f = new DateFr(ConfigUtil.getConf(ConfigKey.END_YEAR.getKey(), dc));
//    DateFr d = new DateFr("01-09-2013");
//    DateFr f = new DateFr("31-08-2014");
//    int m = dateOfPayment.getMonth();
//    DateFormat df = new SimpleDateFormat("MMM");
//
//    return "COTIS " + school + " " + String.valueOf(d.getYear()) + "-" + String.valueOf(f.getYear()) + " " + df.format(dateOfPayment.getDate()) + " : " + batchN;
//  }
//
//  private String getEnd2EndId() {
//    UUID uuid = UUID.randomUUID();
//    long lg = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
//    return Long.toString(lg, Character.MAX_RADIX);
//  }
//}
