/*
 * @(#) TestXmltoPdf.java Algem 2.15.10 01/10/18
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
 */
package net.algem.enrolment;

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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.10
 */
public class TestXmltoPdf {

  public TestXmltoPdf() {
  }

  /**
   * Html conversion is better with iText 5.
   * XmlPeer is obsolete.
   *
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
    props.put("__student_name__", "Mephisto Pheles");
    props.put("__student_birth__", "18/09/1980");
    props.put("__student_address__", "12 rue des Lois");
    props.put("__org_name__", "Le CIAM");
    props.put("__org_referent__", "Michel ANGE");
    props.put("__org_address__", "12 rue des peintres 33000 BORDEAUX");
    props.put("__org_tel__", "01 02 03 04 05");
    props.put("__org_mail__", "info@mycompany.local");
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
    //Properties props = new Properties();
    //FileInputStream io = new FileInputStream(new File("/tmp/contrat.properties"));
    //props.load(io);
    //System.out.println(MessageFormat.format(props.getProperty("line3"), "29-10-60"));
    //PdfReader reader = new PdfReader("/tmp/contrat-tpl.pdf");
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
    Font f1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.RED);
    Properties props = new Properties();
    props.put("__student_name__", "Mephisto Pheles");
    props.put("__student_birth__", "18/09/1980");
    props.put("__student_address__", "12 rue des Lois");
    props.put("__company_name__", "MY COMPANY");
    props.put("__company_ref__", "Michel ANGE");
    props.put("__company_address__", "12 rue des peintres 33000 BORDEAUX");
    props.put("__company_tel__", "01 02 03 04 05");
    props.put("__company_mail__", "info@mycompany.local");
    props.put("__company_fpcode__", "123456");
    props.put("__company_siret__", "fr124578");
    props.put("__company_ape__", "zz456e");
    props.put("__company_city__", "Bordeaux");
    props.put("__training_title__", "Cycle intensif guitare");
    props.put("__training_start__", "10-10-2016");
    props.put("__training_end__", "30-06-2017");
    props.put("__internal_volume__", "505");
    props.put("__external_volume__", "150");
    props.put("__funding__", "Région");
    props.put("__total_cost__", "4600,00");
    props.put("__total_funding__", "4600,00");
    props.put("__total_student__", "00,00");

    InputStream tpl = getClass().getResourceAsStream("/resources/doc/contrat.html");
    Scanner scanner = new Scanner(tpl);
    StringBuilder content = new StringBuilder();
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      for (Entry<Object, Object> entry : props.entrySet()) {
        String k = (String) entry.getKey();
        String v = (String) entry.getValue();
        if (line.contains(k)) {
          line = line.replaceAll(k, v);
        }
      }
      content.append(line);
    }
    tpl = new ByteArrayInputStream(content.toString().getBytes());
    //step 1
    Document document = new Document();
    //step 2
    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("/tmp/contrat.pdf"));
    // step 3
    document.open();
    // step 4
//    XMLWorkerHelper.getInstance().parseXHtml(writer, document, new FileInputStream(html));
    XMLWorkerHelper.getInstance().parseXHtml(writer, document, tpl);
    // step 5
    document.close();

  }

  /*
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
    peer.setContent("Anthony ANONYMOUS");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "birthdate");
    peer.addValue(ElementTags.SIZE, "14");
//      peer.addValue(ElementTags.STYLE, Markup.CSS_KEY_FONTFAMILY);
//      peer.addValue(Markup.CSS_KEY_FONTFAMILY, "serif");
    peer.addValue(ElementTags.STYLE, Markup.CSS_VALUE_BOLD);
    peer.setContent("15/04/1992");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "student_address");
    peer.setContent("86 rue des Tests - 33470 GUJAN MESTRAS");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "org_name");
    peer.setContent("MYCOMPANY");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "org_ref");
    peer.setContent("Michel ANGE");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "org_address");
    peer.setContent("35 rue des peintres – 33000 BORDEAUX");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "org_tel");
    peer.setContent("305 56 91 26 65");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "org_mail");
    peer.setContent("info@mycompany.local");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "fp_number");
    peer.setContent("72 33 00947 33");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "siret");
    peer.setContent("334 008 653 00011");
    tagMap.put(peer.getAlias(), peer);

    peer = new XmlPeer(ElementTags.CHUNK, "ape");
    peer.setContent("8552Z");
    tagMap.put(peer.getAlias(), peer);

    return tagMap;
  }
   */
}
