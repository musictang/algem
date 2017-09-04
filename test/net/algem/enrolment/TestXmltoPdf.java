/*
 * @(#) TestXmltoPdf.java Algem 2.15.0 01/09/2017
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
 */
package net.algem.enrolment;

//import com.lowagie.text.Chunk;
//import com.lowagie.text.Document;
//import com.lowagie.text.DocumentException;
//import com.lowagie.text.ElementTags;
//import com.lowagie.text.Font;
//import com.lowagie.text.FontFactory;
//import com.lowagie.text.Paragraph;
//import com.lowagie.text.Phrase;
//import com.lowagie.text.html.Markup;
//import com.lowagie.text.pdf.PdfWriter;
//import com.lowagie.text.xml.XmlPeer;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.lowagie.text.ElementTags;
//import com.lowagie.text.Paragraph;
import com.lowagie.text.html.Markup;
import com.lowagie.text.xml.XmlPeer;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 */
public class TestXmltoPdf {

  public TestXmltoPdf() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  /**
   * Html conversion is better with iText 5.
   * XmlPeer is obsolete.
   * @throws DocumentException
   * @throws IOException
   * @deprecated
   */
  @Ignore
  public void testFillItext2() throws DocumentException, IOException {
    File xml = new File("/tmp/contrat.xml");
    File html = new File("/tmp/contrat.html");
    File tpl = new File("/tmp/contrat.txt");
    Font f1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
//f1.setColor(Color.RED);

    Document document = new Document();
    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("/tmp/contrat.pdf"));
//      XmlParser.parse(document,xml.getPath(),getTagMap());
    document.open();
    Properties props = new Properties();
    props.put("__student_name__", "Jean-Marc Gobat");
    props.put("__student_birth__", "18/09/1980");
    props.put("__student_address__", "61 B rue Victor Hugo");
    props.put("__org_name__", "Le CIAM");
    props.put("__org_referent__", "Stéphane Allaux");
    props.put("__org_address__", "12 rue Leyteire 33000 BORDEAUX");
    props.put("__org_tel__", "01 02 03 04 05");
    props.put("__org_mail__", "info@leciam.org");
    props.put("__fpcode__", "123456");
    props.put("__siret__", "fr124578");
    props.put("__ape__", "zz456e");

    Scanner scanner = new Scanner(tpl);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      for (Entry<Object, Object> entry : props.entrySet()) {
        String k = (String) entry.getKey();
        String v = (String) entry.getValue();
        if (line.contains(k)) {
          line = line.replaceAll(k, v);
//          System.out.println(line);
        }
      }
      if (line.startsWith("*")) {
        line = line.substring(1);
        Paragraph p = new Paragraph();
        p.setSpacingBefore(20);
        p.add(new Phrase(line, f1));
        document.add(p);
      } else {
        document.add(new Phrase(line));
        document.add(Chunk.NEWLINE);
      }
    }
    scanner.close();
    document.close();

//HtmlParser.parse(document,html.getPath());
//      Properties props = new Properties();
//      FileInputStream io = new FileInputStream(new File("/tmp/contrat.properties"));
//      props.load(io);
//      System.out.println(MessageFormat.format(props.getProperty("line3"), "29-10-60"));
//    PdfReader reader = new PdfReader("/tmp/contrat-tpl.pdf");
//PdfStamper stamper = new PdfStamper(reader, new FileOutputStream("/tmp/contrat2.pdf"));
//AcroFields form = stamper.getAcroFields();
//form.setField("student_name", "Laura Specimen");
//form.setField("org_name", "Musiques Tangentes");
//stamper.setFormFlattening(true);
//stamper.close();
    assertTrue(1 == 1);
  }

  @Test
  public void testFillItext5() throws DocumentException, IOException {
    File xml = new File("/tmp/contrat.xml");
    File html = new File("/tmp/contrat.html");
    File tpl = new File("/tmp/contrat.html");
    Font f1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.RED);
    Properties props = new Properties();
    props.put("__student_name__", "Jean-Marc Gobat");
    props.put("__student_birth__", "18/09/1980");
    props.put("__student_address__", "61 B rue Victor Hugo");
    props.put("__org_name__", "Le CIAM");
    props.put("__org_referent__", "Stéphane Allaux");
    props.put("__org_address__", "12 rue Leyteire 33000 BORDEAUX");
    props.put("__org_tel__", "01 02 03 04 05");
    props.put("__org_mail__", "info@leciam.org");
    props.put("__fpcode__", "123456");
    props.put("__siret__", "fr124578");
    props.put("__ape__", "zz456e");

    Scanner scanner = new Scanner(tpl);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      for (Entry<Object, Object> entry : props.entrySet()) {
        String k = (String) entry.getKey();
        String v = (String) entry.getValue();
        if (line.contains(k)) {
          line = line.replaceAll(k, v);
//          System.out.println(line);
        }
      }
    }

    //step 1
    Document document = new Document();
    //step 2
    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("/tmp/contrat.pdf"));
    // step 3
    document.open();
    // step 4
    XMLWorkerHelper.getInstance().parseXHtml(writer, document, new FileInputStream(html));
    // step 5
    document.close();
  }

  private HashMap<String, XmlPeer> getTagMap() {
    HashMap<String, XmlPeer> tagMap = new HashMap<>();
//    Font largeBold = new Font(Font.COURIER, 32, Font.BOLD);
    XmlPeer peer = new XmlPeer(ElementTags.ITEXT, "contrat");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "test");
    peer.setContent("Une cellule de tableau");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "article1");
    peer.addValue(ElementTags.COLOR, "#FF0000");
    peer.addValue(ElementTags.SIZE, "12");
    peer.addValue(ElementTags.STYLE, Markup.CSS_KEY_MARGINTOP);
    peer.addValue(Markup.CSS_KEY_MARGINTOP, "1em");
    peer.setContent("Article 1 : Objet du contrat");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "student_name");
    peer.addValue(ElementTags.COLOR, "#FF0000");
    peer.addValue(ElementTags.SIZE, "10");
//javax.xml.parsers.SAXParser sax = SAXParserFactory.
    peer.setContent("Anthony ALBERT");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "birthdate");
    peer.addValue(ElementTags.SIZE, "14");
//      peer.addValue(ElementTags.STYLE, Markup.CSS_KEY_FONTFAMILY);
//      peer.addValue(Markup.CSS_KEY_FONTFAMILY, "serif");
    peer.addValue(ElementTags.STYLE, Markup.CSS_VALUE_BOLD);
    peer.setContent("15/04/1992");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "student_address");
    peer.setContent("86 rue Dejean Castaing - 33470 GUJAN MESTRAS");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "org_name");
    peer.setContent("CIAM");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "org_ref");
    peer.setContent("Stéphane ALAUX");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "org_address");
    peer.setContent("35 rue Leyteire – 33000 BORDEAUX");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "org_tel");
    peer.setContent("305 56 91 26 65");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "org_mail");
    peer.setContent("info@le-ciam.com");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "fp_number");
    peer.setContent("72 33 00947 33");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "siret");
    peer.setContent("334 008 653 00027");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "ape");
    peer.setContent("8552Z");
    tagMap.put(peer.getAlias(), peer);

    return tagMap;
  }
}
